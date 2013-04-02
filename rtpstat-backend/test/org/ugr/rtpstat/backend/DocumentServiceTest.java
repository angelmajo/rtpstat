/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ugr.rtpstat.backend;

import com.sun.xml.internal.bind.v2.util.ByteArrayOutputStreamEx;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.ugr.rtpstat.client.orm.GradoDificultad;
import org.ugr.rtpstat.client.orm.Problema;
import static org.junit.Assert.*;

/**
 *
 * @author opsi
 */
public class DocumentServiceTest {

    public DocumentServiceTest() {
    }

    @Test
    public void testExtraerObjeto() throws IOException, ClassNotFoundException {
        Problema p = new Problema("Descripcion", GradoDificultad.Intermedio, "cabecera", "cabecera segunda", null, null, null);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput os = new ObjectOutputStream(bos);
        os.writeObject(p);

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

        DocumentService ds = new DocumentService();
        //ds.extraerObjeto(bis);
    }
}
