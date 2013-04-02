package org.ugr.rtpstat.client.events;

import com.google.gwt.event.shared.HandlerManager;

public class EventBus extends HandlerManager {
	private static EventBus instance = new EventBus(null);
	
	protected EventBus(Object source) {
		super(source);
	}

	protected static void setInstance(EventBus instance) {
		EventBus.instance = instance;
	}

	public static EventBus getInstance() {
		return instance;
	}

}
