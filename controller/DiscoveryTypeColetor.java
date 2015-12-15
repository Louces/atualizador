package controller;

import view.Principal;

import connection.TelnetConnection;
import connection.ValidaIP;

public class DiscoveryTypeColetor {

	private ValidaIP pingColetores = new ValidaIP();
	private TelnetConnection conexao;
	private static int numeroVlans;
	private String coletorUmType, coletorDoisType;
	private String out = "Tipo de Coletor : ";
	
	public void validate() {
		Console.print("Descobrindo o Nº de coletores válidos.");
		pingColetores.setNumeroColetoresValidos();
	
		switch (Info.getnColetoresValidos()) {
		case 0:
			Principal.lbTypeColetor.setText(out + "Nenhum coletor válido");
			Principal.setEnableBtn(1);
			return;
		case 1:
			coletorUmType=discoveryType(Info.getColetorOne());
			break;
		case 2:
			coletorDoisType=discoveryType(Info.getColetorTwo());
			break;
		case 3:
			coletorUmType=discoveryType(Info.getColetorOne());
			coletorDoisType=discoveryType(Info.getColetorTwo());
			break;
		}
	config();
	Console.print("Tipo : " + Info.getTypeColetor());
	}

	public String discoveryType(String server) {
		conexao = new TelnetConnection(server);
		conexao.connectVlan100();
		
		Console.print("Descobrindo o tipo de coletor.");
		String comando = conexao.sendCommand
		("ifconfig | grep eth0. | wc | awk '{print $1}'");

		conexao.closeSession();

		for (int i = 0; i < comando.length() - 1; i++)
			if (comando.charAt(i) == '\n') {
				numeroVlans=Integer.parseInt(comando.charAt(i + 1)+"");
				break;
			}
		
		if(numeroVlans==1){
			Info.setTypeColetor("8886");
			return "8886";
		}else if(numeroVlans==6){
			Info.setTypeColetor("8887");
			return "8887";
		}else{
			Info.setTypeColetor("Indefinido");
			return "Indefinido";
		}
	}
	
	public void config() {
		switch (pingColetores.getNumeroColetoresValidos()) {
		case 1:
			Principal.lbTypeColetor.setText(out + coletorUmType);
			Info.setTypeColetor(coletorUmType);
			break;
		case 2:
			Principal.lbTypeColetor.setText(out + coletorDoisType);
			Info.setTypeColetor(coletorDoisType);
			Info.setTypeColetor(coletorDoisType);
			break;
		case 3:
			if (coletorUmType.equals(coletorDoisType)) {
				Principal.lbTypeColetor.setText(out + coletorUmType);
				Info.setTypeColetor(coletorUmType);
			} else {
				Principal.lbTypeColetor.setText(out + " Coletores distintos");
				Info.setTypeColetor("Coletores distintos");
			}
			break;
		}
	}
	
}
