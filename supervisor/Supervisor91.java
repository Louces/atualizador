package supervisor;

import connection.FtpPutColetor;
import connection.TelnetConnection;
import controller.Console;
import controller.FilterCommand;
import controller.Info;
import controller.SendFile;
import controller.StoreUpgradeToColetor;
import view.Principal;

public class Supervisor91 {

	private TelnetConnection conexaoColetor;
	private String site;
	private String SN;
	private int coletor;
	private String ID;

	public Supervisor91(int coletor) {
		this.coletor = coletor;
		if (coletor == 1) {
			conexaoColetor = Info.getServerOne();
		} else if (coletor == 2) {
			conexaoColetor = Info.getServerTwo();
		}

		getInfSPVL91();
	}

	public void getInfSPVL91() {
		site = FilterCommand
				.filter(conexaoColetor.sendCommand(
						"cat /opt/padtec/pkgs/padtec.sup.tools.netconfig/conf/local.conf  | grep SITE_NAME | awk -F '=' '{print $2}'"))
				.replaceAll("", "");
		SN = FilterCommand
				.filter(conexaoColetor.sendCommand("printenv | grep PADTEC_SERIAL_NUMBER | awk -F \"=\" '{print $2}'"));
		ID = FilterCommand.filter(conexaoColetor.sendCommand("cat /opt/padtec/pkgs/padtec.sup.tools.netconfig/conf/local.conf | grep SITE_ID | awk -F \"=\" '{print $2}'"));
	    Info.setIDColetorOne(ID);

	}

	public String getSN() {
		return SN;
	}

	public String getSite() {
		return site;
	}

	public boolean getSlave4() {
		int comando = Integer.parseInt(FilterCommand
				.filter(conexaoColetor.sendCommand("netstat -na | grep 8886 | grep .37 | grep ESTABLISHED | wc -l")));

		if (comando > 0) {
			return true;
		}
		return false;
	}

	public Supervisor4Slave[] getSupervisor4() {
		Supervisor4Slave[] slaves = new Supervisor4Slave[5];

		for (int i = 0; i < 5; i++) {

			int id = Integer.parseInt(FilterCommand.filter(conexaoColetor.sendCommand(
					"netstat -na | grep 8886 | grep 169.254." + (i + 1) + ".37 | grep ESTABLISHED | wc -l")));
			Supervisor4Slave slave;

			if (id == 1) {
				slave = new Supervisor4Slave();
				conexaoColetor.connectVlan102("169.254."+(i+1)+".37");
				Console.print("Apagando arquivos remanescentes de "+"169.254."+(i+1)+".37");
				conexaoColetor.sendCommand("rm *upgrade*");
				conexaoColetor.sendCommand("rm -rf *bkp*");
				conexaoColetor.sendCommand("rm *default_config.sh*");
			    Console.print("Obtendo dados.");
			    slave.setIdSlave(i+1);
			    String comando = conexaoColetor.sendCommand
			    ("cat /proc/cmdline | awk '{print $1}'");
			    slave.setSerialNumber(FilterCommand.filter(comando).replaceAll("sn=", ""));
			    comando = conexaoColetor.sendCommand
			    ("./supervisor -v | awk '{print $2}'");
			    slave.setVersaoAplicacao(FilterCommand.filter(comando).replaceFirst("V", ""));
			    comando = FilterCommand.filter(conexaoColetor.sendCommand("uname -a | awk '{print $10}'"));
			    slave.setVersaoAplicacao(slave.getVersaoAplicacao() + " | Kernel " + comando);
			    slave.setConexaoColetorSlave(conexaoColetor);
			    conexaoColetor.disconnect();
			    slave.setStatus("Descoberto");
			    slave.setColetor91(coletor);
			    String[] tableRow = new String[6];
			    tableRow[0] = "Coletor " + coletor; 
			    tableRow[1] = slave.getSerialNumber();
			    tableRow[2] = "SPVL-4 | S1";
			    tableRow[3] = slave.getVersaoAplicacao();
			    tableRow[4] = "Descoberto";
			    tableRow[5] = "ENVIAR[X]";
			    Principal.recordTable(tableRow);
			    slaves[i]=slave;
			}

		}

		return slaves;
	}
	
	public boolean sendoFileToSPVL_4(Supervisor4Slave slave){
		conexaoColetor.sendCommand("cd /srv/tftpboot/");
		conexaoColetor.sendCommand("ftp -p " + "169.254." + slave.getIdSlave() + ".37",": ");
		conexaoColetor.write("root");
		conexaoColetor.readUntil("Password:");
		conexaoColetor.write("root");
		conexaoColetor.readUntil("ftp> ");
		conexaoColetor.sendCommand("put "+ Info.getFileUpgrade().getName(), "ftp> ");
		conexaoColetor.sendCommand("quit");
		conexaoColetor.connectVlan101("169.254." + slave.getIdSlave() + ".37");
		String md5 = FilterCommand.filter(conexaoColetor.sendCommand("md5sum " + Info.getFileUpgrade().getName() +" | awk '{print $1}'"));
		
		
		if((StoreUpgradeToColetor.md5).contains(md5)){
			conexaoColetor.disconnect();
			return true;
		}
		conexaoColetor.disconnect();
		return false;
	}

}
