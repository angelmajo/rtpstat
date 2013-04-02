package org.ugr.rtpstat.server;

import org.ugr.rtpstat.publico.client.PublicoService;

import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class PublicoServiceImpl extends RemoteServiceServlet implements PublicoService {
	private static final long serialVersionUID = 7199582410926992091L;

	@Override
	public String getLoginUrl() {
		return UserServiceFactory.getUserService().createLoginURL("/frontend/Rtpstat.html");
	}

}
