package com.flame.orm;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class ItemEntity extends AbstractEntity {
	private static final long serialVersionUID = 1L;
	@Basic
	@Column(name = "number", nullable = false, length = 100)
	private String number = "";
	@Basic
	@Column(name = "name", nullable = false, length = 100)
	private String name = "";

	public String getNumber() {
		return this.number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
