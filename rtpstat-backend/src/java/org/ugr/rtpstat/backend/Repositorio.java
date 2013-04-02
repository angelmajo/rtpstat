/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ugr.rtpstat.backend;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.UUID;
import org.ugr.rtpstat.client.orm.Problema;
import org.ugr.rtpstat.client.orm.RelacionEjercicios;
import org.ugr.rtpstat.server.LibroParaBackend;
import org.ugr.rtpstat.server.RelacionEjerciciosParaBackend;

/**
 *
 * @author opsi
 */
public class Repositorio {
    //TODO sacar todas las configuraciones a un fichero externo

    protected final static String problemaFileName = "problema.object";
    protected final static String relacionFileName = "relacion.object";
    protected final static String libroFileName = "libro.object";
    protected final static String repFolderName = "/home/repositorio-rtpstat/";
    private Generador generador = new Generador();
    private File repFolder = null;


    public Repositorio() throws IOException {
        repFolder = new File(repFolderName);
        if (!repFolder.exists()) {
            repFolder.mkdir();
        } else if (!repFolder.isDirectory()) {
            throw new IOException(repFolderName + " tendria que ser un directorio, pero no lo es");
        } else if (!repFolder.canRead() || !repFolder.canWrite()) {
            throw new IOException("El directorio " + repFolderName + " tiene que tener permisos de lectura y escritura para el usuario (" + System.getProperty("user.name") + ")");
        }
    }

    public String procesarSolicitud(Problema problema) throws IOException {
        UUID uuid = UUID.randomUUID();
        File dir = new File(repFolder, uuid.toString());

        //Nos aseguramos de no sobreescribir nada
        while (dir.exists()) {
            uuid = UUID.randomUUID();
            dir = new File(repFolder, uuid.toString());
        }
        dir.mkdir();
        File objectFile = new File(dir, problemaFileName);
        FileOutputStream fos = new FileOutputStream(objectFile);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(problema);
        oos.close();
        generador.encolar(uuid.toString(), Problema.class);

        return uuid.toString();
    }

    String procesarSolicitud(RelacionEjerciciosParaBackend relacion) throws IOException {
        UUID uuid = UUID.randomUUID();
        File dir = new File(repFolder, uuid.toString());

        //Nos aseguramos de no sobreescribir nada
        while (dir.exists()) {
            uuid = UUID.randomUUID();
            dir = new File(repFolder, uuid.toString());
        }
        dir.mkdir();
        File objectFile = new File(dir, relacionFileName);
        FileOutputStream fos = new FileOutputStream(objectFile);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(relacion);
        oos.close();
        generador.encolar(uuid.toString(), RelacionEjerciciosParaBackend.class);

        return uuid.toString();
    }

    String procesarSolicitud(LibroParaBackend libro) throws IOException {
        UUID uuid = UUID.randomUUID();
        File dir = new File(repFolder, uuid.toString());

        //Nos aseguramos de no sobreescribir nada
        while (dir.exists()) {
            uuid = UUID.randomUUID();
            dir = new File(repFolder, uuid.toString());
        }
        dir.mkdir();
        File objectFile = new File(dir, libroFileName);
        FileOutputStream fos = new FileOutputStream(objectFile);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(libro);
        oos.close();
        generador.encolar(uuid.toString(), LibroParaBackend.class);

        return uuid.toString();
    }
}
