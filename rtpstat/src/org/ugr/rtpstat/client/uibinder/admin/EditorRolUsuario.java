package org.ugr.rtpstat.client.uibinder.admin;

import java.util.Arrays;

import org.ugr.rtpstat.client.RtpstatService;
import org.ugr.rtpstat.client.RtpstatServiceAsync;
import org.ugr.rtpstat.client.imagenes.Imagenes;
import org.ugr.rtpstat.client.orm.Rol;
import org.ugr.rtpstat.client.orm.UsuarioRegistrado;
import org.ugr.rtpstat.client.uibinder.principal.NotificadorGeneral;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

public class EditorRolUsuario extends Composite implements HasValueChangeHandlers<UsuarioRegistrado> {
	private final RtpstatServiceAsync rtpstatService = GWT.create(RtpstatService.class);
	private static EditorRolUsuarioUiBinder uiBinder = GWT.create(EditorRolUsuarioUiBinder.class);

	interface EditorRolUsuarioUiBinder extends UiBinder<Widget, EditorRolUsuario> {
	}

	@UiField
	Label nombre_real;
	@UiField
	Label organizacion;
	@UiField
	ListBox rol;

	@UiField(provided = true)
	PushButton boton_aplicar;
	private NotificadorGeneral notificadorGeneral;

	@UiHandler("boton_aplicar")
	public void aplicarPulsado(ClickEvent event) {
		boton_aplicar.setEnabled(false);
		final EditorRolUsuario thisRef = this;
		Rol rolSeleccionado = Rol.valueOf(this.rol.getValue(this.rol.getSelectedIndex()));
		rtpstatService.cambiarRolUsuario(usuario.getUserId(),rolSeleccionado,new AsyncCallback< org.ugr.rtpstat.client.orm.UsuarioRegistrado>() {
			public void onSuccess(org.ugr.rtpstat.client.orm.UsuarioRegistrado result) {
				usuario = result;
				ValueChangeEvent.fire(thisRef, usuario);
			}
			
			public void onFailure(Throwable caught) {
				boton_aplicar.setEnabled(true);
				notificadorGeneral.warning("Ha fallado la comunicación con el servidor, pruebe a recargar la página");
			}
		});
		
	}

	@UiHandler("rol")
	public void rolCambiado(ChangeEvent event) {
		Rol rolSeleccionado = Rol.valueOf(this.rol.getValue(this.rol.getSelectedIndex()));
		boton_aplicar.setVisible(!usuario.getRol().equals(rolSeleccionado));
	}

	private void setupRolList(Rol rol) {
		for (Rol r : Rol.values()) {
			this.rol.addItem(r.toLongString(), r.toString());
		}
		this.rol.setSelectedIndex(Arrays.asList(Rol.values()).indexOf(rol));
	}

	public EditorRolUsuario(UsuarioRegistrado usuario, NotificadorGeneral notificadorGeneral) {
		this.notificadorGeneral = notificadorGeneral;
		boton_aplicar = new PushButton(new Image(Imagenes.INSTANCE.okIcon24x24()));
		
		initWidget(uiBinder.createAndBindUi(this));
		
		this.usuario = usuario;

		nombre_real.setText(usuario.getNombreReal());
		organizacion.setText(usuario.getOrganizacion());

		setupRolList(usuario.getRol());
	}

	private UsuarioRegistrado usuario;

	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<UsuarioRegistrado> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}
}
