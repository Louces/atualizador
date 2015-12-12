package controller;

import java.util.ArrayList;

import supervisor.Supervisor4Legacy;
import view.Principal;
import connection.TelnetConnection;

public class DiscoveryNetwork {
    private String serverUm = Principal.getTxfColetorUm().getText();
    private String serverDois = Principal.getTxfColetorDois().getText();
    private DiscoveryTypeColetor typeColetor = new DiscoveryTypeColetor();
	private static ArrayList<Object> supervisores = new ArrayList<Object>();
	
	public static ArrayList<Object> getSupervisores() {
		return supervisores;
	}

	public static void setSupervisores(ArrayList<Object> supervisores) {
		DiscoveryNetwork.supervisores = supervisores;
	}
	
	public void network(){
		Principal.eraseTable();
		supervisores.clear();
		typeColetor.validate();
		
		Console.print("Descobrido rede...");
		
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
		Principal.configBtn(1, false);
		Principal.configBtn(2, true);
		
		}else if(Principal.lbTypeColetor.getText().contains("8887")){
			
		}else{
			
		}
		Console.print("Rede descoberta.");	
	}
	
	public void gravaSupervisorLegado(String server){
		String comando;
		Supervisor4Legacy spvl = new Supervisor4Legacy();
		TelnetConnection conexao = new TelnetConnection(server);
		conexao.connectVlan100();
		Console.print("Apagando arquivos remanescentes...");
		conexao.sendCommand("rm *upgrade*");
	    conexao.sendCommand("rm -rf *bkp*");
	    Console.print("Obtendo dados.");
	    comando = conexao.sendCommand
		("cat supervisor.config | grep -m 1 Numero | awk '{print $5}'");
		spvl.setId(FilterCommand.filter(comando));
		comando = conexao.sendCommand
		("cat /proc/cmdline | awk '{print $1}'");
		spvl.setSerialNumber(FilterCommand.filter(comando).replaceAll("sn=", ""));
		comando = conexao.sendCommand
		("./supervisor -v | awk '{print $2}'");
		spvl.setVersaoAplicacao(FilterCommand.filter(comando).replaceFirst("V", ""));
		spvl.setIpVLAN100(server);
		spvl.setStatus("Descoberto");
		conexao.closeSession();
		Console.print("Adicionando dados...");
		supervisores.add(spvl);
	}
    
	public void gravaSupervisor8886(String server, int coletor){
		gravaSupervisorLegado(server);
		String[] tableRow = new String[6];
		Supervisor4Legacy spvl = new Supervisor4Legacy();
		
		if (coletor == 1){
			spvl = (Supervisor4Legacy) supervisores.get(0);
			Strings.setSnColetorOne(spvl.getSerialNumber());
		}else{
			spvl = (Supervisor4Legacy) supervisores.get(1);
			Strings.setSnColetorTwo(spvl.getSerialNumber());
		}
				
		tableRow[0]= spvl.getId();
		tableRow[1]= spvl.getSerialNumber();
		tableRow[2]= "Mestre";
		tableRow[3]= spvl.getVersaoAplicacao();
		tableRow[4]= "Descoberto";
		tableRow[5]= "ENVIAR[X]";
		Console.print("Gravando dados na tabela.");
		Principal.recordTable(tableRow);
	}
}
