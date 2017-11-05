package com.energieip.TCPclient;

/*
 * Java imports
 */
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * TCP client for CORE connection
 * 
 * @author stef
 *
 */
public class TCPClient {

	public static void main(String argv[]) {
		String sentence = System.currentTimeMillis() + ": Test Message from TCP Client";
		Socket clientSocket = null;
		try {
			clientSocket = new Socket("localhost", 8082);

			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			outToServer.writeBytes(sentence);
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