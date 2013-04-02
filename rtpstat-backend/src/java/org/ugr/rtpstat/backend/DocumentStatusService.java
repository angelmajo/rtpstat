/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ugr.rtpstat.backend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.ugr.rtpstat.client.orm.EstadoGeneracion;

/**
 *
 * @author opsi
 */
public class DocumentStatusService extends HttpServlet {

    private static Logger logger = Logger.getLogger(DocumentStatusService.class.getName());

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String verbose = request.getParameter("verbose");
        String token = request.getParameter("token");
        String respuesta = EstadoGeneracion.GENERANDO.toString();
        try {
            UUID uuid = UUID.fromString(token);
            File estadoFile = new File(Repositorio.repFolderName + "/" + token + "/estado.txt");
            File routFile = new File(Repositorio.repFolderName + "/" + token + "/generar.r.Rout");

            if (estadoFile.exists() && estadoFile.canRead()) {
                BufferedReader reader = new BufferedReader(new FileReader(estadoFile));
                try {
                    respuesta = reader.readLine();
                    if (verbose != null && verbose.length() > 0) {
                        respuesta += ":";
                        reader.close();
                        reader = new BufferedReader(new FileReader(routFile));
                        String leido;
                        boolean primeraImportanteLeida = false;
                        while ((leido = reader.readLine()) != null) {
                            if(primeraImportanteLeida || leido.startsWith(">")) {
                                primeraImportanteLeida = true;
                                respuesta += "<p>" + leido + "</p>";
                            }
                        }
                    }
                } finally {
                    reader.close();
                }
                /*respuesta = EstadoGeneracion.GENERANDO.toString();
                File[] contenidos = carpetaToken.listFiles(new FilenameFilter() {

                public boolean accept(File file, String nombre) {
                return nombre.endsWith("documento.pdf");
                }
                });
                if (contenidos.length == 1) {
                respuesta = EstadoGeneracion.OK.toString();
                }
                //TODO Detectar fallos en la generacion
                 *
                 */
            } else {
                logger.warning("Se intentó acceder a la información de un token inexistente (" + token + ") desde " + request.getRemoteAddr());
            }
        } catch (IllegalArgumentException ex) {
            logger.warning("Intento de acceder a la información de un token invalido (" + token + ") desde " + request.getRemoteAddr());
        }
        response.setContentType("text/html;charset=UTF-8");
        response.getOutputStream().write(respuesta.getBytes("UTF-8"));
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
