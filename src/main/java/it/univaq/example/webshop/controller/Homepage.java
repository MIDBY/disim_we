package it.univaq.example.webshop.controller;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import it.univaq.example.webshop.data.dao.impl.WebshopDataLayer;
import it.univaq.example.webshop.data.model.Category;
import it.univaq.example.webshop.data.model.Group;
import it.univaq.example.webshop.data.model.Proposal;
import it.univaq.example.webshop.data.model.Request;
import it.univaq.example.webshop.data.model.User;
import it.univaq.example.webshop.data.model.impl.OrderStateEnum;
import it.univaq.example.webshop.data.model.impl.ProposalStateEnum;
import it.univaq.example.webshop.data.model.impl.UserRoleEnum;
import it.univaq.framework.data.DataException;
import it.univaq.framework.result.TemplateManagerException;
import it.univaq.framework.result.TemplateResult;

public class Homepage extends WebshopBaseController {

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

    private void action_logged(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException, TemplateManagerException {
        try {
            int user_key = Integer.parseInt(request.getSession().getAttribute("userid").toString());
            User user = ((WebshopDataLayer) request.getAttribute("datalayer")).getUserDAO().getUser(user_key);
            if(user != null) {
                Group group = ((WebshopDataLayer) request.getAttribute("datalayer")).getGroupDAO().getGroupByUser(user_key);

                TemplateResult res = new TemplateResult(getServletContext());
                request.setAttribute("username", user.getUsername());
                request.setAttribute("group", group.getName());

                //riquadro nuove richieste
                List<Request> reqThisMonth = ((WebshopDataLayer) request.getAttribute("datalayer")).getRequestDAO().getRequestsByCreationMonth(LocalDate.now());
                List<Request> reqLastMonth = ((WebshopDataLayer) request.getAttribute("datalayer")).getRequestDAO().getRequestsByCreationMonth(LocalDate.now().minusMonths(1));
                int reqThisMonthValue = reqThisMonth.size();
                int reqLastMonthValue = reqLastMonth.size();
                int percentageRequests = calculatePercentage(reqThisMonthValue, reqLastMonthValue);
                request.setAttribute("newRequestsMonth", reqThisMonthValue);
                if(percentageRequests >= 0) {
                    request.setAttribute("percentageRequests", percentageRequests);
                    request.setAttribute("percentageRequestsText", "% higher");
                } else {
                    request.setAttribute("percentageRequests", percentageRequests);
                    request.setAttribute("percentageRequestsText", "% lower");
                }
                request.setAttribute("percentageRequestsCSS", "style=width:" + percentageRequests + "%;");

                //riquadro vendite
                int reqThisMonthClosed = 0;
                int reqLastMonthClosed = 0;
                for (Request i : reqThisMonth) {
                    if(i.getOrderState() == OrderStateEnum.ACCETTATO) reqThisMonthClosed++;
                }
                for (Request i : reqLastMonth) {
                    if(i.getOrderState() == OrderStateEnum.ACCETTATO) reqLastMonthClosed++;
                }
                int percentageSells = calculatePercentage(reqThisMonthClosed, reqLastMonthClosed);
                request.setAttribute("newSellsMonth", reqThisMonthClosed);
                if(percentageSells >= 0) {
                    request.setAttribute("percentageSells", percentageSells);
                    request.setAttribute("percentageSellsText", "% higher");
                } else {
                    request.setAttribute("percentageSells", percentageSells);
                    request.setAttribute("percentageSellsText", "% lower");
                }
                request.setAttribute("percentageSellsCSS", "style=width:" + percentageSells + "%;");

                //riquadro clienti
                Group groupOrdering = ((WebshopDataLayer) request.getAttribute("datalayer")).getGroupDAO().getGroupByName(UserRoleEnum.ORDINANTE);
                List<User> clients = ((WebshopDataLayer) request.getAttribute("datalayer")).getUserDAO().getUsersByGroup(groupOrdering.getKey());
                int clientsTotal = clients.size();
                int clientsAccepted = 0;
                for (User u : clients) {
                    if(u.isAccepted()) clientsAccepted++; 
                }
                int percentageClientsAccepted = calculatePercentage(clientsAccepted, clientsTotal);
                if(percentageClientsAccepted < 0) percentageClientsAccepted = 0;
                request.setAttribute("clientsAccepted", clientsAccepted);
                request.setAttribute("percentageClientsAccepted", percentageClientsAccepted);
                request.setAttribute("clientsTotal", clientsTotal);
                request.setAttribute("percentageClientsAcceptedCSS", "style=width:" + percentageClientsAccepted + "%;");

                //riquadro resi
                List<Request> rReturns = ((WebshopDataLayer) request.getAttribute("datalayer")).getRequestDAO().getRequests();
                int nReturns = 0;
                for(Request r : rReturns) {
                    if(r.getOrderState().equals(OrderStateEnum.RESPINTONONCONFORME) || r.getOrderState().equals(OrderStateEnum.RESPINTONONFUNZIONANTE)){
                        nReturns++;
                    }
                }                
                if(rReturns != null) {
                    request.setAttribute("nRequests", rReturns.size());
                } else {
                    request.setAttribute("nRequests", 0);
                }
                int percentageReturns = calculatePercentage(nReturns, rReturns.size());
                request.setAttribute("nReturns", nReturns);
                request.setAttribute("percentageReturns", percentageReturns);
                request.setAttribute("percentageReturnsCSS", "style=width:" + percentageReturns + "%;");
                
                

                //grafico temporale
                int[] data1 = new int[12];
                int j=0;
                for(LocalDate i = LocalDate.now().minusMonths(11); i.isBefore(LocalDate.now().minusMonths(1)); i = i.plusMonths(1)) {
                    data1[j] = ((WebshopDataLayer) request.getAttribute("datalayer")).getRequestDAO().getRequestsByCreationMonth(i).size();
                    j++;
                }
                j--;
                data1[j] = reqLastMonthValue; j++;
                data1[j] = reqThisMonthValue;
                request.setAttribute("chartData1", data1);

                int[] data2 = new int[12];
                j = 0;
                for(LocalDateTime i = LocalDateTime.now().minusMonths(11); i.isBefore(LocalDateTime.now().plusDays(1)); i = i.plusMonths(1)) {
                    data2[j] = ((WebshopDataLayer) request.getAttribute("datalayer")).getProposalDAO().getProposalsByCreationMonth(i).size();
                    j++;
                }
                request.setAttribute("chartData2", data2);

                int[] data3 = new int[12];
                for(User u : clients) {
                    long monthsAgo = ChronoUnit.MONTHS.between(u.getSubscriptionDate(), LocalDate.now());
                    if (monthsAgo < 12) {
                        data3[11 - (int) monthsAgo]++;
                    }
                }
                request.setAttribute("chartData3", data3);

                //riquadro best technician
                Group groupTechnician = ((WebshopDataLayer) request.getAttribute("datalayer")).getGroupDAO().getGroupByName(UserRoleEnum.TECNICO);
                List<User> technicians = ((WebshopDataLayer) request.getAttribute("datalayer")).getUserDAO().getUsersByGroup(groupTechnician.getKey());
                User bestT = null;
                int rTechAccepted = 0;
                float cTechEarned = 0;
                for (User t : technicians) {
                    int rAccepted = 0;
                    float cashEarned = 0;
                    List<Request> rTech = ((WebshopDataLayer) request.getAttribute("datalayer")).getRequestDAO().getRequestsByTechnician(t.getKey());
                    if(rTech != null){
                        rAccepted = rTech.size();
                        for (Request r : rTech) {
                            Proposal pTech = ((WebshopDataLayer) request.getAttribute("datalayer")).getProposalDAO().getLastProposalByRequest(r.getKey());
                            if(pTech != null && pTech.getProposalState().equals(ProposalStateEnum.APPROVATO) && r.getOrderState().equals(OrderStateEnum.ACCETTATO)){
                                cashEarned += pTech.getProductPrice();
                            } else {
                                continue;
                            }
                        }
                        if(rAccepted > rTechAccepted) {
                            bestT = t;
                            rTechAccepted = rAccepted;
                            cTechEarned = cashEarned;
                        }
                    } else {
                        continue;
                    }
                }
                if(bestT != null) {
                    request.setAttribute("usernameTechnician", bestT.getUsername());
                    request.setAttribute("rTechAccepted", rTechAccepted);
                    request.setAttribute("cTechEarned", cTechEarned);
                } else {
                    //nessun vincitore
                    request.setAttribute("usernameTechnician", "No winner");
                    request.setAttribute("rTechAccepted", 0);
                    request.setAttribute("cTechEarned", 0);
                }

                //categoria più venduta
                List<Category> catMostSold = ((WebshopDataLayer) request.getAttribute("datalayer")).getCategoryDAO().getMostSoldCategories();
                if(catMostSold != null && catMostSold.size() > 0) {
                    List<Request> reqByCat = ((WebshopDataLayer) request.getAttribute("datalayer")).getRequestDAO().getRequestsByCategory(catMostSold.get(0).getKey());
                    request.setAttribute("categorySoldName", catMostSold.get(0).getName());
                    request.setAttribute("categorySoldTimes", reqByCat.size());
                    request.setAttribute("categoryImage", catMostSold.get(0).getImage().getFilename());
                } else {
                    request.setAttribute("categorySoldName", "No one category wins");
                    request.setAttribute("categorySoldTimes", 0);
                    request.setAttribute("categoryImage", "templates/admin_template/assets/images/lg/avatar2.jpg");
                }

                //grafico del mondo dei clienti
                Map<String,Integer> cWorld = new HashMap<>();
                for(User u : clients) {
                    String country = u.getAddress().substring(u.getAddress().length()-3, u.getAddress().indexOf(')'));
                    if(cWorld.containsKey(country)) {
                        cWorld.put(country, cWorld.get(country)+1);
                    } else {
                        cWorld.put(country, 1);
                    }
                }
                if(cWorld.size() > 0) {
                    request.setAttribute("mapData", cWorld);
                } else {
                    request.setAttribute("mapData", new HashMap<String, Integer>());
                }

                //distribuzione dei clienti
                LinkedHashMap<String,Integer> cCountry = new LinkedHashMap<>();
                for(User u : clients) {
                    String country = u.getAddress().substring(u.getAddress().lastIndexOf(',')+1, u.getAddress().indexOf('('));
                    if(cCountry.containsKey(country)) {
                        cCountry.put(country, cCountry.get(country)+1);
                    } else {
                        cCountry.put(country, 1);
                    }
                }
                if(cCountry != null && cCountry.size() > 2) {
                    cCountry = cCountry.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .collect(Collectors.toMap( Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
                    Object[] keys = cCountry.keySet().toArray();
                    request.setAttribute("pieName1", keys[0]);
                    request.setAttribute("pieValue1", cCountry.get(keys[0]));
                    request.setAttribute("pieName2", keys[1]);
                    request.setAttribute("pieValue2", cCountry.get(keys[1]));
                    request.setAttribute("pieName3", keys[2]);
                    request.setAttribute("pieValue3", cCountry.get(keys[2]));
                    request.setAttribute("pieValue4", clients.size() - cCountry.get(keys[0]) - cCountry.get(keys[1]) - cCountry.get(keys[2]));
                } else {
                    request.setAttribute("pieName1", "Country 1");
                    request.setAttribute("pieValue1", 33);
                    request.setAttribute("pieName2", "Country 2");
                    request.setAttribute("pieValue2", 33);
                    request.setAttribute("pieName3", "Country 3");
                    request.setAttribute("pieValue3", 33);
                    request.setAttribute("pieValue3", 0);
                    request.setAttribute("pieValue4", 0);
                }
                

                request.setAttribute("completeRequestURL", "login");
                res.activate("homepage.ftl.html", request, response);
            } else {
                action_anonymous(request, response);
            }

            
        } catch (DataException ex) {
         handleError("Data access exception: " + ex.getMessage(), request, response);
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

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws javax.servlet.ServletException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        request.setAttribute("title", "Homepage");
        request.setAttribute("themeMode", request.getSession().getAttribute("themeMode"));
        request.setAttribute("themeSkin", request.getSession().getAttribute("themeSkin"));

        try {
            HttpSession s = request.getSession(false);
            if (s == null) {
                action_anonymous(request, response);
            } else {
                action_logged(request, response);
            }
        } catch (IOException | TemplateManagerException ex) {
            handleError(ex, request, response);
        }
    }

    /**
     * Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Homepage Back-end Webshop servlet";
    }
    // </editor-fold>
}