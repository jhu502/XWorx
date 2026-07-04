package com.flame.common;

public class FiveEntry<S, T, X, Y, Z> extends XEntry {
	private S oneValue;
	private T twoValue;
	private X threeValue;
	private Y fourValue;
	private Z fiveValue;

	public FiveEntry(S one, T two, X three, Y four, Z five) {
		this.oneValue = one;
		this.twoValue = two;
		this.threeValue = three;
		this.fourValue = four;
		this.fiveValue = five;
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

	public Y getFourValue() {
		return fourValue;
	}

	public void setFourValue(Y value) {
		this.fourValue = value;
	}

	public Z getFiveValue() {
		return fiveValue;
	}

	public void setFiveValue(Z value) {
		this.fiveValue = value;
	}
}
