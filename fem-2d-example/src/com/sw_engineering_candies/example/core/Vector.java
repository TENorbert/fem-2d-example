package com.sw_engineering_candies.example.core;

public class Vector {

	  protected final double[] values;

	   public Vector(final int length) {
	      values = new double[length];
	   }

	   public Vector(final Vector A) {
	      this(A.values);
	   }

	   public Vector(final double[] values) {
	      this.values = new double[values.length];
	      System.arraycopy(values, 0, this.values, 0, values.length);
	   }

	   // return C = A + B
	   public void plus(final Vector B, final Vector result) {
	      for (int i = 0; i < values.length; i++) {
	         result.values[i] = values[i] + B.values[i];
	      }
	   }

	   // return C = A - B
	   public void minus(final Vector B, final Vector result) {
	      for (int i = 0; i < values.length; i++) {
	         result.values[i] = values[i] - B.values[i];
	      }
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
	   public void multi(final double alpha, final Vector result) {
	      for (int i = 0; i < values.length; i++) {
	         result.values[i] = values[i] * alpha;
	      }
	   }

	   public void setValue(final int index, final double value) {
	      values[index] = value;
	   }

	   public double getValue(final int index) {
	      return values[index];
	   }

	   protected double[] getValues() {
	      return values;
	   }

	   public int getMaxRows() {
	      return values.length;
	   }

	   @Override
	   public String toString() {
	      final StringBuilder sb = new StringBuilder("v3.Vector [");
	      for (int i = 0; i < values.length; i++) {
	         sb.append(String.format("%.6E", values[i])).append("  ");
	      }
	      sb.append(']');
	      return sb.toString();
	   }
}