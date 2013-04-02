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
import org.ugr.rtpstat.client.orm.Datos;
import org.ugr.rtpstat.client.orm.EstadoGeneracion;
import org.ugr.rtpstat.client.orm.Problema;

class GeneradorProblema implements Runnable {

    private String uuid;

    public GeneradorProblema(String uuid) {
        this.uuid = uuid;
    }

    public void run() {
        File carpeta = new File(Repositorio.repFolderName, uuid);
        File objectFile = new File(carpeta, Repositorio.problemaFileName);
        try {
            Problema p = leerProblema(objectFile);
            Logger.getLogger(Generador.class.getName()).info("Procesando problema: \r\n" + p.toString() + "\r\nCon datos: \r\n" + p.getDatos().toString());
            String rutaProblema = guardarProblemaEnXml(carpeta, p);
            String rutaDatos = guardarDatos(carpeta, p);
            String macroRFile = generarMacroR(carpeta, rutaProblema.replace(".rtp", ""), p.getDatos().getDecimales());
            String macroSHFile = generarMacroSH(carpeta, macroRFile);
            generarPDF(carpeta, macroSHFile);
        } catch (IOException ex) {
            Logger.getLogger(Generador.class.getName()).log(Level.SEVERE, "Fallo al leer un problema del disco", ex);
        }
    }

    private Problema leerProblema(File objectFile) throws IOException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(objectFile));
        try {
            Problema out = (Problema) ois.readObject();
            return out;
        } catch (ClassNotFoundException ex) {
            throw new IOException(ex);
        }
    }

    private String guardarProblemaEnXml(File carpeta, Problema p) throws IOException {
        File problemaFile = new File(carpeta, "Problema.rtp");
        PrintStream ps = new PrintStream(problemaFile);
        try {
            ps.write(p.toString().getBytes("UTF-8"));
        } finally {
            ps.close();
        }

        File relacionFile = new File(carpeta, "Problema.xml");
        String lineSeparator = "\r\n";
        String relacionXml =
                "<formato>" + lineSeparator
                + "Resueltos" + lineSeparator
                + "</formato>" + lineSeparator
                + "<title>" + lineSeparator
                + "</title>" + lineSeparator
                + "<archivos>" + lineSeparator
                + problemaFile.getAbsolutePath().substring(0,problemaFile.getAbsolutePath().lastIndexOf(".rtp")) + lineSeparator
                + "</archivos>" + lineSeparator;

        ps = new PrintStream(relacionFile);
        try {
            ps.write(relacionXml.toString().getBytes("UTF-8"));
        } finally {
            ps.close();
        }

        return problemaFile.getAbsolutePath();
    }

    private String guardarDatos(File carpeta, Problema p) throws IOException {
        File datosFile = new File(carpeta, "Problema.rtd");
        PrintStream ps = new PrintStream(datosFile);
        Datos datos = p.getDatos();
        try {
            ps.write(datos.toString().getBytes("UTF-8"));
        } finally {
            ps.close();
        }
        return datosFile.getAbsolutePath();
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

    private String generarMacroR(File carpeta, String rutaProblema, int precision) throws IOException {
        String macro = "source(\"" + Generador.rutaFicheroDescriptivaR + "\")" + System.getProperty("line.separator")
                + "CreaA4(\"" + rutaProblema + "\",\"documento.tex\"" + ")" + System.getProperty("line.separator")
                + "CreaFrames(\"" + rutaProblema + "\",\"frames.tex\"" + ")" + System.getProperty("line.separator")
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

    private String generarMacroSH(File carpeta, String macroRFile) throws IOException {
        String macro = "#!/bin/bash" + System.getProperty("line.separator")
                + "echo " + EstadoGeneracion.GENERANDO.toString() + " > estado.txt" + System.getProperty("line.separator")
                + Generador.rutaFicheroR + " CMD BATCH " + macroRFile + "&&" + System.getProperty("line.separator")
                + Generador.rutaFicheroPDFLatex + " -interaction batchmode " + carpeta.getAbsolutePath() + "/documento.tex" + System.getProperty("line.separator")
                + Generador.rutaFicheroPDFLatex + " -interaction batchmode " + carpeta.getAbsolutePath() + "/documento.tex" + System.getProperty("line.separator")
                + Generador.rutaFicheroPDFLatex + " -interaction batchmode " + carpeta.getAbsolutePath() + "/frames.tex" + System.getProperty("line.separator")
                + Generador.rutaFicheroPDFLatex + " -interaction batchmode " + carpeta.getAbsolutePath() + "/frames.tex" + System.getProperty("line.separator")
                + "if [ -f " + carpeta.getAbsolutePath() + "/documento.pdf -a -f " + carpeta.getAbsolutePath() + "/frames.pdf ]; then" + System.getProperty("line.separator")
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
}
