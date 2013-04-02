package org.ugr.rtpstat.client.uibinder.libros;

import org.ugr.rtpstat.client.orm.ResumenProblema;
import org.ugr.rtpstat.client.uibinder.principal.NotificadorGeneral;
import org.ugr.rtpstat.client.uibinder.relaciones.ProblemaEnRelacion;

public class ProblemaEnLibro extends ProblemaEnRelacion {

	private static final String RESUELTO = "Resuelto";
	private static final String PROPUESTO = "Propuesto";

	public ProblemaEnLibro(boolean incluido, ResumenProblema p, NotificadorGeneral notificadorGeneral) {
		super(false,p,notificadorGeneral);
		accionesDisponibles.clear();
		accionesDisponibles.addItem(PROPUESTO);
		accionesDisponibles.addItem(RESUELTO);
		accionesDisponibles.addItem("No incluido");
		accionesDisponibles.setSelectedIndex(2);
	}

	public boolean isResuelto() {
		return RESUELTO.equals(accionesDisponibles.getItemText(accionesDisponibles.getSelectedIndex()));
	}
	
	public boolean isPropuesto() {
		return PROPUESTO.equals(accionesDisponibles.getItemText(accionesDisponibles.getSelectedIndex()));
	}
}
