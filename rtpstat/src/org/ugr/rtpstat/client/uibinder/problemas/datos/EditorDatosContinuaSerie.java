package org.ugr.rtpstat.client.uibinder.problemas.datos;

import java.util.ArrayList;
import java.util.Arrays;

import org.ugr.rtpstat.client.orm.Datos;
import org.ugr.rtpstat.client.orm.Datos.TipoAmplitudIntervalos;
import org.ugr.rtpstat.client.orm.Datos.TipoVariable;
import org.ugr.rtpstat.client.uibinder.FloatTextBox;
import org.ugr.rtpstat.client.uibinder.TablaDinamica;
import org.ugr.rtpstat.client.uibinder.TablaDinamicaValidator;
import org.ugr.rtpstat.client.uibinder.NumberTextBox.TipoNumero;
import org.ugr.rtpstat.client.uibinder.problemas.datos.EditorDatosContinuaSerieIntervalos.FallosValidacion;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

public class EditorDatosContinuaSerie extends Composite implements EditorDatosInterface {

	private static EditorDatosContinuaSerieUiBinder uiBinder = GWT.create(EditorDatosContinuaSerieUiBinder.class);

	interface EditorDatosContinuaSerieUiBinder extends UiBinder<Widget, EditorDatosContinuaSerie> {
	}

	@UiField
	TabPanel tab_panel;

	TablaDinamica datos = new TablaDinamica();
	
	EditorDatosContinuaSerieIntervalos intervalos = new EditorDatosContinuaSerieIntervalos();

	public EditorDatosContinuaSerie() {
		initWidget(uiBinder.createAndBindUi(this));
		datos.setMinRows(2);
		datos.setColumnas(new TablaDinamica.Columna[] {
			datos.getNuevaColumna("Valor", TipoNumero.FLOTANTE)
		});
		final EditorDatosContinuaSerie thisRef = this;
		datos.addValueChangeHandler(new ValueChangeHandler<FloatTextBox>() {
			public void onValueChange(ValueChangeEvent<FloatTextBox> event) {
				validar();
				ValueChangeEvent.fire(thisRef, thisRef);
			}
		});
		intervalos.addValueChangeHandler(new ValueChangeHandler<EditorDatosContinuaSerieIntervalos>() {
			public void onValueChange(ValueChangeEvent<EditorDatosContinuaSerieIntervalos> event) {
				validar();
				ValueChangeEvent.fire(thisRef, thisRef);
			}
		});
		tab_panel.add(datos, "Datos");
		tab_panel.add(intervalos, "Intervalos de clase");
		tab_panel.selectTab(0);

		datos.addValidator(new TablaDinamicaValidator() {
			public boolean isValid(TablaDinamica tabla) {
				ArrayList<Float> valores = new ArrayList<Float>();
				for (int i = 0; i < tabla.getNumeroRegistros(); i++) {
					float actual = tabla.getValue(i, 0);
					if (!valores.contains(actual)) {
						valores.add(actual);
					}
				}
				boolean out = valores.size() > 1;
				if (out) {
					tabla.removeStyleName("campoinvalido");
					tabla.setTitle("");
				} else {
					tabla.addStyleName("campoinvalido");
					tabla.setTitle("Se necesitan al menos dos valores DISTINTOS");
				}
				return out;
			}
		});
	}

	protected void validar() {
		if (intervalos.const_primero.isValid(true) && datos.isValid()) {
			ArrayList<Float> valoresList = datos.getValores(Float.class, 0);
			Float[] valores = valoresList.toArray(new Float[valoresList.size()]);
			Arrays.sort(valores);
			float primerValor = valores[0];
			float primerExtremo = Float.parseFloat(intervalos.const_primero.getText());
			if (primerValor <= primerExtremo) {
				intervalos.addError(FallosValidacion.PRIMERO_INVALIDO);
				intervalos.const_primero.addStyleName("campoinvalido");
				intervalos.const_primero.setInvalido("El primer extremo tiene que ser menor que el primero de los valores");
			} else {
				intervalos.removeError(FallosValidacion.PRIMERO_INVALIDO);
				intervalos.const_primero.removeStyleName("campoinvalido");
				intervalos.const_primero.setInvalido(null);
			}
		}
	}

	public void actualizarPrecision(int precision) {
		datos.setPrecision(precision);
		intervalos.setPrecision(precision);
	}

	public boolean isValid() {
		boolean datosValidos = datos.isValid();
		boolean intervalosValidos = intervalos.isValid();

		return datosValidos && intervalosValidos;
	}

	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<EditorDatosInterface> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	public Datos getDatos() {
		if (isValid()) {
			Datos out = new Datos();
			out.setTipoVariable(TipoVariable.CONTINUA_SERIE);
			switch (intervalos.getTipoAmplitud()) {
				case CONSTANTE:
					out.setTipoAmplitudIntervalos(TipoAmplitudIntervalos.CONSTANTE);
					out.setAmplitudIntervalos(intervalos.getAmplitud());
					out.setExtremosInferiores(new float[] {
						intervalos.getPrimerExtremo()
					});
					break;
				case VARIABLE:
					out.setTipoAmplitudIntervalos(TipoAmplitudIntervalos.VARIABLE);
					out.setExtremosInferiores(intervalos.getExtremosInferiores());
					out.setExtremosSuperiores(intervalos.getExtremosSuperiores());
					break;
			}
			out.setDecimales(datos.getPrecision());
			ArrayList<Float> valores = datos.getValores(Float.class, 0);
			//TODO esto es horrible, pero no tengo tiempo de hacer algo mas bonito
			//No puedo pasar directamente de ArrayList<Float> a float[]
			float[] valores2 = new float[valores.size()];
			for (int i = 0; i < valores.size(); i++) {
				valores2[i] = valores.get(i);
			}
			out.setValores(valores2);
			return out;
		} else {
			return null;
		}
	}

	public void setValores(float[] valores) {
		datos.setValores(valores, 0);
		validar();
	}

	public void setTipoAmplitudIntervalos(TipoAmplitudIntervalos tipoAmplitudIntervalos) {
		intervalos.setTipoAmplitudIntervalos(tipoAmplitudIntervalos);
		validar();
	}

	public void setAmplitudIntervalos(float amplitudIntervalos) {
		intervalos.setAmplitudIntervalos(amplitudIntervalos);
		validar();
	}

	public void setExtremos(float[] extremosInferiores, float[] extremosSuperiores) {
		intervalos.setExtremos(extremosInferiores, extremosSuperiores);
		validar();
	}

	public void limpiar() {
		datos.eliminarTodos(false);
		intervalos.limpiar();
		validar();
	}
}
