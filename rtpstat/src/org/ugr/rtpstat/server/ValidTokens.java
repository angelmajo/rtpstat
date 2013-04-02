package org.ugr.rtpstat.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ugr.rtpstat.server.orm.LibroEnStore;
import org.ugr.rtpstat.server.orm.ProblemaEnStore;
import org.ugr.rtpstat.server.orm.RelacionEjerciciosEnstore;

public class ValidTokens extends HttpServlet {
	private static final long serialVersionUID = 5120384384081644560L;

	private static String[] validTokens() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		ArrayList<String> tokens = new ArrayList<String>();
		try {
			Query q = pm.newQuery(ProblemaEnStore.class);
			List<ProblemaEnStore> listaProblemas = execQueryProblemas(q);
			for (ProblemaEnStore problema : listaProblemas) {
				tokens.add(problema.getToken());
			}

			q = pm.newQuery(RelacionEjerciciosEnstore.class);
			List<RelacionEjerciciosEnstore> listaRelaciones = execQueryRelaciones(q);
			for (RelacionEjerciciosEnstore relacion : listaRelaciones) {
				tokens.add(relacion.getToken());
			}

			q = pm.newQuery(LibroEnStore.class);
			List<LibroEnStore> listaLibros = execQueryLibros(q);
			for (LibroEnStore libro : listaLibros) {
				tokens.add(libro.getToken());
			}
		} finally {
			pm.close();
		}
		return tokens.toArray(new String[tokens.size()]);
	}

	@SuppressWarnings("unchecked")
	private static List<LibroEnStore> execQueryLibros(Query q) {
		return (List<LibroEnStore>) q.execute();
	}

	@SuppressWarnings("unchecked")
	private static List<ProblemaEnStore> execQueryProblemas(Query q) {
		return (List<ProblemaEnStore>) q.execute();
	}

	@SuppressWarnings("unchecked")
	private static List<RelacionEjerciciosEnstore> execQueryRelaciones(Query q) {
		return (List<RelacionEjerciciosEnstore>) q.execute();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		procesarSolicitud(req,resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		procesarSolicitud(req,resp);
	}
	
	private void procesarSolicitud(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		ServletOutputStream outputStream = resp.getOutputStream();
		for(String token: validTokens()) {
			outputStream.println(token);
		}
	}
}
