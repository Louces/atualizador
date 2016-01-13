package controller;

import java.awt.Color;
import java.util.ArrayList;

import connection.TelnetConnection;
import supervisor.Supervisor4Legacy;
import supervisor.Supervisor4Master;
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
				boolean flag = 
				Principal.getTabela().getValueAt(row, 5).equals("ATUALIZAR[X]");

				if (row != -1 && flag) {
					Principal.configBtn(5, false);
					spvl.update();
				} else {
					Principal.configBtn(5, true);
					continue;
				}

			}
			configBTN();
		}else if(typeColetor.contains("8887")){
			ArrayList<Supervisor4Master> spvlMaster = new ArrayList<Supervisor4Master>();
			
			for(Object array : SPVL){
				spvlMaster.add((Supervisor4Master) array);
			}
			
			for(Supervisor4Master spvl : spvlMaster){
				String SN  = spvl.getSerialNumber();
				int row = TableInfo.getRow(SN);
				boolean flag = Principal.getTabela().getValueAt(row, 5).equals("ATUALIZAR[X]");
				
				if(row!=-1 && flag){
					spvl.update();
				}
				Principal.configBtn(5, true);
				
			}
			
			int nUpdade = 0;
			
			for(Supervisor4Master spvl : spvlMaster){
				boolean flag = spvl.isUpdate();
				if(flag){
					nUpdade++;
				}
			}
			
			Console.print("Nupdade : " + nUpdade);
			Console.print("spvlMaster.size() : " + spvlMaster.size());
			
			if(nUpdade==spvlMaster.size()){
				
				if(Info.getServerOne()!=null){
					TelnetConnection conexao = Info.getServerOne();
					conexao.sendCommand("reboot");
					TableInfo.refresh(Info.getSnColetorOne(), 4, "Unidade reinicializada.");
				}
				
				if(Info.getServerTwo()!=null){
					TelnetConnection conexao = Info.getServerTwo();
					conexao.sendCommand("reboot");
					TableInfo.refresh(Info.getSnColetorTwo(), 4, "Unidade reinicializada.");
				}
			}
		}
	}

	public void config() {
		Principal.configBtn(3, false);
		Principal.configBtn(5, false);
		typeColetor = Info.getTypeColetor();
		SPVL = DiscoveryNetwork.getSupervisores();
	}
	
	public void configBTN(){
		if(TableInfo.contains("ATUALIZAR")||TableInfo.contains("ENVIAR")) {
			
			if(TableInfo.contains("ATUALIZAR")){
				Principal.configBtn(5, true);
			}else{
				Principal.configBtn(5, false);	
			}
			
			if(TableInfo.contains("ENVIAR")){
				Principal.configBtn(3, true);	
			}else{
				Principal.configBtn(3, false);
			}
			
		}else{
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
				Principal.configColetores(3);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
