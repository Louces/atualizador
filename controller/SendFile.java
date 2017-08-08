package controller;

import javax.swing.JProgressBar;

import view.Principal;
import connection.TelnetConnection;
import supervisor.Supervisor4Slave;
import supervisor.Supervisor91;

public class SendFile {
	private static JProgressBar progressBar = Principal.getProgressBar();
	private static String name;
	public static int updateSPLV4Master[] = new int[14];
	public static int updateSPVL4Slave[][] = new int[14][5];
	public static String serialMaster[] = new String[14];
	public static String serialSlave[][] = new String[14][5];

	public static void sendMaster() {
		Principal.configBtn(4, false);
		Principal.configBtn(5, false);

		for (int i = 0; i < updateSPLV4Master.length; i++) {

			switch (updateSPLV4Master[i]) {
			case 1:
				ftpget(1, (i + 1));
				break;
			case 2:
				ftpget(2, (i + 1));
				break;
			case 3:
				ftpget(1, (i + 1));
				break;
			default:
				break;
			}
		}
		configBTN();
	}

	public static void sendSlave4Spvl_91() {
		Principal.configBtn(4, false);
		Principal.configBtn(5, false);
		int rows = Principal.getTabela().getRowCount();

		for (int i = 0; i < rows; i++) {
			if (Principal.getTabela().getValueAt(i, 5).equals("ENVIAR[X]")) {
				
				Supervisor4Slave slaveOne = null;
				Supervisor4Slave slaveTwo = null;
				boolean flagOne = false;
				boolean flagTwo = false;

				if (DiscoveryNetwork.spvl4slavesColetorUm != null)
					for (int j = 0; j < DiscoveryNetwork.spvl4slavesColetorUm.length; j++) {
						if (DiscoveryNetwork.spvl4slavesColetorUm[j] != null) {
							slaveOne = DiscoveryNetwork.spvl4slavesColetorUm[j];

							if (slaveOne.getSerialNumber().equals(Principal.getTabela().getValueAt(i, 1))) {
								flagOne = true;
								break;
							}
						}
					}

				if (DiscoveryNetwork.spvl4slavesColetorDois != null)
					for (int j = 0; j < DiscoveryNetwork.spvl4slavesColetorDois.length; j++) {
						if (DiscoveryNetwork.spvl4slavesColetorDois[j] != null) {
							slaveTwo = DiscoveryNetwork.spvl4slavesColetorDois[j];

							if (slaveTwo.getSerialNumber().equals(Principal.getTabela().getValueAt(i, 1))) {
								flagTwo = true;
								break;
							}
						}
					}

				if (flagOne || flagTwo) {
					
					if (flagOne) {
						Supervisor91 supervisor91 = new Supervisor91(1);
						TableInfo.refresh(slaveOne.getSerialNumber(), 4, "Downloading...");
						Console.print("Downloading... > SPVL-4#" + slaveOne.getSerialNumber() +" | Aquarde!");
						
						boolean send =supervisor91.sendoFileToSPVL_4(slaveOne); 
						
						if(send){
							Console.print("Transferencia completa em SPVL-4#" + slaveOne.getSerialNumber());
							TableInfo.refresh(slaveOne.getSerialNumber(), 4, "Disponivel p/ Atualização");
							TableInfo.refresh(slaveOne.getSerialNumber(), 5, "ATUALIZAR[X]");	
						}else{
							TableInfo.refresh(slaveOne.getSerialNumber(), 4, "Falha ao transmitir...");
						}
						
					}

					if (flagTwo) {
						Supervisor91 supervisor91 = new Supervisor91(2);
						TableInfo.refresh(slaveTwo.getSerialNumber(), 4, "Downloading...");
						Console.print("Downloading... > SPVL-4#" + slaveTwo.getSerialNumber());
						if(supervisor91.sendoFileToSPVL_4(slaveTwo)){
							Console.print("Transferencia completa em SPVL-4#" + slaveTwo.getSerialNumber());
							TableInfo.refresh(slaveTwo.getSerialNumber(), 4, "Disponivel p/ Atualização");
							TableInfo.refresh(slaveTwo.getSerialNumber(), 5, "ATUALIZAR[X]");	
						}else{
							TableInfo.refresh(slaveTwo.getSerialNumber(), 4, "Falha ao transmitir...");
						}
					}

				}

			}
		}
		configBTN();
	}

	public static void ftpget(int coletor, int ID) {
		int IDcoletor;
		long tamanho = Info.getFileUpgrade().length();
		name = Info.getFileUpgrade().getName();

		int row = TableInfo.getRow(serialMaster[ID - 1]);

		if (row < 0) {
			return;
		}

		boolean send = Principal.getTabela().getValueAt(row, 5).equals("ENVIAR[X]");

		if (!send) {
			return;
		}

		TableInfo.refresh(serialMaster[ID - 1], 4, "Download...");

		TelnetConnection conexao;

		if (coletor == 2) {
			conexao = Info.getServerTwo();
			IDcoletor = Integer.parseInt(Info.getIDColetorTwo());
		} else {
			conexao = Info.getServerOne();
			IDcoletor = Integer.parseInt(Info.getIDColetorOne());
		}

		conexao.connectVlan101("169.254." + (127 + ID) + ".1");
		conexao.sendCommand(
				"ftpget -uroot -proot " + "169.254." + (127 + IDcoletor) + ".1 " + name + " " + name + " &");

		while (true) {
			conexao.sendCommand("sleep 5");
			String flag = conexao.sendCommand("ls");
			if (flag.contains(name)) {
				break;
			}
		}

		progressBar.setValue(0);
		progressBar.setString(0 + "%");
		progressBar.setVisible(true);

		while (true) {
			long flag = Long.parseLong(
					FilterCommand.filter(conexao.sendCommand("ls -la *supervisor_upgrade* | awk '{print $5}'")));
			if (flag < tamanho) {
				progressBar.setValue((int) ((flag * 100 / tamanho)));
				progressBar.setString((int) ((flag * 100 / tamanho)) + "%");
				conexao.sendCommand("sleep 5");
			} else {
				progressBar.setValue((int) ((flag / tamanho) * 100));
				progressBar.setString((int) ((flag * 100 / tamanho)) + "%");
				progressBar.setVisible(false);
				String md5 = conexao.sendCommand("md5sum " + Info.getFileUpgrade().getName() + "| awk '{print $1}'");

				if (md5.contains(Principal.getMd5())) {
					TableInfo.refresh(serialMaster[ID - 1], 4, "Aquardando atualização");
					TableInfo.refresh(serialMaster[ID - 1], 5, "ATUALIZAR[X]");
					updateSPLV4Master[ID - 1] = -1;
				} else {
					TableInfo.refresh(serialMaster[ID - 1], 4, "Descoberto");
				}

				break;
			}
		}

		for (int j = 0; j < 5; j++) {
			/*
			 * if((ID-1)>=5) break;
			 */
			if (updateSPVL4Slave[ID - 1][j] == 1) {
				ftpgetSlave(conexao, ID - 1, j);
			}
		}

		conexao.disconnect();
	}

	public static void ftpgetSlave(TelnetConnection conexao, int i, int j) {
		TableInfo.refresh(serialSlave[i][j], 4, "Download...");
		name = Info.getFileUpgrade().getName();
		long tamanho = Info.getFileUpgrade().length();
		progressBar.setValue(0);
		progressBar.setString(0 + "%");
		progressBar.setVisible(true);

		conexao.connectVlan102("169.254." + (j + 1) + ".37");
		conexao.sendCommand("ftpget -uroot -proot " + "169.254.0.37 " + name + " " + name + " &");

		while (true) {
			conexao.sendCommand("sleep 5");
			String flag = conexao.sendCommand("ls");
			if (flag.contains(name)) {
				break;
			}
		}

		while (true) {
			long flag = Long.parseLong(
					FilterCommand.filter(conexao.sendCommand("ls -la *supervisor_upgrade* | awk '{print $5}'")));
			if (flag < tamanho) {
				progressBar.setValue((int) ((flag * 100 / tamanho)));
				progressBar.setString((int) ((flag * 100 / tamanho)) + "%");
				conexao.sendCommand("sleep 5");
			} else {
				progressBar.setValue((int) ((flag / tamanho) * 100));
				progressBar.setString((int) ((flag * 100 / tamanho)) + "%");
				progressBar.setVisible(false);
				String md5 = conexao.sendCommand("md5sum " + Info.getFileUpgrade().getName() + "| awk '{print $1}'");

				if (md5.contains(Principal.getMd5())) {
					TableInfo.refresh(serialSlave[i][j], 4, "Aquardando atualização");
					TableInfo.refresh(serialSlave[i][j], 5, "ATUALIZAR[X]");
					updateSPVL4Slave[i][j] = -1;
				} else {
					TableInfo.refresh(serialSlave[i][j], 4, "Descoberto");
				}

				break;
			}
		}
		conexao.disconnect();
	}

	public static void configBTN() {

		if (TableInfo.contains("ENVIAR")) {
			Principal.configBtn(4, true);
		} else {
			Principal.configBtn(4, false);
		}

		if (TableInfo.contains("ATUALIZAR")) {
			Principal.configBtn(5, true);
		} else {
			Principal.configBtn(5, false);
		}

	}

}
