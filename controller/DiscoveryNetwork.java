package controller;

import java.util.ArrayList;

import supervisor.Supervisor4Legacy;
import supervisor.Supervisor4Master;
import supervisor.Supervisor4Slave;
import view.Principal;
import connection.TelnetConnection;

public class DiscoveryNetwork {
    private DiscoveryTypeColetor typeColetor = new DiscoveryTypeColetor();
	private static ArrayList<Object> supervisores = new ArrayList<Object>();
	private Supervisor4Legacy spvlLegacy;
	private Supervisor4Master spvlMaster;
	private int[] Vlan101 = new int[14];
	String[] tableRow = new String[6];
	
	public static ArrayList<Object> getSupervisores() {
		return supervisores;
	}

	public static void setSupervisores(ArrayList<Object> supervisores) {
		DiscoveryNetwork.supervisores = supervisores;
	}
	
	public void network(){
		config();
		
		if(Principal.lbTypeColetor.getText().contains("Nenhum coletor válido")){
			return;
		}
		
		Console.print("Descobrido rede...");
		
		if(Info.getTypeColetor().contains("8886")){
			switch (Info.getnColetoresValidos()) {
			case 1:
				gravaSupervisor8886(1);
				break;
			case 2:
				gravaSupervisor8886(2);
				break;
			case 3:
				gravaSupervisor8886(1);
				gravaSupervisor8886(2);
				break;
			default:
			break;
			}
		Principal.configBtn(1, false);
		Principal.configBtn(2, true);
		
		}else if(Info.getTypeColetor().contains("8887")){
			switch (Info.getnColetoresValidos()) {
			case 1:
				supervisor4(1);
				break;
			case 2:
				supervisor4(2);
				break;
			case 3:
				supervisor4(3);
				break;
			default:
			break;
			}
		Principal.configBtn(1, false);
		Principal.configBtn(2, true);
		
		}else{
			
		}
		Console.print("Rede descoberta.");	
	}
	
	public void supervisorLegado(int server){
		TelnetConnection conexao ;
		spvlLegacy = new Supervisor4Legacy();
		
		if(server==1){
			conexao = Info.getServerOne();
			spvlLegacy.setColetor(1);
		}else{
			conexao = Info.getServerTwo();
			spvlLegacy.setColetor(2);
		}
		getInfoLegacy(conexao);
	}
    
	public void gravaSupervisor8886(int server){
		supervisorLegado(server);
		
		Supervisor4Legacy spvl = new Supervisor4Legacy();
		
		if (server == 1){
			spvl = (Supervisor4Legacy) supervisores.get(0);
			Info.setSnColetorOne(spvl.getSerialNumber());
		}else{
			if(supervisores.size()==1){
				spvl = (Supervisor4Legacy) supervisores.get(0);	
			}else{
				spvl = (Supervisor4Legacy) supervisores.get(1);
			}
			
			Info.setSnColetorTwo(spvl.getSerialNumber());
		}
		
		//tableRow[-1]= Nome do NE
		tableRow[0]= spvl.getId();
		tableRow[1]= spvl.getSerialNumber();
		tableRow[2]= "Mestre";
		tableRow[3]= spvl.getVersaoAplicacao();
		tableRow[4]= "Descoberto";
		tableRow[5]= "ENVIAR[X]";
		Console.print("Gravando dados na tabela.");
		Principal.recordTable(tableRow);
	}
	
	public void gravaSupervisor8887(Supervisor4Master spvl) {
		spvl = (Supervisor4Master) supervisores.get(supervisores.size() - 1);
		tableRow[0] = spvl.getId() + " - " + spvl.getNameNE();
		tableRow[1] = spvl.getSerialNumber();
		tableRow[2] = "Mestre";
		tableRow[3] = spvl.getVersaoAplicacao();
		tableRow[4] = "Descoberto";
		tableRow[5] = "ENVIAR[X]";
		Console.print("Gravando dados na tabela.");
		Principal.recordTable(tableRow);
		
		if(spvl.isContainsSlave()){
		
			Supervisor4Slave supervisor[] = spvl.getEscravo();
			
			for(int i = 0 ; i < supervisor.length ; i++){
				if(supervisor[i]!=null){
					tableRow[0] = supervisor[i].getId();
					tableRow[1] = supervisor[i].getSerialNumber();
					tableRow[2] = "Escravo " + (i+1);
					tableRow[3] = supervisor[i].getVersaoAplicacao();
					tableRow[4] = "Descoberto";
					tableRow[5] = "-";
					Principal.recordTable(tableRow);
				}
			}
			
		}
		
	}

	public void discoveryVLAN101(int server){
		TelnetConnection conexao;
		spvlMaster = new Supervisor4Master();
		
		if(server==1){
			conexao = Info.getServerOne();
			spvlMaster.setColetor(1);
		}else{
			conexao = Info.getServerTwo();
			spvlMaster.setColetor(2);
		}
		
		spvlMaster.telnet0900(conexao, server);
		spvlMaster.setSroutersUp(conexao, server);
		gravaVLAN101(spvlMaster);
	}
	
	public void supervisor4(int coletores) {
		TelnetConnection conexao;
    
		switch (coletores) {
		case 1:
			discoveryVLAN101(coletores);
			break;
		case 2:
			discoveryVLAN101(coletores);
			break;
		case 3:
			discoveryVLAN101(1);
			discoveryVLAN101(2);
			break;
		default:
			break;
		}

		for (int i = 0; i < Vlan101.length; i++) {
			String Vlan = "169.254."+(128+i)+".1";
			switch (Vlan101[i]) {
			case 1:
				conexao = Info.getServerOne();
				getInfoMaster(Vlan, conexao);
				break;
			case 2:
				conexao = Info.getServerTwo();
				getInfoMaster(Vlan, conexao);
				break;
			case 3:
				conexao = Info.getServerOne();
				getInfoMaster(Vlan, conexao);
				break;
			default:
				break;
			}
		}
	}
	
	public void gravaVLAN101(Supervisor4Master spvl) {
		int[] Vlan = spvl.getSroutersUp();
		for (int i = 0; i < Vlan.length; i++) {
			Vlan101[i] += Vlan[i];
			SendFile.updateSPLV4Master[i] += Vlan[i]; //cada supervisor tem q saber por quem é visto! 
		}
	}
	
	public void config(){
		Principal.eraseTable();
		supervisores.clear();
		typeColetor.validate();
	}
	
	public void getInfoLegacy(TelnetConnection conexao){
		String comando;
		Console.print("Apagando arquivos remanescentes...");
		conexao.sendCommand("rm *upgrade*");
	    conexao.sendCommand("rm -rf *bkp*");
	    Console.print("Obtendo dados.");
	    comando = conexao.sendCommand
		("cat supervisor.config | grep -m 1 Numero | awk '{print $5}'");
		spvlLegacy.setId(FilterCommand.filter(comando));
		comando = conexao.sendCommand
		("cat /proc/cmdline | awk '{print $1}'");
		spvlLegacy.setSerialNumber(FilterCommand.filter(comando).replaceAll("sn=", ""));
		comando = conexao.sendCommand
		("./supervisor -v | awk '{print $2}'");
		spvlLegacy.setVersaoAplicacao(FilterCommand.filter(comando).replaceFirst("V", ""));
		spvlLegacy.setStatus("Descoberto");
		Console.print("Adicionando dados...");
		supervisores.add(spvlLegacy);
	}
	
	public void getInfoMaster(String conexaoVLAN101 , TelnetConnection conexao){
		spvlMaster = new Supervisor4Master();
		spvlMaster.setConexaoColetor(conexao);
		conexao.connectVlan101(conexaoVLAN101);	
		String comando;
		Console.print("Apagando arquivos remanescentes...");
		conexao.sendCommand("rm *upgrade*");
	    conexao.sendCommand("rm -rf *bkp*");
	    Console.print("Obtendo dados.");
	    comando = conexao.sendCommand
		("cat config/srouter_info.conf | grep -m 1 ne_id | awk '{print $3}'");
		spvlMaster.setId(FilterCommand.filter(comando));
		comando = conexao.sendCommand
		("cat /proc/cmdline | awk '{print $1}'");
		spvlMaster.setSerialNumber(FilterCommand.filter(comando).replaceAll("sn=", ""));
		comando = conexao.sendCommand("cat config/srouter_info.conf | grep ne_name | awk -F = '{print $2}'");
		spvlMaster.setNameNE(FilterCommand.filter(comando));
		comando = conexao.sendCommand
		("./supervisor -v | awk '{print $2}'");
		spvlMaster.setVersaoAplicacao(FilterCommand.filter(comando).replaceFirst("V", ""));
		spvlMaster.setStatus("Descoberto");
		spvlMaster.discoverySlave(conexao);
		Console.print("Adicionando dados...");
		supervisores.add(spvlMaster);
		gravaSupervisor8887(spvlMaster);
		SendFile.serialMaster[Integer.parseInt(spvlMaster.getId())-1]=spvlMaster.getSerialNumber();
		conexao.disconnect();	
	}

	
}
