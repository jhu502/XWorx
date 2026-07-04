package com.flame.common;

public class FourEntry<S, T, X, Y> extends XEntry {
	private S oneValue;
	private T twoValue;
	private X threeValue;
	private Y fourValue;


	public FourEntry(S one, T two, X three, Y four) {
		this.oneValue = one;
		this.twoValue = two;
		this.threeValue = three;
		this.fourValue = four;
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
}
