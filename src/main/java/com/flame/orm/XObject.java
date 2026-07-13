package com.flame.orm;

import java.sql.Timestamp;
import java.util.Date;

import com.flame.xui.WidgetMode;
import com.flame.xui.XUIWidget;
import com.flame.xui.widget.TextDisplay;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.SequenceGenerator;

@MappedSuperclass
public abstract class XObject implements XPersistable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FLAME_XID_SEQ")
	@SequenceGenerator(name = "flame_xid_seq", sequenceName = "FLAME_XID_SEQ", allocationSize = 1)
	@Column(name = "xid", unique = true, nullable = false, updatable = false)
	private long xid;  //设置初始值会导致detached entity passed to persist异常
	
	@Basic
	@Column(name = "created_stamp")
	private Timestamp createdStamp = new Timestamp(new Date().getTime());

	@Basic
	@Column(name = "modified_stamp")
	private Timestamp modifiedStamp = new Timestamp(new Date().getTime());
	
	public long getXid() {
		return this.xid;
	}

	public Date getCreatedStamp() {
		return this.createdStamp;
	}

	protected void setCreatedStamp(Timestamp createdStamp) {
		this.createdStamp = createdStamp;
	}

	public Date getModifiedStamp() {
		return this.modifiedStamp;
	}

	protected void setModifiedStamp(Timestamp modifiedStamp) {
		this.modifiedStamp = modifiedStamp;
	}

	public String toString() {
		return "OR:" + this.getXclass() + ":" + this.getXid();
	}

	public void newPersistInfo() {
		this.createdStamp = new Timestamp((new Date()).getTime());
		this.modifiedStamp = new Timestamp((new Date()).getTime());
	}

	public void updatePersistInfo() {
		this.modifiedStamp = new Timestamp((new Date()).getTime());
	}

	public XUIWidget getXUIWidget(WidgetMode model) {
		if (WidgetMode.Display.equals(model)) {
			return new TextDisplay(this.getOid());
		} else {
			return null;
		}
	}
}
