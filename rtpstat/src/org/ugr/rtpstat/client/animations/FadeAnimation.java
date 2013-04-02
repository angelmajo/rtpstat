package org.ugr.rtpstat.client.animations;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

public class FadeAnimation extends Animation {
	private Widget widget;

	private enum Direccion {
		IN, OUT
	};

	private Direccion direccion = null;

	public FadeAnimation(Widget widget) {
		if (widget == null) {
			throw new NullPointerException("widget no puede ser nulo");
		}
		this.widget = widget;
	}

	@Override
	protected void onUpdate(double progress) {
		if (direccion == null) {
			throw new IllegalStateException("Solo debe utilizar slideUp y slideDown, no use run directamente");
		}
		switch (direccion) {
			case IN:
				DOM.setStyleAttribute(widget.getElement(), "opacity", "" + progress);
				break;
			case OUT:
				DOM.setStyleAttribute(widget.getElement(), "opacity", "" + (1 - progress));
				break;
		}
	}

	@Override
	protected void onStart() {
		if (direccion == Direccion.IN) {
			DOM.setStyleAttribute(widget.getElement(), "opacity","0");
			widget.setVisible(true);
		}
	}

	@Override
	protected void onComplete() {
		if (direccion == Direccion.OUT) {
			widget.setVisible(false);
			DOM.setStyleAttribute(widget.getElement(), "opacity","0");
		}
		direccion = null;
	}

	public void fadeIn(int duration) {
		direccion = Direccion.IN;
		run(duration);
	}

	public void fadeOut(int duration) {
		direccion = Direccion.OUT;
		run(duration);
	}
}
