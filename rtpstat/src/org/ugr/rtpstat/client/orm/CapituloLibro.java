package org.ugr.rtpstat.client.orm;

import java.io.Serializable;
import java.util.ArrayList;

public class CapituloLibro implements Serializable {
	private static final long serialVersionUID = -786987078827839033L;

	private Long id;

	private String titulo;

	private String resueltosTitulo;

	private ArrayList<ResumenProblema> resueltos;

	private String propuestosTitulo;

	private ArrayList<ResumenProblema> propuestos;

	public CapituloLibro() {
		this(Long.MIN_VALUE, null, null, null, null, null);
	}

	public CapituloLibro(String titulo, String resueltosTitulo,
			ArrayList<ResumenProblema> resueltos, String propuestosTitulo,
			ArrayList<ResumenProblema> propuestos) {
		this(Long.MIN_VALUE, titulo, resueltosTitulo, resueltos,
				propuestosTitulo, propuestos);
	}

	public CapituloLibro(Long id, String titulo, String resueltosTitulo,
			ArrayList<ResumenProblema> resueltos, String propuestosTitulo,
			ArrayList<ResumenProblema> propuestos) {
		this.id = id;
		this.titulo = titulo;
		this.resueltosTitulo = resueltosTitulo;
		this.resueltos = resueltos;
		this.propuestosTitulo = propuestosTitulo;
		this.propuestos = propuestos;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getResueltosTitulo() {
		return resueltosTitulo;
	}

	public void setResueltosTitulo(String resueltosTitulo) {
		this.resueltosTitulo = resueltosTitulo;
	}

	public ArrayList<ResumenProblema> getResueltos() {
		return resueltos;
	}

	public void setResueltos(ArrayList<ResumenProblema> resueltos) {
		this.resueltos = resueltos;
	}

	public String getPropuestosTitulo() {
		return propuestosTitulo;
	}

	public void setPropuestosTitulo(String propuestosTitulo) {
		this.propuestosTitulo = propuestosTitulo;
	}

	public ArrayList<ResumenProblema> getPropuestos() {
		return propuestos;
	}

	public void setPropuestos(ArrayList<ResumenProblema> propuestos) {
		this.propuestos = propuestos;
	}
}
