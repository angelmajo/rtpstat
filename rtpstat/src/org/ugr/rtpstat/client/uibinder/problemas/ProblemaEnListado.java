package org.ugr.rtpstat.client.uibinder.problemas;

import org.ugr.rtpstat.client.Constantes;
import org.ugr.rtpstat.client.RtpstatService;
import org.ugr.rtpstat.client.RtpstatServiceAsync;
import org.ugr.rtpstat.client.events.EventBus;
import org.ugr.rtpstat.client.events.ListaProblemaCambiadaEvent;
import org.ugr.rtpstat.client.imagenes.Imagenes;
import org.ugr.rtpstat.client.orm.EstadoGeneracion;
import org.ugr.rtpstat.client.orm.GradoDificultad;
import org.ugr.rtpstat.client.orm.ResumenProblema;
import org.ugr.rtpstat.client.uibinder.principal.NotificadorGeneral;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ProblemaEnListado extends Composite {
	private boolean botonesHabilitados;

	public ProblemaEnListado(ResumenProblema p, NotificadorGeneral notificadorGeneral) {
		this(p, notificadorGeneral, true);
	}

	protected ProblemaEnListado(ResumenProblema p, NotificadorGeneral notificadorGeneral, boolean botonesHabilitados) {
		this.botonesHabilitados = botonesHabilitados;
		this.notificadorGeneral = notificadorGeneral;
		this.editarProblema = new PushButton(new Image(Imagenes.INSTANCE.editarIcon()));
		this.duplicarProblema = new PushButton(new Image(Imagenes.INSTANCE.duplicarProblema()));
		this.eliminarProblema = new PushButton(new Image(Imagenes.INSTANCE.eliminarIcon()));
		this.descargarDocumento = new PushButton(new Image(Imagenes.INSTANCE.downloadIcon()));

		this.detallesError = new PushButton(new Image(Imagenes.INSTANCE.warningRedIcon()));
		this.iconoGenerando = new PushButton(new Image(Imagenes.INSTANCE
				.ajaxLoaderCuadrado24()));
		this.iconoGenerando.setVisible(true);
		this.iconoEstadoDesconocido = new PushButton(new Image(Imagenes.INSTANCE.questionMark24()));

		dialogoDetallesError.setGlassEnabled(true);
		dialogoDetallesError.setAnimationEnabled(true);
		dialogoDetallesError.setText("Imposible descargar este problema");

		initWidget(uiBinder.createAndBindUi(this));
		if (botonesHabilitados) {
			setEstado(EstadoGeneracion.DESCONOCIDO);
		}
		this.setProblema(p);

	}

	private class DialogDuplicacion extends DialogBox {
		TextBox descripcion = new TextBox();

		public DialogDuplicacion() {
			setAnimationEnabled(true);
			setGlassEnabled(true);
			setText("Indica el nombre del nuevo problema");

			VerticalPanel panel = new VerticalPanel();
			panel.setWidth("100%");
			panel.add(descripcion);
			Button ok = new Button("Duplicar");
			Anchor cancelar = new Anchor("Cancelar");

			HorizontalPanel controles = new HorizontalPanel();
			controles.setWidth("100%");
			controles.add(cancelar);
			controles.add(ok);

			controles.setCellHorizontalAlignment(ok, HasHorizontalAlignment.ALIGN_RIGHT);

			panel.add(controles);
			add(panel);

			descripcion.setWidth("30em");
			descripcion.setText(problema.getDescripcion());
			descripcion.setSelectionRange(0, descripcion.getText().length());

			ok.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					hide();
					duplicar(descripcion.getText());
				}
			});

			cancelar.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					hide();
				}
			});
		}

		public void duplicar(String nuevaDescripcion) {
			rtpstatService.duplicarProblema(problema.getId(), nuevaDescripcion, new AsyncCallback<Long>() {
				public void onFailure(Throwable caught) {
					notificadorGeneral.warning("No se ha podido duplicar el problema " + problema.getId() + "<br>" + caught.getMessage());
				}

				public void onSuccess(Long result) {
					notificadorGeneral.info("Problema " + problema.getId() + " duplicado");
					EventBus.getInstance().fireEvent(new ListaProblemaCambiadaEvent());
				}
			});
		}
	}

	@UiHandler("duplicarProblema")
	public void duplicarProblema(ClickEvent event) {
		new DialogDuplicacion().showRelativeTo(duplicarProblema);
	}

	@UiHandler("detallesError")
	public void detallesErrorPulsado(ClickEvent event) {
		dialogoDetallesError.showRelativeTo(detallesError);
	}

	@UiHandler("editarProblema")
	public void editarProblemaPulsado(ClickEvent event) {
		History.newItem("editar_problema-" + getProblema().getId());
	}

	@UiHandler("eliminarProblema")
	public void eliminarProblemaPulsado(ClickEvent event) {
		DialogoEliminarProblema dialogoEliminar = new DialogoEliminarProblema(this,notificadorGeneral);
		dialogoEliminar.center();
	}

	@UiHandler("descargarDocumento")
	public void clickDescargarDocumento(ClickEvent event) {
		VerticalPanel panel = new VerticalPanel();
		panel.add(new Anchor("Presentación", false, urlPresentacion, "_blank"));
		panel.add(new Anchor("Problema en A4", false, urlDocumento, "_blank"));

		DialogBox dialogo = new DialogBox(true);
		dialogo.setText("Elije un formato:");
		dialogo.setGlassEnabled(true);
		dialogo.setAnimationEnabled(true);
		dialogo.add(panel);
		dialogo.showRelativeTo(descargarDocumento);
	}

	public void setProblema(ResumenProblema problema) {
		this.problema = problema;
		if (botonesHabilitados && problema.isDocumentosGenerados()) {
			setEstado(EstadoGeneracion.OK);
		}
		descripcion.setText(problema.getDescripcion());
		dificultad.setText(problema.getDificultad().comoString());
		setEstiloDificultad(problema.getDificultad());
		areas.setText(problema.getAreasConcatenadas());
	}

	private void setEstiloDificultad(GradoDificultad grado) {
		dificultad.removeStyleName(style.muyFacil());
		dificultad.removeStyleName(style.facil());
		dificultad.removeStyleName(style.intermedio());
		dificultad.removeStyleName(style.dificil());
		dificultad.removeStyleName(style.muyDificil());
		switch (grado) {
			case MuyFacil:
				dificultad.addStyleName(style.muyFacil());
				break;
			case Facil:
				dificultad.addStyleName(style.facil());
				break;
			case Intermedio:
				dificultad.addStyleName(style.intermedio());
				break;
			case Dificil:
				dificultad.addStyleName(style.dificil());
				break;
			case MuyDificil:
				dificultad.addStyleName(style.muyDificil());
				break;

		}
	}

	public ResumenProblema getProblema() {
		return problema;
	}

	private final RtpstatServiceAsync rtpstatService = GWT.create(RtpstatService.class);

	private static final int PERIODO_COMPROBACION = 5000;

	interface Estilos extends CssResource {
		String OK();

		String GENERANDO();

		String ERROR();

		String DESCONOCIDO();

		String muyFacil();

		String facil();

		String intermedio();

		String dificil();

		String muyDificil();
	}

	private void setEstado(EstadoGeneracion estado) {
		if (botonesHabilitados) {
			if (estado == null) {
				estado = EstadoGeneracion.IOERROR;
			}

			descargarDocumento.setVisible(false);

			iconoGenerando.setVisible(false);
			iconoEstadoDesconocido.setVisible(false);

			switch (estado) {
				case IOERROR:
				case ERROR:
					rtpstatService.getCausaErrorProblema(problema.getId(), new AsyncCallback<String>() {
						public void onSuccess(String result) {
							detallesError.setVisible(true);
							dialogoDetallesError.clear();
							dialogoDetallesError.add(new HTML(result));
						}

						public void onFailure(Throwable caught) {
							// TODO Auto-generated method stub

						}
					});
					break;
				case GENERANDO:
					iconoGenerando.setVisible(true);
					break;
				case DESCONOCIDO:
					iconoEstadoDesconocido.setVisible(true);
					break;
				case OK:
					long id = getProblema().getId();
					String incompleta = "/rtpstat/download?id=" + id + "&tipo=" + Constantes.TIPO_DESCARGA_PROBLEMA;
					urlDocumento = incompleta + "&formato=" + Constantes.FORMATO_PROBLEMA_FOLIOS;
					urlPresentacion = incompleta + "&formato=" + Constantes.FORMATO_PROBLEMA_PRESENTACION;
					descargarDocumento.setVisible(true);
					break;

			}
		}
	}

	private static ProblemaEnListadoUiBinder uiBinder = GWT.create(ProblemaEnListadoUiBinder.class);

	interface ProblemaEnListadoUiBinder extends UiBinder<Widget, ProblemaEnListado> {
	}

	private DialogBox dialogoDetallesError = new DialogBox(true);
	protected ResumenProblema problema;

	protected Timer comprobarEstadoTimer = new Timer() {
		@Override
		public void run() {
			comprobarEstadoGeneracion();
		}
	};

	private String urlPresentacion;

	private String urlDocumento;

	private NotificadorGeneral notificadorGeneral;

	@UiField
	Estilos style;

	@UiField
	Label areas;
	
	@UiField
	protected Label descripcion;

	@UiField
	protected Label dificultad;

	@UiField(provided = true)
	PushButton detallesError;

	@UiField(provided = true)
	PushButton iconoGenerando;

	@UiField(provided = true)
	PushButton iconoEstadoDesconocido;

	@UiField
	protected HorizontalPanel panel;

	@UiField(provided = true)
	protected final PushButton editarProblema;

	@UiField(provided = true)
	protected final PushButton duplicarProblema;

	@UiField(provided = true)
	protected final PushButton eliminarProblema;

	@UiField(provided = true)
	protected final PushButton descargarDocumento;

	protected void borrarDelListado() {
		this.removeFromParent();
	}

	protected void comprobarEstadoGeneracion() {
		if (botonesHabilitados) {
			rtpstatService.comprobarEstadoGeneracionProblema(getProblema().getId(), new AsyncCallback<EstadoGeneracion>() {
				public void onSuccess(EstadoGeneracion result) {
					setEstado(result);
					if (result != EstadoGeneracion.OK) {
						comprobarEstadoTimer.schedule(PERIODO_COMPROBACION);
					}
				}

				public void onFailure(Throwable caught) {
					notificadorGeneral.warning("Ha fallado la comunicación con el servidor. Pruebe a recargar la página");
				}
			});
		}
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		if (this.problema != null && this.problema.isDocumentosGenerados()) {
			setEstado(EstadoGeneracion.OK);
		} else {
			if (comprobarEstadoTimer != null) {
				comprobarEstadoTimer.schedule(1);
			}
		}
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		if (comprobarEstadoTimer != null) {
			comprobarEstadoTimer.cancel();
		}
	}
}
