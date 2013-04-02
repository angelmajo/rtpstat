package org.ugr.rtpstat.client.uibinder.libros;

import org.ugr.rtpstat.client.Constantes;
import org.ugr.rtpstat.client.RtpstatService;
import org.ugr.rtpstat.client.RtpstatServiceAsync;
import org.ugr.rtpstat.client.imagenes.Imagenes;
import org.ugr.rtpstat.client.orm.EstadoGeneracion;
import org.ugr.rtpstat.client.orm.Libro;
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

public class LibroEnLista extends Composite {
	private final RtpstatServiceAsync rtpstatService = GWT.create(RtpstatService.class);
	private static LibroEnListaUiBinder uiBinder = GWT.create(LibroEnListaUiBinder.class);

	interface LibroEnListaUiBinder extends UiBinder<Widget, LibroEnLista> {
	}

	interface MiEstilo extends CssResource {

	}

	@UiField
	Label tipo;

	@UiField
	Label titulo;

	@UiField
	Label areas;

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

	private Libro libro;
	private NotificadorGeneral notificadorGeneral;

	@UiHandler("editar")
	public void editar(ClickEvent event) {
		History.newItem("editar_libro_" + libro.getId());
	}

	@UiHandler("borrar")
	public void borrar(ClickEvent event) {
		confirmarBorrado.setEnabled(true);
		dialogoBorrar.showRelativeTo(borrar);
	}
	
	@UiHandler("cancelarBorrado")
	public void cancelarBorrado(ClickEvent event) {
		dialogoBorrar.hide();
	}

	@UiHandler("confirmarBorrado")
	public void confirmarBorrado(ClickEvent event) {
		confirmarBorrado.setEnabled(false);
		rtpstatService.rmLibro(libro.getId(), new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				notificadorGeneral.warning("Ha ocurrido un error al intentar eliminar el libro");
				dialogoBorrar.hide();
			}

			@Override
			public void onSuccess(Void result) {
				dialogoBorrar.hide();
				notificadorGeneral.info("Libro " + libro.getId() + " eliminado");
				History.fireCurrentHistoryState();
			}
		});
	}

	public LibroEnLista(Libro libro, NotificadorGeneral notificadorGeneral) {
		this.notificadorGeneral = notificadorGeneral;
		this.libro = libro;
		this.editar = new PushButton(new Image(Imagenes.INSTANCE.editarIcon()));
		this.descargar = new PushButton(new Image(Imagenes.INSTANCE.downloadIcon()));
		this.detallesError = new PushButton(new Image(Imagenes.INSTANCE.warningRedIcon()));
		this.iconoGenerando = new PushButton(new Image(Imagenes.INSTANCE.ajaxLoaderCuadrado24()));
		this.borrar = new PushButton(new Image(Imagenes.INSTANCE.eliminarIcon()));
		initWidget(uiBinder.createAndBindUi(this));
		comprobarEstadoGeneracion();
		titulo.setText(libro.getTitulo());

		int numCapitulos = libro.getCapitulos().size();
		tipo.setText(numCapitulos + " capitulo" + (numCapitulos == 1 ? "" : "s"));

		String areasConcatenadas = "";
		if (libro.getAreasObjetivo().length == 0) {
			areasConcatenadas = "Sin áreas objetivo";
		} else {
			String[] areas = libro.getAreasObjetivo();
			for (int i = 0; i < areas.length; i++) {
				String area = areas[i];
				areasConcatenadas += area;
				if (i < areas.length - 1) {
					areasConcatenadas += ", ";
				}
			}
		}
		areas.setText(areasConcatenadas);

	}

	@UiHandler("descargar")
	public void descargar(ClickEvent event) {
		String incompleta = "/rtpstat/download?id=" + libro.getId() + "&tipo=" + Constantes.TIPO_DESCARGA_LIBRO;// TODO
																												// TIPO_LIBRO
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

	private Timer comprobarEstadoTimer = new Timer() {
		@Override
		public void run() {
			comprobarEstadoGeneracion();
		}
	};

	private String mensajeError;

	private void comprobarEstadoGeneracion() {
		iconoGenerando.setVisible(true);
		detallesError.setVisible(false);
		descargar.setVisible(false);
		rtpstatService.comprobarEstadoGeneracionLibro(libro.getId(), new AsyncCallback<EstadoGeneracion>() {

			@Override
			public void onSuccess(EstadoGeneracion result) {
				switch (result) {
				case ERROR:
				case IOERROR:
					rtpstatService.getCausarErrorLibro(libro.getId(), new AsyncCallback<String>() {
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
}
