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
		Principal.configBtn(2, false);
		Principal.configBtn(3, false);
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
	Principal.configBtn(2, true);	
	}
	
	public static void configSucess() {
		if (nColetores == 1) {
			if (sucessColetorOne){
				setSucess(true);
			}else{
				setSucess(false);
			}
		} else if (nColetores == 2) {
			if (sucessColetorTwo){
				setSucess(true);
			}else{
				setSucess(false);
			}
		} else if (nColetores == 3) {
			if (sucessColetorOne && sucessColetorTwo){
				setSucess(true);
			}else{
				setSucess(false);
			}
		}
	configBTN();
	refreshTable();
	}

	public static boolean ckeckFile(int coletor) {
		if (coletor == 1) {
			connectColetor(coletor);
			
			if (md5.contains(Principal.getMd5())) {
				Console.print("MD5 OK!");
				return true;
			} else{
				Console.print("MD5 NOK(Arquivo corrompido!");
				return false;
			}
		} else {
			connectColetor(2);

			if (md5.contains(Principal.getMd5())) {
				Console.print("MD5 OK!");
				return true;
			} else{
				Console.print("MD5 NOK(Arquivo corrompido!");
				return false;
			}
		}
	}

	public static void connectColetor(int coletor) {
		TelnetConnection telnet;
		
		if (coletor == 1)
			telnet = new TelnetConnection(serverOne);
		else
			telnet = new TelnetConnection(serverTwo);

		telnet.connectVlan100();
		Console.print("Obtendo MD5 do arquivo transferido...");
		md5 = telnet.sendCommand("md5sum "+ Principal.getFileUpgrade().getName() + "| awk '{print $1}'");
		telnet.closeSession();
	}
	
	public static void refreshTable() {
		switch (nColetores) {
		case 1:
			TableInfo.refresh(Strings.getSnColetorOne(), 4,"Aquardando atualização");
			TableInfo.refresh(Strings.getSnColetorOne(), 5,"ATUALIZAR[X]");
			break;
		case 2:
			TableInfo.refresh(Strings.getSnColetorTwo(), 4,"Aquardando atualização");
			TableInfo.refresh(Strings.getSnColetorTwo(), 5,"ATUALIZAR[X]");
			break;
		case 3:
			TableInfo.refresh(Strings.getSnColetorOne(), 4,"Aquardando atualização");
			TableInfo.refresh(Strings.getSnColetorOne(), 5,"ATUALIZAR[X]");
			TableInfo.refresh(Strings.getSnColetorTwo(), 4,"Aquardando atualização");
			TableInfo.refresh(Strings.getSnColetorTwo(), 5,"ATUALIZAR[X]");
		default:
			break;
		}
	}
	
	public static void configBTN(){
		if(isSucess()){
			if(DiscoveryTypeColetor.getTypeColetor().equals("8886")){
				Principal.configBtn(2, false);
				Principal.configBtn(3, false);
				Principal.configBtn(5, true);
			}
		}else{
			
		}
	}
	
}
