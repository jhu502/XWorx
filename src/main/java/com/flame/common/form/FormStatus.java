package com.flame.common.form;

public enum FormStatus {
	SUCCESS(0), FAILURE(1), NON_FATAL_ERROR(2);

	private int id;

	private FormStatus(int id) {
		this.id = id;
	}

	public int id() {
		return this.id;
	}
}
