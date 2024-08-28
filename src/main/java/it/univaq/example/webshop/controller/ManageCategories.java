package it.univaq.example.webshop.controller;

import it.univaq.example.webshop.data.dao.impl.WebshopDataLayer;
import it.univaq.example.webshop.data.model.Category;
import it.univaq.example.webshop.data.model.Characteristic;
import it.univaq.example.webshop.data.model.Group;
import it.univaq.example.webshop.data.model.RequestCharacteristic;
import it.univaq.example.webshop.data.model.User;
import it.univaq.framework.data.DataException;
import it.univaq.framework.result.TemplateResult;
import it.univaq.framework.security.SecurityHelpers;
import it.univaq.framework.result.TemplateManagerException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ManageCategories extends WebshopBaseController {

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
            
            List<Category> categories = ((WebshopDataLayer) request.getAttribute("datalayer")).getCategoryDAO().getCategoriesByDeleted(false);
            for(Category c : categories) {
                c.getCharacteristics();
                c.getFatherCategory();
                c.getImage();
            }

            List<Category> sortedCategories = new ArrayList<>();
            List<Category> topCategories = new ArrayList<>();
            for(Category c: categories) {
                if(c.getFatherCategory() == null)
                    topCategories.add(c);
            }

            topCategories.sort(Comparator.comparing(Category::getKey));

            for(Category category : topCategories) {
                sortedCategories.add(category);
                addChilds(category, sortedCategories, categories);
            }
            request.setAttribute("categories", sortedCategories);
            
            res.activate("listCategories.html", request, response);
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_delete(HttpServletRequest request, HttpServletResponse response, int cat_key) throws IOException, ServletException, TemplateManagerException {
        try {
            Category category = null;
            if(cat_key > 0)
                category = ((WebshopDataLayer) request.getAttribute("datalayer")).getCategoryDAO().getCategory(cat_key);

            if(category != null) {
                int orders = ((WebshopDataLayer) request.getAttribute("datalayer")).getRequestDAO().getRequestsByCategory(cat_key).size();
                List<Category> sons = ((WebshopDataLayer) request.getAttribute("datalayer")).getCategoryDAO().getCategoriesSonsOf(cat_key);
                if(orders > 0) {
                    for(Category c : sons) {
                        if(category.getFatherCategory() != null) {
                            c.setFatherCategory(category.getFatherCategory());
                        } else {
                            c.setFatherCategory(null);
                        }
                        ((WebshopDataLayer) request.getAttribute("datalayer")).getCategoryDAO().setCategory(c);
                    }       
                    category.setDeleted(true);
                    ((WebshopDataLayer) request.getAttribute("datalayer")).getCategoryDAO().setCategory(category);;

                } else {
                    boolean saveCategory = false;
                    for(Characteristic c : category.getCharacteristics()) {
                        List<RequestCharacteristic> chars = ((WebshopDataLayer) request.getAttribute("datalayer")).getRequestCharacteristicDAO().getRequestCharacteristicsByCharacteristic(c.getKey());
                        if(chars != null && chars.size() > 0){
                            saveCategory = true;
                        }
                    }

                    for(Category c : sons) {
                        if(category.getFatherCategory() != null) {
                            c.setFatherCategory(category.getFatherCategory());
                        } else {
                            c.setFatherCategoryNull();
                        }
                        ((WebshopDataLayer) request.getAttribute("datalayer")).getCategoryDAO().setCategory(c);
                    }
                    
                    if(saveCategory) {
                        category.setDeleted(true);
                        ((WebshopDataLayer) request.getAttribute("datalayer")).getCategoryDAO().setCategory(category);
                    } else  {
                        ((WebshopDataLayer) request.getAttribute("datalayer")).getImageDAO().deleteImage(category.getImage());
                        ((WebshopDataLayer) request.getAttribute("datalayer")).getCategoryDAO().deleteCategory(category);
                    }
                    
                }
                action_default(request, response);
            } else {
                action_default(request, response);
            }
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }


    private static void addChilds(Category father, List<Category> sortedCategories, List<Category> total) {
        List<Category> childs = new ArrayList<>();
        for(Category c : total) {
            if(c.getFatherCategory() != null && c.getFatherCategory().getKey() == father.getKey())
                childs.add(c);
        }
        childs.sort(Comparator.comparing(Category::getKey));
        for(Category child : childs){
            sortedCategories.add(child);
            addChilds(child, sortedCategories, total);
        }
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        request.setAttribute("title", "Categories");
        request.setAttribute("themeMode", request.getSession().getAttribute("themeMode"));
        request.setAttribute("themeSkin", request.getSession().getAttribute("themeSkin"));
        
        try {
            HttpSession s = request.getSession(false);
            if (s != null) {
                if(request.getParameter("del") != null) {
                    int cat_key = SecurityHelpers.checkNumeric(request.getParameter("del"));
                    action_delete(request, response, cat_key);
                } else
                    action_default(request, response);
            } else {
                action_anonymous(request, response);
            }
        } catch (NumberFormatException ex) {
            handleError("Invalid number submitted", request, response);
        } catch (IOException | TemplateManagerException ex) {
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
        return "Manage Categories servlet";
    }// </editor-fold>
  

}