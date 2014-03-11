/**
 * Copyright (C) 2012-2013, Markus Sprunck
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 *
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials provided
 *   with the distribution.
 *
 * - The name of its contributor may be used to endorse or promote
 *   products derived from this software without specific prior
 *   written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package com.sw_engineering_candies.example.core;

public class Model {

	// Simple data structure
	private class Node {
		double x = 0.0f; // in mm
		double y = 0.0f; // in mm
		int nodeID = 0;
	}

	// Model with triangles and corners [elementId][cornerId]
	private Node[][] model;

	// Vector of input forces in N
	private Vector forcesIn;

	// Vector of known displacements in mm
	private Vector deltasIn;

	// Vector of resulting forces in N
	private Vector forcesOut;

	// Vector of resulting displacements in mm
	private Vector deltasOut;

	// Number of nodes
	private int numberOfNodes = 0;

	// Number of elements
	private int numberOfElements = 0;

	// ///////////////////////////////////////////////////////////////
	// getters, setters and helper for model

	public void initNodesByElement() {
		model = new Node[1 + numberOfElements][1 + numberOfNodes];
		for (int i = 0; i < 1 + numberOfElements; i++) {
			for (int k = 0; k < 1 + numberOfNodes; k++) {
				model[i][k] = new Node();
			}
		}
	}

	public int getNodeId(int elementId, int cornerId) {
		return model[elementId][cornerId].nodeID;
	}

	public void setX(int elementId, int cornerId, double value) {
		model[elementId][cornerId].x = value;
	}

	public void setY(int elementId, int cornerId, double value) {
		model[elementId][cornerId].y = value;
	}

	public double getX(int elementId, int cornerId) {
		return model[elementId][cornerId].x;
	}

	public double getY(int elementId, int cornerId) {
		return model[elementId][cornerId].y;
	}

	public void setNodeId(int elementId, int cornerId, int value) {
		model[elementId][cornerId].nodeID = value;
	}

	// ///////////////////////////////////////////////////////////////
	// getters, setters and helper for forcesIn

	public void initForcesIn() {
		final double[] newInputForces = new double[numberOfNodes * 2];
		for (int i = 0; i < numberOfNodes * 2; i++) {
			newInputForces[i] = Double.NaN;
		}
		forcesIn = new Vector(newInputForces);
	}

	public Vector getForcesIn() {
		return forcesIn;
	}

	public double getForceInY(int nodeId) {
		return forcesIn.getValue(nodeId * 2 - 1);
	}

	public double getForceInX(int nodeId) {
		return forcesIn.getValue(nodeId * 2 - 2);
	}

	public void setForceInY(int nodeId, double value) {
		forcesIn.setValue(nodeId * 2 - 1, value);
	}

	public void setForceInX(int nodeId, double value) {
		forcesIn.setValue(nodeId * 2 - 2, value);
	}

	// ///////////////////////////////////////////////////////////////
	// getters, setters and helper for deltasIn

	public void initDeltasIn() {
		final double[] newInputdeltas = new double[numberOfNodes * 2];
		for (int i = 0; i < numberOfNodes * 2; i++) {
			newInputdeltas[i] = Double.NaN;
		}
		deltasIn = new Vector(newInputdeltas);
	}

	public Vector getDeltasIn() {
		return deltasIn;
	}

	protected double getDeltaInY(int nodeId) {
		return deltasIn.getValue(nodeId * 2 - 1);
	}

	protected double getDeltaInX(int nodeId) {
		return deltasIn.getValue(nodeId * 2 - 2);
	}

	public void setDeltaInX(int nodeId, double value) {
		deltasIn.setValue(nodeId * 2 - 2, value);
	}

	public void setSeltaInY(int nodeId, double value) {
		deltasIn.setValue(nodeId * 2 - 1, value);
	}

	public boolean isFixedY(int nodeId) {
		return !Double.isNaN(deltasIn.getValue(nodeId * 2 - 1));
	}

	public boolean isFixedX(int nodeId) {
		return !Double.isNaN(deltasIn.getValue(nodeId * 2 - 2));
	}

	// ///////////////////////////////////////////////////////////////
	// getters, setters and helper for forcesOut

	public void setForcesOut(Vector value) {
		forcesOut = value;
	}

	public Vector getForcesOut() {
		return forcesOut;
	}

	public double getForceOutY(int nodeId) {
		return forcesOut.getValue(nodeId * 2 - 1);
	}

	public double getForceOutX(int nodeId) {
		return forcesOut.getValue(nodeId * 2 - 2);
	}

	// ///////////////////////////////////////////////////////////////
	// getters, setters and helper for deltasOut

	public void setDeltaOut(Vector value) {
		deltasOut = value;
	}

	public Vector getDeltasOut() {
		return deltasOut;
	}

	public double getDeltaOutY(int nodeId) {
		return deltasOut.getValue(nodeId * 2 - 1);
	}

	public double getDeltaOutX(int nodeId) {
		return deltasOut.getValue(nodeId * 2 - 2);
	}

	public double getDeltaYMean(int elementId) {
		return (getDeltaOutY(getNodeId(elementId, 3)) + getDeltaOutY(getNodeId(elementId, 2)) + getDeltaOutY(getNodeId(
				elementId, 1))) / 3.0f;
	}

	// ///////////////////////////////////////////////////////////////
	// getter and helper for numberOfNodes

	public void incementNumberOfNodes() {
		numberOfNodes++;
	}

	public int getNumberOfNodes() {
		return numberOfNodes;
	}

	// ///////////////////////////////////////////////////////////////
	// getter and helper for numberOfElements

	public void incementNumberOfElements() {
		numberOfElements++;
	}

	public int getNumberOfElements() {
		return numberOfElements;
	}

}