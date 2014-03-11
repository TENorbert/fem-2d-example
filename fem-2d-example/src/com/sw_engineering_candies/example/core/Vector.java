package com.sw_engineering_candies.example.core;

public class Vector {

	final double[] values;

	public int getLength() {
		return values.length;
	}

	public double getValue(final int index) {
		return index >= 0 && index < values.length ? values[index] : 0.0;
	}

	public void setValue(final int index, final double value) {
		values[index] = value;
	}

	public void addValue(final int index, final double value) {
		values[index] += value;
	}

	public Vector(final int length) {
		values = new double[length];
	}

	public Vector(final Vector A) {
		this(A.values);
	}

	public Vector(final double[] values) {
		this.values = new double[values.length];
		for (int i = 0; i < values.length; i++) {
			this.values[i] = values[i];
		}
	}

	// return C = A + B
	public Vector plus(final Vector B) {
		final Vector C = new Vector(values.length);
		for (int i = 0; i < values.length; i++) {
			C.values[i] = values[i] + B.values[i];
		}
		return C;
	}

	// return C = A - B
	public Vector minus(final Vector B) {
		final Vector C = new Vector(values.length);
		for (int i = 0; i < values.length; i++) {
			C.values[i] = values[i] - B.values[i];
		}
		return C;
	}

	// return C = A o B
	public double dotProduct(final Vector B) {
		double C = 0.0f;
		for (int i = 0; i < values.length; i++) {
			C += values[i] * B.values[i];
		}
		return C;
	}

	// return C = A * alpha
	public Vector multi(final double alpha) {
		final Vector C = new Vector(values.length);
		for (int i = 0; i < values.length; i++) {
			C.values[i] = values[i] * alpha;
		}
		return C;
	}

}