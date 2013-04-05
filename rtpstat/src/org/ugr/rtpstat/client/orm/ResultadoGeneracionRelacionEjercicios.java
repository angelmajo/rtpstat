package org.ugr.rtpstat.client.orm;

import java.util.HashMap;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ResultadoGeneracionRelacionEjercicios implements IsSerializable{
	private HashMap<GradoDificultad, Integer> problemas = new HashMap<GradoDificultad, Integer>();
	private long idRelacion;

	public void incrementar(GradoDificultad dificultad) {
		if (problemas.get(dificultad) == null) {
			problemas.put(dificultad, 1);
		} else {
			problemas.put(dificultad, problemas.get(dificultad) + 1);
		}
	}

	public void setIdRelacion(long idRelacion) {
		this.idRelacion = idRelacion;
	}

	public long getIdRelacion() {
		return idRelacion;
	}

	public int getNumProblemas(GradoDificultad grado) {
		Integer i = problemas.get(grado);
		return i == null ? 0 : i;
	}

}
