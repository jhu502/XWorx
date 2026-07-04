package com.flame.common.form;

import java.io.Serializable;

public class FormResult implements Serializable {
	private static final long serialVersionUID = 1L;
	private FormStatus status;
	private String message;
	private Object data;

	public static FormResult newFormResult(FormStatus status, String message) {
		FormResult result = new FormResult();
		result.setStatus(status);
		result.setMessage(message);

		return result;
	}

	public FormStatus getStatus() {
		return status;
	}

	public void setStatus(FormStatus status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
