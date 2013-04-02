package org.ugr.rtpstat.client;

import org.ugr.rtpstat.client.uibinder.MainCompositeBase;
import org.ugr.rtpstat.client.uibinder.registro.FormularioRegistro;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Rtpstat implements EntryPoint, ValueChangeHandler<String> {
	private static final HTML HTML_ERROR_COMUNICACION = new HTML("Error de comunicación con el servidor1, pruebe a <a href=\"javascript:window.location.reload()\">recargar la página</a>");
	private static final HTML HTML_ERROR_COMUNICACION2 = new HTML("Error de comunicación con el servidor2, pruebe a <a href=\"javascript:window.location.reload()\">recargar la página</a>");
	
	private MainCompositeBase mainComposite = null;
	private final RtpstatServiceAsync rtpstatService = GWT.create(RtpstatService.class);

	public void onValueChange(ValueChangeEvent<String> event) {
		final String token = event.getValue();
		if (mainComposite != null) {
			mainComposite.limpiarPrincipal();
		}
		rtpstatService.isRegisteredUser(new AsyncCallback<Boolean>() {
			public void onSuccess(final Boolean result) {
				GWT.runAsync(new RunAsyncCallback() {
					public void onSuccess() {
						
						RootLayoutPanel panel = RootLayoutPanel.get();

						if (mainComposite == null) {
							mainComposite = GWT.create(MainCompositeBase.class);
						}

						if (result) {
							if (panel.getWidgetIndex(mainComposite) == -1) {
								panel.clear();
								mainComposite.limpiarPorRol();
								panel.add(mainComposite);

							}

							if ("nuevo_usuario".equals(token)) {
								History.newItem("editar_usuario");
							} else if ("autorizar_usuarios".equals(token)) {
								mainComposite.cambiarEstado(token, MainCompositeBase.ESTADO.AUTORIZAR_USUARIOS);
							} else if ("feed_oficial".equals(token)) {
								mainComposite.cambiarEstado(token, MainCompositeBase.ESTADO.FEED_OFICIAL);
							} else if ("mis_problemas".equals(token)) {
								mainComposite.cambiarEstado(token, MainCompositeBase.ESTADO.MIS_PROBLEMAS);
							} else if ("crear_problema".equals(token)) {
								mainComposite.cambiarEstado(token, MainCompositeBase.ESTADO.CREAR_PROBLEMA);
							} else if ("editar_usuario".equals(token)) {
								mainComposite.cambiarEstado(token, MainCompositeBase.ESTADO.EDITAR_USUARIO);
							} else if ("mis_relaciones".equals(token)) {
								mainComposite.cambiarEstado(token, MainCompositeBase.ESTADO.GESTIONAR_RELACIONES_EJERCICIOS);
							} else if ("nueva_relacion_ejercicios".equals(token)) {
								mainComposite.cambiarEstado(token, MainCompositeBase.ESTADO.NUEVA_RELACION_EJERCICIOS);
							} else if ("mis_libros".equals(token)) {
								mainComposite.cambiarEstado(token, MainCompositeBase.ESTADO.MIS_LIBROS);
							} else if ("nuevo_libro".equals(token)) {
								mainComposite.cambiarEstado(token, MainCompositeBase.ESTADO.NUEVO_LIBRO);
							} else if (token != null && token.matches("^editar_problema-[0-9]+$")) {
								mainComposite.cambiarEstado(token, MainCompositeBase.ESTADO.EDITAR_PROBLEMA);
							} else if (token != null && token.matches("^editar_relacion_(nueva_){0,1}[0-9]+$")) {
								mainComposite.cambiarEstado(token, MainCompositeBase.ESTADO.EDITAR_RELACION_EJERCICIOS);
							} else if (token != null && token.matches("^editar_libro_[0-9]+$")) {
								mainComposite.cambiarEstado(token, MainCompositeBase.ESTADO.EDITAR_LIBRO);
							} else {
								mainComposite.cambiarEstado(token, MainCompositeBase.ESTADO.INICIO);
							}

							//
						} else {
							panel.clear();
							panel.add(new FormularioRegistro());
						}
						eliminarCargandoAplicacion();
					}

					public void onFailure(Throwable reason) {
						eliminarCargandoAplicacion();
						RootPanel.get().add(HTML_ERROR_COMUNICACION);
					}
				});

			}

			public void onFailure(Throwable caught) {
				eliminarCargandoAplicacion();
				RootPanel.get().add(HTML_ERROR_COMUNICACION2);
			}
		});
		// tokenAnterior = token;
	}

	private void eliminarCargandoAplicacion() {
		Element el = DOM.getElementById("cargando_aplicacion");
		if (el != null) {
			el.removeFromParent();
		}
	}

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		History.addValueChangeHandler(this);
		History.fireCurrentHistoryState();
	}

	public native void reloadPage() /*-{
		$wnd.location.reload();
	}-*/;

}
