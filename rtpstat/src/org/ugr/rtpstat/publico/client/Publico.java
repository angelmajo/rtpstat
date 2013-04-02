package org.ugr.rtpstat.publico.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

public class Publico implements EntryPoint {
	private final PublicoServiceAsync service = GWT.create(PublicoService.class);

	@Override
	public void onModuleLoad() {
		service.getLoginUrl(new AsyncCallback<String>() {
			@Override
			public void onSuccess(String loginUrl) {
				RootPanel.get().add(new PublicoComposite(loginUrl));
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
}
