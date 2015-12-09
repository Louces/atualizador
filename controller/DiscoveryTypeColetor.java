package controller;

import view.Principal;

import connection.TelnetConnection;
import connection.ValidaIP;

public class DiscoveryTypeColetor {

	private ValidaIP pingColetores = new ValidaIP();
	private TelnetConnection conexao;
	private static int numeroVlans;
	private static int coletoresValidos;
	private String coletorUmType, coletorDoisType;
	private static String typeColetor; 
	private String out = "Tipo de Coletor : ";
	
	public static String getTypeColetor() {
		return typeColetor;
	}

	public static void setTypeColetor(String typeColetor) {
		DiscoveryTypeColetor.typeColetor = typeColetor;
	}

	public static int getColetoresValidos() {
		return coletoresValidos;
	}

	public static void setColetoresValidos(int coletoresValidos) {
		DiscoveryTypeColetor.coletoresValidos = coletoresValidos;
	}

	public void validate() {
		pingColetores.setNumeroColetoresValidos();
		setColetoresValidos(pingColetores.getNumeroColetoresValidos());

		switch (pingColetores.getNumeroColetoresValidos()) {
		case 0:
			Principal.lbTypeColetor.setText(out + "Nenhum coletor válido");
			return;
		case 1:
			coletorUmType=discoveryType(Principal.getTxfColetorUm().getText());
			break;
		case 2:
			coletorDoisType=discoveryType(Principal.getTxfColetorDois().getText());
			break;
		case 3:
			coletorUmType=discoveryType(Principal.getTxfColetorUm().getText());
			coletorDoisType=discoveryType(Principal.getTxfColetorDois().getText());
			break;
		}
	config();	
	}

	public String discoveryType(String server) {
		conexao = new TelnetConnection(server);
		conexao.connectVlan100();

		String comando = conexao.sendCommand
		("ifconfig | grep eth0. | wc | awk '{print $1}'");

		conexao.closeSession();

		for (int i = 0; i < comando.length() - 1; i++)
			if (comando.charAt(i) == '\n') {
				numeroVlans=Integer.parseInt(comando.charAt(i + 1)+"");
				break;
			}

		if(numeroVlans==1){
			return "8886";
		}else if(numeroVlans==6){
			return "8887";
		}else{
			return "Indefinido";
		}
	}
	
	public void config(){
		switch (pingColetores.getNumeroColetoresValidos()) {
		case 1:
			Principal.lbTypeColetor.setText(out + coletorUmType);
			setTypeColetor(coletorUmType);
			break;
		case 2:
			Principal.lbTypeColetor.setText(out + coletorDoisType);
			setTypeColetor(coletorDoisType);
			break;
		case 3:
			if (coletorUmType.equals(coletorDoisType)){
				Principal.lbTypeColetor.setText(out + coletorUmType);
				setTypeColetor(coletorUmType);
			}else{
			Principal.lbTypeColetor.setText(out +" Coletores distintos");
			setTypeColetor("Coletores distintos");
			}
			break;
		}
	}
	
}
