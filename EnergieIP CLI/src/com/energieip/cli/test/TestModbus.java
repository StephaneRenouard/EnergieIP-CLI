package com.energieip.cli.test;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;


import de.re.easymodbus.exceptions.ModbusException;
import de.re.easymodbus.modbusclient.ModbusClient;

public class TestModbus {

	ModbusClient modbusClient;

	//final String DEFAULT_IP = "192.168.0.118";
	//final int DEFAULT_PORT = 502;
	final String DEFAULT_IP = "91.160.78.238";
	final int DEFAULT_PORT = 41115;
	
	public static void main(String[] args) {
		new TestModbus();

	}
	
	public TestModbus() {
		
		modbusClient = new ModbusClient(DEFAULT_IP, DEFAULT_PORT);
		
		try {
			modbusClient.Connect();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		
		try {
			modbusClient.setUnitIdentifier((byte) 3); // ID 3 (watchdog)
			int[] Result  = modbusClient.ReadHoldingRegisters(0, 5);
			
			for (int i = 0; i < Result.length; i++) {
				System.out.println("["+i+"]="+ Result[i]);				
			}
			
			modbusClient.WriteSingleRegister(0, 1200);
			
			modbusClient.setUnitIdentifier((byte) 3); // ID 3 (watchdog)
			Result  = modbusClient.ReadHoldingRegisters(0, 5);
			
			for (int i = 0; i < Result.length; i++) {
				System.out.println("["+i+"]="+ Result[i]);				
			}
			
			modbusClient.WriteSingleRegister(0, 1200);
			
			
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ModbusException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // first register in ID2
		//CommonLists.iD3.watchog = Result[0]; 
	}

}
