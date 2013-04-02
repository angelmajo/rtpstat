package org.ugr.rtpstat.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class ListaProblemaCambiadaEvent extends GwtEvent<ListaProblemaCambiadaHandler> {
    public static final Type<ListaProblemaCambiadaHandler> TYPE = new Type<ListaProblemaCambiadaHandler>();
	
	@Override
	protected void dispatch(ListaProblemaCambiadaHandler handler) {
		handler.actualizarListaProblemas();
	}

	@Override
	public Type<ListaProblemaCambiadaHandler> getAssociatedType() {
		return TYPE;
	}
}
