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

public class EditorDatosDiscretaSerie extends Composite implements EditorDatosInterface {

	@UiField
	TablaDinamica tabla;

	private static EditorDatosDiscretaSerieUiBinder uiBinder = GWT.create(EditorDatosDiscretaSerieUiBinder.class);

	interface EditorDatosDiscretaSerieUiBinder extends UiBinder<Widget, EditorDatosDiscretaSerie> {
	}

	@UiHandler("tabla")
	public void tablaCambiada(ValueChangeEvent<FloatTextBox> evento) {
		ValueChangeEvent.fire(this, this);
	}

	public EditorDatosDiscretaSerie() {
		initWidget(uiBinder.createAndBindUi(this));
		tabla.setColumnas(new TablaDinamica.Columna[] {
			tabla.getNuevaColumna("Valor", TipoNumero.ENTERO)
		});
		tabla.addValidator(new TablaDinamicaValidator() {
			public boolean isValid(TablaDinamica tabla) {
				ArrayList<Float> valores = new ArrayList<Float>();
				for(int i = 0; i < tabla.getNumeroRegistros();i++) {
					float actual = tabla.getValue(i, 0);
					if(!valores.contains(actual)) {
						valores.add(actual);
					}
				}
				boolean out = valores.size() > 1;
				if(out) {
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

	public void actualizarPrecision(int pr) {
	}

	public boolean isValid() {
		return tabla.isValid();
	}

	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<EditorDatosInterface> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	public Datos getDatos() {
		if (isValid()) {
			Datos datos = new Datos();
			datos.setTipoVariable(TipoVariable.DISCRETA_SERIE);
			datos.setValores(getValores());
			return datos;
		} else {
			return null;
		}
	}

	private int[] getValores() {
		int[] out = new int[tabla.getNumeroRegistros()];
		for (int i = 0; i < out.length; i++) {
			out[i] = (int) tabla.getValue(i, 0);
		}
		return out;
	}

	public void setValores(float[] valores) {
		tabla.setValores(valores, 0);
	}
	
	public void limpiar() {
		tabla.eliminarTodos(false);
	}
}
