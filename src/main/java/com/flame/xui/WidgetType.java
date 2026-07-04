package com.flame.xui;

import com.flame.annotations.UIDataGrid;
import com.flame.annotations.UIMeshGrid;
import com.flame.annotations.UITreeGrid;
import com.flame.annotations.UIWidget;
import com.flame.xui.widget.*;

import java.lang.annotation.Annotation;

public enum WidgetType {
	DataGrid(XUIDataGrid.class, UIDataGrid.class), //
	TreeGrid(XUITreeGrid.class, UITreeGrid.class), //
	PropertyGrid(XUIDataGrid.class, UIDataGrid.class), //
	MeshGrid(XUIMeshGrid.class, UIMeshGrid.class), //
	AppButton(AppButton.class, UIWidget.class), //
	Button(Button.class, UIWidget.class), //
	CheckBox(CheckBox.class, UIWidget.class), //
	ComboBox(ComboBox.class, UIWidget.class), //
	DateBox(DateBox.class, UIWidget.class), //
	DetailIcon(DetailIcon.class, UIWidget.class),
	DialogIcon(DialogIcon.class, UIWidget.class),
	Element(HTMLElement.class, UIWidget.class),
	FileBox(FileBox.class, UIWidget.class),
	Hidden(Hidden.class, UIWidget.class),
	HyperLink(HyperLink.class, UIWidget.class), //
	IconBox(IconBox.class, UIWidget.class), //
	Label(Label.class, UIWidget.class), //
	LinkButton(LinkButton.class, UIWidget.class), //
	NumberBox(NumberBox.class, UIWidget.class), //
	RadioBox(RadioBox.class, UIWidget.class), //
	TextBox(TextBox.class, UIWidget.class), //
	TextDisplay(TextDisplay.class, UIWidget.class),
	Draggable(Draggable.class, UIWidget.class);

	private Class<? extends IComponent> widget;
	private Class<? extends Annotation> annotation;

	WidgetType(Class<? extends IComponent> widget, Class<? extends Annotation> annotation) {
		this.widget = widget;
		this.annotation = annotation;
	}

	public Class<? extends IComponent> getWidget() {
		return this.widget;
	}

	public String getName() {
		return this.name().toLowerCase();
	}

	public Class<? extends Annotation> getAnnotation() {
		return annotation;
	}

	public void setAnnotation(Class<? extends Annotation> annotation) {
		this.annotation = annotation;
	}

}
