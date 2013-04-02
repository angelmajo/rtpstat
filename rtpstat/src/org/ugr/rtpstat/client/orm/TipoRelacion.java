package org.ugr.rtpstat.client.orm;

public enum TipoRelacion {
	Propuestos, Resueltos;//, Libro;

	public String toLongString() {
		String out = null;
		switch (this) {
			case Resueltos:
				out = "Problemas resueltos";
				break;
			case Propuestos:
				out = "Problemas propuestos";
				break;
			/*case Libro:
				out = "Ejercicios resueltos y propuestos";
				break;*/
		}
		return out;
	}
}
