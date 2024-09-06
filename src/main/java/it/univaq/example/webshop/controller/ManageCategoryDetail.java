package it.univaq.example.webshop.controller;

import it.univaq.example.webshop.data.dao.impl.WebshopDataLayer;
import it.univaq.example.webshop.data.model.Category;
import it.univaq.example.webshop.data.model.Characteristic;
import it.univaq.example.webshop.data.model.Group;
import it.univaq.example.webshop.data.model.Image;
import it.univaq.example.webshop.data.model.RequestCharacteristic;
import it.univaq.example.webshop.data.model.User;
import it.univaq.example.webshop.data.model.impl.UserRoleEnum;
import it.univaq.framework.data.DataException;
import it.univaq.framework.result.TemplateResult;
import it.univaq.framework.security.SecurityHelpers;
import it.univaq.framework.result.TemplateManagerException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
 
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

            List<Category> categories = ((WebshopDataLayer) request.getAttribute("datalayer")).getCategoryDAO().getCategoriesByDeleted(false);
            categories.remove(category);
            request.setAttribute("categories", categories);
            
            res.activate("categoryDetail.html", request, response);
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_update(HttpServletRequest request, HttpServletResponse response, int cat_key) throws IOException, ServletException, TemplateManagerException {
        try {
            Category cat = null;
            if(cat_key > 0) {
                cat = ((WebshopDataLayer) request.getAttribute("datalayer")).getCategoryDAO().getCategory(cat_key);
            } else
                cat = ((WebshopDataLayer) request.getAttribute("datalayer")).getCategoryDAO().createCategory();

            if (cat != null) {               
                cat.setName(request.getParameter("name"));
                int father_key = Integer.parseInt(request.getParameter("fatherCategory").toString());
                if(father_key > 0)
                    cat.setFatherCategory(((WebshopDataLayer) request.getAttribute("datalayer")).getCategoryDAO().getCategory(father_key));
                                    
                Part file_to_upload = request.getPart("image");
                if(file_to_upload.getSubmittedFileName() != ""){
                    Image image;
                    if(cat.getImage() != null) 
                        image = cat.getImage();
                    else 
                        image = ((WebshopDataLayer) request.getAttribute("datalayer")).getImageDAO().createImage();
                    image.setFilename(SecurityHelpers.sanitizeFilename(file_to_upload.getSubmittedFileName()));
                    image.setImageType(file_to_upload.getContentType());
                    image.setImageSize(file_to_upload.getSize()); 
                    image.setCaption(cat.getName() + " image");
                    if (image.getImageSize() > 0 && image.getFilename() != null && !image.getFilename().isBlank()) {
                        Path target = Paths.get(getServletContext().getInitParameter("images.directory") + File.separator + image.getFilename());
                        //handle files with the same name in the output directory
                        int guess = 0;
                        while (Files.exists(target, LinkOption.NOFOLLOW_LINKS)) {
                            target = Paths.get(getServletContext().getInitParameter("images.directory") + File.separator + (++guess) + "_" + image.getFilename());
                        }
                        try (InputStream temp_upload = file_to_upload.getInputStream()) {
                            Files.copy(temp_upload, target, StandardCopyOption.REPLACE_EXISTING); //nio utility. Otherwise, use a buffer and copy from inputstream to fileoutputstream
                        }
                        image.setImageData(file_to_upload.getInputStream());
                    }
                    ((WebshopDataLayer) request.getAttribute("datalayer")).getImageDAO().setImage(image);
                    cat.setImage(image);
                }

                /* Logica:
                    prendo e formatto le caratteristiche inserite
                    se la categoria è nuova, non ha caratteristiche, allora aggiungo tutte le nuove caratteristiche e salvo
                    se la categoria esiste, può avere caratteristiche, controllo se:
                    1 - la caratteristica inserita è uguale (stesso nome e stessa key), non la aggiungo
                    2 - la caratteristica inserita ha nome diverso da quella esistente. Controllo se quella esistente è usata in qualche richiesta. Se si la elimino dalla tabella richiesta_caratteristica. Creo nuova char
                    3 - la caratteristica inserita non ha una chiave (key = null or 0), controllo se nelle vecchie ci sia una con lo stesso nome. Se si gli passo la chiave
                    4 - la categoria ha caratteristiche non inserite: le elimino tutte
                 */
                String[] characteristicKeyA = request.getParameterValues("characteristicKey[]");
                String[] characteristicNameA = request.getParameterValues("characteristicName[]");
                String[] characteristicValueA = request.getParameterValues("characteristicValue[]");
                
                List<Characteristic> insert = new ArrayList<>();
                for(int i=0; i<characteristicNameA.length; i++) {
                    Characteristic c = ((WebshopDataLayer) request.getAttribute("datalayer")).getCharacteristicDAO().createCharacteristic();
                    if(characteristicKeyA != null && characteristicKeyA.length > i)
                        c.setKey(Integer.parseInt(characteristicKeyA[i]));
                    else
                        c.setKey(0);
                    c.setCategory(cat);
                    c.setName(characteristicNameA[i]);
                    c.setDefaultValues(characteristicValueA[i]);
                    insert.add(c);
                }

                //formatto i valori di default come "valore1","valore2"... così via senza spazi tra le virgole
                for(Characteristic s : insert) {
                    String value = "";
                    String[] elems = s.getDefaultValues().split(",");
                    for(String elem : elems) {
                        value += elem.trim() + ",";
                    }
                    if(value.contains("Indifferent")){
                        value = value.replaceAll("Indifferent,", "");
                    }
                    value += "Indifferent";
                    s.setDefaultValues(value);
                }

                if(cat_key > 0) { 
                    List<Characteristic> oldChars = new ArrayList<>();
                    List<RequestCharacteristic> usedChars = ((WebshopDataLayer) request.getAttribute("datalayer")).getRequestCharacteristicDAO().getRequestCharacteristics();

                    try {
                        oldChars = cat.getCharacteristics();
                    } catch (NullPointerException e) { /*Recupero dei dati manuali (LEGACY), non faccio nulla*/ } 

                    for(Characteristic i : insert) {
                        Iterator<Characteristic> iterator = oldChars.iterator();
                        while (iterator.hasNext()) {
                            Characteristic c = iterator.next();
                            if(i.getKey() == c.getKey()) {
                                if(i.getName().equalsIgnoreCase(c.getName())) {
                                    //stesso nome e stessa chiave, vecchia char riusata
                                    i.setVersion(c.getVersion());
                                    iterator.remove();
                                } else {
                                    i.setKey(0);
                                    //stessa chiave ma nome diverso. E' stata usata? Si: elimino la caratteristica dalla tabella richiesta_caratteristica (elimino la vecchia caratteristica lasciandola li)
                                    for(RequestCharacteristic u : usedChars)
                                        if(i.getKey() == u.getCharacteristic().getKey()) 
                                            ((WebshopDataLayer) request.getAttribute("datalayer")).getRequestCharacteristicDAO().deleteRequestCharacteristic(u);
                                }
                            } else {
                                if(i.getKey() == 0) {
                                    //può essere una esistente con stesso nome
                                    if(i.getName().equalsIgnoreCase(c.getName())) {
                                        i.setKey(c.getKey());
                                        i.setVersion(c.getVersion());
                                        iterator.remove();
                                    }
                                }
                            }
                        }
                    }

                    for(Characteristic c : oldChars) {
                        ((WebshopDataLayer) request.getAttribute("datalayer")).getCharacteristicDAO().deleteCharacteristic(c);
                    }
                }

                ((WebshopDataLayer) request.getAttribute("datalayer")).getCategoryDAO().setCategory(cat);


                for(Characteristic c : insert) {
                    c.setCategory(cat);
                    ((WebshopDataLayer) request.getAttribute("datalayer")).getCharacteristicDAO().setCharacteristic(c);
                }

                response.sendRedirect("categories");
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
        request.setAttribute("themeMode", request.getSession().getAttribute("themeMode"));
        request.setAttribute("themeSkin", request.getSession().getAttribute("themeSkin"));
        
        int cat_key;
        try {
            HttpSession s = request.getSession(false);
            if (s != null) {
                if(SecurityHelpers.checkPermissionScript(request)) {
                    if(request.getParameter("catid") != null) {
                        cat_key = SecurityHelpers.checkNumeric(request.getParameter("catid"));
                        if(request.getParameter("save") != null)
                            action_update(request, response, cat_key);  
                        else
                            action_default(request, response, cat_key);                           
                    } else
                        response.sendRedirect("categories");
                } else {
                    if(s.getAttribute("userid") != null) {
                        Group myGroup = ((WebshopDataLayer) request.getAttribute("datalayer")).getGroupDAO().getGroupByUser(Integer.parseInt(request.getSession().getAttribute("userid").toString()));
                        if(myGroup.getName().equals(UserRoleEnum.TECNICO))
                            response.sendRedirect("homepage");
                        else
                            response.sendRedirect("index");
                    } else
                        response.sendRedirect("logout");
                    
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
        return "Manage Category detail servlet";
    }// </editor-fold>
  

}