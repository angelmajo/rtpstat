package org.ugr.rtpstat.client.uibinder.relaciones;

import java.io.IOException;
import java.util.HashMap;

import org.ugr.rtpstat.client.RtpstatService;
import org.ugr.rtpstat.client.RtpstatServiceAsync;
import org.ugr.rtpstat.client.orm.GradoDificultad;
import org.ugr.rtpstat.client.orm.ResultadoGeneracionRelacionEjercicios;
import org.ugr.rtpstat.client.orm.TipoRelacion;
import org.ugr.rtpstat.client.uibinder.principal.NotificadorGeneral;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class GeneradorRelacionEjercicios extends Composite {

	private final RtpstatServiceAsync rtpstatService = GWT.create(RtpstatService.class);

	private static final int MIN_CARACTERES_TITULO = 3;

	private static GeneradorRelacionEjerciciossUiBinder uiBinder = GWT.create(GeneradorRelacionEjerciciossUiBinder.class);

	interface GeneradorRelacionEjerciciossUiBinder extends UiBinder<Widget, GeneradorRelacionEjercicios> {
	}

	private boolean valid;

	private NotificadorGeneral notificadorGeneral;

	public GeneradorRelacionEjercicios(NotificadorGeneral notificadorGeneral) {
		this.notificadorGeneral = notificadorGeneral;
		this.nuevaRelacionAutomatica = new NuevaRelacionAutomatica(notificadorGeneral);
		initWidget(uiBinder.createAndBindUi(this));
		limpiar();
	}

	public boolean isValid() {
		return valid;
	}

	public void limpiar() {
		titulo.setText("");
		rellenarListBoxTipo();
		generarAutomaticamente.setValue(false);
		noGenerarAutomaticamente.setValue(false);
		validar();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				titulo.setFocus(true);
			}
		});

	}

	@UiHandler("titulo")
	public void cambioTitulo(ChangeEvent e) {
		validar();
	}

	@UiHandler("titulo")
	public void cambioTitulo(KeyUpEvent e) {
		validar();
	}

	@UiHandler("tipo")
	public void cambioTipo(ChangeEvent e) {
		eliminarTipoVacio();
		validar();
	}

	@UiHandler("tipo")
	public void cambioTipo(KeyUpEvent e) {
		eliminarTipoVacio();
		validar();
	}

	@UiHandler("generarAutomaticamente")
	public void generarAutomaticamente(ValueChangeEvent<Boolean> e) {
		validar();
	}

	@UiHandler("noGenerarAutomaticamente")
	public void noGenerarAutomaticamente(ValueChangeEvent<Boolean> e) {
		validar();
	}

	@UiHandler("nuevaRelacionAutomatica")
	public void nuevaRelacionAutomatica(ValueChangeEvent<NuevaRelacionAutomatica> e) {
		validar();
	}

	@UiHandler("generar_relacion")
	public void generarRelacionAutomatica(ClickEvent e) {
		String titulo = this.titulo.getText();
		HashMap<GradoDificultad, Integer> problemas = nuevaRelacionAutomatica.getProblemas();
		String[] areas = nuevaRelacionAutomatica.getAreasSeleccionadas();
		try {
			rtpstatService.addRelacion(titulo, problemas, areas, TipoRelacion.valueOf(this.tipo.getValue(this.tipo.getSelectedIndex())),
					new AsyncCallback<ResultadoGeneracionRelacionEjercicios>() {
						public void onFailure(Throwable caught) {
							notificadorGeneral.warning("Ha fallado la comunicación con el servidor, vuelva a cargar");
						}

						public void onSuccess(ResultadoGeneracionRelacionEjercicios result) {
							String mensaje = "<table width=\"100%\">";
							mensaje += "<thead><tr><th colspan=2>Generada:</th></tr></thead><tbody>";
							for (GradoDificultad g : GradoDificultad.values()) {
								if (result.getNumProblemas(g) > 0) {
									mensaje += "<tr><td>" + g.comoString() + "</td><td>" + result.getNumProblemas(g) + "</td></tr>";
								}
							}
							mensaje += "</tbody></table>";
							notificadorGeneral.info(mensaje);
							History.newItem("editar_relacion_nueva_" + result.getIdRelacion());
						}
					});
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public void crearRelacionVacia() {
		String titulo = this.titulo.getText();
		try {
			rtpstatService.addRelacion(titulo, TipoRelacion.valueOf(this.tipo.getValue(this.tipo.getSelectedIndex())),new AsyncCallback<ResultadoGeneracionRelacionEjercicios>() {
				public void onFailure(Throwable caught) {
					notificadorGeneral.warning("Ha fallado la comunicación con el servidor, vuelva a cargar");
				}

				public void onSuccess(ResultadoGeneracionRelacionEjercicios result) {
					String mensaje = "Relación creada";
					notificadorGeneral.info(mensaje);
					History.newItem("editar_relacion_nueva_" + result.getIdRelacion());
				}
			});
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void rellenarListBoxTipo() {
		tipo.clear();
		tipo.addItem("");
		for (TipoRelacion t : TipoRelacion.values()) {
			tipo.addItem(t.toLongString(), t.toString());
		}
	}

	private void validar() {
		setValid(true);
		if (titulo.getText().length() <= MIN_CARACTERES_TITULO) {
			titulo.addStyleName("campoinvalido");
			setValid(false);
		} else {
			titulo.removeStyleName("campoinvalido");
		}

		tipo.getParent().setVisible(isValid());

		if ("".equals(tipo.getValue(tipo.getSelectedIndex()))) {
			tipo.addStyleName("campoinvalido");
			setValid(false);
		} else {
			tipo.removeStyleName("campoinvalido");
		}

		generarAutomaticamente.getParent().getParent().setVisible(isValid());

		if (!generarAutomaticamente.getValue() && !noGenerarAutomaticamente.getValue()) {
			generarAutomaticamente.getParent().addStyleName("campoinvalido");
			setValid(false);
		} else {
			generarAutomaticamente.getParent().removeStyleName("campoinvalido");
		}

		if (isValid()) { //Una vez elegido el tipo ya no se puede cambiar
			tipo.getParent().setVisible(false);
			generarAutomaticamente.getParent().getParent().setVisible(false);
		}
		boolean generarRelacionAutomaticamente = isValid() && generarAutomaticamente.getValue();
		boolean noGenerarRelacionAutomaticamente = isValid() && noGenerarAutomaticamente.getValue();

		titulo.setReadOnly(generarRelacionAutomaticamente || noGenerarRelacionAutomaticamente);
		nuevaRelacionAutomatica.setVisible(generarRelacionAutomaticamente);
		
		if (generarRelacionAutomaticamente) {
			if (!nuevaRelacionAutomatica.isValid()) {
				setValid(false);
			}
			generar_relacion.setVisible(isValid());
		} else if(noGenerarRelacionAutomaticamente) {
			crearRelacionVacia();
		}

		//guardar_relacion.setVisible(isValid());
	}

	private void setValid(boolean valid) {
		this.valid = valid;
	}

	private void eliminarTipoVacio() {
		if (tipo.getSelectedIndex() != 0 && "".equals(tipo.getItemText(0))) {
			tipo.removeItem(0);
		}
	}

	@UiField
	TextBox titulo;

	@UiField
	ListBox tipo;

	@UiField
	Button generar_relacion;

	@UiField
	RadioButton generarAutomaticamente;

	@UiField
	RadioButton noGenerarAutomaticamente;

	@UiField(provided = true)
	NuevaRelacionAutomatica nuevaRelacionAutomatica;
}
