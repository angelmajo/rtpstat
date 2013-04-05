package org.ugr.rtpstat.client.orm;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Apartado implements IsSerializable, IsValidable{
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
