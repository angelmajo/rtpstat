/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ugr.rtpstat.backend;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.ugr.rtpstat.client.orm.Problema;
import org.ugr.rtpstat.server.LibroParaBackend;
import org.ugr.rtpstat.server.RelacionEjerciciosParaBackend;

/**
 * 
 * @author opsi
 */
public class Generador {
    //ATENCION!!!!
    //En ubuntu : sudo apt-get install texlive-latex-base texlive-latex-extra texlive-lang-spanish r-base-core latex-beamer
    //WARNING!!!

    //TODO sacar todas las configuraciones a un fichero externo
    protected static final String rutaFicheroPDFLatex = "/usr/bin/pdflatex";
    protected static final String rutaFicheroR = "/usr/bin/R";
    protected static final String rutaFicheroDescriptivaR = "/home/jjserra/Descriptiva1D_RTPSTAT.R";

    private Executor ejecutor;

    public Generador() {
        //ejecutor = new ThreadPoolExecutor(3,6,10,TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(50));
        ejecutor = Executors.newFixedThreadPool(4);
    }

    public void encolar(String uuid, Class tipo) {
        if (Problema.class.equals(tipo)) {
            ejecutor.execute(new GeneradorProblema(uuid));
        } else if (RelacionEjerciciosParaBackend.class.equals(tipo) || LibroParaBackend.class.equals(tipo)) {
            ejecutor.execute(new GeneradorRelacion(uuid));
        } else {
            throw new IllegalArgumentException("No se puede procesar un objeto del tipo: " + tipo);
        }
    }
}
