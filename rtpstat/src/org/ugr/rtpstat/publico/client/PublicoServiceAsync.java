package org.ugr.rtpstat.publico.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface PublicoServiceAsync {

	void getLoginUrl(AsyncCallback<String> callback);

}
