package org.ugr.rtpstat.client.orm;

import java.io.Serializable;

public class Apartado implements Serializable, IsValidable{
	private static final long serialVersionUID = -3660691225891513502L;
	
	private String enunciado;
	public void setEnunciado(String enunciado) {
		this.enunciado = enunciado;
	}
	public String getEnunciado() {
		return enunciado;
	}

	public boolean isValid() {
		return enunciado != null && enunciado.length() > 0;
	}
	
	public Apartado() {
		super();
	}
	
	public Apartado(String enunciado) {
		super();
		this.enunciado = enunciado;
	}
}
