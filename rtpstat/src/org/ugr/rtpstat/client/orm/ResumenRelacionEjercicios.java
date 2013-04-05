package org.ugr.rtpstat.client.orm;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ResumenRelacionEjercicios implements IsSerializable {
	private long id;
	private String titulo;
	private String[] areasObjetivo;
	private TipoRelacion tipo;
	private int numeroEjercicios;
	private boolean generada;

	public ResumenRelacionEjercicios() {
		this(Long.MIN_VALUE, null, null, null, -1,false);
	}

	public ResumenRelacionEjercicios(long id, String titulo,
			String[] areasObjetivo, TipoRelacion tipo, int numeroEjercicios,boolean generada) {
		this.setId(id);
		this.setTitulo(titulo);
		this.setAreasObjetivo(areasObjetivo);
		this.setTipo(tipo);
		this.setNumeroEjercicios(numeroEjercicios);
		this.setGenerada(generada);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public void setAreasObjetivo(String[] areasObjetivo) {
		this.areasObjetivo = areasObjetivo;
	}

	public String[] getAreasObjetivo() {
		return areasObjetivo;
	}

	public void setTipo(TipoRelacion tipo) {
		this.tipo = tipo;
	}

	public TipoRelacion getTipo() {
		return tipo;
	}

	public void setNumeroEjercicios(int numeroEjercicios) {
		this.numeroEjercicios = numeroEjercicios;
	}

	public int getNumeroEjercicios() {
		return numeroEjercicios;
	}

	public boolean isGenerada() {
		return generada;
	}

	public void setGenerada(boolean generada) {
		this.generada = generada;
	}
}
