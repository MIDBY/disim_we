package it.univaq.example.webshop.controller;

import it.univaq.framework.data.DataException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ThemeServlet extends WebshopBaseController {

    private void action_error(HttpServletRequest request, HttpServletResponse response) {
        //ad esempio, possiamo caricare un'immagine di default se non riusciamo a trovare quella indicata
        //as an example, we can send a default image if we cannot find the requested one
        try {
            action_download(request, response);
        } catch (IOException | DataException ex) {
            //if the error image cannot be sent, try a standard HTTP error message
            //se non possiamo inviare l'immagine di errore, proviamo un messaggio di errore HTTP standard
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
            } catch (IOException ex1) {
                //if ALSO this error status cannot be notified, write to the server log
                //se ANCHE questo stato di errore non può essere notificato, scriviamo sul log del server
                Logger.getLogger(RenderImage.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    private void action_save(HttpServletRequest request, HttpServletResponse response) throws IOException, DataException {
        String theme;
        if(request.getParameter("theme") != null) {
            theme = request.getParameter("theme");
            request.getSession().setAttribute("themeMode", theme);
        } else {
            theme = request.getParameter("themeSkin");
            request.getSession().setAttribute("themeSkin", theme);
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void action_download(HttpServletRequest request, HttpServletResponse response) throws IOException, DataException {
        if(request.getSession().getAttribute("themeMode") != null) {
            String theme = request.getSession().getAttribute("themeMode").toString();
            response.setContentType("text/plain"); 
            PrintWriter out = response.getWriter();
            out.print(theme); 
            out.flush();
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        try {
            if(request.getParameter("save") != null)
                action_save(request, response);
            else {
                action_download(request, response);

            }

        } catch (NumberFormatException ex) {
            action_error(request, response);
        } catch (IOException ex) {
            action_error(request, response);
        } catch (DataException ex) {
            action_error(request, response);
        }

    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "An image download servlet";
    }// </editor-fold>
}