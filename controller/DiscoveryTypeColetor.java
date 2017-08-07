package controller;

import view.Principal;

import java.awt.Color;

import connection.TelnetConnection;
import connection.ValidaIP;

public class DiscoveryTypeColetor {

	private ValidaIP pingColetores = new ValidaIP();
	public static TelnetConnection conexao;
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
			coletorUmType = discoveryType(1);
			break;
		case 2:
			coletorDoisType = discoveryType(2);
			break;
		case 3:
			coletorUmType = discoveryType(1);
			coletorDoisType = discoveryType(2);
			break;
		}
		config();
		Console.print("Tipo : " + Info.getTypeColetor());
	}

	public String discoveryType(int server) {

		if (server == 1) {
			conexao = new TelnetConnection(Info.getColetorOne());
			Info.setServerOne(conexao);
			conexao.connectVlan100();
			String SNONE = conexao.sendCommand("cat /proc/cmdline | awk '{print $1}'");
			Info.setSnColetorOne((FilterCommand.filter(SNONE).replaceAll("sn=", "")));

			if (SNONE.contains("root=/dev/sda2")) {
				SNONE = FilterCommand
						.filter(conexao.sendCommand("printenv | grep PADTEC_SERIAL_NUMBER | awk -F \"=\" '{print$2}'"));
				Info.setSnColetorOne(SNONE);
			}

		} else {
			conexao = new TelnetConnection(Info.getColetorTwo());
			Info.setServerTwo(conexao);
			conexao.connectVlan100();
			String SNTWO = conexao.sendCommand("cat /proc/cmdline | awk '{print $1}'");
			Info.setSnColetorTwo((FilterCommand.filter(SNTWO).replaceAll("sn=", "")));

			if (SNTWO.contains("root=/dev/sda2")) {
				SNTWO = FilterCommand
						.filter(conexao.sendCommand("printenv | grep PADTEC_SERIAL_NUMBER | awk -F \"=\" '{print$2}'"));
				Info.setSnColetorTwo(SNTWO);
			}

		}

		Console.print("Descobrindo o tipo de coletor.");
		String comando = conexao.sendCommand("ifconfig | grep eth0. | wc | awk '{print $1}'");

		for (int i = 0; i < comando.length() - 1; i++)
			if (comando.charAt(i) == '\n') {
				numeroVlans = Integer.parseInt(comando.charAt(i + 1) + "");
				break;
			}

		if (numeroVlans == 1 || numeroVlans == 2) {
			Info.setTypeColetor("8886");
			return "8886";
		} else if (numeroVlans == 6) {
			Info.setTypeColetor("8887");
			return "8887";
		} else if (numeroVlans == 0) {
			int nVlans = Integer.parseInt(FilterCommand.filter(conexao.sendCommand("ifconfig | grep cpu | wc -l")));
			if (nVlans > 0) {
				Info.setTypeColetor("8887 | SPVL-91");
				conexao.sendCommand("cd /srv/tftpboot");
				conexao.sendCommand("rm *upgrade*");
				conexao.sendCommand("rm -rf *bkp*");
				conexao.sendCommand("cd");
				return "8887 | SPVL-91";
			} else {
				return "Indefinido";
			}
		} else {
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
				Console.print("Fim do processo de atualização.");
				Principal.setDisableAll();
				Principal.getTxfColetorUm().setText("");
				Principal.getTxfColetorDois().setText("");
				Principal.getLbColetorUm().setBackground(Color.gray);
				Principal.getLbColetorDois().setBackground(Color.gray);
				Principal.configBtn(1, true);
				Principal.configBtn(6, false);
				Principal.flagRebootColetor = false;
				Principal.configColetores(3);
			}
			break;
		}
	}

}
