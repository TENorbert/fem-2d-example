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

package com.sw_engineering_candies.example.tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sw_engineering_candies.example.core.Matrix;
import com.sw_engineering_candies.example.core.MatrixBanded;

public class MatrixBandTest {

	MatrixBanded sysmetricBandMatrix;
	Matrix symetricMatrix;
	Matrix b;

	@Before
	public void setup() {
		sysmetricBandMatrix = new MatrixBanded(new double[][] { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 }, { 10, 11, 0 },
				{ 12, 0, 0 } });
		symetricMatrix = new Matrix(new double[][] { { 1, 2, 3, 0, 0 }, { 2, 4, 5, 6, 0 }, { 3, 5, 7, 8, 9 },
				{ 0, 6, 8, 10, 11 }, { 0, 0, 9, 11, 12 } });
		b = new Matrix(new double[][] { { 2, 4, 6, 8, 10 } }).transpose();
	}

	@Test
	public void testSolveBandMatrixTimes() {
		final Matrix x = new Matrix(new double[] { -0.30023578726748745, -0.15614356824731482, 0.8708409745873722,
				0.1451401624312287, 0.04715745349751121 });
		final Matrix resultBandMatix = sysmetricBandMatrix.times(x.transpose());
		Assert.assertArrayEquals(b.getData()[0], resultBandMatix.getData()[0], 1E-10);
	}

	@Test
	public void testSolveConjungateGradientBandMatrix() {
		final Matrix result = symetricMatrix.solve(b);
		final double[] expected = new double[] { -0.30023578726748745, -0.15614356824731482, 0.8708409745873722,
				0.1451401624312287, 0.04715745349751121 };
		Assert.assertArrayEquals(expected, result.transpose().getData()[0], 0.0);

		final Matrix resultBandMatix = MatrixBanded.solve(sysmetricBandMatrix, b);
		Assert.assertArrayEquals(expected, resultBandMatix.getData()[0], 1E-11);
	}

	@Test
	public void testGetValueBandMatrix() {
		for (int row = 0; row < 5; row++) {
			for (int col = 0; col < 5; col++) {
				final double expected = symetricMatrix.getData()[row][col];
				final double result = MatrixBanded.getValue(sysmetricBandMatrix, row, col);
				Assert.assertEquals(expected, result, 0.0);
			}
		}
	}

	@Test
	public void testSetValueBandMatrix() {
		for (int row = 0; row < 5; row++) {
			for (int col = 0; col < 5; col++) {
				double expected = symetricMatrix.getData()[row][col];
				if (expected != 0.0) {
					expected += Math.PI;
					MatrixBanded.setValue(sysmetricBandMatrix, row, col, expected);
				}
				final double result = MatrixBanded.getValue(sysmetricBandMatrix, row, col);
				Assert.assertEquals(expected, result, 0.0);
			}
		}
	}

}
