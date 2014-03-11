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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import com.sw_engineering_candies.example.core.Model;
import com.sw_engineering_candies.example.core.Solver;

public class WebServer extends Thread {

	private static final String NL = System.getProperty("line.separator");

	private final int port = 1234;

	private final Model sf;

	public WebServer(Model sf) {
		this.sf = sf;
		String hostname = "localhost";

		try {
			try {
				final InetAddress addr = InetAddress.getLocalHost();
				hostname = addr.getCanonicalHostName();
			} catch (final UnknownHostException e) {
				System.out.println("Error - " + e.getMessage() + NL);
			}

			final InetAddress addr = InetAddress.getLocalHost();
			hostname = addr.getCanonicalHostName();

			addr.getCanonicalHostName();
		} catch (final UnknownHostException e) {
			System.out.println(e.getMessage());
		}
		// System.out.println("listen at url: http://localhost:1234/index.html");
		System.out.println("webserver started        [http://" + hostname + ':' + port + "/index.html]");

		final Thread serverThread = this;
		serverThread.start();
	}

	@Override
	public void run() {
		Socket connection = null;
		while (true) {
			try {
				final ServerSocket server = new ServerSocket(port);
				connection = server.accept();
				final OutputStream out = new BufferedOutputStream(connection.getOutputStream());
				final InputStream in = new BufferedInputStream(connection.getInputStream());
				final String request = readFirstLineOfRequest(in).toString();
				System.out.println("get request " + request.toString());

				if (request.toLowerCase().startsWith("get /index.html")) {
					// Create content of response
					final String contentText = getPage("com/sw_engineering_candies/example/io/ResultTemplate.html")
							.toString();
					final byte[] content = contentText.getBytes();
					// For HTTP/1.0 or later send a MIME header
					if (request.indexOf("HTTP/") != -1) {
						final String headerString = "HTTP/1.0 200 OK" + NL + "Server: FEM 1.0" + NL
								+ "Content-length: " + content.length + NL + "Content-type: text/html" + NL + NL;
						final byte[] header = headerString.getBytes("ASCII");
						out.write(header);
					}
					out.write(content);
					out.flush();
				} else if (request.toLowerCase().startsWith("get /paper.js")) {
					// Create content of response
					final String contentText = getPage("com/sw_engineering_candies/example/io/paper.js").toString();
					final byte[] content = contentText.getBytes();
					// For HTTP/1.0 or later send a MIME header
					if (request.indexOf("HTTP/") != -1) {
						final String headerString = "HTTP/1.0 200 OK" + NL + "Server: FEM 1.0" + NL
								+ "Content-length: " + content.length + NL + "Content-type: text/javascript" + NL + NL;
						final byte[] header = headerString.getBytes("ASCII");
						out.write(header);
					}
					out.write(content);
					out.flush();
				} else if (request.toLowerCase().startsWith("get /rendermodel.js")) {
					// Create content of response
					final String contentText = getPage("com/sw_engineering_candies/example/io/renderModel.js")
							.toString();
					final byte[] content = contentText.getBytes();
					// For HTTP/1.0 or later send a MIME header
					if (request.indexOf("HTTP/") != -1) {
						final String headerString = "HTTP/1.0 200 OK" + NL + "Server: FEM 1.0" + NL
								+ "Content-length: " + content.length + NL + "Content-type: text/javascript" + NL + NL;
						final byte[] header = headerString.getBytes("ASCII");
						out.write(header);
					}
					out.write(content);
					out.flush();
				} else if (request.startsWith("GET /terminate")) {
					server.close();
					throw new RuntimeException("terminate");
				}
				// Close the socket
				connection.close();
				in.close();
				out.close();
				server.close();
			} catch (final IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	private StringBuffer readFirstLineOfRequest(final InputStream in) throws IOException {
		final StringBuffer request;
		request = new StringBuffer(100);
		while (true) {
			final int character = in.read();
			if (character == '\n' || character == '\r' || character == -1) {
				break;
			}
			request.append((char) character);
		}
		return request;
	}

	public StringBuffer getPage(String filepath) {
		final StringBuffer fw = new StringBuffer(1000);
		try {
			final InputStream inputstream = this.getClass().getClassLoader().getResourceAsStream(filepath);
			final InputStreamReader is = new InputStreamReader(inputstream);
			final BufferedReader br = new BufferedReader(is);
			for (String s = br.readLine(); s != null; s = br.readLine()) {
				if (s.contains("XX_MODEL_PLACE_HOLDER")) {
					fw.append(ModelUtil.getModelAsJSON(sf));
				} else {
					fw.append(s).append('\n');
				}
			}
			inputstream.close();
		} catch (final Exception xc) {
			xc.printStackTrace();
		}
		return fw;
	}

	public static void main(String[] args) {
		final Solver fem = new Solver();
		new WebServer(fem);

		String createDefaultModel = ModelUtil.createDefaultModel(fem);
		fem.run(createDefaultModel);		
	}
}
