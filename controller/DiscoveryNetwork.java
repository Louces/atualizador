package controller;

import java.util.ArrayList;

import supervisor.Supervisor4Legacy;
import supervisor.Supervisor4Master;
import supervisor.Supervisor4Slave;
import supervisor.Supervisor91;
import view.Principal;
import connection.TelnetConnection;

public class DiscoveryNetwork {
	private DiscoveryTypeColetor typeColetor = new DiscoveryTypeColetor();
	private static ArrayList<Object> supervisores = new ArrayList<Object>();
	private Supervisor4Legacy spvlLegacy;
	private Supervisor4Master spvlMaster;
	public static Supervisor4Slave[] spvl4slavesColetorUm;
	public static Supervisor4Slave[] spvl4slavesColetorDois;
	private int[] Vlan101 = new int[14];
	String[] tableRow = new String[6];

	public static ArrayList<Object> getSupervisores() {
		return supervisores;
	}

	public static void setSupervisores(ArrayList<Object> supervisores) {
		DiscoveryNetwork.supervisores = supervisores;
	}

	public void network() {
		config();

		if (Principal.lbTypeColetor.getText().contains("Nenhum coletor v�lido")) {
			return;
		}

		Console.print("Descobrido rede...");

		if (Info.getTypeColetor().contains("8886")) {
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
		} else if (Info.getTypeColetor().equals("8887")) {
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
		} else if (Info.getTypeColetor().equals("8887 | SPVL-91")) {
			switch (Info.getnColetoresValidos()) {
			case 1:
				SPVL91(1);
				break;
			case 2:
				SPVL91(2);
				break;
			case 3:
				SPVL91(3);
				break;

			default:
				break;
			}
		}

		Principal.configBtn(1, false);
		if (!Info.getTypeColetor().equals("Coletores distintos")) {

			if (Info.getnColetoresValidos() != 0) {
				Principal.configBtn(2, true);
				Console.print("Rede descoberta.");
			}

		} else {
			Console.print("ERRO: Atualiza��o disponivel apenas para coletores do mesmo tipo.");
			try {
				Thread.sleep(7000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.exit(0);
		}

	}

	public void supervisorLegado(int server) {
		TelnetConnection conexao;
		spvlLegacy = new Supervisor4Legacy();

		if (server == 1) {
			conexao = Info.getServerOne();
			spvlLegacy.setColetor(1);
		} else {
			conexao = Info.getServerTwo();
			spvlLegacy.setColetor(2);
		}
		getInfoLegacy(conexao);
	}

	public void gravaSupervisor8886(int server) {
		supervisorLegado(server);

		Supervisor4Legacy spvl = new Supervisor4Legacy();

		if (server == 1) {
			spvl = (Supervisor4Legacy) supervisores.get(0);
			Info.setSnColetorOne(spvl.getSerialNumber());
		} else {
			if (supervisores.size() == 1) {
				spvl = (Supervisor4Legacy) supervisores.get(0);
			} else {
				spvl = (Supervisor4Legacy) supervisores.get(1);
			}

			Info.setSnColetorTwo(spvl.getSerialNumber());
		}

		// tableRow[-1]= Nome do NE
		tableRow[0] = spvl.getId();
		tableRow[1] = spvl.getSerialNumber();
		tableRow[2] = "Mestre";
		tableRow[3] = spvl.getVersaoAplicacao();
		tableRow[4] = "Descoberto";
		tableRow[5] = "ENVIAR[X]";
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

		if (spvl.isContainsSlave()) {

			Supervisor4Slave supervisor[] = spvl.getEscravo();

			for (int i = 0; i < supervisor.length; i++) {
				if (supervisor[i] != null) {
					tableRow[0] = supervisor[i].getId();
					tableRow[1] = supervisor[i].getSerialNumber();
					tableRow[2] = "Escravo " + (i + 1);
					tableRow[3] = supervisor[i].getVersaoAplicacao();
					tableRow[4] = "Descoberto";
					tableRow[5] = "-";
					Principal.recordTable(tableRow);
				}
			}

		}

	}

	public void discoveryVLAN101(int server) {
		TelnetConnection conexao;
		spvlMaster = new Supervisor4Master();

		if (server == 1) {
			conexao = Info.getServerOne();
			spvlMaster.setColetor(1);
		} else {
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
			String Vlan = "169.254." + (128 + i) + ".1";
			switch (Vlan101[i]) {
			case 1:
				conexao = Info.getServerOne();
				if (!getInfoMaster(Vlan, conexao)) {
					Vlan101[i] = 0;
					SendFile.updateSPLV4Master[i] = 0;
				}
				break;
			case 2:
				conexao = Info.getServerTwo();
				if (!getInfoMaster(Vlan, conexao)) {
					Vlan101[i] = 0;
					SendFile.updateSPLV4Master[i] = 0;
				}
				break;
			case 3:
				// corrigir aqui...
				Console.print("Coletor 3");
				conexao = Info.getServerOne();
				if (!getInfoMaster(Vlan, conexao)) {
					Vlan101[i] = 0;
					SendFile.updateSPLV4Master[i] = 0;
				}
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

			SendFile.updateSPLV4Master[i] += Vlan[i]; // cada supervisor tem q
														// saber por quem �
														// visto!
		}
	}

	public void config() {
		Principal.eraseTable();
		supervisores.clear();
		typeColetor.validate();
	}

	public void getInfoLegacy(TelnetConnection conexao) {
		eraseFilesSPVL4(conexao);
		Console.print("Obtendo dados.");
		String comando;
		comando = conexao.sendCommand("cat supervisor.config | grep -m 1 Numero | awk '{print $5}'");
		spvlLegacy.setId(FilterCommand.filter(comando));
		spvlLegacy.setSerialNumber(getSerialSPVL4(conexao));
		spvlLegacy.setVersaoAplicacao(getVersionSPVL4(conexao));
		spvlLegacy.setStatus("Descoberto");
		Console.print("Adicionando dados...");
		supervisores.add(spvlLegacy);
	}

	public boolean getInfoMaster(String conexaoVLAN101, TelnetConnection conexao) {
		spvlMaster = new Supervisor4Master();
		spvlMaster.setConexaoColetor(conexao);
		boolean flag = conexao.connectVlan101(conexaoVLAN101);
		if (!flag) {
			conexao.sendCommand("");
			// conexao.disconnect();
			return false;
		} else {
			eraseFilesSPVL4(conexao);
			Console.print("Obtendo dados.");
			String comando;
			comando = conexao.sendCommand("cat config/srouter_info.conf | grep -m 1 ne | awk -F \"=\" '{print $2}'");
			String ID = FilterCommand.filter(comando).trim();
			spvlMaster.setId(ID);
			spvlMaster.setSerialNumber(getSerialSPVL4(conexao));
			comando = conexao.sendCommand("cat config/srouter_info.conf | grep ne_name | awk -F = '{print $2}'");
			spvlMaster.setNameNE(FilterCommand.filter(comando));
			spvlMaster.setVersaoAplicacao(getVersionSPVL4(conexao));
			spvlMaster.setStatus("Descoberto");
			spvlMaster.discoverySlave(conexao);
			Console.print("Adicionando dados...");
			supervisores.add(spvlMaster);
			gravaSupervisor8887(spvlMaster);
			SendFile.serialMaster[Integer.parseInt(spvlMaster.getId()) - 1] = spvlMaster.getSerialNumber();
			conexao.disconnect();
			return true;
		}

	}

	public void eraseFilesSPVL4(TelnetConnection conexao) {
		Console.print("Apagando arquivos remanescentes...");
		conexao.sendCommand("rm *upgrade*");
		conexao.sendCommand("rm -rf *bkp*");
		conexao.sendCommand("rm *default_config.sh*");
	}

	public String getSerialSPVL4(TelnetConnection conexao) {
		String comando = conexao.sendCommand("cat /proc/cmdline | awk '{print $1}'");
		return FilterCommand.filter(comando).replaceAll("sn=", "");
	}

	public String getVersionSPVL4(TelnetConnection conexao) {
		String comando = conexao.sendCommand("./supervisor -v | awk '{print $2}'");
		String comandoKernel = FilterCommand.filter(conexao.sendCommand("uname -a | awk '{print $10}'"));
		return FilterCommand.filter(comando).replaceFirst("V", "") + " | Kernel " + comandoKernel;
	}

	public void SPVL91(int coletores) {
		TelnetConnection conexao;

		switch (coletores) {
		case 1:
			getInfoSPVL91(1);
			break;
		case 2:
			getInfoSPVL91(2);
			break;
		case 3:
			getInfoSPVL91(1);
			getInfoSPVL91(2);
			break;
		default:
			break;
		}
	}

	public void getInfoSPVL91(int coletor) {
		Console.print("Coletando informa��es do SPVL-91");
		Supervisor91 supervisor = new Supervisor91(coletor);
		tableRow[0] = supervisor.getSite();
		tableRow[1] = supervisor.getSN();
		tableRow[2] = "SPVL-91 - Mestre";
		tableRow[3] = "---------------------------------";
		tableRow[4] = "---------------------------------";
		tableRow[5] = "---------------------------------";
		

		if (supervisor.getSlave4()) {
			Principal.recordTable(tableRow);
			Console.print("Obtendo informa��es das placas SPVL-4 escravas.");

			if (coletor == 1) {
				spvl4slavesColetorUm = supervisor.getSupervisor4();
			} else if (coletor == 2) {
				spvl4slavesColetorDois = supervisor.getSupervisor4();
			}
		} else {
			Console.print("Coletor " + coletor + " sem unidades SPVL-4 escravos.");
			Info.setnColetoresValidos(Info.getnColetoresValidos() - coletor);
			Principal.configColetores(Info.getnColetoresValidos());
		}

	}
}
