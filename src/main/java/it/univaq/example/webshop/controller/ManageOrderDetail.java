package it.univaq.example.webshop.controller;

import it.univaq.example.webshop.data.dao.impl.WebshopDataLayer;
import it.univaq.example.webshop.data.model.Group;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
 
public class ManageOrderDetail extends WebshopBaseController {

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

    private void action_default(HttpServletRequest request, HttpServletResponse response, int req_key) throws IOException, ServletException, TemplateManagerException {
        try {
            TemplateResult res = new TemplateResult(getServletContext());
            int user_key = Integer.parseInt(request.getSession().getAttribute("userid").toString());
            User user = ((WebshopDataLayer) request.getAttribute("datalayer")).getUserDAO().getUser(user_key);
            Group group = ((WebshopDataLayer) request.getAttribute("datalayer")).getGroupDAO().getGroupByUser(user_key);

            request.setAttribute("username", user.getUsername());
            request.setAttribute("group", group.getName());            
            
            Request req = ((WebshopDataLayer) request.getAttribute("datalayer")).getRequestDAO().getRequest(req_key);
            Map<String, String> chars = new HashMap<>();
            req.getCategory();
            req.getOrdering();
            req.getRequestCharacteristics();
            for(RequestCharacteristic reqc : req.getRequestCharacteristics()) {
                chars.put(reqc.getCharacteristic().getName(), reqc.getValue());
            }
            request.setAttribute("request", req);
            request.setAttribute("reqChars", chars);

            List<Request> totReq = ((WebshopDataLayer) request.getAttribute("datalayer")).getRequestDAO().getRequestsByOrdering(req.getOrdering().getKey());
            int openReq = 0;
            int closedReq = 0;
            int affidability = 0;
            for(Request r : totReq) {
                if(r.getRequestState().equals(RequestStateEnum.CHIUSO) || r.getRequestState().equals(RequestStateEnum.ANNULLATO)) {
                    closedReq++;
                    if(r.getRequestState().equals(RequestStateEnum.ANNULLATO))    
                        affidability++;
                } else
                    openReq++;
            }
            request.setAttribute("totReq", totReq.size());
            request.setAttribute("openReq", openReq);
            request.setAttribute("closedReq", closedReq);
            request.setAttribute("canceledReq", affidability);

            int percentageAffidability = calculatePercentage(totReq.size(), affidability);
            request.setAttribute("percentageAffidability", percentageAffidability);
            request.setAttribute("percentageAffidabilityCSS", "style=width:" + percentageAffidability + "%;");
            
            int percentageProgress = 0;
            switch (req.getRequestState().toString()) {
                case "PRESOINCARICO":   
                    percentageProgress = 30;
                    break;
                case "ORDINATO":   
                    percentageProgress = 50;
                    switch (req.getOrderState().toString()) {
                        case "SPEDITO":
                            percentageProgress = 80;
                            break;
                        case "ACCETTATO":
                            percentageProgress = 100;
                            break;
                        case "RESPINTONONCONFORME":
                            percentageProgress = 100;
                            break;
                        case "RESPINTONONFUNZIONANTE":
                            percentageProgress = 100;
                            break;
                        default:
                            percentageProgress = 50;
                            break;
                    }
                    break;
                case "CHIUSO":
                    percentageProgress = 100;
                    break;
                case "ANNULLATO":
                    percentageProgress = 0;
                    break;
                default: percentageProgress = 10;
                    break;
            }
            request.setAttribute("percentageProgress", percentageProgress);
            request.setAttribute("percentageProgressCSS", "style=width:" + percentageProgress + "%;");

            List<Proposal> proposals = ((WebshopDataLayer) request.getAttribute("datalayer")).getProposalDAO().getProposalsByRequest(req.getKey());
            Proposal prop = ((WebshopDataLayer) request.getAttribute("datalayer")).getProposalDAO().getLastProposalByRequest(req.getKey());
            request.setAttribute("proposals", proposals);
            if(prop != null) {
                if(!prop.getProposalState().equals(ProposalStateEnum.RESPINTO))
                    request.setAttribute("diactivateEdit", true);
                else
                    request.setAttribute("diactivateEdit", false);
            } else
                request.setAttribute("diactivateEdit", false);

            res.activate("orderDetail.html", request, response);
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
        }
    }

    private void action_update(HttpServletRequest request, HttpServletResponse response, int req_key) throws IOException, ServletException, TemplateManagerException {
        try {
            Request req = null;
            if (req_key > 0) {
                req = ((WebshopDataLayer) request.getAttribute("datalayer")).getRequestDAO().getRequest(req_key);
            }
            if (req != null) {                
                int user_key = Integer.parseInt(request.getSession().getAttribute("userid").toString());
                User user = ((WebshopDataLayer) request.getAttribute("datalayer")).getUserDAO().getUser(user_key);
                
                boolean newP = false;
                Proposal proposal;
                if(req.getProposals().size() == 0 || req.getProposals().get(req.getProposals().size() - 1).getProposalState().equals(ProposalStateEnum.RESPINTO)) {
                    proposal = ((WebshopDataLayer) request.getAttribute("datalayer")).getProposalDAO().createProposal();
                    newP = true;
                } else
                    proposal = req.getProposals().get(req.getProposals().size() - 1);
                
                proposal.setRequest(req);
                proposal.setTechnician(user);
                proposal.setProductName(request.getParameter("productName"));
                proposal.setProducerName(request.getParameter("producerName"));
                proposal.setProductDescription(request.getParameter("productDescription"));
                proposal.setProductPrice(Float.parseFloat(request.getParameter("productPrice")));
                String url = request.getParameter("url");
                if(!(url.contains("http") || url.contains("https")))
                    url = "https://" + url;
                proposal.setUrl(url);
                proposal.setNotes(request.getParameter("notes"));
                if(newP) { 
                    proposal.setCreationDate(LocalDateTime.now());
                    proposal.setProposalState(ProposalStateEnum.INATTESA);
                }
                ((WebshopDataLayer) request.getAttribute("datalayer")).getProposalDAO().setProposal(proposal);

                //sends really emails, than activate it when there is a real email or it will send accidentally mails to real email's people 
                if(newP) {
                    //sendMail(req.getOrdering().getEmail(), "Info mail: Your request: '+req.getTitle()+' has received a new proposal, go to check it!");
                    sendNotification(request, response, req.getOrdering(), "Request: "+req.getTitle()+".\n Our technician has sent a new proposal to you, go to check it!", NotificationTypeEnum.NUOVO, ""); 
                    //TODO: Inserire link per pagina visualizzazione proposta del cliente
                } else {
                    //sendMail(req.getOrdering().getEmail(), "Info mail: Proposal of your request: '+req.getTitle()+' has been edited, go to check it!");
                    sendNotification(request, response, req.getOrdering(), "Request: "+req.getTitle()+".\n Our technician has edited proposal, go to check it!", NotificationTypeEnum.MODIFICATO, ""); 
                    //TODO: Inserire link per pagina visualizzazione proposta del cliente
                }
                action_default(request, response, req_key);
            } else {
                handleError("Cannot update request", request, response);
            }
            
        } catch (DataException ex) {
            handleError("Data access exception: " + ex.getMessage(), request, response);
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

    @SuppressWarnings("unused")
    private void sendCreatedMail(String email, String text) {
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

    private int calculatePercentage(int valueNow, int valueLast) {
        if(valueNow != 0) {
            if(valueLast != 0){
                float support = (float) valueNow / valueLast;
                if(support > 1) {
                    support= (support-1)*100;
                    return (int)support;
                } else {
                    support*=100;
                    return (int)support;
                }
            } else {
                return valueNow * 100;
            }
        } else {
            if(valueLast != 0) {
                return -valueLast * 100;
            } else {
                return 0;
            }
        }
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        request.setAttribute("title", "Detail");
        request.setAttribute("themeMode", request.getSession().getAttribute("themeMode"));
        request.setAttribute("themeSkin", request.getSession().getAttribute("themeSkin"));
        
        int req_key;
        try {
            HttpSession s = request.getSession(false);
            if (s != null) {
                if(request.getParameter("order") != null) {
                    req_key = SecurityHelpers.checkNumeric(request.getParameter("order"));
                    if(request.getParameter("crud") != null && request.getParameter("crud").equals("update")) 
                        action_update(request, response, req_key);          
                    else
                        action_default(request, response, req_key);
                } else
                    response.sendRedirect("orders?myOrders");
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
        return "Manage Order detail servlet";
    }// </editor-fold>
  

}