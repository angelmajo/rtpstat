package org.ugr.rtpstat.client.uibinder.problemas.datos;

import org.ugr.rtpstat.client.orm.Datos;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;

public interface EditorDatosInterface extends HasValueChangeHandlers<EditorDatosInterface>{
	public void actualizarPrecision(int pr);

	public boolean isValid();

	public Datos getDatos();
	
	public void limpiar();
}
