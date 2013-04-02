package org.ugr.rtpstat.client.uibinder.problemas.datos;

import java.util.ArrayList;

import org.ugr.rtpstat.client.orm.Datos;
import org.ugr.rtpstat.client.orm.Datos.TipoVariable;
import org.ugr.rtpstat.client.uibinder.FloatTextBox;
import org.ugr.rtpstat.client.uibinder.TablaDinamica;
import org.ugr.rtpstat.client.uibinder.TablaDinamicaValidator;
import org.ugr.rtpstat.client.uibinder.NumberTextBox.TipoNumero;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class EditorDatosContinuaTabla extends Composite implements
		EditorDatosInterface {
	@UiField
	TablaDinamica tabla;

	private int precision;
	
	private static EditorContinuaTablaUiBinder uiBinder = GWT
			.create(EditorContinuaTablaUiBinder.class);

	interface EditorContinuaTablaUiBinder extends
			UiBinder<Widget, EditorDatosContinuaTabla> {
	}
	
	@UiHandler("tabla")
	public void tablaCambiada(ValueChangeEvent<FloatTextBox> evento) {
		ValueChangeEvent.fire(this, this);
	}

	public EditorDatosContinuaTabla() {
		initWidget(uiBinder.createAndBindUi(this));
		tabla.setColumnas(new TablaDinamica.Columna[]{
			tabla.getNuevaColumna("Extremo inferior", TipoNumero.FLOTANTE),
			tabla.getNuevaColumna("Extremo superior", TipoNumero.FLOTANTE),
			tabla.getNuevaColumna("Frecuencia absoluta", TipoNumero.NATURAL)
		});
		tabla.setColumnaEditable(1, false, true);
		tabla.addValidator(new TablaDinamicaValidator() {
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
					if(fila < inferiores.size()-1) {
						if(superiores.get(fila) != inferiores.get(fila+1)) {
							//setInvalido(fila,1,"Exte extremo superior tiene que ser igual al extremo inferior del siguiente intervalo")
							tabla.setValue(fila,1,inferiores.get(fila+1));
						}
					}
				}
				return out;
			}
		});
		tabla.eliminarTodos(false);
	}

	public void actualizarPrecision(int pr) {
		this.precision = pr;
		tabla.setPrecision(pr);
		
	}

	protected void validarExtremos() {
		// TODO validarExtremos
	}

	public void valueChanged(FloatTextBox box) {
		validarExtremos();
	}

	public boolean isValid() {
		return tabla.isValid();
	}

	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<EditorDatosInterface> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	public Datos getDatos() {
		if(isValid()) {
			Datos datos = new Datos();
			datos.setDecimales(precision);
			datos.setTipoVariable(TipoVariable.CONTINUA_TABLA);
			datos.setExtremosInferiores(getExtremosInferiores());
			datos.setExtremosSuperiores(getExtremosSuperiores());
			datos.setFrecuenciasAbsolutas(getFrecuenciasAbsolutas());
			return datos;
		} else {
			return null;
		}
	}

	private float[] getFrecuenciasAbsolutas() {
		float [] out = new float[tabla.getNumeroRegistros()];
		for(int i = 0; i < out.length; i++) {
			out[i] = tabla.getValue(i,2);
		}
		return out;
	}

	private float[]getExtremosSuperiores() {
		float [] out = new float[tabla.getNumeroRegistros()];
		for(int i = 0; i < out.length; i++) {
			out[i] = tabla.getValue(i,1);
		}
		return out;
	}

	private float[] getExtremosInferiores() {
		float [] out = new float[tabla.getNumeroRegistros()];
		for(int i = 0; i < out.length; i++) {
			out[i] = tabla.getValue(i,0);
		}
		return out;
	}

	public void setValores(float[] extremosInferiores, float[] extremosSuperiores, float[] frecuenciasAbsolutas) {
		tabla.setValores(extremosInferiores, 0);
		tabla.setValores(extremosSuperiores, 1);
		tabla.setValores(frecuenciasAbsolutas, 2);
	}

	public void limpiar() {
		tabla.eliminarTodos(false);
	}
}
