package org.ugr.rtpstat.client.uibinder.registro;

import org.ugr.rtpstat.client.RtpstatService;
import org.ugr.rtpstat.client.RtpstatServiceAsync;
import org.ugr.rtpstat.client.imagenes.Imagenes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class FormularioRegistro extends Composite {
	private final RtpstatServiceAsync rtpstatService = GWT.create(RtpstatService.class);
	private static FormularioRegistroUiBinder uiBinder = GWT.create(FormularioRegistroUiBinder.class);

	interface FormularioRegistroUiBinder extends UiBinder<Widget, FormularioRegistro> {
	}

	public FormularioRegistro() {

		final MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
		rtpstatService.listarEntidades(new AsyncCallback<String[]>() {
			public void onSuccess(String[] result) {
				for (String r : result) {
					oracle.add(r);
				}
			}

			public void onFailure(Throwable caught) {
			}
		});
		institucion = new SuggestBox(oracle);

		institucion.addSelectionHandler(new SelectionHandler<Suggestion>() {
			public void onSelection(SelectionEvent<Suggestion> event) {
				validarCamposNoVacios();
			}
		});

		nombreObligatorio = new Image(Imagenes.INSTANCE.warningRedIcon());
		institucionObligatoria = new Image(Imagenes.INSTANCE.warningRedIcon());

		initWidget(uiBinder.createAndBindUi(this));
	}

	private boolean validarCamposNoVacios() {
		boolean todoCorrecto = true;
		//El nombre real
		String nombreReal = nombre_real.getText();

		if (nombreReal.length() < 3) {
			todoCorrecto = false;
			nombreObligatorio.setVisible(true);
		} else {
			nombreObligatorio.setVisible(false);
		}

		String nombreInstitucion = institucion.getText();

		if (nombreInstitucion.length() < 3) {
			todoCorrecto = false;
			institucionObligatoria.setVisible(true);
		} else {
			institucionObligatoria.setVisible(false);
		}

		//La institución

		boton_registrar.setEnabled(todoCorrecto);
		return todoCorrecto;
	}

	@UiField(provided = true)
	Image nombreObligatorio;

	@UiHandler("nombreObligatorio")
	void clickNombreObligatorio(ClickEvent ev) {
		DialogoCampoObligatorio dialog = new DialogoCampoObligatorio("El nombre es obligatorio y tiene que tener al menos tres caracteres.");
		dialog.showRelativeTo(nombreObligatorio);
	}

	@UiField(provided = true)
	Image institucionObligatoria;

	@UiHandler("institucionObligatoria")
	void clickEntidadObligatoria(ClickEvent ev) {
		DialogoCampoObligatorio dialog = new DialogoCampoObligatorio("Tienes que seleccionar la entidad a la que perteneces.");
		dialog.showRelativeTo(institucionObligatoria);
	}

	@UiField
	TextBox nombre_real;

	@UiHandler("nombre_real")
	void nombreRealCambiado(ValueChangeEvent<String> ev) {
		validarCamposNoVacios();
	}

	@UiHandler("nombre_real")
	void nombreRealKeyup(KeyUpEvent ev) {
		validarCamposNoVacios();
	}

	@UiField(provided = true)
	SuggestBox institucion;

	@UiHandler("institucion")
	void institucionCambiado(ValueChangeEvent<String> ev) {
		validarCamposNoVacios();
	}

	@UiHandler("institucion")
	void institucionKeyup(KeyUpEvent ev) {
		validarCamposNoVacios();
	}

	@UiField
	Button boton_registrar;

	@UiHandler("boton_registrar")
	void manejarRegistro(ClickEvent ev) {
		if (validarCamposNoVacios()) {
			boton_registrar.setEnabled(false);
			String nombreReal = nombre_real.getText();
			String institucion = this.institucion.getText();
			rtpstatService.registrarUsuario(nombreReal, institucion, new AsyncCallback<Boolean>() {
				public void onSuccess(Boolean result) {
					if (result) {
						History.newItem("inicio");
					} else {
						Window.alert("El registro ha fallado, compruebe los datos y vuelva a intentarlo por favor");
					}
				}

				public void onFailure(Throwable caught) {
					Window.alert("Ha fallado la comunicación con el servidor, vuelva a intentarlo por favor");
				}
			});
		} else {
			Window.alert("Los campos no son validos!");
		}
	}

	@Override
	public void onAttach() {
		super.onAttach();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				nombre_real.setFocus(true);
			}
		});
	}
}
