package org.ugr.rtpstat.publico.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("service")
public interface PublicoService extends RemoteService {
	String getLoginUrl();
}
