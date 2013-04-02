package org.ugr.rtpstat.client.uibinder.relaciones;

import org.ugr.rtpstat.client.orm.ResumenProblema;
import org.ugr.rtpstat.client.uibinder.principal.NotificadorGeneral;
import org.ugr.rtpstat.client.uibinder.problemas.ProblemaEnListado;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.ListBox;

public class ProblemaEnRelacion extends ProblemaEnListado implements HasValueChangeHandlers<Boolean> {
	private static final String NO = "No";
	private static final String SI = "Sí";
	protected ListBox accionesDisponibles = new ListBox();

	public ProblemaEnRelacion(boolean incluido, ResumenProblema p, NotificadorGeneral notificadorGeneral) {
		super(p, notificadorGeneral, false);
		comprobarEstadoTimer = null;
		eliminarProblema.setVisible(false);
		descargarDocumento.setVisible(false);
		editarProblema.setVisible(false);
		duplicarProblema.setVisible(false);

		accionesDisponibles.addItem(SI);
		accionesDisponibles.addItem(NO);
		accionesDisponibles.setSelectedIndex(incluido ? 0 : 1);
		final HasValueChangeHandlers<Boolean> thisRef = this;
		accionesDisponibles.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				switch (accionesDisponibles.getSelectedIndex()) {
				case 0:
					ValueChangeEvent.fire(thisRef, true);
					break;
				case 1:
					ValueChangeEvent.fire(thisRef, false);
					break;
				default:
					throw new IllegalStateException("Algún desarrollador ha hecho cambios sin adaptar el resto del código.");
				}
			}
		});

		panel.insert(accionesDisponibles, 0);

		panel.setCellWidth(descripcion, "100%");
	}

	public boolean isIncluido() {
		return SI.equals(accionesDisponibles.getItemText(accionesDisponibles.getSelectedIndex()));
	}

	public void setIncluido(boolean b) {
		accionesDisponibles.setSelectedIndex(b ? 0 : 1);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Boolean> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}
}
