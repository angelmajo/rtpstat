package org.ugr.rtpstat.client.orm;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ResumenProblema implements IsSerializable{
	private long id;
	private String descripcion;
	private boolean documentosGenerados;
	private GradoDificultad dificultad;
	private String areasConcatenadas;

	public ResumenProblema() {
		setId(-1);
		setDescripcion(null);
		setDocumentosGenerados(false);
	}

	public ResumenProblema(Problema problema) {
		setId(problema.getId());
		setDescripcion(problema.getDescripcion());
		setDocumentosGenerados(problema.isDocumentosGenerados());
		setDificultad(problema.getDificultad());
		if (problema.getAreasObjetivo() != null && problema.getAreasObjetivo().length > 0) {
			setAreasConcatenadas(implode(problema.getAreasObjetivo(), ","));
		} else {
			setAreasConcatenadas("Sin áreas objetivo");
		}
	}

	private static String implode(String[] ary, String delim) {
		String out = "";
		for (int i = 0; i < ary.length; i++) {
			if (i != 0) {
				out += delim;
			}
			out += ary[i];
		}
		return out;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public void setDocumentosGenerados(boolean documentosGenerados) {
		this.documentosGenerados = documentosGenerados;
	}

	public boolean isDocumentosGenerados() {
		return documentosGenerados;
	}

	public void setDificultad(GradoDificultad dificultad) {
		this.dificultad = dificultad;
	}

	public GradoDificultad getDificultad() {
		return dificultad;
	}

	public void setAreasConcatenadas(String areasConcatenadas) {
		this.areasConcatenadas = areasConcatenadas;
	}

	public String getAreasConcatenadas() {
		return areasConcatenadas;
	}
}
