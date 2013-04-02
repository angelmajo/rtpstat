/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ugr.rtpstat.backend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ugr.rtpstat.client.orm.EstadoGeneracion;
import org.ugr.rtpstat.server.LibroParaBackend;
import org.ugr.rtpstat.server.RelacionEjerciciosParaBackend;

/**
 *
 * @author opsi
 */
class GeneradorRelacion implements Runnable {

    private String uuid;

    public GeneradorRelacion(String uuid) {
        this.uuid = uuid;
    }

    public void run() {
        File carpeta = new File(Repositorio.repFolderName, uuid);

        boolean generado = false;
        try {
            File objectFile = new File(carpeta, Repositorio.relacionFileName);
            RelacionEjerciciosParaBackend r = leerRelacion(objectFile);
            r.setCarpetaRepositorio(Repositorio.repFolderName);//Mejor tener el path separator dos veces que ninguna

            Logger.getLogger(Generador.class.getName()).warning("Procesando relación: \r\n" + r.toString());

            String rutaRelacion = guardarRelacionEnXml(carpeta, r);
            String macroRFile = generarMacroR(carpeta, rutaRelacion.replace(".rtp", ""));
            String macroSHFile = generarMacroSH(carpeta, macroRFile);
            generarPDF(carpeta, macroSHFile);
            generado = true;
        } catch (IOException ex) {
            Logger.getLogger(Generador.class.getName()).log(Level.SEVERE, "Fallo al leer una relación del disco", ex);
        }

        if (!generado) {//Si no es una relación a lo mejor es un libro..
            try {
                File objectFile = new File(carpeta, Repositorio.libroFileName);
                LibroParaBackend libro = leerLibro(objectFile);
                libro.setCarpetaRepositorio(Repositorio.repFolderName);
                Logger.getLogger(Generador.class.getName()).warning("Procesando relación: \r\n" + libro.toString());

                String rutaRelacion = guardarLibroEnXml(carpeta, libro);
                String macroRFile = generarMacroR(carpeta, rutaRelacion.replace(".rtp", ""));
                String macroSHFile = generarMacroSH(carpeta, macroRFile);
                generarPDF(carpeta, macroSHFile);
                generado = true;
            } catch (IOException ex) {
                Logger.getLogger(GeneradorRelacion.class.getName()).log(Level.SEVERE, "Tampoco se pudo leer un libro", ex);
            }
        }
    }

    private RelacionEjerciciosParaBackend leerRelacion(File objectFile) throws IOException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(objectFile));
        try {
            RelacionEjerciciosParaBackend out = (RelacionEjerciciosParaBackend) ois.readObject();
            return out;
        } catch (ClassNotFoundException ex) {
            throw new IOException(ex);
        }
    }

    private LibroParaBackend leerLibro(File objectFile) throws IOException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(objectFile));
        try {
            LibroParaBackend out = (LibroParaBackend) ois.readObject();
            return out;
        } catch (ClassNotFoundException ex) {
            throw new IOException(ex);
        }
    }

    private void generarPDF(File carpeta, String macroFile) throws IOException {
        Process proceso = Runtime.getRuntime().exec(new String[]{"/bin/bash", macroFile}, null, carpeta);
        try {
            proceso.waitFor();
            File log = new File(carpeta, "log.txt");
            PrintStream ps = new PrintStream(log);
            ps.println("Inicio del log:\r\n");
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(proceso.getErrorStream()));
                String linea = null;
                while ((linea = reader.readLine()) != null) {
                    ps.println(linea);
                }
            } finally {
                ps.close();
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Generador.class.getName()).log(Level.SEVERE, "Proceso interrumpido", ex);
        }
    }

    private String generarMacroSH(File carpeta, String macroRFile) throws IOException {
        String macro = "#!/bin/bash" + System.getProperty("line.separator")
                + "echo " + EstadoGeneracion.GENERANDO.toString() + " > estado.txt" + System.getProperty("line.separator")
                + Generador.rutaFicheroR + " CMD BATCH " + macroRFile + "&&" + System.getProperty("line.separator")
                + Generador.rutaFicheroPDFLatex + " -interaction batchmode " + carpeta.getAbsolutePath() + "/relacion.tex" + System.getProperty("line.separator")
                + Generador.rutaFicheroPDFLatex + " -interaction batchmode " + carpeta.getAbsolutePath() + "/relacion.tex" + System.getProperty("line.separator")
                + "if [ -f " + carpeta.getAbsolutePath() + "/relacion.pdf ]; then" + System.getProperty("line.separator")
                + "echo OK > estado.txt" + System.getProperty("line.separator") + "else" + System.getProperty("line.separator")
                + "echo ERROR > estado.txt" + System.getProperty("line.separator") + "fi;";
        File macroFile = new File(carpeta, "generar.sh");
        PrintStream ps = new PrintStream(macroFile);
        try {
            ps.write(macro.getBytes("UTF-8"));
        } finally {
            ps.close();
        }
        return macroFile.getAbsolutePath();
    }

    private String guardarRelacionEnXml(File carpeta, RelacionEjerciciosParaBackend r) throws IOException {
        File relacionFile = new File(carpeta, "Relacion.xml");
        PrintStream ps = new PrintStream(relacionFile);
        try {
            ps.write(r.toString().getBytes("UTF-8"));
        } finally {
            ps.close();
        }
        return relacionFile.getAbsolutePath();
    }

    private String guardarLibroEnXml(File carpeta, LibroParaBackend libro) throws IOException {
        File relacionFile = new File(carpeta, "Relacion.xml");
        PrintStream ps = new PrintStream(relacionFile);
        try {
            ps.write(libro.toString().getBytes("UTF-8"));
        } finally {
            ps.close();
        }
        return relacionFile.getAbsolutePath();
    }

    private String generarMacroR(File carpeta, String rutaRelacion) throws IOException {
        String macro = "source(\"" + Generador.rutaFicheroDescriptivaR + "\")" + System.getProperty("line.separator")
                + "CreaA4(\"" + rutaRelacion.replace(".xml", "")
                + "\",\"relacion.tex\"" + ")" + System.getProperty("line.separator")
                + "q(save = \"no\", runLast = FALSE)" + System.getProperty("line.separator");
        File macroFile = new File(carpeta, "generar.r");
        PrintStream ps = new PrintStream(macroFile);
        try {
            ps.write(macro.getBytes("UTF-8"));
        } finally {
            ps.close();
        }
        return macroFile.getAbsolutePath();
    }
}
