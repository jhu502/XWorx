package com.flame.vc;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import com.flame.orm.XObject;

@MappedSuperclass
public abstract class Master extends XObject implements IMastered {
	private static final long serialVersionUID = 1L;
	@Basic
	@Column(name = "number", length = 100, nullable = false, unique = true)
	private String number;
	
	@Basic
	@Column(name = "name", length = 300, nullable = false)
	private String name;

	@Override
	public String getNumber() {
		return this.number;
	}

	@Override
	public void setNumber(String number) {
		this.number = number;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
}
