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

import com.sw_engineering_candies.example.io.ModelUtil;

public class Solver extends Model {

	// Same for all elements
	private static final Matrix MATRIX_W = createElasticityMatrix();

	// Thickness of 2D structure in mm
	private static final double THICKNESS = 10.0f;

	// Poisson's Ratio of material, e.g. steel=0.27–0.30
	private static final double POISSON_RATIO = 0.2f;

	// Young's Modulus of material in N/mm^2
	private static final double YOUNGS_MODULUS = 1.6E+05f;

	public void run(String input) {
		// The bandwidth is the maximal nodeId difference in the model
		final int bandWidth = ModelUtil.parseModelFromString(this, input);

		// Stiffness matrix of all elements
		final double[][] stiffnesMatrix = createSystemStiffnessMatrix(bandWidth);
		final MatrixBanded stiffnessOriginal = new MatrixBanded(stiffnesMatrix);

		// Stiffness matrix with replacement of known forces
		rearangeGlobalStiffnesMatrix(stiffnesMatrix);
		final MatrixBanded stiffnessRearanged = new MatrixBanded(stiffnesMatrix);

		// Solve the linear equations
		setDeltaOut(MatrixBanded.solve(stiffnessRearanged, getForcesIn()));
		setForcesOut(stiffnessOriginal.times(getDeltasOut().transpose()).transpose());
	}

	private double[][] createSystemStiffnessMatrix(int bandWidth) {
		// will be done as band matrix to save memory and improve speed
		final double[][] result = new double[getNumberOfNodes() * 2][bandWidth];
		for (int elementID = 1; elementID <= getNumberOfElements(); elementID++) {
			final Matrix Ke = createElementStiffnessMatrix(elementID);
			// insert each element stiffness matrix into the system matrix
			for (int i = 1; i <= 3; i++) {
				for (int j = 1; j <= 3; j++) {
					final int col = getNodeId(elementID, i) * 2;
					final int row = getNodeId(elementID, j) * 2;
					// put just the right side of symmetric matrix
					if (col - row >= 0) {
						result[row - 2][col - row] += Ke.getValue(i * 2 - 2, j * 2 - 2);
						result[row - 2][col - row + 1] += Ke.getValue(i * 2 - 1, j * 2 - 2);
						result[row - 1][col - row] += Ke.getValue(i * 2 - 1, j * 2 - 1);
					}
					if (col - row >= 1) {
						result[row - 1][col - row - 1] += Ke.getValue(i * 2 - 2, j * 2 - 1);
					}
				}
			}
		}
		return result;
	}

	private Matrix createElementStiffnessMatrix(int elementId) {
		final double area = calculateElementArea(elementId);
		final Matrix matrixB = createDeltaDifferentiationMatrix(elementId, area);
		return matrixB.times(MATRIX_W).times(matrixB.transpose()).mult(area * THICKNESS);
	}

	private double calculateElementArea(int elementId) {
		return 0.5f * ((getX(elementId, 3) - getX(elementId, 2)) * (getY(elementId, 1) - getY(elementId, 2)) + (getX(
				elementId, 1) - getX(elementId, 2)) * (getY(elementId, 2) - getY(elementId, 3)));
	}

	private Matrix createDeltaDifferentiationMatrix(int elementId, double area) {
		final double factor = 1.0 / (area * 2);
		final Matrix result = new Matrix(6, 3);
		result.setValue(0, 0, factor * (getY(elementId, 2) - getY(elementId, 3)));
		result.setValue(1, 0, 0);
		result.setValue(2, 0, factor * (getY(elementId, 3) - getY(elementId, 1)));
		result.setValue(3, 0, 0);
		result.setValue(4, 0, factor * (getY(elementId, 1) - getY(elementId, 2)));
		result.setValue(5, 0, 0);

		result.setValue(0, 1, 0);
		result.setValue(1, 1, factor * (getX(elementId, 3) - getX(elementId, 2)));
		result.setValue(2, 1, 0);
		result.setValue(3, 1, factor * (getX(elementId, 1) - getX(elementId, 3)));
		result.setValue(4, 1, 0);
		result.setValue(5, 1, factor * (getX(elementId, 2) - getX(elementId, 1)));

		result.setValue(0, 2, factor * (getX(elementId, 3) - getX(elementId, 2)));
		result.setValue(1, 2, factor * (getY(elementId, 2) - getY(elementId, 3)));
		result.setValue(2, 2, factor * (getX(elementId, 1) - getX(elementId, 3)));
		result.setValue(3, 2, factor * (getY(elementId, 3) - getY(elementId, 1)));
		result.setValue(4, 2, factor * (getX(elementId, 2) - getX(elementId, 1)));
		result.setValue(5, 2, factor * (getY(elementId, 1) - getY(elementId, 2)));
		return result;
	}

	private void rearangeGlobalStiffnesMatrix(double[][] K) {
		final int bandbreite = K[0].length;
		for (int nodeID = 1; nodeID <= getNumberOfNodes(); nodeID++) {
			if (isFixedX(nodeID)) {
				final int currentRow = nodeID * 2 - 1;
				for (int index = 1; index <= getNumberOfNodes(); index++) {
					final int row = index * 2 - 1;
					// upper part of equations
					if (currentRow > row) {
						setForceInX(index, getForceInX(index) - getDeltaInX(nodeID) * K[row - 1][currentRow - row]);
						setForceInY(index, getForceInY(index) - getDeltaInX(nodeID) * K[row][currentRow - row - 1]);
						K[row - 1][currentRow - row] = 0;
						K[row][currentRow - row - 1] = 0;
					}
					// lower part of equations
					if (currentRow <= row) {
						if (row - currentRow + 1 <= bandbreite) {
							setForceInX(index, getForceInX(index) - getDeltaInX(nodeID)
									* K[currentRow - 1][row - currentRow]);
							setForceInY(index, getForceInY(index) - getDeltaInX(nodeID)
									* K[currentRow - 1][row - currentRow + 1]);
							K[currentRow - 1][row - currentRow] = 0;
							K[currentRow - 1][row - currentRow + 1] = 0;
						}
					}
				}
				K[currentRow - 1][0] = 1;
				setForceInX(nodeID, getDeltaInX(nodeID));
			}
			if (isFixedY(nodeID)) {
				final int currentRow = nodeID * 2;
				for (int index = 1; index <= getNumberOfNodes(); index++) {
					final int row = index * 2;
					// upper part of equations
					if (currentRow >= row) {
						setForceInX(index, getForceInX(index) - getDeltaInY(nodeID) * K[row - 2][currentRow - row + 1]);
						setForceInY(index, getForceInY(index) - getDeltaInY(nodeID) * K[row - 1][currentRow - row + 1]);
						K[row - 2][currentRow - row + 1] = 0;
						K[row - 1][currentRow - row] = 0;
					}
					if (currentRow == row) {
						K[currentRow - 1][1] = 0;
					}
					// lower part of equations
					if (currentRow < row) {
						if (row - currentRow + 1 <= bandbreite) {
							setForceInX(index, getForceInX(index) - getDeltaInY(nodeID)
									* K[currentRow - 1][row - currentRow]);
							setForceInY(index, getForceInY(index) - getDeltaInY(nodeID)
									* K[currentRow - 1][row - currentRow + 1]);
							K[currentRow - 1][row - currentRow] = 0;
							K[currentRow - 1][row - currentRow + 1] = 0;
						}
					}
				}
				setForceInY(nodeID, getDeltaInY(nodeID));
				K[currentRow - 1][0] = 1;
			}
		}

		// Now all forces are known and will be set to zero
		for (int nodeId = 0; nodeId < getNumberOfNodes() * 2; nodeId++) {
			if (Double.isNaN(getForcesIn().getValue(nodeId, 0))) {
				getForcesIn().setValue(nodeId, 0, 0.0f);
			}
		}
	}

	private static Matrix createElasticityMatrix() {
		final double factor = YOUNGS_MODULUS / (1 - POISSON_RATIO * POISSON_RATIO);
		final Matrix result = new Matrix(3, 3);
		result.setValue(0, 0, factor);
		result.setValue(1, 0, factor * POISSON_RATIO);
		result.setValue(2, 0, 0);
		result.setValue(0, 1, factor * POISSON_RATIO);
		result.setValue(1, 1, factor);
		result.setValue(2, 1, 0);
		result.setValue(0, 2, 0);
		result.setValue(1, 2, 0);
		result.setValue(2, 2, factor * POISSON_RATIO * (1 - POISSON_RATIO) / 2);
		return result;
	}
}