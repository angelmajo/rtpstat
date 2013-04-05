package org.ugr.rtpstat.client.orm;

public class ApartadoConCalculos extends Apartado {
  public ApartadoConCalculos() {
  }

  public ApartadoConCalculos(String enunciado, Calculo[] calculos) {
    super(enunciado);
    setCalculos(calculos);
  }

  // public final static String[] CALCULOS_DISPONIBLES = { "Tabulación (Tab)",
  // "Media Aritmética (m1)", "Media Armónica (H)",
  // "Media Cuadrática (Q)", "Media Geométrica (G)",
  // "Primer Cuartil (Q1)", "Mediana (Me)", "Tercer Cuartil (Q3)",
  // "Deciles (D1-D9)", "Percentiles (P1-P99)",
  // "Función de distribución en x (F(x))", "Moda (Mo)", "Rango (R)",
  // "Rango Intercuartílico (RI)", "Varianza (Var)",
  // "Desviación Típica (Sd)",
  // "Desviación absoluta media respecto a la media (Dm1)",
  // "Desviación absoluta media respecto a la mediana (DMe)",
  // "Coeficiente de apertura (CA)", "Recorrido relativo (Rr)",
  // "Coeficiente de Variación de Pearson (CV)",
  // "Indice de dispersión respecto de la mediana (IMe)",
  // "Momentos No Centrales (m)", "Momentos Centrales (µ)",
  // "Coeficiente de Asimetría de Fisher (Gamma1)",
  // "Coeficiente de Curtosis ó Apuntamiento (Gamma2)" };

  private Calculo[] calculos;

  @Override
  public boolean isValid() {
    return super.isValid() && calculos != null && calculos.length > 0;
  }

  public void setCalculos(Calculo[] calculos) {
    this.calculos = calculos;
  }

  public Calculo[] getCalculos() {
    return calculos;
  }

}
