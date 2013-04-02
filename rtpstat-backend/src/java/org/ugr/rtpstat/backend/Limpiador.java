/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ugr.rtpstat.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author opsi
 */
public class Limpiador implements Runnable {

    private static final String URL_VALIDADOR = "https://rtpstat.appspot.com/rtpstat/validTokens";
    private static final Logger logger = Logger.getLogger(Limpiador.class.getName());

    public void run() {
        try {
            URL urlValidadorTokens = new URL(URL_VALIDADOR);
            InputStream is = urlValidadorTokens.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            ArrayList<UUID> activos = new ArrayList<UUID>();
            String linea;
            while ((linea = reader.readLine()) != null) {
                try {
                    UUID uuid = UUID.fromString(linea);
                    activos.add(uuid);
                } catch (IllegalArgumentException ex) {
                    logger.log(Level.SEVERE, URL_VALIDADOR + " ha devuelto un token no valido (" + linea + ")");
                }
            }
            logger.info("Leidos los siguientes tokens desde el frontend: " + activos);
            if (activos.size() > 0) {
                archivarInactivos(activos);
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Ha fallado la descarga de la lista de tokens validos", ex);
        }
    }

    private void archivarInactivos(ArrayList<UUID> activos) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
