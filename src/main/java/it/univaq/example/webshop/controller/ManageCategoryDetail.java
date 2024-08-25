package it.univaq.example.webshop.controller;

import it.univaq.example.webshop.data.dao.impl.WebshopDataLayer;
import it.univaq.example.webshop.data.model.Category;
import it.univaq.example.webshop.data.model.Characteristic;
import it.univaq.example.webshop.data.model.Group;
import it.univaq.example.webshop.data.model.Image;
import it.univaq.example.webshop.data.model.User;
import it.univaq.framework.data.DataException;
import it.univaq.framework.result.TemplateResult;
import it.univaq.framework.security.SecurityHelpers;
import it.univaq.framework.result.TemplateManagerException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
 
@MultipartConfig
public class ManageCategoryDetail extends WebshopBaseController {

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

    private void action_default(HttpServletRequest request, HttpServletResponse response, int cat_key) throws IOException, ServletException, TemplateManagerException {
        try {
            TemplateResult res = new TemplateResult(getServletContext());
            int user_key = Integer.parseInt(request.getSession().getAttribute("userid").toString());
            User user = ((WebshopDataLayer) request.getAttribute("datalayer")).getUserDAO().getUser(user_key);
            Group group = ((WebshopDataLayer) request.getAttribute("datalayer")).getGroupDAO().getGroupByUser(user_key);

            request.setAttribute("username", user.getUsername());
            request.setAttribute("group", group.getName());   

            Category category = null;
            if(cat_key == 0) {
                category = ((WebshopDataLayer) request.getAttribute("datalayer")).getCategoryDAO().createCategory();
                category.setKey(0);
            } else {
                category = ((WebshopDataLayer) request.getAttribute("datalayer")).getCategoryDAO().getCategory(cat_key);
                category.getFatherCategory();
                category.getCharacteristics();
                category.getImage();
            }
            request.setAttribute("category", category);

            List<Category> categories = ((WebshopDataLayer) request.getAttribute("datalayer")).getCategoryDAO().getCategories();
            request.setAttribute("categories", categories);
            
            res.activate("categoryDetail.html", request, response);
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_update(HttpServletRequest request, HttpServletResponse response, int cat_key) throws IOException, ServletException, TemplateManagerException {
        try {
            Category cat = null;
            if(cat_key > 0)
                cat = ((WebshopDataLayer) request.getAttribute("datalayer")).getCategoryDAO().getCategory(cat_key);
            else
                cat = ((WebshopDataLayer) request.getAttribute("datalayer")).getCategoryDAO().createCategory();

            if (cat != null) {               
                cat.setName(request.getParameter("name"));
                int father_key = Integer.parseInt(request.getParameter("fatherCategory").toString());
                if(father_key > 0)
                    cat.setFatherCategory(((WebshopDataLayer) request.getAttribute("datalayer")).getCategoryDAO().getCategory(father_key));
                
                Part file_to_upload = request.getPart("image");
                if(file_to_upload != null){
                    Image image = ((WebshopDataLayer) request.getAttribute("datalayer")).getImageDAO().createImage();
                    image.setFilename(file_to_upload.getSubmittedFileName());
                    image.setImageType(file_to_upload.getContentType());
                    image.setImageSize(file_to_upload.getSize()); 
                    image.setImageData(file_to_upload.getInputStream());
                    image.setCaption(request.getParameter("caption"));
                    ((WebshopDataLayer) request.getAttribute("datalayer")).getImageDAO().setImage(image);
                    cat.setImage(image);
                }

                List<Characteristic> characteristics = cat.getCharacteristics();

                String[] characteristicKeyA = request.getParameterValues("characteristicKey[]");
                String[] characteristicNameA = request.getParameterValues("characteristicName[]");
                String[] characteristicValueA = request.getParameterValues("characteristicValue[]");
                List<String> characteristicKey = new ArrayList<String>(Arrays.asList(characteristicKeyA));
                List<String> characteristicName = new ArrayList<String>(Arrays.asList(characteristicNameA));
                List<String> characteristicValue = new ArrayList<String>(Arrays.asList(characteristicValueA));


                //formatto i valori di default come "valore1","valore2"... così via senza spazi tra le virgole
                for(String s : characteristicValue) {
                    String value = "";
                    String[] elems = s.split(",");
                    for(String elem : elems) {
                        value += elem.trim() + ",";
                    }
                    value = value.substring(0, value.length()-1);
                    s = value;
                }

                //ritrovo le vecchie caratteristiche se sono da aggiornare
                if(!characteristics.isEmpty()) {
                    for(Characteristic c : characteristics) {
                        for(int i=0;i<characteristicKey.size();i++) {
                            if(characteristicKey.get(i).equals(c.getKey().toString())) {
                                c.setCategory(cat);
                                c.setName(characteristicName.get(i));
                                c.setDefaultValues(characteristicValue.get(i));
                                characteristicKey.remove(i);
                                characteristicName.remove(i);
                                characteristicValue.remove(i);
                                continue;
                            }
                        }
                    }
                }

                //se sono state inviate altre caratteristiche, le inserisco
                if(characteristicName.size()>0) {
                    for(int i=0; i<characteristicName.size();i++) {
                        Characteristic element = ((WebshopDataLayer) request.getAttribute("datalayer")).getCharacteristicDAO().createCharacteristic();
                        element.setCategory(cat);
                        element.setName(characteristicName.get(i));
                        element.setDefaultValues(characteristicValue.get(i));
                        characteristics.add(element);
                    }
                }




                ((WebshopDataLayer) request.getAttribute("datalayer")).getCategoryDAO().setCategory(cat);
                action_default(request, response, cat_key);
            } else {
                handleError("Cannot update category", request, response);
            }
            
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        request.setAttribute("title", "Detail");
        request.setAttribute("userid", request.getSession().getAttribute("userid"));

        int cat_key;
        try {
            HttpSession s = request.getSession(false);
            if (s != null) {
                if(request.getParameter("catid") != null) {
                    cat_key = SecurityHelpers.checkNumeric(request.getParameter("catid"));
                    if(request.getParameter("save") != null)
                        action_update(request, response, cat_key);  
                    else
                        action_default(request, response, cat_key);                           
                } else
                    response.sendRedirect("categories");
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
        return "Manage Category detail servlet";
    }// </editor-fold>
  

}