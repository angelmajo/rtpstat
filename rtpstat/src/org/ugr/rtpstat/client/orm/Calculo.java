package org.ugr.rtpstat.client.orm;

import java.io.Serializable;

public class Calculo implements Serializable {
	private static final long serialVersionUID = -4146996365386885058L;

	public static enum TipoCalculo {
		Tabulación, MediaAritmética, MediaArmónica, MediaCuadrática, MediaGeométrica, PrimerCuartil, Mediana, TercerCuartil, Decil, Percentil, FunciónDeDistribucion, Moda, Rango, RangoIntercuartílico, Varianza, DesviaciónTípica, DesviaciónAbsolutaRespectoMedia, DesviaciónAbsolutaRespectoMediana, CoeficienteApertura, RecorridoRelativo, CoeficienteVariaciónPearson, IndiceDispersiónRespectoMediana, MomentoNoCentral, MomentoCentral, CoeficienteAsimetríaFisher, CoeficienteCurtosis
	}

	private Float parametro = null;
	private TipoCalculo tipo = null;

	public Calculo() {

	}

	public Calculo(TipoCalculo tipo) {
		this.tipo = tipo;
	}

	public Calculo(TipoCalculo tipo, Float parametro) {
		this.tipo = tipo;
		if (!necesitaParametro()) {
			this.tipo = null;
			throw new IllegalArgumentException("Para crear un cálculo de tipo " + tipo + " NO es necesario un parametro");
		}
		this.parametro = parametro;
	}

	public String toShortString() {
		String out = null;
		switch (this.tipo) {
			case Tabulación:
				out = "Tab";
				break;
			case MediaAritmética:
				out = "m1";
				break;
			case MediaArmónica:
				out = "H";
				break;
			case MediaCuadrática:
				out = "Q";
				break;
			case MediaGeométrica:
				out = "G";
				break;
			case PrimerCuartil:
				out = "Q1";
				break;
			case Mediana:
				out = "Me";
				break;
			case TercerCuartil:
				out = "Q3";
				break;
			case Decil:
				if (getParametro() == null) {
					throw new NullPointerException("Los cálculos con parametros, necesitan tener parametros.");
				}
				out = "D" + getParametro().intValue();
				break;
			case Percentil:
				if (getParametro() == null) {
					throw new NullPointerException("Los cálculos con parametros, necesitan tener parametros.");
				}
				out = "P" + getParametro().intValue();
				break;
			case FunciónDeDistribucion:
				if (getParametro() == null) {
					throw new NullPointerException("Los cálculos con parametros, necesitan tener parametros.");
				}
				//TODO Este return aquí muy feo
				return "Fx(" + getParametro() + ")";
			case Moda:
				out = "Mo";
				break;
			case Rango:
				out = "R";
				break;
			case RangoIntercuartílico:
				out = "RI";
				break;
			case Varianza:
				out = "Var";
				break;
			case DesviaciónTípica:
				out = "Sd";
				break;
			case DesviaciónAbsolutaRespectoMedia:
				out = "Dm1";
				break;
			case DesviaciónAbsolutaRespectoMediana:
				out = "DMe";
				break;
			case CoeficienteApertura:
				out = "CA";
				break;
			case RecorridoRelativo:
				out = "Rr";
				break;
			case CoeficienteVariaciónPearson:
				out = "CV";
				break;
			case IndiceDispersiónRespectoMediana:
				out = "IMe";
				break;
			case MomentoNoCentral:
				if (getParametro() == null) {
					throw new NullPointerException("Los cálculos con parametros, necesitan tener parametros.");
				}
				out = "m" + getParametro().intValue();
				break;
			case MomentoCentral:
				if (getParametro() == null) {
					throw new NullPointerException("Los cálculos con parametros, necesitan tener parametros.");
				}
				out = "µ" + getParametro().intValue();
				break;
			case CoeficienteAsimetríaFisher:
				out = "Gamma1";
				break;
			case CoeficienteCurtosis:
				out = "Gamma2";
				break;
			default:
				throw new IllegalStateException("Existe un cálculo sin manejar, pidale al administrador que resuelva este problema (Unknown state: " + this
						+ ")");
		}
		return out + "(X)";
	}

	public String toLongString() {
		return toLongString(getTipo());//TODO Parametros
	}

	public static String toLongString(TipoCalculo c) {
		String out = c.toString();
		switch (c) {
			case Tabulación:
			case Mediana:
			case Moda:
			case Rango:
			case Varianza:
				break;

			case Decil:
			case Percentil:
				break;
			case MediaAritmética:
				out = "Media aritmética";
				break;
			case MediaArmónica:
				out = "Media armónica";
				break;
			case MediaCuadrática:
				out = "Media cuadrática";
				break;
			case MediaGeométrica:
				out = "Media geométrica";
				break;
			case PrimerCuartil:
				out = "Primer cuartil";
				break;
			case TercerCuartil:
				out = "Tercer cuartil";
				break;
			case FunciónDeDistribucion:
				out = "Función de distribución";
				break;
			case RangoIntercuartílico:
				out = "Rango intercuartílico";
				break;
			case DesviaciónTípica:
				out = "Desviación típica";
				break;
			case DesviaciónAbsolutaRespectoMedia:
				out = "Desviación absoluta media respecto a la media";
				break;
			case DesviaciónAbsolutaRespectoMediana:
				out = "Desviación absoluta media respecto a la mediana";
				break;
			case CoeficienteApertura:
				out = "Coeficiente de apertura";
				break;
			case RecorridoRelativo:
				out = "Recorrido relativo";
				break;
			case CoeficienteVariaciónPearson:
				out = "Coeficiente de variación de Pearson";
				break;
			case IndiceDispersiónRespectoMediana:
				out = "Indice de dispersión respecto de la mediana";
				break;
			case MomentoNoCentral:
				out = "Momentos no centrales";
				break;
			case MomentoCentral:
				out = "Momentos centrales";
				break;
			case CoeficienteAsimetríaFisher:
				out = "Coeficiente de asimetría de Fisher";
				break;
			case CoeficienteCurtosis:
				out = "Coeficiente de curtosis";
				break;
			default:
				throw new IllegalStateException("Existe un cálculo sin manejar, pidale al administrador que resuelva este problema (Unknown state: " + c + ")");
		}
		return out;
	}

	/*
	 * "Tabulación (Tab)", "Media Aritmética (m1)", "Media Armónica (H)",
	 * "Media Cuadrática (Q)", "Media Geométrica (G)", "Primer Cuartil (Q1)",
	 * "Mediana (Me)", "Tercer Cuartil (Q3)", "Deciles (D1-D9)",
	 * "Percentiles (P1-P99)", "Función de distribución en x (F(x))",
	 * "Moda (Mo)", "Rango (R)", "Rango Intercuartílico (RI)", "Varianza (Var)",
	 * "Desviación Típica (Sd)",
	 * "Desviación absoluta media respecto a la media (Dm1)",
	 * "Desviación absoluta media respecto a la mediana (DMe)",
	 * "Coeficiente de apertura (CA)", "Recorrido relativo (Rr)",
	 * "Coeficiente de Variación de Pearson (CV)",
	 * "Indice de dispersión respecto de la mediana (IMe)",
	 * "Momentos No Centrales (m)", "Momentos Centrales (µ)",
	 * "Coeficiente de Asimetría de Fisher (Gamma1)",
	 * "Coeficiente de Curtosis ó Apuntamiento (Gamma2)"
	 */

	public boolean necesitaParametro() {
		boolean out = false;
		switch (this.tipo) {
			case Decil:
			case Percentil:
			case MomentoCentral:
			case MomentoNoCentral:
			case FunciónDeDistribucion:
				out = true;
				break;
			default:;
		}
		return out;
	}

	public void setParametro(Float parametro) {
		if (!necesitaParametro()) {
			throw new IllegalStateException("Este calculo " + this + " no tiene parametros!");
		}
		this.parametro = parametro;
	}

	public Float getParametro() {
		if (!necesitaParametro()) {
			throw new IllegalStateException("Este calculo " + this + " no tiene parametros!");
		}
		return parametro;
	}

	public Float getMinParam() {
		if (!necesitaParametro()) {
			throw new IllegalStateException("Este calculo " + this + " no tiene parametros!");
		}
		float out = 1;
		if (TipoCalculo.FunciónDeDistribucion.equals(this.tipo)) {
			out = Float.MIN_VALUE;
		}
		return out;
	}

	public Float getMaxParam() {
		if (!necesitaParametro()) {
			throw new IllegalStateException("Este calculo " + this + " no tiene parametros!");
		}
		float out = 0;
		switch (this.tipo) {
			case Decil:
				out = 9;
				break;
			case Percentil:
				out = 99;
				break;
			case MomentoCentral:
				out = 4;
				break;
			case MomentoNoCentral:
				out = 4;
				break;
			case FunciónDeDistribucion:
				out = Float.MAX_VALUE;
				break;
			default:;
		}
		return out;
	}

	public TipoCalculo getTipo() {
		return tipo;
	}

	public static Calculo valueOf(String itemText) {
		if (itemText.matches("Decil[0-9]")) {
			return new Calculo(TipoCalculo.Decil, Float.parseFloat(itemText.replace("Decil", "")));
		} else if (itemText.matches("Percentil[0-9]{1,2}")) {
			return new Calculo(TipoCalculo.Percentil, Float.parseFloat(itemText.replace("Percentil", "")));
		} else if (itemText.matches("MomentoCentral[0-9]")) {
			return new Calculo(TipoCalculo.MomentoCentral, Float.parseFloat(itemText.replace("MomentoCentral", "")));
		} else if (itemText.matches("MomentoNoCentral[0-9]")) {
			return new Calculo(TipoCalculo.MomentoNoCentral, Float.parseFloat(itemText.replace("MomentoNoCentral", "")));
		} else if (itemText.matches("FunciónDeDistribucion[-+]?[0-9]*\\.?[0-9]+")) {
			return new Calculo(TipoCalculo.FunciónDeDistribucion, Float.parseFloat(itemText.replace("FunciónDeDistribucion", "")));
		} else {
			return new Calculo(TipoCalculo.valueOf(itemText));
		}
	}

	@Override
	public String toString() {
		String out;
		if (necesitaParametro() && getParametro() != null) {
			if (getTipo() == TipoCalculo.FunciónDeDistribucion) {
				out = tipo.toString() + getParametro();
			} else {
				out = tipo.toString() + getParametro().intValue();
			}
		} else {
			out = tipo.toString();
		}
		return out;

	}
}
