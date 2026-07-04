package com.flame.xui;

import java.util.Vector;

public class ArrayComponent implements IWidget {
	private Vector<IWidget> vector = new Vector<IWidget>();

	public ArrayComponent(IWidget... uicomps) {
		for (IWidget uicomp : uicomps) {
			this.vector.add(uicomp);
		}
	}

	@Override
	public void inflate(Object object) {
	}

	public String renderHTML() {
		StringBuffer result = new StringBuffer();
		for (IWidget uicomp : vector) {
			result.append(uicomp.renderHTML());
		}
		return result.toString();
	}

	@Override
	public void setWidgetMode(WidgetMode model) {
		for (IWidget widget : vector) {
			if (widget != null) {
				widget.setWidgetMode(model);
			}
		}
	}

	@Override
	public String getId() {
		return "";
	}
}
