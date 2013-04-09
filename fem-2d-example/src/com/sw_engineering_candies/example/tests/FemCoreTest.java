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
import org.junit.BeforeClass;
import org.junit.Test;

import com.sw_engineering_candies.example.core.Matrix;
import com.sw_engineering_candies.example.core.Model;
import com.sw_engineering_candies.example.core.Solver;

public class FemCoreTest {

	private static Solver fem = new Solver();

	@BeforeClass
	public static void setup() {
		fem.run(createDefaultModel(fem, 4, 16).toString());
	}

	@Test
	public void testInputForces() {
		final Matrix result = fem.getForcesIn().transpose();
		final Matrix expected = new Matrix(new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 500.0, 500.0 });
		Assert.assertTrue(expected.isEqual(result));
	}

	@Test
	public void testInputdeltas() {
		final double[] result = fem.getDeltasIn().transpose().getData()[0];
		final double[] expected = new double[] { 0.0, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, 0.0,
				0.0, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN,
				Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN,
				Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN,
				Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN,
				Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN,
				Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN,
				Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN,
				Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN,
				Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN,
				Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN,
				Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN,
				Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN,
				Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN,
				Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN,
				Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN };
		Assert.assertArrayEquals(expected, result, 0.0);
	}

	@Test
	public void testSumOfInputForces() {
		double resultX = 0.0;
		double resultY = 0.0;
		for (int i = 1; i <= fem.getNumberOfNodes(); i++) {
			resultX += fem.getForceOutX(i);
			resultY += fem.getForceOutY(i);
		}
		Assert.assertEquals(0.0, resultX, 1.0E-8);
		Assert.assertEquals(0.0, resultY, 1.0E-8);
	}

	@Test
	public void testSolutionForces() {
		final double[] result = fem.getForcesOut().getData()[0];
		final double[] expected = new double[] { -2499.9999999979577, -2.2737367544323206E-13, 0.0,
				2.2737367544323206E-13, 0.0, 0.0, 1999.9999999979375, -499.9999999993248, 1.5916157281026244E-12,
				-1.1368683772161603E-12, -9.094947017729282E-13, 4.547473508864641E-13, -4.547473508864641E-13,
				4.547473508864641E-13, 1.0231815394945443E-12, -4.547473508864641E-13, 5.4569682106375694E-12,
				3.637978807091713E-12, 2.7284841053187847E-12, 2.7284841053187847E-12, -1.8189894035458565E-12,
				-3.637978807091713E-12, -1.5916157281026244E-12, -1.8189894035458565E-12, 4.547473508864641E-13,
				-2.2737367544323206E-12, 9.094947017729282E-13, 2.7284841053187847E-12, 0.0, 9.094947017729282E-13,
				1.3642420526593924E-12, 5.4569682106375694E-12, -6.366462912410498E-12, -4.547473508864641E-12,
				1.8189894035458565E-12, -5.4569682106375694E-12, 3.637978807091713E-12, -1.4551915228366852E-11,
				-4.547473508864641E-13, -9.094947017729282E-13, 5.4569682106375694E-12, 1.4551915228366852E-11,
				5.4569682106375694E-12, 2.546585164964199E-11, -1.8189894035458565E-12, -1.8189894035458565E-11,
				3.637978807091713E-12, 7.275957614183426E-12, 1.8189894035458565E-12, 1.0913936421275139E-11,
				-3.637978807091713E-12, -1.8189894035458565E-11, -7.275957614183426E-12, -2.1827872842550278E-11,
				9.094947017729282E-13, -3.637978807091713E-12, -5.4569682106375694E-12, -2.000888343900442E-11,
				7.275957614183426E-12, -1.8189894035458565E-11, -3.637978807091713E-12, 2.1827872842550278E-11,
				-5.4569682106375694E-12, -1.0913936421275139E-11, 0.0, 3.637978807091713E-12, 1.4551915228366852E-11,
				7.275957614183426E-12, -1.0913936421275139E-11, -2.1827872842550278E-11, 7.275957614183426E-12,
				5.4569682106375694E-12, 1.4551915228366852E-11, -4.3655745685100555E-11, -2.1827872842550278E-11,
				-2.1827872842550278E-11, 2.9103830456733704E-11, -2.9103830456733704E-11, 1.8189894035458565E-12,
				-2.1827872842550278E-11, -1.4551915228366852E-11, 1.0913936421275139E-11, 0.0, 5.093170329928398E-11,
				0.0, -4.3655745685100555E-11, 5.4569682106375694E-12, 3.637978807091713E-11, 1.4551915228366852E-11,
				-4.3655745685100555E-11, 2.1827872842550278E-11, -7.275957614183426E-12, 0.0, 3.637978807091713E-11,
				7.275957614183426E-12, 4.001776687800884E-11, 1.0913936421275139E-11, 3.637978807091713E-12,
				-2.9103830456733704E-11, -5.820766091346741E-11, 0.0, 1.4551915228366852E-11, -3.637978807091713E-12,
				4.3655745685100555E-11, -2.9103830456733704E-11, -1.8189894035458565E-11, 7.275957614183426E-12,
				-1.0913936421275139E-10, -7.275957614183426E-12, -7.275957614183426E-12, 3.637978807091713E-12,
				5.4569682106375694E-11, 2.9103830456733704E-11, 7.275957614183426E-11, 0.0, -5.820766091346741E-11,
				-2.1827872842550278E-11, 2.0372681319713593E-10, -1.0913936421275139E-11, -3.637978807091713E-11,
				1.4551915228366852E-11, 1.4551915228366852E-11, 7.275957614183426E-12, 1.4551915228366852E-11,
				-7.275957614183426E-12, 5.820766091346741E-11, 499.9999999999927, 499.99999999998545 };

		Assert.assertArrayEquals(expected, result, 1E-3);
	}

	@Test
	public void testSolutiondeltas() {
		final double[] result = fem.getDeltasOut().getData()[0];
		final double[] expected = new double[] { 0.0, 0.0017031680292120116, 0.0021579679212843277,
				8.460823706439331E-4, -5.761780677106217E-4, 4.7074330889380916E-4, 0.0, 0.0, 0.0028929145129768584,
				0.003026482216181817, 0.0023919054534906757, 0.002776465304468151, -6.68101819861606E-4,
				0.002716261483642727, -0.0022182142700234706, 0.002936011119322781, 0.0052996670847178734,
				0.0066574695124418555, 0.002783038549572265, 0.006431775498035073, -9.8447761606071E-4,
				0.006421511751678502, -0.004154791683229952, 0.00664949169778981, 0.007408709567813943,
				0.01169598571587718, 0.0032662295449016605, 0.011477588799609383, -0.0013500806200735934,
				0.011469457842296131, -0.005894427934446791, 0.011681051954535856, 0.009275471647048962,
				0.017945395145098048, 0.003785377130837217, 0.01773879144170817, -0.0017189819643994807,
				0.017729485553173906, -0.007453968597892965, 0.017921758980525258, 0.010931450511269378,
				0.02526968964030741, 0.004303615680947874, 0.02507890544950223, -0.0020665914890442756,
				0.02506862272361978, -0.008840638781875671, 0.025240492412915642, 0.01239542044945774,
				0.03354580788049062, 0.004798818205607814, 0.033373326897232644, -0.002378954179433005,
				0.03336245117799071, -0.010057955248803227, 0.03351333895801901, 0.013678794952197208,
				0.04265627413645132, 0.005257830585394586, 0.042503557706775275, -0.0026482469436652175,
				0.04249243639235162, -0.011107428300484916, 0.0426220389363448, 0.01478872104200629,
				0.05248666593414168, 0.005672801720236963, 0.05235455293874562, -0.00287032454169979,
				0.05234352662150719, -0.011989306735112724, 0.0524517272078594, 0.015729987427695755,
				0.06292424636064911, 0.006038988516671217, 0.06281319007128654, -0.003043405733074985,
				0.062802650041323, -0.012702844583116449, 0.0628894865637538, 0.016506282164010302, 0.0738571119958351,
				0.006353443076390625, 0.07376731057744125, -0.003167473823782429, 0.07375778221110081,
				-0.013246229106844734, 0.0738234576862758, 0.01712113191336064, 0.08517359409336649,
				0.0066142260641994955, 0.08510505093508826, -0.0032442034223351944, 0.08509731606740242,
				-0.013616187417206115, 0.08514225794067534, 0.017578769407277393, 0.09676167382962331,
				0.00681992684417974, 0.09671422302352137, -0.0032773779369482834, 0.09670957397953275,
				-0.013807196306778515, 0.09673460445789113, 0.01788519097531485, 0.10850765572098428,
				0.006969336323958344, 0.10848117786224144, -0.0032740182009985755, 0.10848263218213526,
				-0.013810202046692935, 0.10848995443561667, 0.018050308491208054, 0.12028627936055325,
				0.00706077168512646, 0.12028351214453782, -0.003247962857532004, 0.12030789494387849,
				-0.013612340268242937, 0.12031417788051164, 0.01809916197387787, 0.1318570432007111,
				0.0070884097459334204, 0.1318980137892731, -0.003241615754747537, 0.13209600377247177,
				-0.01325416976851726, 0.1324541742936403 };
		Assert.assertArrayEquals(expected, result, 1E-9);
	}

	@Test
	public void testSolutiondeltasMeanX() {
		Assert.assertEquals(0.001858577538679254, fem.getDeltaYMean(1), 0.0);
		Assert.assertEquals(0.08132004973786383, fem.getDeltaYMean(fem.getNumberOfNodes()), 0.0);
	}

	static StringBuffer createDefaultModel(Model femCore, int maxRows, int maxCols) {
		final StringBuffer nodeText = new StringBuffer();
		final int scaleFactorX = 12;
		final int scaleFactorY = 12;
		for (int col = 1; col <= maxCols; col++) {
			for (int row = 1; row <= maxRows; row++) {
				final int nodeId = row + maxRows * (col - 1);
				nodeText.append("N, ").append(nodeId).append(", ").append(col * scaleFactorX).append(", ")
						.append(row * scaleFactorY).append(",\n");
			}
		}

		for (int col = 1; col < maxCols; col++) {
			for (int row = 1; row < maxRows; row++) {
				final int firstElementId = row * 2 - 1 + (maxRows - 1) * 2 * (col - 1);
				final int secondElementId = row * 2 + (maxRows - 1) * 2 * (col - 1);
				final int node1Id = row + maxRows * (col - 1);
				final int node2Id = row + maxRows * col;
				final int node3Id = row + 1 + maxRows * (col - 1);
				final int node4Id = row + 1 + maxRows * (col + 1 - 1);
				nodeText.append("E, ").append(firstElementId).append(", ").append(node1Id).append(", ").append(node2Id)
						.append(", ").append(node3Id).append(",\n");
				nodeText.append("E, ").append(secondElementId).append(", ").append(node2Id).append(", ")
						.append(node4Id).append(", ").append(node3Id).append(",\n");
			}
		}

		nodeText.append("D, ").append(maxRows).append(", x, ").append(0).append(",\n");
		nodeText.append("D, ").append(maxRows).append(", y, ").append(0).append(",\n");
		nodeText.append("D, ").append(1).append(", x, ").append(0).append(",\n");

		nodeText.append("F, ").append(maxRows * maxCols).append(", y, ").append(500.0).append(",\n");
		nodeText.append("F, ").append(maxRows * maxCols).append(", x, ").append(500.0).append(",\n");

		return nodeText;
	}

}
