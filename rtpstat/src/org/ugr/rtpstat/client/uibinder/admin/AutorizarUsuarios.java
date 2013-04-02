package org.ugr.rtpstat.client.uibinder.admin;

import java.util.List;

import org.ugr.rtpstat.client.RtpstatService;
import org.ugr.rtpstat.client.RtpstatServiceAsync;
import org.ugr.rtpstat.client.imagenes.Imagenes;
import org.ugr.rtpstat.client.orm.UsuarioRegistrado;
import org.ugr.rtpstat.client.uibinder.principal.NotificadorGeneral;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class AutorizarUsuarios extends Composite {
	@UiField
	VerticalPanel listado_por_autorizar;

	@UiField 
	DisclosurePanel panel_listado_por_autorizar;
	
	@UiField
	VerticalPanel listado_resto;

	@UiField 
	DisclosurePanel panel_listado_resto;
	
	@UiField(provided = true)
	Image spinWheel;
	
	@UiField
	HTMLPanel cargandoListado;
	
	private final RtpstatServiceAsync rtpstatService = GWT.create(RtpstatService.class);

	private NotificadorGeneral notificadorGeneral;

	private static AutorizarUsuariosUiBinder uiBinder = GWT.create(AutorizarUsuariosUiBinder.class);

	interface AutorizarUsuariosUiBinder extends UiBinder<Widget, AutorizarUsuarios> {
	}

	public AutorizarUsuarios(NotificadorGeneral notificadorGeneral) {
		this.notificadorGeneral = notificadorGeneral;
		this.spinWheel = new Image(Imagenes.INSTANCE.ajaxLoader());
		initWidget(uiBinder.createAndBindUi(this));
		
		actualizarListado();
	}

	private void actualizarListado(List<UsuarioRegistrado> result, DisclosurePanel panel, VerticalPanel listado, String mensajeVacio) {
		panel.getHeaderTextAccessor().setText(panel.getHeaderTextAccessor().getText().replaceAll(" \\([0-9]+\\)","") + " (" + result.size() +")");
		if (result.size() == 0) {
			panel.setOpen(false);
			
			listado.add(new Label(mensajeVacio ));
		} else {
			panel.setOpen(true);
			for (UsuarioRegistrado usuario : result) {
				EditorRolUsuario editor = new EditorRolUsuario(usuario,notificadorGeneral);
				editor.addValueChangeHandler(new ValueChangeHandler<UsuarioRegistrado>() {
					public void onValueChange(ValueChangeEvent<UsuarioRegistrado> event) {
						actualizarListado();
					}
				});
				listado.add(editor);
			}
		}
	}
	
	private void actualizarListado() {
		cargandoListado.setVisible(true);
		listado_por_autorizar.clear();
		rtpstatService.usuariosSinAutorizar(new AsyncCallback<List<UsuarioRegistrado>>() {
			public void onSuccess(List<UsuarioRegistrado> result) {
				actualizarListado(result, panel_listado_por_autorizar, listado_por_autorizar, "No existe ningún usuario pendiente de autorización");
				cargandoListado.setVisible(false);
			}
			
			public void onFailure(Throwable caught) {
				cargandoListado.setVisible(false);
				notificadorGeneral.warning("Ha fallado la comunicación con el servidor, pruebe a recargar la página");
			}
		});

		listado_resto.clear();
		rtpstatService.usuariosAutorizados(new AsyncCallback<List<UsuarioRegistrado>>() {
			public void onSuccess(List<UsuarioRegistrado> result) {
				actualizarListado(result, panel_listado_resto, listado_resto, "No existen usuarios autorizados");
			}

			public void onFailure(Throwable caught) {
				notificadorGeneral.warning("Ha fallado la comunicación con el servidor, pruebe a recargar la página");
			}
		});
	}
}
