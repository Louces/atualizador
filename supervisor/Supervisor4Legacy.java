package supervisor;

import view.Principal;
import connection.TelnetConnection;

public class Supervisor4Legacy extends Supervisor4Master {
	private TelnetConnection conexao;
	private final String msgEndUpgrade = "Fim do processo de upgrade";
	private final String msnNoSpace = "nao possui espaco suficiente para realizar esta atualizacao";
	private final String msgSyslogChange = "Syslog modificado";
	private final String msgSyslognNoChange = "Syslog nao precisa de modificacao";
	private String status;
	private String nameScript;
	private boolean flag;

	public boolean Update(String server) {
		connect(server);
		stopSupervisor(conexao);
		nameScript = Principal.getFileUpgrade().getName();
		conexao.sendCommand("chmod +x " + nameScript);
		conexao.sendCommand("touch update.log");
		conexao.sendCommand("./" + nameScript + " >./update.log &");
		sleep(conexao, 60);

		while (true) {
			status = conexao.sendCommand("cat update.log");
			flag = status.contains(msgEndUpgrade);

			if (flag) {
				if (status.contains(msnNoSpace)) {
					conexao.closeSession();
					return false;
				}
			sleep(conexao, 4);

				while (true) {
					status = conexao.sendCommand("cat update.log");
					flag = status.contains(msgSyslogChange);
					if (flag) {
						sleep(conexao, 4);
						conexao.sendCommand("killall klogd");
						conexao.sendCommand("rm " + nameScript);
						conexao.sendCommand("reboot");
						return true;
					} else if (status.contains(msgSyslognNoChange)) {
						conexao.sendCommand("rm " + nameScript);
						conexao.sendCommand("reboot");
						return true;
					} else {
						sleep(conexao, 4);
					}
				}
			} else {
				sleep(conexao, 10);
			}
		}
	}
	
	public void connect(String server){
		conexao = new TelnetConnection(server);
		conexao.connectVlan100();
	}

}
