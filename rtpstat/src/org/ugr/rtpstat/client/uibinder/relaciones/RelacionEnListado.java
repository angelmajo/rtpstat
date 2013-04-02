package org.ugr.rtpstat.client.uibinder.relaciones;

import org.ugr.rtpstat.client.Constantes;
import org.ugr.rtpstat.client.RtpstatService;
import org.ugr.rtpstat.client.RtpstatServiceAsync;
import org.ugr.rtpstat.client.imagenes.Imagenes;
import org.ugr.rtpstat.client.orm.EstadoGeneracion;
import org.ugr.rtpstat.client.orm.ResumenRelacionEjercicios;
import org.ugr.rtpstat.client.uibinder.principal.NotificadorGeneral;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

public class RelacionEnListado extends Composite {
	private final RtpstatServiceAsync rtpstatService = GWT.create(RtpstatService.class);
	private ResumenRelacionEjercicios resumenRelacionEjercicios;

	private static RelacionEnListadoUiBinder uiBinder = GWT.create(RelacionEnListadoUiBinder.class);

	interface RelacionEnListadoUiBinder extends UiBinder<Widget, RelacionEnListado> {
	}

	interface MiEstilo extends CssResource {
		String propuestos();

		String resueltos();
	}

	@UiField
	MiEstilo style;

	@UiField
	Label titulo;

	@UiField
	Label tipo;

	@UiField(provided = true)
	PushButton editar;

	@UiField(provided = true)
	PushButton descargar;

	@UiField(provided = true)
	PushButton detallesError;

	@UiField(provided = true)
	PushButton iconoGenerando;

	@UiField(provided = true)
	PushButton borrar;

	@UiField
	Button confirmarBorrado;
	
	@UiField
	DialogBox dialogoBorrar;

	@UiField
	Label areas;
	private NotificadorGeneral notificadorGeneral;

	@UiHandler("cancelarBorrado")
	public void cancelarBorrado(ClickEvent event) {
		dialogoBorrar.hide();
	}

	@UiHandler("confirmarBorrado")
	public void confirmarBorrado(ClickEvent event) {
		confirmarBorrado.setEnabled(false);
		rtpstatService.rmRelacion(resumenRelacionEjercicios.getId(), new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				notificadorGeneral.warning("Ha ocurrido un error al intentar eliminar la relación de problemas");
				dialogoBorrar.hide();
			}

			@Override
			public void onSuccess(Void result) {
				dialogoBorrar.hide();
				notificadorGeneral.info("Relación de problemas " + resumenRelacionEjercicios.getId() + " eliminada");
				History.fireCurrentHistoryState();
			}
		});
	}

	@UiHandler("borrar")
	public void borrar(ClickEvent event) {
		confirmarBorrado.setEnabled(true);
		dialogoBorrar.showRelativeTo(borrar);
	}

	@UiHandler("editar")
	public void editar(ClickEvent event) {
		History.newItem("editar_relacion_" + resumenRelacionEjercicios.getId());
	}

	@UiHandler("descargar")
	public void descargar(ClickEvent event) {
		String incompleta = "/rtpstat/download?id=" + resumenRelacionEjercicios.getId() + "&tipo=" + Constantes.TIPO_DESCARGA_RELACION;
		Window.open(incompleta, "_blank", "");
	}

	@UiHandler("detallesError")
	public void mostarError(ClickEvent event) {
		DialogBox dialogo = new DialogBox(true);
		dialogo.setGlassEnabled(true);
		dialogo.setAnimationEnabled(true);
		dialogo.setText("Haz click fuera del dialogo para cerrarlo");
		dialogo.add(new HTML(mensajeError));
		dialogo.showRelativeTo(detallesError);
	}

	public RelacionEnListado(ResumenRelacionEjercicios resumenRelacionEjercicios, NotificadorGeneral notificadorGeneral) {
		this.notificadorGeneral = notificadorGeneral;
		this.editar = new PushButton(new Image(Imagenes.INSTANCE.editarIcon()));
		this.descargar = new PushButton(new Image(Imagenes.INSTANCE.downloadIcon()));
		this.detallesError = new PushButton(new Image(Imagenes.INSTANCE.warningRedIcon()));
		this.borrar = new PushButton(new Image(Imagenes.INSTANCE.eliminarIcon()));
		this.iconoGenerando = new PushButton(new Image(Imagenes.INSTANCE.ajaxLoaderCuadrado24()));
		this.iconoGenerando.setEnabled(false);
		initWidget(uiBinder.createAndBindUi(this));

		this.setResumenRelacionEjercicios(resumenRelacionEjercicios);
	}

	public void setResumenRelacionEjercicios(ResumenRelacionEjercicios resumenRelacionEjercicios) {
		if (resumenRelacionEjercicios == null) {
			throw new NullPointerException("El resumen de la relación de problemas no puede ser null");
		}
		this.resumenRelacionEjercicios = resumenRelacionEjercicios;
		titulo.setText(resumenRelacionEjercicios.getTitulo());

		tipo.setText(resumenRelacionEjercicios.getTipo().toLongString() + " (" + resumenRelacionEjercicios.getNumeroEjercicios() + ")");
		tipo.removeStyleName(style.propuestos());
		tipo.removeStyleName(style.resueltos());
		switch (resumenRelacionEjercicios.getTipo()) {
		case Resueltos:
			tipo.addStyleName(style.resueltos());
			break;
		case Propuestos:
			tipo.addStyleName(style.propuestos());
			break;
		}

		String areasConcatenadas = "";
		if (resumenRelacionEjercicios.getAreasObjetivo().length == 0) {
			areasConcatenadas = "Sin áreas objetivo";
		} else {
			String[] areas = resumenRelacionEjercicios.getAreasObjetivo();
			for (int i = 0; i < areas.length; i++) {
				String area = areas[i];
				areasConcatenadas += area;
				if (i < areas.length - 1) {
					areasConcatenadas += ", ";
				}
			}
		}
		areas.setText(areasConcatenadas);

		// Estado de generación
		if (resumenRelacionEjercicios.isGenerada()) {
			descargar.setVisible(true);
		} else {
			iconoGenerando.setVisible(true);
			comprobarEstadoGeneracion();
		}
	}

	private Timer comprobarEstadoTimer = new Timer() {
		@Override
		public void run() {
			comprobarEstadoGeneracion();
		}
	};
	private String mensajeError;

	private void comprobarEstadoGeneracion() {
		rtpstatService.comprobarEstadoGeneracionRelacion(resumenRelacionEjercicios.getId(), new AsyncCallback<EstadoGeneracion>() {

			@Override
			public void onSuccess(EstadoGeneracion result) {
				switch (result) {
				case ERROR:
				case IOERROR:
					rtpstatService.getCausarErrorRelacion(resumenRelacionEjercicios.getId(), new AsyncCallback<String>() {
						@Override
						public void onFailure(Throwable caught) {
							mensajeError = "Ha fallado la comunicación con el servidor";
							detallesError.setVisible(true);
							iconoGenerando.setVisible(false);
						}

						@Override
						public void onSuccess(String result) {
							mensajeError = result;
							detallesError.setVisible(true);
							iconoGenerando.setVisible(false);

						}
					});
					break;
				case OK:
					descargar.setVisible(true);
					iconoGenerando.setVisible(false);
					break;
				case GENERANDO:
				case DESCONOCIDO:
					comprobarEstadoTimer.schedule(3000);
					break;
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				iconoGenerando.setVisible(false);
				// TODO Mostrar errores al pulsar detallesError
				mensajeError = "Error de comunicación con el servidor";
				detallesError.setVisible(true);
			}
		});
	}

	public ResumenRelacionEjercicios getResumenRelacionEjercicios() {
		return resumenRelacionEjercicios;
	}

}
