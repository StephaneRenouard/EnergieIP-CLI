package com.energieip.cli.test;

public class TestExtractSA {

	public static void main(String[] args) {
		new TestExtractSA();
	}
	
	
	public TestExtractSA() {
		
		String friendlyName = "L23";
		
		int SA = Integer.parseInt(friendlyName.substring(1, friendlyName.length()));
		
		System.out.println(SA);
		
	}

}
