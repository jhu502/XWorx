package com.flame.xui;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public abstract class GridComponent extends XUIComponent {
	public static final String ROW_ID = "rowId";
	private String title = "";
	private String idField = "";
	private Object toolbar = "";
	private String actionModel = "";
	private String contextMenu = "";
	private String sortName = "";
	private String sortOrder = "";
	private boolean striped = true;
	private boolean fit = false;
	private boolean rownumbers = true;
	private boolean selectOnCheck = false;
	private boolean checkOnSelect = false;
	private transient List<XUIAction> actions = new ArrayList<>();

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIdField() {
		return idField;
	}

	public void setIdField(String idField) {
		this.idField = idField;
	}

	public Object getToolbar() {
		return toolbar;
	}

	public void setToolbar(Object toolbar) {
		this.toolbar = toolbar;
	}

	public String getActionModel() {
		return actionModel;
	}

	public void setActionModel(String actionModel) {
		this.actionModel = actionModel;
	}

	public String getContextMenu() {
		return contextMenu;
	}

	public void setContextMenu(String contextMenu) {
		this.contextMenu = contextMenu;
	}

	public String getSortName() {
		return sortName;
	}

	public void setSortName(String sortName) {
		this.sortName = sortName;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	public boolean isStriped() {
		return striped;
	}

	public void setStriped(boolean striped) {
		this.striped = striped;
	}

	public boolean isFit() {
		return fit;
	}

	public void setFit(boolean fit) {
		this.fit = fit;
	}

	public boolean isRownumbers() {
		return rownumbers;
	}

	public void setRownumbers(boolean rownumbers) {
		this.rownumbers = rownumbers;
	}

	public void setSelectOnCheck(boolean selectOnCheck) {
		this.selectOnCheck = selectOnCheck;
	}

	public boolean isSelectOnCheck() {
		return this.selectOnCheck;
	}

	public void setCheckOnSelect(boolean checkOnSelect) {
		this.checkOnSelect = checkOnSelect;
	}

	public boolean isCheckOnSelect() {
		return this.checkOnSelect;
	}

	public void addXAction(XUIAction xaction) { this.actions.add(xaction); }

	@JsonIgnore
	public List<XUIAction> getActions() { return this.actions; }
}
