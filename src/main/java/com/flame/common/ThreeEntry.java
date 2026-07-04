package com.flame.common;

public class ThreeEntry<S, T, X> extends XEntry {
	private S oneValue;
	private T twoValue;
	private X threeValue;

	public ThreeEntry(S one, T two, X three) {
		this.oneValue = one;
		this.twoValue = two;
		this.threeValue = three;
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

	public X getThreeValue() {
		return threeValue;
	}

	public void setThreeValue(X value) {
		this.threeValue = value;
	}
}
