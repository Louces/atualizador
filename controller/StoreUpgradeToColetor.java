package controller;

import view.Principal;
import connection.FtpPutColetor;
import connection.TelnetConnection;

public class StoreUpgradeToColetor {
	private static int nColetores = DiscoveryTypeColetor.getColetoresValidos();
	private static String serverOne = Principal.getTxfColetorUm().getText();
	private static String serverTwo = Principal.getTxfColetorDois().getText();
	private static String md5;
	private static boolean sucess;
	private static boolean sucessColetorOne,sucessColetorTwo;
	@SuppressWarnings("unused")
	private static FtpPutColetor put;

	public static boolean isSucess() {
		return sucess;
	}

	public static void setSucess(boolean sucess) {
		StoreUpgradeToColetor.sucess = sucess;
	}
	
	public static void store() {

		if (Principal.lbTypeColetor.getText().contains("888")) {

			switch (nColetores) {
			case 1:
				putFTPColetor(1);
				break;
			case 2:
				putFTPColetor(2);
				break;
			case 3:
				putFTPColetor(3);
				break;

			default:
				break;
			}
		configSucess();
		}
	}

	public static void putFTPColetor(int coletor) {

		switch (coletor) {
		case 1:
			put = new FtpPutColetor(serverOne);
			sucessColetorOne=ckeckFile(coletor);
			break;
		case 2:
			put = new FtpPutColetor(serverTwo);
			sucessColetorTwo=(ckeckFile(coletor));
			break;
		case 3:
			put = new FtpPutColetor(serverOne);
			sucessColetorOne=ckeckFile(1);
			put = new FtpPutColetor(serverTwo);
			sucessColetorTwo=(ckeckFile(2));
			break;
		default:
			break;
		}

	}
	
	public static void configSucess() {

		if (nColetores == 1) {
			if (sucessColetorOne)
				setSucess(true);
			else
				setSucess(false);
		} else if (nColetores == 2) {
			if (sucessColetorTwo)
				setSucess(true);
			else
				setSucess(false);
		} else if (nColetores == 3) {
			if (sucessColetorOne && sucessColetorTwo)
				setSucess(true);
			else
				setSucess(false);
		}

	}

	public static boolean ckeckFile(int coletor) {

		if (coletor == 1) {

			connectColetor(coletor);

			if (md5.contains(Principal.getMd5())) {
				return true;
			} else
				return false;

		} else {
			connectColetor(2);

			if (md5.contains(Principal.getMd5())) {
				return true;
			} else
				return false;
		}

	}

	public static void connectColetor(int coletor) {
		TelnetConnection telnet;

		if (coletor == 1)
			telnet = new TelnetConnection(serverOne);
		else
			telnet = new TelnetConnection(serverTwo);

		telnet.connectVlan100();
		md5 = telnet.sendCommand("md5sum "+ Principal.getFileUpgrade().getName() + "| awk '{print $1}'");
		telnet.closeSession();

	}
	
}
