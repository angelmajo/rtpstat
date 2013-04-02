package org.ugr.rtpstat.client.uibinder;


import org.ugr.rtpstat.client.RtpstatService;
import org.ugr.rtpstat.client.RtpstatServiceAsync;
import org.ugr.rtpstat.client.uibinder.principal.NotificadorGeneral;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class UserInfo extends Composite {

	private static UserInfoUiBinder uiBinder = GWT.create(UserInfoUiBinder.class);
	private final RtpstatServiceAsync rtpstatService = GWT.create(RtpstatService.class);

	interface UserInfoUiBinder extends UiBinder<Widget, UserInfo> {
	}

	@UiField
	Label nombre_usuario;

	@UiField
	Anchor logout;
	private NotificadorGeneral notificadorGeneral;

	public UserInfo(NotificadorGeneral notificadorGeneral) {
		this.notificadorGeneral = notificadorGeneral;
		initWidget(uiBinder.createAndBindUi(this));
		refresh();
	}

	public void refresh() {
		rtpstatService.getUserName(new AsyncCallback<String>() {
			public void onSuccess(String result) {
				nombre_usuario.setText(result);
			}

			public void onFailure(Throwable caught) {
				notificadorGeneral.warning("Fallo al obtener el nombre de usuario: " + caught.getMessage());
			}
		});
		rtpstatService.getLogoutUrl(new AsyncCallback<String>() {
			public void onSuccess(String result) {
				logout.setHref(result);
			}

			public void onFailure(Throwable caught) {
				notificadorGeneral.warning("Fallo al obtener la URL de fin de sesión: " + caught.getMessage());
			}
		});
	}
}
