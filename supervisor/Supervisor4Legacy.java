package supervisor;

import view.Principal;
import connection.TelnetConnection;
import controller.Console;
import controller.Info;
import controller.TableInfo;

public class Supervisor4Legacy extends Supervisor4Master {
	private TelnetConnection conexao;
	private final String msgEndUpgrade = "Fim do processo de upgrade";
	private final String msnNoSpace = "nao possui espaco suficiente para realizar esta atualizacao";
	private final String msgSyslogChange = "Syslog modificado";
	private final String msgSyslognNoChange = "Syslog nao precisa de modificacao";
	private String status;
	private String nameScript;
	private boolean flag;
	private int coletor;

	public boolean Update() {
		connect();
		stopSupervisor(conexao);
		Console.print("Iniciando atualiza��o em : " + getSerialNumber());
		nameScript = Principal.getFileUpgrade().getName();
		conexao.sendCommand("chmod +x " + nameScript);
		conexao.sendCommand("touch update.log");
		conexao.sendCommand("./" + nameScript + " >./update.log &");
		TableInfo.refresh(getSerialNumber(), 4, "Em atualiza��o");
		Console.print("Aguarde 60 segundos.");
		sleep(conexao, 60);
		
		while (true) {
			status = conexao.sendCommand("cat update.log");
			flag = status.contains(msgEndUpgrade);

			if (flag) {
				Console.print("Fim da atualiza��o...");
				if (status.contains(msnNoSpace)) {
					Console.print("Atualiza��o falhou devido a falta de espa�o.");
					TableInfo.refresh(getSerialNumber(), 4, "Falta de espa�o.");
					conexao.closeSession();
					return false;
				}
			Console.print("Aguarde 4 segundos.");	
			sleep(conexao, 4);

				while (true) {
					status = conexao.sendCommand("cat update.log");
					flag = status.contains(msgSyslogChange);
					if (flag) {
						Console.print("Aguarde 4 segundos.");
						sleep(conexao, 4);
						conexao.sendCommand("killall klogd");
						conexao.sendCommand("rm " + nameScript);
						Console.print("Atualizando tabela");
						refreshTable(conexao);
						Console.print("Reiniciando a unidade");
						conexao.sendCommand("reboot");
						return true;
					} else if (status.contains(msgSyslognNoChange)) {
						conexao.sendCommand("rm " + nameScript);
						Console.print("Atualizando tabela");
						refreshTable(conexao);
						Console.print("Reiniciando a unidade");
						conexao.sendCommand("reboot");
						return true;
					} else {
						Console.print("Aguarde 4 segundos.");
						sleep(conexao, 4);
					}
				}
			} else {
				Console.print("Aguarde 10 segundos.");
				sleep(conexao, 10);
			}
		}
	}
	
	public void connect(){
		if(getColetor()==1){
			conexao = Info.getServerOne();
		}else{
			conexao = Info.getServerTwo();
		}
	}

	public int getColetor() {
		return coletor;
	}

	public void setColetor(int coletor) {
		this.coletor = coletor;
	}
	

}
