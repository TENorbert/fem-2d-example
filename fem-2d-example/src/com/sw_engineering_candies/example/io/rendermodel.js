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


function ModelRenderer() {

	this.offset_x = 200;
	this.offset_y = 100;
	
	this.pointNull = new paper.Point(this.offset_x*4, this.offset_y );

	var delta = 0.2;
	var offset_x_scala = 10;
	var offset_y_scala = 350;
	var scala_size_x = 20.0;
	var scala_size_y = 150.0;

	this.factorForce = 0.05;
	this.factorDisplacement = 1000.0;

	this.beta = -90.0;
	this.gamma = 0.0;	
	
	this.orientation = 'normal portrait';

	this.minColor = -2.5;
	this.maxColor = 2.5;

	function getColor(mean) {
		mean = -1.0 * mean;
		red = Math.sin(mean + 2) * 127 + 128;
		green = Math.sin(mean + 1) * 127 + 128;
		blue = Math.sin(mean + 4) * 127 + 128;
		return '#' + toHex(red) + toHex(green) + toHex(blue);
	}

	function toHex(n) {
		return "0123456789ABCDEF".charAt((n - n % 16) / 16)
				+ "0123456789ABCDEF".charAt(n % 16);
	}

	ModelRenderer.prototype.draw_scala_color = function() {
		for ( var index = this.minColor; index < this.maxColor; index += delta) {
			var path = new paper.Path();
			path.add(new paper.Point(offset_x_scala, offset_y_scala
					+ (-index + delta) * scala_size_y));
			path.add(new paper.Point(offset_x_scala, offset_y_scala - index
					* scala_size_y));
			path.add(new paper.Point(offset_x_scala + scala_size_x,
					offset_y_scala - index * scala_size_y));
			path.add(new paper.Point(offset_x_scala + scala_size_x,
					offset_y_scala + (-index + delta) * scala_size_y));
			path.strokeWidth = 0.5;
			path.fillColor = getColor(index);
			path.selected = false;
		}
	}

	ModelRenderer.prototype.draw_scala_text = function() {
		x = offset_x_scala + scala_size_x * 1.2;
		for ( var index = this.minColor; index < this.maxColor; index += delta) {
			var point = new paper.Point(x, offset_y_scala
					+ (-index + delta / 2) * scala_size_y);
			var text = new paper.PointText(point);
			text.content = ' '
					+ Math
							.round(1000 * (this.minColor + (this.maxColor - this.minColor)
									* index / 2)) / 1000.0 + ' mm';
			text.fontSize = 11;
			text.justification = 'left';
			text.fillColor = 'white';
		}
	}

	ModelRenderer.prototype.draw_elements = function(elements) {

	   for ( var ele = elements.length - 1; ele >= 0; ele--) {
			var path = new paper.Path();
			path.strokeWidth = 0.5;
			path.strokeColor = '#0a0a0a';
			path.selected = false;

			var deltaArea = elements[ele][0].deltaArea;
					
			path.fillColor = getColor(deltaArea);

			for ( var nodeId = 0; nodeId < 3; nodeId++) {
				element = elements[ele][nodeId];

				var point = new paper.Point(element.x + this.offset_x + element.x_d
						* this.factorDisplacement, element.y + element.y_d
						* this.factorDisplacement + this.offset_y);

				path.add(point);

				if (element.x_fixed) {
					this.drawFixedVertical(point);
					if (30.0 < Math.abs(element.x_force)) {
						this.drawVector(point, point.add(new paper.Point(
								element.x_force * this.factorForce, 0)), true,
								(element.x_force > 0.0));
					}
				}

				if (element.y_fixed) {
					this.drawFixedHorizontal(point);
					if (30.0 < Math.abs(element.y_force)) {
						this.drawVector(point, point.add(new paper.Point(0,
								element.y_force * this.factorForce)), false,
								(element.y_force > 0.0));
					}
				}
			}			
		}

		
			this.draw_scala_color();
			this.draw_scala_text();
		
	}

	ModelRenderer.prototype.drawVector = function(vectorStart, vectorEnd,
			horizontal, positive) {
		var arrow = new paper.Path();
		arrow.strokeWidth = 0.75;
		arrow.strokeColor = '#FF0000';
		arrow.add(vectorStart);
		arrow.add(vectorEnd);
		
		var length = 5;
		var head = new paper.Path();
		head.strokeWidth = 0.75;
		head.strokeColor = '#FF0000';
		head.add(vectorEnd);
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

	ModelRenderer.prototype.drawFixedVertical = function(vectorEnd) {
		var length = 10;
		var head = new paper.Path();
		head.strokeWidth = 0.75;
		head.strokeColor = '#FFFFFF';
		head.add(vectorEnd);
		head.add(vectorEnd.add(new paper.Point(-length, -length * 0.75)));
		head.add(vectorEnd.add(new paper.Point(-length, length * 0.75)));
		head.add(vectorEnd);	
	}

	ModelRenderer.prototype.drawFixedHorizontal = function(vectorEnd) {
		var length = 10;
		var head = new paper.Path();
		head.strokeWidth = 0.75;
		head.strokeColor = '#FFFFFF';
		head.add(vectorEnd);
		head.add(vectorEnd.add(new paper.Point(length * 0.75, -length)));
		head.add(vectorEnd.add(new paper.Point(-length * 0.75, -length)));
		head.add(vectorEnd);	
	}

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

