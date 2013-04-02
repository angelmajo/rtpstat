package org.ugr.rtpstat.client.uibinder.problemas.datos;

import java.util.ArrayList;

import org.ugr.rtpstat.client.orm.Datos.TipoAmplitudIntervalos;
import org.ugr.rtpstat.client.uibinder.FloatTextBox;
import org.ugr.rtpstat.client.uibinder.NumberTextBox;
import org.ugr.rtpstat.client.uibinder.TablaDinamica;
import org.ugr.rtpstat.client.uibinder.TablaDinamicaValidator;
import org.ugr.rtpstat.client.uibinder.NumberTextBox.TipoNumero;
import org.ugr.rtpstat.client.uibinder.problemas.EditorProblema;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class EditorDatosContinuaSerieIntervalos extends Composite implements HasValueChangeHandlers<EditorDatosContinuaSerieIntervalos> {

	private static EditorDatosContinuaSerieIntervalosUiBinder uiBinder = GWT.create(EditorDatosContinuaSerieIntervalosUiBinder.class);

	interface EditorDatosContinuaSerieIntervalosUiBinder extends UiBinder<Widget, EditorDatosContinuaSerieIntervalos> {
	}

	private ArrayList<FallosValidacion> fallosConstante = new ArrayList<FallosValidacion>();
	private ArrayList<FallosValidacion> fallosVariable = new ArrayList<FallosValidacion>();
	// @UiField
	// Grid extremos;

	// @UiField
	// Grid extremos_head;

	@UiField
	TablaDinamica tablaIntervalosVariables;

	@UiField
	RadioButton constante;

	@UiField
	RadioButton variable;

	@UiField
	VerticalPanel constante_panel;

	@UiField
	NumberTextBox const_amplitud;

	@UiField
	NumberTextBox const_primero;

	@UiHandler( {
			"constante", "variable"
	})
	public void valueChangeBoolean(ValueChangeEvent<Boolean> event) {
		revisarCambioTipoIntervalos();
		validar();
		ValueChangeEvent.fire(this, this);
	}

	@UiHandler( {
			"const_amplitud", "const_primero"
	})
	public void valuChangeString(ValueChangeEvent<String> event) {
		validar();
		ValueChangeEvent.fire(this, this);
	}

	@UiHandler( {
			"const_primero", "const_amplitud"
	})
	public void keyUps(KeyUpEvent event) {
		validar();
		ValueChangeEvent.fire(this, this);
	}

	@UiHandler("tablaIntervalosVariables")
	public void extremosChanged(ValueChangeEvent<FloatTextBox> event) {
		validar();
		ValueChangeEvent.fire(this, this);
	}

	public EditorDatosContinuaSerieIntervalos() {
		initWidget(uiBinder.createAndBindUi(this));

		tablaIntervalosVariables.setColumnas(new TablaDinamica.Columna[] {
				tablaIntervalosVariables.getNuevaColumna("Extremo inferior", TipoNumero.FLOTANTE),
				tablaIntervalosVariables.getNuevaColumna("Extremo superior", TipoNumero.FLOTANTE)
		});

		tablaIntervalosVariables.setColumnaEditable(0, true, true);
		tablaIntervalosVariables.setColumnaEditable(1, false, true);
		tablaIntervalosVariables.eliminarTodos(false);

		tablaIntervalosVariables.addValidator(new TablaDinamicaValidator() {
			public boolean isValid(TablaDinamica tabla) {
				boolean out = true;
				ArrayList<Float> inferiores = tabla.getValores(Float.class, 0);
				ArrayList<Float> superiores = tabla.getValores(Float.class, 1);
				for (int fila = 0; fila < inferiores.size(); fila++) {
					if (inferiores.get(fila) >= superiores.get(fila)) {
						tabla.setInvalido(fila, 0, "Este extremo inferior tiene que ser menor que su extremo superior asociado");
						out = false;
					} else {
						tabla.setValido(fila, 0);
					}
					if (fila < inferiores.size() - 1) {
						if (superiores.get(fila) != inferiores.get(fila + 1)) {
							//setInvalido(fila,1,"Exte extremo superior tiene que ser igual al extremo inferior del siguiente intervalo")
							tabla.setValue(fila, 1, inferiores.get(fila + 1));
						}
					}
				}
				return out;
			}
		});
		validar();
	}

	private void revisarCambioTipoIntervalos() {
		tablaIntervalosVariables.setVisible(false);
		constante_panel.setVisible(false);
		if (constante.getValue()) {
			constante_panel.setVisible(true);
			ValueChangeEvent.fire(this, this);
		} else if (variable.getValue()) {
			tablaIntervalosVariables.setVisible(true);
			ValueChangeEvent.fire(this, this);
		} else {
			//Acabamos de limpiar, no es necesario alertar a nadie
			//Window.alert("Existe un error en RTPSTAT, por favor contacte con el administrador e indique el código de error 0x0002. Gracias");
		}
	}

	public void setPrecision(int precision) {
		tablaIntervalosVariables.setPrecision(precision);
		const_amplitud.setPrecision(precision);
		const_primero.setPrecision(precision);
	}

	public boolean isValid() {
		if (constante.getValue()) {
			return fallosConstante.size() == 0;
		} else if (variable.getValue()) {
			return fallosVariable.size() == 0;
		} else {
			return false;
		}
	}

	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<EditorDatosContinuaSerieIntervalos> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	void validar() {
		if (constante.getValue()) {
			removeError(FallosValidacion.NINGUNA_OPCION_SELECCIONADA);
			constante.getParent().removeStyleName("campoinvalido");
			constante.getParent().setTitle("");
			if (const_amplitud.isValid(false)) {
				removeError(FallosValidacion.AMPLITUD_INVALIDO);
				if (Float.parseFloat(const_amplitud.getValue()) <= 0) {
					addError(FallosValidacion.AMPLITUD_INTERVALOS_DISTINTA_CERO);
					const_amplitud.addStyleName("campoinvalido");
					const_amplitud.setTitle("La amplitud de los intervalos tiene que ser positiva");
				} else {
					removeError(FallosValidacion.AMPLITUD_INTERVALOS_DISTINTA_CERO);
					const_amplitud.removeStyleName("campoinvalido");
					const_amplitud.setTitle("");
				}
			} else {
				addError(FallosValidacion.AMPLITUD_INVALIDO);
			}
			/*
			 * if (const_primero.isValid(true)) {
			 * removeError(FallosValidacion.PRIMERO_INVALIDO); Datos d =
			 * editorPadre.getDatos(); if (d != null) { float[] valores =
			 * d.getValores(); Arrays.sort(valores); float primerValor =
			 * valores[0]; float primerExtremo =
			 * Float.parseFloat(const_primero.getText()); if (primerValor <
			 * primerExtremo) { addError(FallosValidacion.PRIMERO_INVALIDO);
			 * const_primero.addStyleName("campoinvalido");
			 * const_primero.setInvalido(
			 * "El primer extremo tiene que ser menor que el primero de los valores"
			 * ); } else { removeError(FallosValidacion.PRIMERO_INVALIDO);
			 * const_primero.removeStyleName("campoinvalido");
			 * const_primero.setInvalido(null); } } else {
			 * Window.alert("D == null"); } } else {
			 * addError(FallosValidacion.PRIMERO_INVALIDO); }
			 */

		} else if (variable.getValue()) {
			constante.getParent().removeStyleName("campoinvalido");
			constante.getParent().setTitle("");
			removeError(FallosValidacion.NINGUNA_OPCION_SELECCIONADA);
			if (!tablaIntervalosVariables.isValid()) {
				addError(FallosValidacion.INTERVALOS_VARIABLES);
			} else {
				removeError(FallosValidacion.INTERVALOS_VARIABLES);
			}
		} else {
			addError(FallosValidacion.NINGUNA_OPCION_SELECCIONADA);
			constante.getParent().setTitle(EditorProblema.CAMPO_OBLIGATORIO);
			constante.getParent().addStyleName("campoinvalido");

		}
	}

	static enum FallosValidacion {
		NUM_INTERVALOS_MAYOR_CERO, AMPLITUD_INTERVALOS_DISTINTA_CERO, INTERVALOS_VARIABLES, NINGUNA_OPCION_SELECCIONADA, NUM_INTERVALOS_INVALIDO, AMPLITUD_INVALIDO, PRIMERO_INVALIDO
	}

	protected void addError(FallosValidacion fallo) {
		if (constante.getValue()) {
			if (!fallosConstante.contains(fallo)) {
				fallosConstante.add(fallo);
			}
		} else if (variable.getValue()) {
			if (!fallosVariable.contains(fallo)) {
				fallosVariable.add(fallo);
			}
		}
		revisarErrores();
	}

	protected void removeError(FallosValidacion fallo) {
		if (constante.getValue()) {
			fallosConstante.remove(fallo);
		} else if (variable.getValue()) {
			fallosVariable.remove(fallo);
		}
		revisarErrores();
	}

	private void revisarErrores() {

	}

	public TipoAmplitudIntervalos getTipoAmplitud() {
		if (isValid()) {
			if (constante.getValue()) {
				return TipoAmplitudIntervalos.CONSTANTE;
			} else {// Como es valido podemos asumir que variables esta marcado
				return TipoAmplitudIntervalos.VARIABLE;
			}
		} else {
			return null;
		}
	}

	public float getAmplitud() {
		return Float.parseFloat(const_amplitud.getText());
	}

	public float[] getExtremosInferiores() {
		float[] out = new float[tablaIntervalosVariables.getNumeroRegistros()];
		for (int i = 0; i < out.length; i++) {
			out[i] = tablaIntervalosVariables.getValue(i, 0);
		}
		return out;
	}

	public float[] getExtremosSuperiores() {
		float[] out = new float[tablaIntervalosVariables.getNumeroRegistros()];
		for (int i = 0; i < out.length; i++) {
			out[i] = tablaIntervalosVariables.getValue(i, 1);
		}
		return out;
	}

	public void setTipoAmplitudIntervalos(TipoAmplitudIntervalos tipoAmplitudIntervalos) {
		switch (tipoAmplitudIntervalos) {
			case CONSTANTE:
				constante.setValue(true);
				break;
			case VARIABLE:
				variable.setValue(true);
				break;
		}
		revisarCambioTipoIntervalos();
		validar();
	}

	public void setAmplitudIntervalos(float amplitudIntervalos) {
		const_amplitud.setText("" + amplitudIntervalos);
		validar();
	}

	public void setExtremos(float[] extremosInferiores, float[] extremosSuperiores) {
		tablaIntervalosVariables.setValores(extremosInferiores, 0);
		tablaIntervalosVariables.setValores(extremosSuperiores, 1);
		validar();
	}

	public void limpiar() {
		tablaIntervalosVariables.eliminarTodos(false);
		constante.setValue(false);
		variable.setValue(false);
		const_amplitud.setText("0");
		revisarCambioTipoIntervalos();
	}

	public float getPrimerExtremo() {
		return Float.parseFloat(const_primero.getText());
	}
}
