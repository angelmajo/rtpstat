package org.ugr.rtpstat.client.orm;

public enum GradoDificultad {
	MuyFacil, Facil, Intermedio, Dificil, MuyDificil;

	public String comoString() {
		String out = null;
		switch (this) {
			case MuyFacil:
				out = "Muy Fácil";
				break;
			case Facil:
				out = "Fácil";
				break;
			case Intermedio:
				out = "Intermedio";
				break;
			case Dificil:
				out = "Difícil";
				break;
			case MuyDificil:
				out = "Muy Difícil";
				break;
		}
		return out;
	}
}
