package controller;

import view.Principal;
import connection.FtpPutColetor;
import connection.TelnetConnection;

public class StoreUpgradeToColetor {

	

	public static String md5;
	private static boolean sucess;
	private static boolean sucessColetorOne, sucessColetorTwo;
	@SuppressWarnings("unused")
	private static FtpPutColetor put;
	private static boolean coletorOneSPVL91containsFile;
	private static boolean coletorTwoSPVL91containsFile;

	public static boolean isColetorOneSPVL91containsFile() {
		return coletorOneSPVL91containsFile;
	}

	public static void setColetorOneSPVL91containsFile(boolean coletorSPVL91containsFile) {
		StoreUpgradeToColetor.coletorOneSPVL91containsFile = coletorSPVL91containsFile;
	}
	
	public static boolean isColetorTwoSPVL91containsFile() {
		return coletorTwoSPVL91containsFile;
	}

	public static void setColetorTwoSPVL91containsFile(boolean coletorTwoSPVL91containsFile) {
		StoreUpgradeToColetor.coletorTwoSPVL91containsFile = coletorTwoSPVL91containsFile;
	}

	public static boolean isSucess() {
		return sucess;
	}

	public static void setSucess(boolean sucess) {
		StoreUpgradeToColetor.sucess = sucess;
	}

	public static void store() {

		if (Info.getTypeColetor().contains("888")) {

			int coletores = Info.getnColetoresValidos();

			if (Info.getTypeColetor().equals("8886")) {
				coletores = configStore(coletores);
			}

			switch (coletores) {
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
				Principal.configBtn(2, false);
				return;
			}

			if (TableInfo.contains("ENVIAR") && Info.getTypeColetor().equals("8886")) {
				Principal.configBtn(3, true);
			} else {
				Principal.configBtn(3, false);
			}

		}
	}

	public static void putFTPColetor(int coletores) {
		Principal.configBtn(2, false);
		Principal.configBtn(3, false);
		Principal.configBtn(5, false);

		switch (coletores) {
		case 1:
			put = new FtpPutColetor(Info.getColetorOne());
			sucessColetorOne = ckeckFile(coletores);
			break;
		case 2:
			put = new FtpPutColetor(Info.getColetorTwo());
			sucessColetorTwo = (ckeckFile(coletores));
			break;
		case 3:
			put = new FtpPutColetor(Info.getColetorOne());
			sucessColetorOne = ckeckFile(1);
			put = new FtpPutColetor(Info.getColetorTwo());
			sucessColetorTwo = (ckeckFile(2));
			break;
		default:
			break;
		}
		configSucess(coletores);
	}

	public static int configStore(int coletor) {

		if (coletor == 1) {
			int row = TableInfo.getRow(Info.getSnColetorOne());
			boolean flag = Principal.getTabela().getValueAt(row, 5).equals("ENVIAR[X]");
			if (flag)
				return 1;
		} else if (coletor == 2) {
			int row = TableInfo.getRow(Info.getSnColetorTwo());
			boolean flag = Principal.getTabela().getValueAt(row, 5).equals("ENVIAR[X]");
			if (flag)
				return 2;
		} else if (coletor == 3) {
			int rowOne = TableInfo.getRow(Info.getSnColetorOne());
			int rowTwo = TableInfo.getRow(Info.getSnColetorTwo());
			boolean flagOne = Principal.getTabela().getValueAt(rowOne, 5).equals("ENVIAR[X]");
			boolean flagTwo = Principal.getTabela().getValueAt(rowTwo, 5).equals("ENVIAR[X]");

			if (flagOne && !flagTwo) {
				return 1;
			} else if (!flagOne && flagTwo) {
				return 2;
			} else if (flagOne && flagTwo) {
				return 3;
			} else {
				return 0;
			}
		}
		return 0;
	}

	public static void configSucess(int coletores) {
		if (coletores == 1) {
			if (sucessColetorOne) {
				setSucess(true);

				if (!Info.getTypeColetor().equals("8886")) {
					if(!Info.getTypeColetor().equals("8887 | SPVL-91")){
						int id = Integer.parseInt(Info.getIDColetorOne());
						SendFile.updateSPLV4Master[id - 1] = -1;
						slave(1);	
					}else{
						
					}
				}

			} else {
				setSucess(false);
			}
		} else if (coletores == 2) {
			if (sucessColetorTwo) {
				setSucess(true);

				if (!Info.getTypeColetor().equals("8886")) {
					
					if (!Info.getTypeColetor().equals("8887 | SPVL-91")) {
						int id = Integer.parseInt(Info.getIDColetorTwo());
						SendFile.updateSPLV4Master[id - 1] = -1;
						slave(2);	
					}else{
						
					}
					
				}

			} else {
				setSucess(false);
			}
		} else if (coletores == 3) {
			if (sucessColetorOne && sucessColetorTwo) {
				setSucess(true);

				if (!Info.getTypeColetor().equals("8886")) {
					
					if (!Info.getTypeColetor().equals("8887 | SPVL-91")) {
						int id = Integer.parseInt(Info.getIDColetorOne());
						SendFile.updateSPLV4Master[id - 1] = -1;
						slave(1);
						id = Integer.parseInt(Info.getIDColetorTwo());
						SendFile.updateSPLV4Master[id - 1] = -1;
						slave(2);	
					}else{
						
					}
					
				}

			} else {
				setSucess(false);
			}
		}
		refreshTable(coletores);
		configBTN();
	}

	public static boolean ckeckFile(int coletor) {
		if (coletor == 1) {
			connectColetor(coletor);

			if (md5.contains(Principal.getMd5())) {
				Console.print("MD5 OK!");
				return true;
			} else {
				Console.print("MD5 NOK(Arquivo corrompido!)");
				return false;
			}
		} else {
			connectColetor(2);

			if (md5.contains(Principal.getMd5())) {
				Console.print("MD5 OK!");
				return true;
			} else {
				Console.print("MD5 NOK(Arquivo corrompido!)");
				return false;
			}
		}
	}

	public static void connectColetor(int coletor) {
		TelnetConnection telnet;

		if (coletor == 1) {
			telnet = Info.getServerOne();

		} else {
			telnet = Info.getServerTwo();
		}

		telnet.connectVlan100();
		Console.print("Obtendo MD5 do arquivo transferido...");
		if(Info.getTypeColetor().contains("SPVL-91")){
			md5 = telnet.sendCommand("md5sum /srv/tftpboot/" + Info.getFileUpgrade().getName() + " | awk '{print $1}'");
		}else{
			md5 = telnet.sendCommand("md5sum " + Info.getFileUpgrade().getName() + " | awk '{print $1}'");
		}
		

	}

	public static void refreshTable(int coletores) {
		switch (coletores) {
		case 1:
			if(Info.getTypeColetor().equals("8887 | SPVL-91")){
				TableInfo.refresh(Info.getSnColetorOne(), 4, "Pronto p/ Transferir");
				setColetorOneSPVL91containsFile(true);
			}else{
				TableInfo.refresh(Info.getSnColetorOne(), 4, "Aquardando atualização");
				TableInfo.refresh(Info.getSnColetorOne(), 5, "ATUALIZAR[X]");
			}
			
			break;
		case 2:
			if(Info.getTypeColetor().equals("8887 | SPVL-91")){
				TableInfo.refresh(Info.getSnColetorTwo(), 4, "Pronto p/ Transferir");
				setColetorTwoSPVL91containsFile(true);
			}else{
				TableInfo.refresh(Info.getSnColetorTwo(), 4, "Aquardando atualização");
				TableInfo.refresh(Info.getSnColetorTwo(), 5, "ATUALIZAR[X]");
			}
			
			break;
		case 3:
			if(Info.getTypeColetor().equals("8887 | SPVL-91")){
				TableInfo.refresh(Info.getSnColetorOne(), 4, "Pronto p/ Transferir");
				TableInfo.refresh(Info.getSnColetorTwo(), 4, "Pronto p/ Transferir");
				setColetorOneSPVL91containsFile(true);
				setColetorTwoSPVL91containsFile(true);
			}else{
				TableInfo.refresh(Info.getSnColetorOne(), 4, "Aquardando atualização");
				TableInfo.refresh(Info.getSnColetorOne(), 5, "ATUALIZAR[X]");
				TableInfo.refresh(Info.getSnColetorTwo(), 4, "Aquardando atualização");
				TableInfo.refresh(Info.getSnColetorTwo(), 5, "ATUALIZAR[X]");
			}
			
		default:
			break;
		}
	}

	public static void configBTN() {
		if (isSucess()) {
			if (Info.getTypeColetor().equals("8886")) {
				if (!TableInfo.contains("ENVIAR")) {
					Principal.configBtn(2, false);
					Principal.configBtn(3, false);
					Principal.configBtn(5, true);
				} else {
					Principal.configBtn(2, false);
					Principal.configBtn(3, true);
					Principal.configBtn(5, true);
				}

			} else if (Info.getTypeColetor().equals("8887")) {
				if (!TableInfo.contains("ENVIAR")) {
					Principal.configBtn(2, false);
					Principal.configBtn(3, false);
					Principal.configBtn(5, true);
				} else {
					Principal.configBtn(2, false);
					Principal.configBtn(3, false);
					Principal.configBtn(5, true);
				}

				if (TableInfo.contains("ENVIAR")) {
					Principal.configBtn(4, true);
				} else {
					Principal.configBtn(4, false);
				}
			}else if(Info.getTypeColetor().equals("8887 | SPVL-91")){
				if (TableInfo.contains("ENVIAR")) {
					Principal.configBtn(4, true);
				} else {
					Principal.configBtn(4, false);
				}
			}
		} else {

		}
	}

	public static void slave(int coletor) {
		String id;
		TelnetConnection conexao;

		if (coletor == 1) {
			id = Info.getIDColetorOne();
			conexao = Info.getServerOne();
		} else {
			id = Info.getIDColetorTwo();
			conexao = Info.getServerTwo();
		}

		for (int j = 0; j < 5; j++) {
			/*
			 * if((Integer.parseInt(id)-1)>=5) break;
			 */
			if (SendFile.updateSPVL4Slave[Integer.parseInt(id) - 1][j] == 1) {
				SendFile.ftpgetSlave(conexao, Integer.parseInt(id) - 1, j);
			}
		}
	}

}
