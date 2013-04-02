/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ugr.rtpstat.backend;

import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.IOError;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.ugr.rtpstat.client.orm.Problema;
import org.ugr.rtpstat.server.LibroParaBackend;
import org.ugr.rtpstat.server.RelacionEjerciciosParaBackend;
/** 
 *
 * @author opsi
 */
public class DocumentService extends HttpServlet {

    private Logger logger = Logger.getLogger(
            DocumentService.class.getName());
    private Repositorio repositorio = null;

    public DocumentService() {
        try {
            repositorio = new Repositorio();
        } catch (IOException ex) {
            Logger.getLogger(DocumentService.class.getName()).log(Level.SEVERE, "Imposible continuar, abortando", ex);
            throw new IOError(ex);
        }
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        BufferedWriter respuesta = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()));
        try {
            ObjectInputStream oi = new ObjectInputStream(request.getInputStream());
            Object leido = oi.readObject();
            if (leido instanceof Problema) {
                Problema problema = (Problema) leido;
                logger.warning("Nuevo problema recibido correctmente desde " + request.getRemoteAddr() + ", id = " + problema.getId());
                String token = repositorio.procesarSolicitud(problema);

                if (token != null && token.length() > 0) {
                    respuesta.write(token);
                } else {
                    respuesta.write("ERROR: Token vacio");
                }
            } else if (leido instanceof RelacionEjerciciosParaBackend) {
                RelacionEjerciciosParaBackend relacion = (RelacionEjerciciosParaBackend) leido;
                logger.warning("Nueva relación de ejercicios recibida correctmente desde " + request.getRemoteAddr() + ", id = " + relacion.getId());

                String token = repositorio.procesarSolicitud(relacion);
                logger.warning("Token para la nueva relación :" + token);
                if (token != null && token.length() > 0) {
                    respuesta.write(token);
                } else {
                    respuesta.write("ERROR: Token vacio");
                }
            } else if(leido instanceof LibroParaBackend) {
                LibroParaBackend libro = (LibroParaBackend) leido;
                logger.warning("Nuevo libro recibido correctamente desde " + request.getRemoteAddr() + ", id = " + libro.getId());

                String token = repositorio.procesarSolicitud(libro);
                logger.warning("Token para el nuevo libro:" + token);
                if (token != null && token.length() > 0) {
                    respuesta.write(token);
                } else {
                    respuesta.write("ERROR: Token vacio");
                }
            }
        } catch (EOFException ex) {
            logger.log(Level.SEVERE, "Ningún objeto recibido desde " + request.getRemoteAddr(), ex);
            respuesta.write("ERROR: NO OBJECT");
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error de Entrada/Salida", ex);
            respuesta.write("ERROR: Error de Entrada/Salida");
        } catch (ClassNotFoundException ex) {
            logger.log(Level.SEVERE, "ClassNotFound", ex);
            respuesta.write("ERROR: El backend no esta actualizado");
        }
        respuesta.close();
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
        return "RTPSTAT DocumentService";
    }// </editor-fold>
}
