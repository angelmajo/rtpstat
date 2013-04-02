package org.ugr.rtpstat.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Navigator;

public class BrowserDetector {
	public static int getIEVersion() {
		int out = -1;
		// if(Navigator.getAppName() == "Microsoft Internet Explorer") {
		Window.alert("Navegador: " + Navigator.getAppName());
		Window.alert("Versión: " + Navigator.getAppVersion());
		Window.alert("CodeName: " + Navigator.getAppCodeName());
		Window.alert("UserAgent: " + Navigator.getUserAgent());
		// }
		return out;
		/*
		 * { var rv = -1; // Return value assumes failure. if (navigator.appName
		 * == 'Microsoft Internet Explorer') { var ua = navigator.userAgent; var
		 * re = new RegExp("MSIE ([0-9]{1,}[\.0-9]{0,})"); if (re.exec(ua) !=
		 * null) rv = parseFloat( RegExp.$1 ); } return rv; }
		 */
	}
}
