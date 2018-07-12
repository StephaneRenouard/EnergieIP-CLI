package com.energieip.cli;


import java.util.Scanner;

import com.energieip.api.EnergieAPI;

import fr.handco.lib.time.Time;

public class CLI implements Runnable {
	
	
	EnergieAPI energieAPI;
	
	private Scanner scan;

	Thread thread;

	private boolean ListFlag = false; // indicate that we have a file in memory
	private boolean ConnectionFlag = false; // indicate connection state


	/**
	 * Default param
	 */
	static String SERVER_IP = "127.0.0.1";
	static int SERVER_PORT = 8082;

	
	
	/**
	 * Main entry point
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		if(args.length==1){
			new CLI(args[0], SERVER_PORT);  // custom IP,  default server port
		}
		else if(args.length==2){
			new CLI(args[0], Integer.parseInt(args[1])); // custom IP,  custom server port
		}
		else{
			// missing args. Exit with code 0.
			System.out.println("ERROR: missing server IP");
			System.out.println("CLI USAGE: java -jar cli.jar 127.0.0.1");
			System.out.println("           java -jar cli.jar 127.0.0.1 8082");
			System.exit(0);
		}
		
	}

	/**
	 * Constructor
	 */
	public CLI(String _SERVER_IP, int _SERVER_PORT) {

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

		System.out.println(Time.timeStamp("EnergieIP CLI v1.2"));
		
		// set SERVER IP as global
		SERVER_IP = _SERVER_IP;
		SERVER_PORT = _SERVER_PORT;
		
		energieAPI = new EnergieAPI();
		energieAPI.set_TCPserver_IP(SERVER_IP);
		
		
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
		
		String[] input = text.split(" "); // split input to extract args

		String[] list; // list from EnergieAPI
		
		if (input.length > 0) { // ensure that at least one command is provided

			String key = input[0]; // first command

			switch (key) {
			case "connect":
					System.out.println(Time.timeStamp("Autoconnected to " + SERVER_IP));
				break;
			case "disconnect":
				try {
					System.out.println(Time.timeStamp("Autoconnected to " + SERVER_IP));
					//modbusClient.Disconnect();
					ConnectionFlag = false;
				} catch (Exception ee) {
					System.out.println(Time.timeStamp("modbus disconnected"));
				}
				break;

			case "help":
				System.out.println("help yourself");
				break;

			case "show":
				System.out.println("show1");
				break;

			case "scan":
				
				break;

			case "read":

				break;

			case "list":
				
				switch(input[1]){
				case "group":
					list = energieAPI.get_list_groups();
					for (int i = 0; i < list.length; i++) {
						System.out.println(list[i]);
					}
					
					System.out.println(list.length + " group(s) found");
				break;
				case "light":
					list = energieAPI.get_list_light_drivers();
					for (int i = 0; i < list.length; i++) {
						System.out.println("[" + i + "] " + list[i]);
					}
					
					System.out.println(list.length + " light driver(s) found");
				break;
				case "blind":
					list = energieAPI.get_list_blind_drivers();
					for (int i = 0; i < list.length; i++) {
						System.out.println("[" + i + "] " + list[i]);
					}
					
					System.out.println(list.length + " bind driver(s) found");
				break;
				case "hvac":
					list = energieAPI.get_list_HVAC_drivers();
					for (int i = 0; i < list.length; i++) {
						System.out.println("[" + i + "] " + list[i]);
					}
					
					System.out.println(list.length + " hvac driver(s) found");
				break;
				case "io":
					list = energieAPI.get_list_TOR_drivers();
					for (int i = 0; i < list.length; i++) {
						System.out.println("[" + i + "] " + list[i]);
					}
					
					System.out.println(list.length + " I/O driver(s) found");
				break;
				// default case
				default:
				list = energieAPI.get_list();
				
				for (int i = 0; i < list.length; i++) {
					System.out.println("[" + i + "] " + list[i]);
				}
				
				System.out.println(list.length + " drivers found");
				break; // end of default
				} // end of switch "input[1]
				
				break; // end of case "list"
				/*
				 * SET
				 */
			case "set":
				
				if (ListFlag && ConnectionFlag && input.length>1) {
					
					switch (input[1]) {
					case "watchdog":
						
						if (input.length < 4) { // to get the #3 parameter
							
							int value  = Integer.parseInt(input[2]);
							
						}else{
							System.err.println(Time.timeStamp("ERROR: bad syntax"));
						}
						
						break;
					case "group": // set group L21 2
						
						if (input.length < 5) { // to get the #3 and #4 parameters
						
							String friendlyName = input[2];
							int SA = Integer.parseInt(friendlyName.substring(1, friendlyName.length()));
							int target = Integer.parseInt(input[3]);
							
							//moveGroup(friendlyName, SA, target);
						
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
	
	
	


}// end of class