package org.ugr.rtpstat.client.uibinder.libros;

import com.google.gwt.dom.client.InputElement;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.MenuItem;

public class CheckableMenuItem extends MenuItem {
	public String text;
	public InputElement checkbox;

	public CheckableMenuItem(String text) {
		super("", true, (Command) null);
		this.text = text;
		this.checkbox = InputElement.as(DOM.createInputCheck());

		setHTML(text);

		getElement().insertFirst(checkbox);
	}

	public boolean isChecked() {
		return checkbox.isChecked();
	}
	
	public void setChecked(boolean checked) {
		checkbox.setChecked(checked);
	}

	public String getText() {
		return text;
	}
}
