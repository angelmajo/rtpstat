package org.ugr.rtpstat.client.uibinder.principal;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class NotificadorTemporal extends Composite {

	private static NotificadorTemporalUiBinder uiBinder = GWT.create(NotificadorTemporalUiBinder.class);

	interface NotificadorTemporalUiBinder extends UiBinder<Widget, NotificadorTemporal> {
	}

	public NotificadorTemporal() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
