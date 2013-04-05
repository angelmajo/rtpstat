package org.ugr.rtpstat.server;

import java.io.File;
import java.util.ArrayList;

import org.ugr.rtpstat.client.orm.RelacionEjercicios;
import org.ugr.rtpstat.client.orm.ResumenProblema;
import org.ugr.rtpstat.client.orm.TipoRelacion;

public class RelacionEjerciciosParaBackend extends RelacionEjercicios {
	private static final String lineSeparator = "\r\n";
	private String[] tokens;
	private String carpetaRepositorio = null;

	public RelacionEjerciciosParaBackend() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RelacionEjerciciosParaBackend(long id, String titulo, TipoRelacion tipo, String[] areasObjetivo, String[] tokens) {
		super(id, titulo, tipo, areasObjetivo, null);
		this.setTokens(tokens);
	}

	@Override
	@Deprecated
	public ArrayList<ResumenProblema> getProblemas() {
		throw new NoSuchMethodError("En una relación de ejercicios para el backend no se usan los problemas, se usan sus tokens");
	}

	@Override
	public void setProblemas(ArrayList<ResumenProblema> problemas) {
		// TODO Auto-generated method stub
		super.setProblemas(problemas);
	}

	public void setTokens(String[] tokens) {
		this.tokens = tokens;
	}

	public String[] getTokens() {
		return tokens;
	}

	@Override
	public String toString() {
		if(carpetaRepositorio == null) {
			throw new NullPointerException("Establezca la carpeta donde esta el repositorio usando setCarpetaRepositorio(String) antes de usar el método toString()");
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("<formato>");
		sb.append(lineSeparator);
		sb.append(getTipo());
		sb.append(lineSeparator);
		sb.append("</formato>");
		sb.append(lineSeparator);
		sb.append("<title>");
		sb.append(lineSeparator);
		sb.append(getTitulo());
		sb.append(lineSeparator);
		sb.append("</title>");
		sb.append(lineSeparator);
		sb.append("<archivos>");
		sb.append(lineSeparator);
		for (String archivo : tokens) {
			sb.append(new File(carpetaRepositorio,archivo).getAbsolutePath()+File.separator+"Problema");
			sb.append(lineSeparator);
		}

		sb.append("</archivos>");
		sb.append(lineSeparator);
		return sb.toString();
	}

	/**
	 * Establece el path completo a la carpeta del repositorio. Debe ser un path absoluto, terminado en File.pathSeparator
	 * @param carpetaRepositorio
	 */
	public void setCarpetaRepositorio(String carpetaRepositorio) {
		this.carpetaRepositorio = carpetaRepositorio;
	}

	public String getCarpetaRepositorio() {
		return carpetaRepositorio;
	}

}
