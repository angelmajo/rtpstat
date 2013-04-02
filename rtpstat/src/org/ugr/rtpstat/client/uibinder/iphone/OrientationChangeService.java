package org.ugr.rtpstat.client.uibinder.iphone;

public class OrientationChangeService {
	public static native void registerOrientationChangedHandler(OrientationChangeEventHandler handler) /*-{
		var callback = function(){
			handler.@org.ugr.rtpstat.client.uibinder.iphone.OrientationChangeEventHandler::onOrientationChange(Ljava/lang/String;)(window.orientation);

		}

		$wnd.addEventListener("orientationchange", callback, false);
	}-*/;
}
