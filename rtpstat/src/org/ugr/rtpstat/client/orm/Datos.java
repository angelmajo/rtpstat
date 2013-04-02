package org.ugr.rtpstat.client.orm;

import java.io.Serializable;

public class Datos implements Serializable, IsValidable {
	public static enum TipoAmplitudIntervalos {
		CONSTANTE, VARIABLE
	};

	public static enum TipoVariable {
		CONTINUA_SERIE, CONTINUA_TABLA, DISCRETA_SERIE, DISCRETA_TABLA
	}

	private static final long serialVersionUID = -7455771903685429658L;
	private static final String DEFAULT_STRING = "DEFAULT";

	private TipoVariable tipoVariable;
	private TipoAmplitudIntervalos tipoAmplitudIntervalos;
	private String descripcionVariable;
	private int decimales;
	private float[] valores;
	private float amplitudIntervalos;
	private float[] extremosInferiores;
	private float[] extremosSuperiores;
	private float[] frecuenciasAbsolutas;

	//TODO Crear cuartro constructores distintos, uno para cada tipo de variable
	public Datos() {
		this.setDescripcionVariable(DEFAULT_STRING);
	}

	public float[] getFrecuenciasAbsolutas() {
		return frecuenciasAbsolutas;
	}

	public void setFrecuenciasAbsolutas(float[] frecuenciasAbsolutas) {
		this.frecuenciasAbsolutas = frecuenciasAbsolutas;
	}

	public void setExtremosSuperiores(float[] extremosSuperiores) {
		this.extremosSuperiores = extremosSuperiores;
	}

	public float[] getExtremosSuperiores() {
		return extremosSuperiores;
	}

	public void setExtremosInferiores(float[] extremosInferiores) {
		this.extremosInferiores = extremosInferiores;
	}

	public float[] getExtremosInferiores() {
		return extremosInferiores;
	}

	public float getAmplitudIntervalos() {
		return amplitudIntervalos;
	}

	public void setAmplitudIntervalos(float amplitudIntervalos) {
		this.amplitudIntervalos = amplitudIntervalos;
	}

	public float[] getValores() {
		return valores;
	}

	public void setValores(float[] valores) {
		this.valores = valores;
	}

	public void setTipoAmplitudIntervalos(TipoAmplitudIntervalos tipoAmplitudIntervalos) {
		this.tipoAmplitudIntervalos = tipoAmplitudIntervalos;
	}

	public TipoAmplitudIntervalos getTipoAmplitudIntervalos() {
		return tipoAmplitudIntervalos;
	}

	public void setDecimales(int decimales) {
		this.decimales = decimales;
	}

	public int getDecimales() {
		return decimales;
	}

	public void setDescripcionVariable(String descripcionVariable) {
		this.descripcionVariable = descripcionVariable;
	}

	public String getDescripcionVariable() {
		return descripcionVariable;
	}

	public void setTipoVariable(TipoVariable tipoVariable) {
		this.tipoVariable = tipoVariable;
	}

	public TipoVariable getTipoVariable() {
		return tipoVariable;
	}

	public boolean isValid() {
		// TODO Los datos siempre son validos!!!
		boolean out = true;
//		switch (tipoVariable) {
//			case CONTINUA_SERIE:
//				break;
//			case CONTINUA_TABLA:
//				break;
//			case DISCRETA_SERIE:
//				break;
//			case DISCRETA_TABLA:
//				break;
//		}
		return out;
	}

	// Ayuditas para poder pasar arrays de enteros
	public void setValores(int[] valores2) {
		float[] out = new float[valores2.length];
		for (int i = 0; i < valores2.length; i++) {
			out[i] = valores2[i];
		}
		setValores(out);
	}

	public void setFrecuenciasAbsolutas(int[] frecuanciasAbsolutas) {
		float[] out = new float[frecuanciasAbsolutas.length];
		for (int i = 0; i < frecuanciasAbsolutas.length; i++) {
			out[i] = frecuanciasAbsolutas[i];
		}
		setFrecuenciasAbsolutas(out);
	}

	@Override
	public String toString() {
		if (!this.isValid()) {
			return "DATOS INVALIDOS";
		} else {
			StringBuilder out = new StringBuilder();
			out.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
			out.append("<datos>\r\n");
			switch (getTipoVariable()) {
				case CONTINUA_SERIE:
					switch (getTipoAmplitudIntervalos()) {
						case CONSTANTE:
							out.append("\t<tipo>\r\ncontinua_serie_intervalos_constantes\r\n\t</tipo>\r\n");
							out.append("\t\t<valores>\r\n");
							for (float valor : getValores()) {
								out.append("\t\t\t" + valor + "\r\n");
							}
							out.append("\t\t</valores>\r\n");
							out.append("\t\t<amplitud>\r\n");
							out.append("\t\t\t" + getAmplitudIntervalos() + "\r\n");
							out.append("\t\t</amplitud>\r\n");
							out.append("\t\t<primer_extremo>\r\n");
							out.append("\t\t\t" + getExtremosInferiores()[0]+ "\r\n");
							out.append("\t\t</primer_extremo>\r\n");
							break;
						case VARIABLE:
							out.append("\t<tipo>\r\ncontinua_serie_intervalos_variables\r\n\t</tipo>\r\n");
							out.append("\t\t<valores>\r\n");
							for (float valor : getValores()) {
								out.append("\t\t\t" + valor + "\r\n");
							}
							out.append("\t\t</valores>\r\n");
							out.append("\t\t<extremos_inferiores>\r\n");
							for (float valor : getExtremosInferiores()) {
								out.append("\t\t\t" + valor + "\r\n");
							}
							out.append("\t\t</extremos_inferiores>\r\n");
							out.append("\t\t<ultimo_superior>\r\n");
							out.append("\t\t\t" + getExtremosSuperiores()[getExtremosSuperiores().length - 1] + "\r\n");
							out.append("\t\t</ultimo_superior>\r\n");
							break;
					}
					break;
				case CONTINUA_TABLA:
					out.append("\t<tipo>\r\ncontinua_tabla\r\n\t</tipo>\r\n");
					out.append("\t\t<extremos_inferiores>\r\n");
					for (float valor : getExtremosInferiores()) {
						out.append("\t\t\t" + valor + "\r\n");
					}
					out.append("\t\t</extremos_inferiores>\r\n");
					out.append("\t\t<ultimo_superior>\r\n");
					out.append("\t\t\t" + getExtremosSuperiores()[getExtremosSuperiores().length - 1] + "\r\n");
					out.append("\t\t</ultimo_superior>\r\n");
					out.append("\t\t<frecuencias>\r\n");
					for (float valor : getFrecuenciasAbsolutas()) {
						out.append("\t\t\t" + valor + "\r\n");
					}
					out.append("\t\t</frecuencias>\r\n");
					break;
				case DISCRETA_SERIE:
					out.append("\t<tipo>\r\ndiscreta_serie\r\n\t</tipo>\r\n");
					out.append("\t\t<valores>\r\n");
					for (float valor : getValores()) {
						out.append("\t\t\t" + valor + "\r\n");
					}
					out.append("\t\t</valores>\r\n");
					break;
				case DISCRETA_TABLA:
					out.append("\t<tipo>\r\ndiscreta_tabla\r\n\t</tipo>\r\n");
					out.append("\t\t<valores>\r\n");
					for (float valor : getValores()) {
						out.append("\t\t\t" + valor + "\r\n");
					}
					out.append("\t\t</valores>\r\n");
					out.append("\t\t<frecuencias>\r\n");
					for (float valor : getFrecuenciasAbsolutas()) {
						out.append("\t\t\t" + valor + "\r\n");
					}
					out.append("\t\t</frecuencias>\r\n");
					break;
			}
			out.append("</datos>\r\n");
			return out.toString();
		}
	}
}
