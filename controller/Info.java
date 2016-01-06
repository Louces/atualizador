package controller;

import java.io.File;

import connection.TelnetConnection;
import view.Principal;

public class Info {
	private static String coletorOne;
	private static String coletorTwo;
	private static String snColetorOne;
	private static String snColetorTwo;
	private static String typeColetor;
	private static int nColetoresValidos;
	private static TelnetConnection serverOne;
	private static TelnetConnection serverTwo;
	private static File fileUpgrade;
	private static String IDColetorOne;
	private static String IDColetorTwo;
	
	public static File getFileUpgrade() {
		return fileUpgrade;
	}

	public static void setFileUpgrade(File fileUpgrade) {
		Info.fileUpgrade = fileUpgrade;
	}	
	
	public static void setServerOne(TelnetConnection server){
		serverOne=server;
	}
	
	public static TelnetConnection getServerOne(){
		return serverOne;
	}
	
	public static void setServerTwo(TelnetConnection server){
		serverTwo=server;
	}
	
	public static TelnetConnection getServerTwo(){
		return serverTwo;
	}
	
	public static String getSnColetorOne() {
		return snColetorOne;
	}

	public static void setSnColetorOne(String snColetorOne) {
		Info.snColetorOne = snColetorOne;
	}

	public static String getSnColetorTwo() {
		return snColetorTwo;
	}

	public static void setSnColetorTwo(String snColetorTwo) {
		Info.snColetorTwo = snColetorTwo;
	}

	public static String getColetorOne() {
		setColetorOne(Principal.getTxfColetorUm().getText());
		return coletorOne;
	}

	public static void setColetorOne(String snColetorOne) {
		Info.coletorOne = snColetorOne;
	}

	public static String getColetorTwo() {
		setColetorTwo(Principal.getTxfColetorDois().getText());
		return coletorTwo;
	}

	public static void setColetorTwo(String snColetorTwo) {
		Info.coletorTwo = snColetorTwo;
	}

	public static int getnColetoresValidos() {
		return nColetoresValidos;
	}

	public static void setnColetoresValidos(int nColetoresValidos) {
		Info.nColetoresValidos = nColetoresValidos;
	}

	public static String getTypeColetor() {
		return typeColetor;
	}

	public static void setTypeColetor(String typeColetor) {
		Info.typeColetor = typeColetor;
	}

	public static String getIDColetorOne() {
		return IDColetorOne;
	}

	public static void setIDColetorOne(String iDColetorOne) {
		IDColetorOne = iDColetorOne;
	}

	public static String getIDColetorTwo() {
		return IDColetorTwo;
	}

	public static void setIDColetorTwo(String iDColetorTwo) {
		IDColetorTwo = iDColetorTwo;
	}

}
