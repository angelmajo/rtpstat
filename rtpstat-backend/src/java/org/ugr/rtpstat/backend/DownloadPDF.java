/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ugr.rtpstat.backend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author opsi
 */
public class DownloadPDF extends HttpServlet {

    private static Logger logger = Logger.getLogger(DownloadPDF.class.getName());

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String token = request.getParameter("token");
        final String tipo = request.getParameter("tipo");

        try {
            UUID uuid = UUID.fromString(token);
            File carpetaToken = new File(Repositorio.repFolderName + "/" + token);
            if (carpetaToken.exists() && carpetaToken.isDirectory() && carpetaToken.canRead()) {
                File[] contenidos = carpetaToken.listFiles(new FilenameFilter() {
                    public boolean accept(File file, String nombre) {
                        boolean out = false;
                        if ("documento".equals(tipo)) {
                            out = nombre.endsWith("documento.pdf");
                        } else if ("presentacion".equals(tipo)) {
                            out = nombre.endsWith("frames.pdf");
                        } else if("relacion".equals(tipo)) {
                            out = nombre.endsWith("relacion.pdf");
                        }
                        return out;
                    }
                });
                if (contenidos.length == 1) {
                    response.setContentType("application/pdf");
                    response.setHeader("Content-Disposition", "attachment; filename=\"" + tipo + uuid.getLeastSignificantBits() + ".pdf\"");
//header("Content-Disposition: attachment; filename=\"$nombreFichero\"");

                    InputStream is = new FileInputStream(contenidos[0]);
                    OutputStream os = response.getOutputStream();

                    byte[] buffer = new byte[1024];
                    int leido;
                    try {
                        while ((leido = is.read(buffer)) != -1) {
                            os.write(buffer, 0, leido);
                        }
                    } finally {
                        is.close();
                        os.close();
                    }
                } else {
                    logger.warning("Ningun " + tipo + " encontrado para el token " + token);
                }
                //TODO Detectar fallos en la generacion
            } else {
                logger.warning("Se intentó descargar el pdf de un token inexistente (" + token + ") desde " + request.getRemoteAddr());
            }
        } catch (IllegalArgumentException ex) {
            logger.warning("Se intentó descargar el pdf de un token invalido (" + token + ") desde " + request.getRemoteAddr());
        }

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
