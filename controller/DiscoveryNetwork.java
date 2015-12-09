package controller;

import java.util.ArrayList;

import supervisor.Supervisor4Legacy;
import view.Principal;
import connection.TelnetConnection;

public class DiscoveryNetwork {
    private String serverUm = Principal.getTxfColetorUm().getText();
    private String serverDois = Principal.getTxfColetorDois().getText();
    private DiscoveryTypeColetor typeColetor = new DiscoveryTypeColetor();
	public static ArrayList<Object> supervisores = new ArrayList<Object>();
	
	public void network(){
		Principal.eraseTable();
		supervisores.clear();
		typeColetor.validate();
		
		if(Principal.lbTypeColetor.getText().contains("8886")){
			switch (DiscoveryTypeColetor.getColetoresValidos()) {
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
		Supervisor4Legacy spvl = new Supervisor4Legacy();
		TelnetConnection conexao = new TelnetConnection(server);
		conexao.connectVlan100();
		conexao.sendCommand("rm *upgrade*");
	    conexao.sendCommand("rm -rf *bkp*");
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
		spvl.setStatus("Descoberto");
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

				if (end != 0){
					break;
				}
			}
		}
		return command.substring(start, end);
	}
	
	public void gravaSupervisor8886(String server, int coletor){
		gravaSupervisorLegado(server);
		String[] tableRow = new String[6];
		Supervisor4Legacy spvl = new Supervisor4Legacy();
		
		if (coletor == 1)
			spvl = (Supervisor4Legacy) supervisores.get(0);
		else
			spvl = (Supervisor4Legacy) supervisores.get(1);	
		
		tableRow[0]= spvl.getId();
		tableRow[1]= spvl.getSerialNumber().replaceAll("sn=", "");
		tableRow[2]= "Mestre";
		tableRow[3]= spvl.getVersaoAplicacao().replaceFirst("V", "");
		tableRow[4]= "Descoberto";
		tableRow[5]= "ENVIAR[X]";
		Principal.recordTable(tableRow);
	}
}
