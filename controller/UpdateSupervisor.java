package controller;

import java.util.ArrayList;

import supervisor.Supervisor4Legacy;
import view.Principal;

public class UpdateSupervisor {

	@SuppressWarnings("unused")
	private int nColetores;
	private String typeColetor;
	private ArrayList<Object> SPVL;

	public void update() {
		Principal.configBtn(5, false);
		config();

		if (typeColetor.contains("8886")) {
			ArrayList<Supervisor4Legacy> spvlLegacy = new ArrayList<Supervisor4Legacy>();

			for (Object array : SPVL) {
				spvlLegacy.add((Supervisor4Legacy) array);
			}

			for (Supervisor4Legacy spvl : spvlLegacy) {
				String SN = spvl.getSerialNumber();
				int row = TableInfo.getRow(SN);
				boolean flag = Principal.getTabela().getValueAt(row, 5)
						.equals("ATUALIZAR[X]");

				if (row != -1 && flag) {
					Principal.configBtn(5, false);
					spvl.Update();
				} else {
					Principal.configBtn(5, true);
					continue;
				}

			}

			if (TableInfo.contains("ATUALIZAR")) {
				
				Principal.configBtn(5, true);
			} else {
				
				try {
					Console.print("Fim do processo de atualização.");
					Console.print("Liberando para nova atualização em 15 segundos...");
					Thread.sleep(15000);
					Principal.getTxfColetorUm().setText("");
					Principal.getTxfColetorDois().setText("");
					Principal.configBtn(1, true);
					Principal.configBtn(5, false);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void config() {
		nColetores = DiscoveryTypeColetor.getColetoresValidos();
		typeColetor = DiscoveryTypeColetor.getTypeColetor();
		SPVL = DiscoveryNetwork.getSupervisores();
	}
}
