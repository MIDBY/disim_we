package it.univaq.example.webshop.controller;

import it.univaq.example.webshop.data.dao.impl.NewspaperDataLayer;
import it.univaq.example.webshop.data.model.Image;
import it.univaq.framework.data.DataException;
import it.univaq.framework.result.StreamResult;
import it.univaq.framework.security.SecurityHelpers;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Giuseppe Della Penna
 */
public class RenderImage extends NewspaperBaseController {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private void action_error(HttpServletRequest request, HttpServletResponse response) {
        //ad esempio, possiamo caricare un'immagine di default se non riusciamo a trovare quella indicata
        //as an example, we can send a default image if we cannot find the requested one
        try {
            action_download(request, response, 0);
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

    private void action_download(HttpServletRequest request, HttpServletResponse response, int imgid) throws IOException, DataException {
        StreamResult result = new StreamResult(getServletContext());
        Image image = ((NewspaperDataLayer) request.getAttribute("datalayer")).getImageDAO().getImage(imgid);
        if (image != null) {
            //settiamo il tipo del file da trasmettere
            //set the media type of the file to send
            request.setAttribute("contentType", image.getImageType());
            //il file non sara' scaricato, ma mostrato nel browser
            //this file will not be saved, but displayed in the browser window
            request.setAttribute("contentDisposition", "inline");
            //prendiamo il file dal filesystem, anche se la classe Image supporterebbe anche la lettura dal DB
            //get the image from the filesystem, even if the Image class supports also reading image data from the DB
            result.setResource(new File(getServletContext().getAttribute("images.directory") + image.getFilename()));
            result.activate(request, response);
            //result.activate(image.getImageData(), image.getImageSize(), image.getImageType(), request, response);
        } else {
            throw new DataException("Image not available");
        }
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        try {
            int imgid = SecurityHelpers.checkNumeric(request.getParameter("imgid"));
            action_download(request, response, imgid);
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