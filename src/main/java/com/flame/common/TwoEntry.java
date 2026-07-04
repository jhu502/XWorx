package com.flame.common;

public class TwoEntry<S, T> extends XEntry {
	private S oneValue;
	private T twoValue;

	public TwoEntry(S one, T two) {
		this.oneValue = one;
		this.twoValue = two;
	}

	public S getOneValue() {
		return oneValue;
	}

	public void setOneValue(S value) {
		this.oneValue = value;
	}

	public T getTwoValue() {
		return twoValue;
	}

	public void setTwoValue(T value) {
		this.twoValue = value;
	}
}
