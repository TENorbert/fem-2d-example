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

includeJavaScript('paper.js');

// Next line will be replaced by the JSON model, don't change it ...
XX_MODEL_PLACE_HOLDER;


// Dimensions for display
var height = 400;
var width = 750;

// Scaling factors for stretch or compress during painting
var factorX = 2.5;
var factorY = 2.5;
var factorForce = 0.0005;
var factordelta = 10;

// Offset for model
var offset_x = 40;
var offset_y = 40;

// Offset and size for scale boxs
var offset_x_scale = width - 150; 
var scale_size_x = 25;
var scale_size_y = 250;

// Fixed scale
var minColor = 10;
var maxColor = 0;

function main(elements) {

	// draw all elements
	for ( var ele = 0; ele < elements.length; ele++) {
		var path = new paper.Path();
		path.closed = true;
		path.strokeColor = '#FFFFFF';
		path.strokeWidth = 0.2;
		path.selected = false;
		path.opacity = 0.9;
		for ( var nodeId = 0; nodeId < elements[ele].length; nodeId++) {
			var element = elements[ele][nodeId];
			path.fillColor = getColor(element.y_delta_mean);
			var point = new paper.Point(element.x * factorX + offset_x
					+ element.x_delta * factordelta, element.y * factorY
					+ element.y_delta * factordelta + offset_y);
			path.add(point);
		}
		ele.first = true;
	}

	for ( var ele = 0; ele < elements.length; ele++) {
		var path = new paper.Path();
		for ( var nodeId = 0; nodeId < elements[ele].length; nodeId++) {
			var element = elements[ele][nodeId];
			var point = new paper.Point(element.x * factorX + offset_x
					+ element.x_delta * factordelta, element.y * factorY
					+ element.y_delta * factordelta + offset_y);

			// ensure that all is just drawn once
			if (element.first) {

				// var text = new paper.PointText(point);
				// text.content = ' ' + element.id;
				// text.fontSize = 6;
				// text.justification = 'left';

				if (10000.0 < Math.abs(element.x_force)) {
					drawVector(point, point.add(new paper.Point(element.x_force
							* factorForce, 0)), true, (element.x_force > 0.0));
				}

				if (10000.0 < Math.abs(element.y_force)) {
					drawVector(point, point.add(new paper.Point(0,
							element.y_force * factorForce)), false,
							(element.y_force > 0.0));
				}

				drawFixed(point, element.y_fixed, element.x_fixed);
			}
		}
	}

	drawBoxes();
	drawScale();
}

function drawVector(vectorStart, vectorEnd, horizontal, positive) {
	var arrow = new paper.Path();
	arrow.strokeWidth = 1.5;
	arrow.strokeColor = '#FF0000';
	arrow.add(vectorStart);
	arrow.add(vectorEnd);
	arrow.opacity = 1;

	var length = 5;
	var head = new paper.Path();
	head.strokeWidth = 1.0;
	head.strokeColor = '#FF0000';
	head.add(vectorEnd);
	head.opacity = 1;
	if (horizontal && !positive) {
		head.add(vectorEnd.add(new paper.Point(length, -length)));
		head.add(vectorEnd);
		head.add(vectorEnd.add(new paper.Point(length, length)));
	} else if (horizontal && positive) {
		head.add(vectorEnd.add(new paper.Point(-length, -length)));
		head.add(vectorEnd);
		head.add(vectorEnd.add(new paper.Point(-length, length)));
	} else if (!horizontal && positive) {
		head.add(vectorEnd.add(new paper.Point(length, -length)));
		head.add(vectorEnd);
		head.add(vectorEnd.add(new paper.Point(-length, -length)));
	} else if (!horizontal && !positive) {
		head.add(vectorEnd.add(new paper.Point(-length, length)));
		head.add(vectorEnd);
		head.add(vectorEnd.add(new paper.Point(length, length)));
	}
}

function drawFixed(vectorEnd, horizontal, vertical) {
	var length = 7;
	if (horizontal) {
		var head = new paper.Path();
		head.strokeWidth = 0.5;
		head.strokeColor = '#000000';
		head.add(vectorEnd);
		head.add(vectorEnd.add(new paper.Point(length * 0.75, length)));
		head.add(vectorEnd.add(new paper.Point(-length * 0.75, length)));
		head.add(vectorEnd);
	}

	if (vertical) {
		var head = new paper.Path();
		head.strokeWidth = 0.5;
		head.strokeColor = '#000000';
		head.add(vectorEnd);
		head.add(vectorEnd.add(new paper.Point(-length, -length * 0.75)));
		head.add(vectorEnd.add(new paper.Point(-length, length * 0.75)));
		head.add(vectorEnd);
	}
}

function drawBoxes() {
	var path = new paper.Path();
	path.closed = true;
	path.strokeWidth = 0.25;
	path.strokeColor = '#000000';
	path.selected = false;
	path.add(new paper.Point(0, 0));
	path.add(new paper.Point(0, height));
	path.add(new paper.Point(offset_x_scale - 5, height));
	path.add(new paper.Point(offset_x_scale - 5, 0));

	var path2 = new paper.Path();
	path2.closed = true;
	path2.strokeWidth = 0.25;
	path2.strokeColor = '#000000';
	path2.selected = false;
	path2.add(new paper.Point(width, 0));
	path2.add(new paper.Point(width, height));
	path2.add(new paper.Point(offset_x_scale, height));
	path2.add(new paper.Point(offset_x_scale, 0));
}

function drawScale() {
	var path = new paper.Path();
	var text = new paper.PointText(new paper.Point(10+offset_x_scale, 20));
	text.content = 'Average delta in y: ';
	text.justification = 'left';
	var delta = 0.10;
	for ( var index = 0.0; index <= 1.0; index += delta) {
		var path = new paper.Path();
		path.add(new paper.Point(10+offset_x_scale, 				40	+ index * scale_size_y));
		path.add(new paper.Point(10+offset_x_scale, 				40  + (index + delta) * scale_size_y));
		path.add(new paper.Point(10+offset_x_scale + scale_size_x, 40	+ (index + delta) * scale_size_y));
		path.add(new paper.Point(10+offset_x_scale + scale_size_x, 40	+ index* scale_size_y));
		var text = new paper.PointText(new paper.Point(10+offset_x_scale+ scale_size_x * 1.2, 40 + (index + delta / 2) * scale_size_y));

		var value = (minColor + (maxColor - minColor) * index);
		text.content = ' ' + (Math.round(100 * value) / 100.0) + ' mm';
		text.fontSize = 8;
		text.justification = 'left';
		path.closed = true;
		path.strokeWidth = 0.5;
		path.fillColor = getColor(value);
		path.opacity = 0.8;
		path.selected = false;
	}
}

// Color code helper /////////////////////////////////////////////////////////

var frequency = 0.5;

function getColor(value) {
	red = Math.sin(2 - frequency * value) * 127 + 128;
	green = Math.sin(1 - frequency * value) * 127 + 128;
	blue = Math.sin(4 - frequency * value) * 127 + 128;
	return '#' + integerToHex(red) + integerToHex(green) + integerToHex(blue);
}

function integerToHex(n) {
	n = Math.max(0, Math.min(parseInt(n, 10), 255));
	charFirst = "0123456789ABCDEF".charAt((n - n % 16) / 16);
	charSecond = "0123456789ABCDEF".charAt(n % 16);
	return charFirst + charSecond;
}

// Include helper ////////////////////////////////////////////////////////////

function includeJavaScript(filename) {
	// helper for lazy load of additional JavaScript libraries
	var head = document.getElementsByTagName('head')[0];
	var script = document.createElement('script');
	script.src = filename;
	script.type = 'text/javascript';
	head.appendChild(script)
}

