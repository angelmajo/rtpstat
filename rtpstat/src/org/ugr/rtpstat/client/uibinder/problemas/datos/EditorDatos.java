package org.ugr.rtpstat.client.uibinder.problemas.datos;

import org.ugr.rtpstat.client.orm.Datos;
import org.ugr.rtpstat.client.uibinder.problemas.EditorProblema;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class EditorDatos extends Composite implements HasValueChangeHandlers<EditorDatos> {
	private EditorDatosInterface editorActivo;

	private static EditorDatosUiBinder uiBinder = GWT.create(EditorDatosUiBinder.class);

	interface EditorDatosUiBinder extends UiBinder<Widget, EditorDatos> {
	}

	interface Estilos extends CssResource {
		String opcionseleccionada();
	}

	@UiField
	Estilos style;

	@UiField
	TextArea descripcion_variable;

	@UiField
	VerticalPanel detalles_tipo_variable;

	@UiField
	RadioButton tipo_continua;

	@UiField
	RadioButton tipo_discreta;

	@UiField
	RadioButton organizacion_serie;

	@UiField
	RadioButton organizacion_tabla;

	@UiField
	HTMLPanel precision_container;

	@UiField
	ListBox precision;

	@UiField
	EditorDatosContinuaSerie editor_continua_serie;

	@UiField
	EditorDatosContinuaTabla editor_continua_tabla;

	@UiField
	EditorDatosDiscretaSerie editor_discreta_serie;

	@UiField
	EditorDatosDiscretaTabla editor_discreta_tabla;

	@UiHandler("descripcion_variable")
	public void descripcionCambiada(ValueChangeEvent<String> evento) {
		validar();
		ValueChangeEvent.fire(this, this);
	}

	@UiHandler("descripcion_variable")
	public void descripcionCambiada(KeyUpEvent evento) {
		validar();
		ValueChangeEvent.fire(this, this);
	}

	@UiHandler("precision")
	protected void doChange(ChangeEvent event) {
		validar();
		actualizarPrecision();
		ValueChangeEvent.fire(this, this);
	}

	@UiHandler("editor_continua_serie")
	public void editorContinuaSerie(ValueChangeEvent<EditorDatosInterface> evento) {
		validar();
		ValueChangeEvent.fire(this, this);
	}

	@UiHandler("editor_continua_tabla")
	public void editorContinuaTabla(ValueChangeEvent<EditorDatosInterface> evento) {
		validar();
		ValueChangeEvent.fire(this, this);
	}

	@UiHandler("editor_discreta_serie")
	public void editorDiscretaSerie(ValueChangeEvent<EditorDatosInterface> evento) {
		validar();
		ValueChangeEvent.fire(this, this);
	}

	@UiHandler("editor_discreta_tabla")
	public void editorDiscretaTabla(ValueChangeEvent<EditorDatosInterface> evento) {
		validar();
		ValueChangeEvent.fire(this, this);
	}

	private void actualizarPrecision() {
		if (editorActivo != null) {
			int pr = Integer.parseInt(precision.getValue(precision.getSelectedIndex()));
			editorActivo.actualizarPrecision(pr);
		}
	}

	public EditorDatos() {
		initWidget(uiBinder.createAndBindUi(this));

		for (int i = 0; i <= 6; i++) {
			precision.addItem(i + (i == 1 ? " decimal" : " decimales"), "" + i);
		}
		final EditorDatos thisRef = this;
		ClickHandler cambioTipoVariableHandler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				validarCambioTipoVariable();
				ValueChangeEvent.fire(thisRef, thisRef);
			}
		};

		tipo_continua.addClickHandler(cambioTipoVariableHandler);
		tipo_discreta.addClickHandler(cambioTipoVariableHandler);
		organizacion_serie.addClickHandler(cambioTipoVariableHandler);
		organizacion_tabla.addClickHandler(cambioTipoVariableHandler);
	}

	public void limpiar() {
		descripcion_variable.setText("");
		tipo_continua.setValue(false);
		tipo_discreta.setValue(false);
		organizacion_serie.setValue(false);
		organizacion_tabla.setValue(false);
		
		editor_continua_serie.limpiar();
		editor_continua_tabla.limpiar();
		editor_discreta_serie.limpiar();
		editor_discreta_tabla.limpiar();
	}

	protected boolean validarCambioTipoVariable() {
		boolean out = false;
		tipo_continua.getParent().removeStyleName(style.opcionseleccionada());
		tipo_discreta.removeStyleName(style.opcionseleccionada());
		organizacion_serie.removeStyleName(style.opcionseleccionada());
		organizacion_tabla.removeStyleName(style.opcionseleccionada());

		editor_continua_serie.setVisible(false);
		editor_continua_tabla.setVisible(false);
		editor_discreta_serie.setVisible(false);
		editor_discreta_tabla.setVisible(false);

		if (tipo_continua.getValue()) {
			tipo_continua.getParent().addStyleName(style.opcionseleccionada());
			precision_container.setVisible(true);
			if (organizacion_serie.getValue()) {
				organizacion_serie.addStyleName(style.opcionseleccionada());
				editor_continua_serie.setVisible(true);
				editorActivo = editor_continua_serie;
				out = true;
			} else if (organizacion_tabla.getValue()) {
				organizacion_tabla.addStyleName(style.opcionseleccionada());
				editor_continua_tabla.setVisible(true);
				editorActivo = editor_continua_tabla;
				out = true;

			}
		} else if (tipo_discreta.getValue()) {
			tipo_discreta.addStyleName(style.opcionseleccionada());
			precision_container.setVisible(false);
			if (organizacion_serie.getValue()) {
				organizacion_serie.addStyleName(style.opcionseleccionada());
				editor_discreta_serie.setVisible(true);
				editorActivo = editor_discreta_serie;
				out = true;
			} else if (organizacion_tabla.getValue()) {
				organizacion_tabla.addStyleName(style.opcionseleccionada());
				editor_discreta_tabla.setVisible(true);
				editorActivo = editor_discreta_tabla;
				out = true;
			}
		}
		return out;
	}

	public boolean validar() {
		boolean valid = true;
		if (descripcion_variable.getText().length() == 0) {
			valid = false;
			descripcion_variable.addStyleName("campoinvalido");
			descripcion_variable.setTitle(EditorProblema.CAMPO_OBLIGATORIO);
		} else {
			descripcion_variable.removeStyleName("campoinvalido");
			descripcion_variable.setTitle("");
		}
		if (validarCambioTipoVariable() == false) {
			valid = false;
			detalles_tipo_variable.addStyleName("campoinvalido");
			detalles_tipo_variable.setTitle(EditorProblema.CAMPO_OBLIGATORIO);
		} else {
			detalles_tipo_variable.removeStyleName("campoinvalido");
			detalles_tipo_variable.setTitle("");
			if (!editorActivo.isValid()) {
				valid = false;
			}
		}
		return valid;
	}

	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<EditorDatos> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	public Datos getDatos() {
		Datos out = null;
		if (editorActivo != null && editorActivo.isValid()) {
			out = editorActivo.getDatos();
			out.setDescripcionVariable(descripcion_variable.getText());
		}
		return out;
	}

	public void setDatos(Datos datos) {
		if (datos == null) {
			Window.alert("datos es null (EditorDatos.setDatos)");
		} else {
			descripcion_variable.setText(datos.getDescripcionVariable());
			switch(datos.getTipoVariable()) {
				case CONTINUA_SERIE:
					precision.setSelectedIndex(datos.getDecimales());
					editorActivo = editor_continua_serie;
					actualizarPrecision();
					editor_continua_serie.setValores(datos.getValores());
					editor_continua_serie.setTipoAmplitudIntervalos(datos.getTipoAmplitudIntervalos());
					switch(datos.getTipoAmplitudIntervalos()) {
						case CONSTANTE:
							editor_continua_serie.setAmplitudIntervalos(datos.getAmplitudIntervalos());
							//FIXME Puro spaghetti
							editor_continua_serie.intervalos.const_primero.setText(""+datos.getExtremosInferiores()[0]);
							editor_continua_serie.validar();
							break;
						case VARIABLE:
							editor_continua_serie.setExtremos(datos.getExtremosInferiores(),datos.getExtremosSuperiores());
							break;
					}
					tipo_continua.setValue(true);
					organizacion_serie.setValue(true);
					actualizarPrecision();
					break;
				case CONTINUA_TABLA:
					precision.setSelectedIndex(datos.getDecimales());
					editorActivo = editor_continua_tabla;
					actualizarPrecision();
					editor_continua_tabla.setValores(datos.getExtremosInferiores(),datos.getExtremosSuperiores(),datos.getFrecuenciasAbsolutas());
					tipo_continua.setValue(true);
					organizacion_tabla.setValue(true);
					actualizarPrecision();
					break;
				case DISCRETA_SERIE:
					editor_discreta_serie.setValores(datos.getValores());
					tipo_discreta.setValue(true);
					organizacion_serie.setValue(true);
					break;
				case DISCRETA_TABLA:
					editor_discreta_tabla.setValores(datos.getValores(),datos.getFrecuenciasAbsolutas());
					tipo_discreta.setValue(true);
					organizacion_tabla.setValue(true);
					break;
			}
			;
		}
	}
}
