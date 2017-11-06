package com.energieip.TCPclient;

import java.io.BufferedReader;
/*
 * Java imports
 */
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * TCP client for CORE connection
 * 
 * @author stef
 *
 */
public class TCPClient { 

	public static void main(String argv[]) {
		
		//String sentence = System.currentTimeMillis() + ": Test Message from TCP Client\0"; //\u0000
		String sentence = "0123456" + "Hello World" + "\n";
		
		Socket clientSocket = null;
		
		try {
			clientSocket = new Socket("localhost", 8082);

			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			
			System.out.println("Sending: " + sentence);
			outToServer.writeBytes(sentence);
			
			System.out.println("Waiting for response...");
			
			BufferedReader in = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
			
			String inputLine, message = "";

			while ((inputLine = in.readLine()) != null) {
				message += inputLine;
			}
			
			System.out.println("From Server: " + message);
									
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (clientSocket != null)
					clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	} // end of main

} // end of class