package controller;

import java.awt.Color;
import java.util.ArrayList;

import connection.TelnetConnection;
import supervisor.Supervisor4Legacy;
import supervisor.Supervisor4Master;
import supervisor.Supervisor4Slave;
import supervisor.Supervisor91;
import view.Principal;

public class UpdateSupervisor {

	private String typeColetor;
	private ArrayList<Object> SPVL;

	public void update() {
		config();

		if (typeColetor.contains("8886")) {
			ArrayList<Supervisor4Legacy> spvlLegacy = new ArrayList<Supervisor4Legacy>();

			for (Object array : SPVL) {
				spvlLegacy.add((Supervisor4Legacy) array);
			}

			for (Supervisor4Legacy spvl : spvlLegacy) {
				String SN = spvl.getSerialNumber();
				int row = TableInfo.getRow(SN);
				boolean flag = Principal.getTabela().getValueAt(row, 5).equals("ATUALIZAR[X]");

				if (row != -1 && flag) {
					Principal.configBtn(5, false);
					spvl.update();
				} else {
					Principal.configBtn(5, true);
					continue;
				}

			}
			configBTN(8886);
		} else if (typeColetor.equals("8887")) {
			ArrayList<Supervisor4Master> spvlMaster = new ArrayList<Supervisor4Master>();

			for (Object array : SPVL) {
				spvlMaster.add((Supervisor4Master) array);
			}

			for (Supervisor4Master spvl : spvlMaster) {

				if (spvl.isContainsSlave()) {
					Supervisor4Slave[] slave = spvl.getEscravo();

					for (int i = 0; i < 5; i++) {
						if (slave[i] != null) {
							slave[i].update();
						}
					}
				}

				String SN = spvl.getSerialNumber();
				int row = TableInfo.getRow(SN);
				boolean flag = Principal.getTabela().getValueAt(row, 5).equals("ATUALIZAR[X]");

				if (row != -1 && flag) {
					Principal.configBtn(4, false);
					Principal.configBtn(5, false);
					spvl.update();
				}
			}

			int nUpdade = 0;

			for (Supervisor4Master spvl : spvlMaster) {
				boolean flag = spvl.isUpdate();
				if (flag) {
					nUpdade++;
				}
			}

			if (nUpdade == spvlMaster.size() && TableInfo.endUpdate()) {

				if (Info.getServerOne() != null) {
					TelnetConnection conexao = Info.getServerOne();
					conexao.sendCommand("reboot");
					TableInfo.refresh(Info.getSnColetorOne(), 4, "Unidade reinicializada.");
				}

				if (Info.getServerTwo() != null) {
					TelnetConnection conexao = Info.getServerTwo();
					conexao.sendCommand("reboot");
					TableInfo.refresh(Info.getSnColetorTwo(), 4, "Unidade reinicializada.");
				}
			}
			configBTN(8887);

			if (Principal.flagRebootColetor) {
				Principal.configBtn(6, true);
			}
		} else if (typeColetor.equals("8887 | SPVL-91")) {
			Supervisor4Slave[] slaveUm = DiscoveryNetwork.spvl4slavesColetorUm;
			Supervisor4Slave[] slaveDois = DiscoveryNetwork.spvl4slavesColetorDois;

			if (slaveUm != null) {
				for (int i = 0; i < slaveUm.length; i++) {
					if (slaveUm[i] != null) {
						slaveUm[i].update();
					}
				}

			}
			
			if (slaveDois != null) {
				for (int i = 0; i < slaveDois.length; i++) {
					if (slaveDois[i] != null) {
						slaveDois[i].update();
					}
				}

			}
			
			configBTN(8887);

		}
	}

	public void config() {
		Principal.configBtn(3, false);
		Principal.configBtn(4, false);
		Principal.configBtn(5, false);
		Principal.configBtn(6, false);
		typeColetor = Info.getTypeColetor();
		SPVL = DiscoveryNetwork.getSupervisores();
	}

	public void configBTN(int tipo) {
		int btn;

		if (tipo == 8886) {
			btn = 3;
		} else {
			btn = 4;
		}

		if (TableInfo.contains("ATUALIZAR") || TableInfo.contains("ENVIAR")) {

			if (TableInfo.contains("ATUALIZAR")) {
				Principal.configBtn(5, true);
				Principal.configBtn(6, true);
			} else {
				Principal.configBtn(5, false);
				Principal.configBtn(6, false);
			}

			if (TableInfo.contains("ENVIAR")) {
				Principal.configBtn(btn, true);
			} else {
				Principal.configBtn(btn, false);
			}

		} else {
			try {
				Console.print("Fim do processo de atualização.");
				Console.print("Liberando para nova atualização em 15s.");
				Principal.setDisableAll();
				Principal.getTxfColetorUm().setText("");
				Principal.getTxfColetorDois().setText("");
				Thread.sleep(15000);
				Principal.getLbColetorUm().setBackground(Color.gray);
				Principal.getLbColetorDois().setBackground(Color.gray);
				Principal.configBtn(1, true);
				Principal.configBtn(6, false);
				Principal.flagRebootColetor = false;
				Principal.configColetores(3);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
