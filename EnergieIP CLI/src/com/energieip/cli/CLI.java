package com.energieip.cli;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import com.energieip.mobus.objects.CommonLists;
import com.energieip.mobus.objects.ID11;
import com.energieip.mobus.objects.ID2;
import com.energieip.modbus.tools.ModbusDataBuilder;

import com.energieip.modbuscan.ModbusScan;

import de.re.easymodbus.exceptions.ModbusException;
import de.re.easymodbus.modbusclient.ModbusClient;
import fr.handco.lib.time.Time;

public class CLI implements Runnable {

	private Scanner scan;

	ModbusClient modbusClient;

	Thread thread;

	private boolean ListFlag = false; // indicate that we have a file in memory
	private boolean ConnectionFlag = false; // indicate connection state

	// private static List<com.energieip.mobus.objects.ID2> driverList;

	/**
	 * Default param
	 */
	final String DEFAULT_FILE = "driverList.eip";
	//final String DEFAULT_IP = "192.168.0.118";
	//final int DEFAULT_PORT = 502;
	final String DEFAULT_IP = "91.160.78.238";
	final int DEFAULT_PORT = 41115;
	
	
	/**
	 * Main entry point
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new CLI();
	}

	/**
	 * Constructor
	 */
	public CLI() {

		/*
		 * 
		 * // en francais
		 * 
		 * EnergieCLI -p default (default option)
		 * 
		 * ORDRE %1 %2 %3 %4 %5
		 * 
		 * show -> show commands help -> show scan IP:PORT scan (default
		 * 192.168.0.118:502) -> perform a modbus scan read FILE read
		 * (default=driverList.eip) list rack list -> return NO SCAN / FILE
		 * error; other list drivers L25 (SA25, L), MC total light=SS
		 * 
		 * Total driver=XX list L25 L25: show details
		 * 
		 * list groups list g1 list L25 power set L25 20% set S52 up/down/stop
		 * set watchdog XX set S52 g1 set g2 copyfrom g1 write FILE
		 * 
		 * 
		 * 
		 * remove S52 g1 (put in group1 by default)
		 * 
		 * check // default: check if something in g1; each group must have MC
		 * 
		 * delete S52 (default from file)
		 * 
		 */

		System.out.println("EnergieIP CLI v1.0");

		// Launch Thread
		thread = new Thread(this);
		thread.start();

	}

	/**
	 * loop thread
	 */
	@SuppressWarnings({ "deprecation", "static-access" })
	@Override
	public void run() {
		while (!Thread.interrupted()) {

			scan = new Scanner(System.in);

			String text = scan.nextLine();

			InputAnalyse(text);

			// auto gen block
			try {
				thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		} // end while

		// Close connection if thread is stopped
		thread.stop();

	} // end of thread

	/**
	 * 
	 * @param text
	 */
	private void InputAnalyse(String text) {

		String[] input = text.split(" ");

		if (input.length > 0) {

			String key = input[0];

			switch (key) {
			case "connect":

				String ip = DEFAULT_IP;
				int port = DEFAULT_PORT;
				
				if (input.length > 1) {
					ip = input[1];
				}
				if (input.length > 2) {
					port = Integer.parseInt(input[2]);
				}
				
				modbusClient = new ModbusClient(ip, port);
				
				try {
					
					modbusClient.Connect();
					
					ConnectionFlag=true;
					
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println(Time.timeStamp("modbus connected with timeout="+ modbusClient.getConnectionTimeout()));

				break;
			case "disconnect":
				try {
					modbusClient.Disconnect();
					ConnectionFlag = false;
				} catch (IOException e) {
					System.out.println(Time.timeStamp("modbus disconnected"));
				} catch (Exception ee) {
					System.out.println(Time.timeStamp("modbus disconnected"));
				}
				break;

			case "help":
				System.out.println("key1");
				break;

			case "show":
				System.out.println("show1");
				break;

			case "scan":
				int _port = DEFAULT_PORT;
				String _ip = DEFAULT_IP;
				String output_file = DEFAULT_FILE;

				if (input.length > 1) {
					_ip = input[1];
				}
				if (input.length > 2) {
					_port = Integer.parseInt(input[2]);
				}
				if (input.length > 3) {
					output_file = input[3];
				}
				System.out.println(Time.timeStamp("Scanning " + _ip + ":" + _port));
				
				ModbusScan.Scan(_ip, _port, output_file);
				// System.out.println("writing file " + output_file);
				break;

			case "read":

				String file = DEFAULT_FILE;

				if (input.length > 1) {
					file = input[1];
				}

				System.out.println(Time.timeStamp("Reading data from " + file));

				ObjectInputStream ois;
				try {
					ois = new ObjectInputStream(new FileInputStream(file));
					CommonLists.driverList = (List<ID2>) ois.readObject();
					ois.close();
				} catch (FileNotFoundException e) {
					System.err.println(Time.timeStamp("ERROR: File not Found"));
				} catch (IOException e) {
					System.err.println(Time.timeStamp("ERROR: IOException"));
				} catch (ClassNotFoundException e) {
					System.err.println(Time.timeStamp("ERROR: Internal Error (ClassNotFoundException)"));
				}

				System.out.println(Time.timeStamp("Building objects"));

				// ID3 is a standalone register
				CommonLists.iD3 = new com.energieip.mobus.objects.ID3();

				// make ID11 (light) list
				ModbusDataBuilder.makeID11List();

				// make ID12 (HVAC) list
				ModbusDataBuilder.makeID12List();

				// make ID13 (shutter) list
				ModbusDataBuilder.makeID13List();

				// make group list
				ModbusDataBuilder.makeGroupList();

				// make ID100 (group param) list
				ModbusDataBuilder.makeID100List();

				// file found and data acquired
				System.out.println(Time.timeStamp("Data acquired"));
				ListFlag = true;
				break;

			case "list":

				if (ListFlag) {

					String fileName = DEFAULT_FILE;

					if (input.length > 1) {
						fileName = input[1];
					}

					System.out.println(Time.timeStamp("LIGHT drivers:"));

					int count = 0;

					for (Iterator<ID11> iterator = CommonLists.ID11List.iterator(); iterator.hasNext();) {

						ID11 id11 = (ID11) iterator.next();
						System.out.println("L" + id11.shortAddress + " (group " + id11.group + ")");

						count++;

					}
					System.out.println("Number of light drivers=" + CommonLists.ID11List.size());

					// default case
				} // end if listFlag
				else {
					// no list in memory
					System.err.println(Time.timeStamp("ERROR: no data in memory, perform a scan first"));
				}

				break;
				/*
				 * SET
				 */
			case "set":
				
				if (ListFlag && ConnectionFlag && input.length>1) {
					
					switch (input[1]) {
					case "watchdog":
						
						if (input.length < 4) { // to get the #3 parameter
							
							setWatchdog(Integer.parseInt(input[2]));
							
						}else{
							System.err.println(Time.timeStamp("ERROR: bad syntax"));
						}
						
						break;
					case "group": // set group L21 2
						
						if (input.length < 5) { // to get the #3 and #4 parameters
						
							String friendlyName = input[2];
							int SA = Integer.parseInt(friendlyName.substring(1, friendlyName.length()));
							int target = Integer.parseInt(input[3]);
							
							moveGroup(friendlyName, SA, target);
						
						}	
						
						
						break;
					case "":
						System.err.println(Time.timeStamp("Error: bad syntax"));
						break;
					default:
						System.err.println(Time.timeStamp("Error: bad syntax"));
						
						break;
					}
					
				}
				else {
					if(ListFlag==false){
						System.err.println(Time.timeStamp("Error: you first need to load a description file in memory"));
					}
					if(ConnectionFlag==false){
						System.err.println(Time.timeStamp("Error: you need to connect to modbus"));
							
					}
					else{
						System.err.println(Time.timeStamp("Error: bad syntax"));
						
					}
				}
				
					break;

			case "":
				break;

			default:
				System.out.println("Bad order");
				break;
			}// end switch

		} else {
			// input size is 0, do nothing
		}

	}// end of InputAnalyse()
	
	private void moveGroup(String friendlyName, int sA, int target) {
		
			
			try {
				modbusClient.Disconnect();
				modbusClient.Connect();
				
				modbusClient.setUnitIdentifier((byte) 3); // ID 3 (watchdog)
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			int[] Result  = modbusClient.ReadHoldingRegisters(0, 1);
		
	}

	/**
	 * setWatchdog
	 * @param value
	 */
public boolean setWatchdog(int value) {
			
		try {
			
			modbusClient.Disconnect();
			modbusClient.Connect();
			
			modbusClient.setUnitIdentifier((byte) 3); // ID 3 (watchdog)
			int[] Result  = modbusClient.ReadHoldingRegisters(0, 1);
			
			// keep previous value for comparaison
			int previous_value = Result[0];
			
			modbusClient.WriteSingleRegister(0, value);
			
			// check result
			Result  = modbusClient.ReadHoldingRegisters(0, 5);
			
			int actual_value = Result[0];
			
			System.out.println(Time.timeStamp("[OK] Watchdog value from " + previous_value + " to " + actual_value ));
			
			

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
		
		
		return true;
	}


}// end of class