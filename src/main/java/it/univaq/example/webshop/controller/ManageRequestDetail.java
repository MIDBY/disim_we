package it.univaq.example.webshop.controller;

import it.univaq.example.webshop.data.dao.impl.WebshopDataLayer;
import it.univaq.example.webshop.data.model.Category;
import it.univaq.example.webshop.data.model.Characteristic;
import it.univaq.example.webshop.data.model.Notification;
import it.univaq.example.webshop.data.model.Proposal;
import it.univaq.example.webshop.data.model.Request;
import it.univaq.example.webshop.data.model.RequestCharacteristic;
import it.univaq.example.webshop.data.model.User;
import it.univaq.example.webshop.data.model.impl.NotificationTypeEnum;
import it.univaq.example.webshop.data.model.impl.ProposalStateEnum;
import it.univaq.example.webshop.data.model.impl.RequestStateEnum;
import it.univaq.framework.data.DataException;
import it.univaq.framework.result.TemplateResult;
import it.univaq.framework.security.SecurityHelpers;
import it.univaq.framework.result.TemplateManagerException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class ManageRequestDetail extends WebshopBaseController {

    private void action_anonymous(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException, TemplateManagerException {
        TemplateResult res = new TemplateResult(getServletContext());
        String completeRequestURL = request.getRequestURL()
                + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        request.setAttribute("template", 2);
        request.setAttribute("email", "Hacker");
        request.setAttribute("address", "Stranger");
        request.setAttribute("completeRequestURL", "login?referrer=" + completeRequestURL);
        res.activate("index.html", request, response);
    }

    private void action_showCategories(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, TemplateManagerException {
        try {
            TemplateResult res = new TemplateResult(getServletContext());
            int user_key = Integer.parseInt(request.getSession().getAttribute("userid").toString());
            User user = ((WebshopDataLayer) request.getAttribute("datalayer")).getUserDAO().getUser(user_key);

            request.setAttribute("email", user.getEmail());
            request.setAttribute("address", user.getAddress());
            
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
            
            res.activate("storeCategories.html", request, response);
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_default(HttpServletRequest request, HttpServletResponse response, int req_key, int cat_key) throws IOException, ServletException, TemplateManagerException {
        try {
            TemplateResult res = new TemplateResult(getServletContext());
            int user_key = Integer.parseInt(request.getSession().getAttribute("userid").toString());
            User user = ((WebshopDataLayer) request.getAttribute("datalayer")).getUserDAO().getUser(user_key);

            request.setAttribute("email", user.getEmail());
            request.setAttribute("address", user.getAddress());
            
            if(req_key > 0) {
                Request req = ((WebshopDataLayer) request.getAttribute("datalayer")).getRequestDAO().getRequest(req_key);
                req.getCategory().getCharacteristics();
                req.getCategory().getImage();
                req.getRequestCharacteristics();
                for(RequestCharacteristic rc : req.getRequestCharacteristics())
                    rc.getCharacteristic();
                String tree = req.getCategory().getName();
                List<Characteristic> totalChars = new ArrayList<>();
                if(req.getCategory().getFatherCategory() != null) {
                    totalChars.addAll(req.getCategory().getCharacteristics());
                    tree = tree(req.getCategory().getFatherCategory(), totalChars) + " > " + tree;
                }

                request.setAttribute("request", req);
                request.setAttribute("familyTree", tree);
                request.setAttribute("totalChars", totalChars);

                if(req.getProposals().size() > 0 || req.getRequestState().equals(RequestStateEnum.ANNULLATO))
                    request.setAttribute("readonly", true);
            }

            if(cat_key > 0) {
                Category category = ((WebshopDataLayer) request.getAttribute("datalayer")).getCategoryDAO().getCategory(cat_key);
                category.getFatherCategory();
                category.getImage();
                category.getCharacteristics();

                String tree = category.getName();
                List<Characteristic> totalChars = new ArrayList<>();
                if(category.getFatherCategory() != null) {
                    totalChars.addAll(category.getCharacteristics());
                    tree = tree(category.getFatherCategory(), totalChars) + " > " + tree;
                }

                request.setAttribute("category", category);
                request.setAttribute("familyTree", tree);
                request.setAttribute("totalChars", totalChars);
            }

            

            if(request.getAttribute("familyTree") != null)
                res.activate("requestDetail.html", request, response);
            else
                handleError("Passed data not enough", request, response);
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_update(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, TemplateManagerException {
        try {
            Request req = null;
            if(request.getParameter("id") != null && request.getParameter("id") != "")
                req = ((WebshopDataLayer) request.getAttribute("datalayer")).getRequestDAO().getRequest(Integer.parseInt(request.getParameter("id").toString()));
            else
                req = ((WebshopDataLayer) request.getAttribute("datalayer")).getRequestDAO().createRequest();

            if(req != null) {
                if(req.getTitle() == "") {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                    String title = LocalDateTime.now().format(formatter);
                    req.setTitle("Request#"+ title);
                }
                req.setDescription(request.getParameter("description"));
                req.setCategory(((WebshopDataLayer) request.getAttribute("datalayer")).getCategoryDAO().getCategory(Integer.parseInt(request.getParameter("category"))));
                req.setOrdering(((WebshopDataLayer) request.getAttribute("datalayer")).getUserDAO().getUser(Integer.parseInt(request.getSession().getAttribute("userid").toString())));
                req.setNotes(request.getParameter("notes"));
                
                ((WebshopDataLayer) request.getAttribute("datalayer")).getRequestDAO().setRequest(req);

                String tree = req.getCategory().getName();
                List<Characteristic> totalChars = new ArrayList<>();
                if(req.getCategory().getFatherCategory() != null) {
                    totalChars.addAll(req.getCategory().getCharacteristics());
                    tree = tree(req.getCategory().getFatherCategory(), totalChars) + " > " + tree;
                }

                for(Characteristic c : totalChars) {
                    RequestCharacteristic rc = null;
                    if(request.getParameter("id") != null && request.getParameter("id") != "") {
                        for(RequestCharacteristic find : req.getRequestCharacteristics())
                            if(find.getCharacteristic().getKey() == c.getKey())
                                rc = find;
                    }
                    if(rc == null) {
                        rc = ((WebshopDataLayer) request.getAttribute("datalayer")).getRequestCharacteristicDAO().createRequestCharacteristic();
                        rc.setRequest(req);
                        rc.setCharacteristic(c);
                    }
                    String value = request.getParameter("characteristic" + c.getKey());
                    rc.setValue(value);
                    ((WebshopDataLayer) request.getAttribute("datalayer")).getRequestCharacteristicDAO().setRequestCharacteristic(rc);
                }

                if(req.getTechnician() != null) {
                    boolean send = Boolean.parseBoolean(getServletContext().getInitParameter("sendEmail"));
                    if(send)
                        sendMail(req.getTechnician().getEmail(), "WebShop: \nUser has edited the request.\nGo to check now!");
                    sendNotification(request, response, req.getTechnician(), "User has updated own request!", NotificationTypeEnum.MODIFICATO, "orderDetail?order="+req.getKey());
                }

                response.sendRedirect("index");
            } else {
                handleError("Cannot update request", request, response);
            }
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_editProposal(HttpServletRequest request, HttpServletResponse response, int prop_key) throws IOException, ServletException, TemplateManagerException {
        try {
            Proposal proposal = null;
            if (prop_key > 0) {
                proposal = ((WebshopDataLayer) request.getAttribute("datalayer")).getProposalDAO().getProposal(prop_key);
            }
            if (proposal != null) {
                proposal.setProposalState(ProposalStateEnum.valueOf(request.getParameter("proposalState")));
                proposal.setMotivation(request.getParameter("proposalMotivation"));
                ((WebshopDataLayer) request.getAttribute("datalayer")).getProposalDAO().setProposal(proposal);

                Request req = proposal.getRequest();
                req.setRequestState(RequestStateEnum.ORDINATO);

                ((WebshopDataLayer) request.getAttribute("datalayer")).getRequestDAO().setRequest(req);
        
                boolean send = Boolean.parseBoolean(getServletContext().getInitParameter("sendEmail"));
                if(send)
                    sendMail(proposal.getTechnician().getEmail(), "WebShop: \n"+req.getOrdering().getUsername()+" responded to your proposal of " + proposal.getProductName() + "!\nGo to check it.");
                sendNotification(request, response, proposal.getTechnician(), req.getOrdering().getUsername() + " responded to your proposal!", NotificationTypeEnum.MODIFICATO, "orderDetail?order=" + req.getKey());
                action_default(request, response, req.getKey(), 0);
            } else {
                handleError("Cannot update request", request, response);
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

    private String tree(Category c, List<Characteristic> chars) {
        if(c.getFatherCategory() != null) {
            chars.addAll(c.getCharacteristics());
            return tree(c.getFatherCategory(), chars) + " > " + c.getName();
        } else {
            chars.addAll(c.getCharacteristics());
            return c.getName();
        }
    }

    private void sendNotification(HttpServletRequest request, HttpServletResponse response, User user, String message, NotificationTypeEnum type, String link) {
        try {
            Notification notification = ((WebshopDataLayer) request.getAttribute("datalayer")).getNotificationDAO().createNotification();
            notification.setRecipient(user);
            notification.setCreationDate(LocalDateTime.now());
            notification.setMessage(message);
            notification.setType(type);
            notification.setLink(link);
            ((WebshopDataLayer) request.getAttribute("datalayer")).getNotificationDAO().setNotification(notification);
        } catch (DataException e) {
            handleError("Send notification exception: " + e.getMessage(), request, response);
        }
    }

    private void sendMail(String email, String text) {
        String sender = getServletContext().getInitParameter("emailSender");
        String securityCode = getServletContext().getInitParameter("securityCode");
        String to = email;
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication(sender, securityCode);
            }
        });
        //compose message
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("WebMarket");
            message.setText(text);

            Transport.send(message);
            System.out.println("Message sent successfully");
        } catch (MessagingException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        request.setAttribute("title", "Request");
        
        try {
            HttpSession s = request.getSession(false);
            if (s != null) {
                if(SecurityHelpers.checkPermissionScript(request)) {
                    if(request.getParameter("save") != null) 
                        action_update(request, response);
                    else {
                        if(request.getParameter("editP") != null) {
                            int prop_key = SecurityHelpers.checkNumeric(request.getParameter("editP"));
                            action_editProposal(request, response, prop_key);
                        } else {
                            if(request.getParameter("reqid") != null) {
                                //edit request
                                int req_key = SecurityHelpers.checkNumeric(request.getParameter("reqid"));
                                action_default(request, response, req_key, 0);
                            } else {
                                request.setAttribute("title", "New Request");

                                if(request.getParameter("catid") != null) {
                                    //new request
                                    int cat_key = SecurityHelpers.checkNumeric(request.getParameter("catid"));
                                    action_default(request, response, 0, cat_key);
                                } else {
                                    action_showCategories(request, response);
                                }
                            }
                        }
                    }
                } else {
                    response.sendRedirect("homepage");
                }
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
        return "Manage user Requests servlet";
    }// </editor-fold>
  

}