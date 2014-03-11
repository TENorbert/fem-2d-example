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

package com.sw_engineering_candies.example.io;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.sw_engineering_candies.example.core.Model;

public class ModelUtil {

	public static int parseModelFromString(Model femCore, String input) {

		class Point {
			public double x = 0.0f;
			public double y = 0.0f;
		}
		final List<Point> tempNodes = new LinkedList<Point>();
		tempNodes.add(new Point());

		int bandwidthExpected = 0;

		final List<Integer[]> tempElements = new LinkedList<Integer[]>();
		tempElements.add(new Integer[5]);

		final String[] lines = input.toString().split("\\n");
		for (final String line : lines) {
			if (!line.trim().isEmpty()) {
				final String[] args = line.split(",");
				if (0 == args[0].trim().compareToIgnoreCase("N")) {
					femCore.incementNumberOfNodes();
					final int number = Integer.valueOf(args[1].trim());
					for (int index = tempNodes.size(); index <= number; index++) {
						tempNodes.add(new Point());
					}

					final Integer first = Integer.valueOf(args[2].trim());
					final Integer second = Integer.valueOf(args[3].trim());

					tempNodes.get(number).x = first;
					tempNodes.get(number).y = second;
				}
				if (0 == args[0].trim().compareToIgnoreCase("E")) {
					femCore.incementNumberOfElements();
					final int number = Integer.valueOf(args[1].trim());
					for (int index = tempElements.size(); index <= number; index++) {
						tempElements.add(new Integer[5]);
					}

					final Integer first = Integer.valueOf(args[2].trim());
					final Integer second = Integer.valueOf(args[3].trim());
					final Integer third = Integer.valueOf(args[4].trim());

					tempElements.get(femCore.getNumberOfElements())[1] = number;
					tempElements.get(femCore.getNumberOfElements())[2] = first;
					tempElements.get(femCore.getNumberOfElements())[3] = second;
					tempElements.get(femCore.getNumberOfElements())[4] = third;

					final int max = Math.max(Math.max(first, second), third);
					final int min = Math.min(Math.min(first, second), third);
					final int bandwidthOfElement = (1 + max - min) * 2;
					bandwidthExpected = Math.max(bandwidthExpected, bandwidthOfElement);
				}
				if (0 == args[0].trim().compareToIgnoreCase("D")) {

					if (femCore.getDeltasIn() == null) {
						femCore.initDeltasIn();
					}

					final int number = Integer.valueOf(args[1].trim());
					if (0 == args[2].trim().compareToIgnoreCase("x")) {
						femCore.setDeltaInX(number, Double.valueOf(args[3].trim()));
					}
					if (0 == args[2].trim().compareToIgnoreCase("y")) {
						femCore.setSeltaInY(number, Double.valueOf(args[3].trim()));
					}
				}
				if (0 == args[0].trim().compareToIgnoreCase("F")) {

					if (femCore.getForcesIn() == null) {
						femCore.initForcesIn();
					}

					final int number = Integer.valueOf(args[1].trim());
					if (0 == args[2].trim().compareToIgnoreCase("x")) {
						femCore.setForceInX(number, Double.valueOf(args[3].trim()));
					}
					if (0 == args[2].trim().compareToIgnoreCase("y")) {
						femCore.setForceInY(number, Double.valueOf(args[3].trim()));
					}
				}
			}
		}

		femCore.initNodesByElement();

		for (int i = 1; i <= femCore.getNumberOfElements(); i++) {
			femCore.setX(tempElements.get(i)[1], 1, tempNodes.get(tempElements.get(i)[2]).x);
			femCore.setX(tempElements.get(i)[1], 2, tempNodes.get(tempElements.get(i)[3]).x);
			femCore.setX(tempElements.get(i)[1], 3, tempNodes.get(tempElements.get(i)[4]).x);

			femCore.setY(tempElements.get(i)[1], 1, tempNodes.get(tempElements.get(i)[2]).y);
			femCore.setY(tempElements.get(i)[1], 2, tempNodes.get(tempElements.get(i)[3]).y);
			femCore.setY(tempElements.get(i)[1], 3, tempNodes.get(tempElements.get(i)[4]).y);

			femCore.setNodeId(tempElements.get(i)[1], 1, tempElements.get(i)[2]);
			femCore.setNodeId(tempElements.get(i)[1], 2, tempElements.get(i)[3]);
			femCore.setNodeId(tempElements.get(i)[1], 3, tempElements.get(i)[4]);
		}
		return bandwidthExpected;
	}

	public static String getModelAsJSON(Model model) {
		final HashMap<Integer, Boolean> nodeIds = new HashMap<Integer, Boolean>();
		final StringBuilder pre = new StringBuilder("var elements = [");
		final int numberOfElements = model.getNumberOfElements();
		for (int elementId = 1; elementId <= numberOfElements; elementId++) {
			pre.append("[");
			for (int cornerId = 1; cornerId < 4; cornerId++) {
				final int nodeId = model.getNodeId(elementId, cornerId);
				pre.append("\n{\"id\": " + nodeId //
						+ ", \"x_force\" : " + model.getForceOutX(nodeId) //
						+ ", \"y_force\" : " + model.getForceOutY(nodeId) //
						+ ", \"x_delta\" : " + model.getDeltaOutX(nodeId) //
						+ ", \"y_delta\" : " + model.getDeltaOutY(nodeId) //
						+ ", \"x_fixed\" : " + model.isFixedX(nodeId) //
						+ ", \"y_fixed\" : " + model.isFixedY(nodeId) //
						+ ", \"first\" : " + !nodeIds.containsKey(nodeId) //
						+ ", \"x\" : " + model.getX(elementId, cornerId) //
						+ ", \"y\" : " + model.getY(elementId, cornerId) //
						+ ", \"y_delta_mean\" : " + model.getDeltaYMean(elementId) //
						+ "  }");
				if (cornerId <= 3) {
					pre.append(',');
				}
				nodeIds.put(nodeId, true);
			}
			pre.append("]");
			if (elementId < numberOfElements + 1) {
				pre.append(',');
			}
		}
		pre.append("];");
		return pre.toString();
	}

	public static String createDefaultModel(Model femCore) {
		final StringBuffer nodeText = new StringBuffer();
		final int maxCols = 100;
		final int maxRows = 20;
		final int scaleFactorX = 2;
		final int scaleFactorY = 2;
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

		nodeText.append("D, ").append(1).append(", y, ").append(0).append(",\n");
		for (int row = 1; row <= maxRows; row++) {
			nodeText.append("D, ").append(row).append(", x, ").append(0).append(",\n");
		}

		nodeText.append("F, ").append(maxRows * maxCols).append(", y, ").append(30000.0).append(",\n");

		return nodeText.toString();
	}

}
