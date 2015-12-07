package controller;

import java.util.ArrayList;
import java.util.Collections;

import connection.TelnetConnection;
import supervisor.Supervisor_4_Legado;
import view.Principal;

public class DiscoveryNetwork {
    
	private String serverUm = Principal.getTxfColetorUm().getText();
    private String serverDois = Principal.getTxfColetorDois().getText();
    
	DiscoveryTypeColetor typeColetor = new DiscoveryTypeColetor();
	
	public static ArrayList<Object> supervisores = new ArrayList<Object>();
	
	public void network(){
		
		Principal.eraseTable();
		
		supervisores.clear();
		
		typeColetor.validate();
		
		if(Principal.lbTypeColetor.getText().contains("8886")){
			
			switch (typeColetor.getColetoresValidos()) {
			
			case 1:
				gravaSupervisor8886(serverUm,1);
				break;
			case 2:
				gravaSupervisor8886(serverDois,1);
				break;
			case 3:
				gravaSupervisor8886(serverUm,1);
				gravaSupervisor8886(serverDois,2);
				break;
			default:
				break;
			}
			
		}else if(Principal.lbTypeColetor.getText().contains("8887")){
			
		}else{
			
		}
		
	}
	
	public void gravaSupervisorLegado(String server){
		
		String comando;
		Supervisor_4_Legado spvl = new Supervisor_4_Legado();
		TelnetConnection conexao = new TelnetConnection(server);
		
		conexao.connectVlan100();
		
		comando = conexao.sendCommand
		("cat supervisor.config | grep -m 1 Numero | awk '{print $5}'");
		spvl.setId(filterCommand(comando));
		
		comando = conexao.sendCommand
		("cat /proc/cmdline | awk '{print $1}'");
		spvl.setSerialNumber(filterCommand(comando));
		
		comando = conexao.sendCommand
		("./supervisor -v | awk '{print $2}'");
		spvl.setVersaoAplicacao(filterCommand(comando));
		
		spvl.setIpVLAN100(server);
		
		conexao.closeSession();
		
		supervisores.add(spvl);
		
		
	}
    
	public String filterCommand(String command) {
		int start = 0, end = 0;

		for (int i = 0; i < command.length() - 1; i++) {
			if (command.charAt(i) == '\n') {
				start = i + 1;
				for (int j = i + 1; j < command.length() - 1; j++) {
					if (command.charAt(j) == '\n') {
						end = j - 1;
						break;
					}
				}

				if (end != 0)
					break;
			}

		}
		
		return command.substring(start, end);
	}
	
	public void gravaSupervisor8886(String server, int coletor){
	
		gravaSupervisorLegado(server);
		
		String[] tableRow = new String[6];
		
		Supervisor_4_Legado spvl = new Supervisor_4_Legado();
		
		if (coletor == 1)
			spvl = (Supervisor_4_Legado) supervisores.get(0);
		else
			spvl = (Supervisor_4_Legado) supervisores.get(1);	
		
		tableRow[0]= spvl.getId();
		tableRow[1]= spvl.getSerialNumber().replaceAll("sn=", "");
		tableRow[2]= "Mestre";
		tableRow[3]= spvl.getVersaoAplicacao().replaceFirst("V", "");
		tableRow[4]= "Descoberto";
		tableRow[5]= "ENVIAR[X]";
		
		Principal.recordTable(tableRow);
		
	}
}
