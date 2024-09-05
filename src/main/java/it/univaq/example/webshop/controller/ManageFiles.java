package it.univaq.example.webshop.controller;

import it.univaq.example.webshop.data.dao.impl.WebshopDataLayer;
import it.univaq.example.webshop.data.model.Group;
import it.univaq.example.webshop.data.model.Image;
import it.univaq.example.webshop.data.model.User;
import it.univaq.example.webshop.data.model.impl.UserRoleEnum;
import it.univaq.framework.data.DataException;
import it.univaq.framework.result.TemplateResult;
import it.univaq.framework.security.SecurityHelpers;
import it.univaq.framework.result.TemplateManagerException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class ManageFiles extends WebshopBaseController {

    private void action_anonymous(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException, TemplateManagerException {
        TemplateResult res = new TemplateResult(getServletContext());
        String completeRequestURL = request.getRequestURL()
                + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        request.setAttribute("username", "Hacker");
        request.setAttribute("group", "Stranger");
        request.setAttribute("completeRequestURL", "login?referrer=" + completeRequestURL);
        res.activate("homepage.ftl.html", request, response);
    }

    private void action_default(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, TemplateManagerException {
        try {
            TemplateResult res = new TemplateResult(getServletContext());
            int user_key = Integer.parseInt(request.getSession().getAttribute("userid").toString());
            User user = ((WebshopDataLayer) request.getAttribute("datalayer")).getUserDAO().getUser(user_key);
            Group group = ((WebshopDataLayer) request.getAttribute("datalayer")).getGroupDAO().getGroupByUser(user_key);

            request.setAttribute("username", user.getUsername());
            request.setAttribute("group", group.getName());  
            
            List<Image> images = ((WebshopDataLayer) request.getAttribute("datalayer")).getImageDAO().getImages();
            long storageWeight = 0;
            Map<Integer,Integer> imageUsages = new HashMap<>();
            for(Image i : images) {
                storageWeight += i.getImageSize();
                imageUsages.put(i.getKey(), ((WebshopDataLayer) request.getAttribute("datalayer")).getCategoryDAO().getCategoriesByImage(i.getKey()).size());
            }
            int percentageStorage = calculatePercentage(storageWeight, 1e12f);
            request.setAttribute("storageWeight", humanReadableFileSize(storageWeight));
            request.setAttribute("percentageStorage", percentageStorage);
            request.setAttribute("percentageStorageCSS", "style=width:" + percentageStorage + "%;");
            request.setAttribute("images", images);
            request.setAttribute("imageUsages", imageUsages);
            
            res.activate("fileDashboard.html", request, response);
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_delete(HttpServletRequest request, HttpServletResponse response, int image_id) throws IOException, ServletException, TemplateManagerException {
        try {
            Image image = ((WebshopDataLayer) request.getAttribute("datalayer")).getImageDAO().getImage(image_id);
            if(image != null)
                ((WebshopDataLayer) request.getAttribute("datalayer")).getImageDAO().deleteImage(image);
            
            action_default(request, response);
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private String humanReadableFileSize(long size) {
        final String[] units = new String[]{"bytes", "KB", "MB", "GB", "TB", "PB", "EB"};
        if (size <= 1) {
            return size + " byte";
        }
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    private int calculatePercentage(long valueNow, float valueLast) {
        if(valueNow != 0) {
            float support = (float) valueNow / valueLast;
            if(support > 1) {
                support= (support-1)*100;
                return (int)support;
            } else {
                support*=100;
                return (int)support;
            }
        } else {
            return 0;
        }
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        request.setAttribute("title", "Files");
        request.setAttribute("themeMode", request.getSession().getAttribute("themeMode"));
        request.setAttribute("themeSkin", request.getSession().getAttribute("themeSkin"));
                
        int image_id;
        try {
            HttpSession s = request.getSession(false);
            if (s != null) {
                if(SecurityHelpers.checkPermissionScript(request)) {
                    if(request.getParameter("delete") != null) {
                        image_id = SecurityHelpers.checkNumeric(request.getParameter("imgid"));
                        action_delete(request, response, image_id);
                    } else
                        action_default(request, response);
                } else {
                    Group myGroup = ((WebshopDataLayer) request.getAttribute("datalayer")).getGroupDAO().getGroupByUser(Integer.parseInt(request.getSession().getAttribute("userid").toString()));
                    if(myGroup.getName().equals(UserRoleEnum.TECNICO))
                        response.sendRedirect("homepage");
                    else
                        response.sendRedirect("index");
                }
            } else {
                action_anonymous(request, response);
            }
        } catch (NumberFormatException ex) {
            handleError("Invalid number submitted", request, response);
        } catch (IOException | TemplateManagerException | DataException ex) {
            handleError(ex, request, response);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Manage Files servlet";
    }// </editor-fold>
  

}