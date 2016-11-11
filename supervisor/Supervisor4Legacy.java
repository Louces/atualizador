package supervisor;

import connection.TelnetConnection;
import controller.Console;
import controller.FilterCommand;
import controller.Info;
import controller.TableInfo;

public class Supervisor4Legacy extends Supervisor4Master {
	private TelnetConnection conexao;
	
	
	@Override
	public boolean update() {
		connect();
		/*//Tratando o problema de espaço
		String espacoString = FilterCommand.filter(conexao.sendCommand("df -h | grep -m 1 /dev | awk '{print $5}'").replaceAll("%",""));
		int espaco = Integer.parseInt(espacoString);
		
		if(espaco >= 80){
			Console.print("Atualização falhou devido a falta de espaço.");
			TableInfo.refresh(getSerialNumber(), 4, "Falta de espaço.");
			conexao.disconnect();
			return false;
		}//Tratando o problema de espaço
		 */		
		stopSupervisor(conexao);
		Console.print("Iniciando atualização em : " + getSerialNumber());
		nameScript = Info.getFileUpgrade().getName();
		conexao.sendCommand("chmod +x " + nameScript);
		conexao.sendCommand("touch update.log");
		conexao.sendCommand("./" + nameScript + " >./update.log &");
		TableInfo.refresh(getSerialNumber(), 4, "Em atualização");
		Console.print("Aguarde 60 segundos.");
		sleep(conexao, 60);
		
		while (true) {
			status = conexao.sendCommand("cat update.log");
			flag = status.contains(msgEndUpgrade);

			if (flag) {
				Console.print("Fim da atualização...");
				if (status.contains(msnNoSpace)) {
					Console.print("Atualização falhou devido a falta de espaço.");
					TableInfo.refresh(getSerialNumber(), 4, "Falta de espaço.");
					conexao.closeSession();
					return false;
				}
			Console.print("Aguarde 4 segundos.");	
			sleep(conexao, 4);

				while (true) {
					status = conexao.sendCommand("cat update.log");
					flag = status.contains(msgSyslogChange);
					boolean endUpgrade = status.contains(busyBox);
					if (flag) {
						Console.print("Aguarde 4 segundos.");
						sleep(conexao, 4);
						conexao.sendCommand("killall klogd");
						conexao.sendCommand("rm " + nameScript);
						Console.print("Atualizando tabela");
						runDefaultConfig(conexao);
						refreshTable(conexao,8886);
						Console.print("Reiniciando a unidade");
						conexao.sendCommand("reboot");
						return true;
					} else if (status.contains(msgSyslognNoChange) || endUpgrade) {
						conexao.sendCommand("rm " + nameScript);
						Console.print("Atualizando tabela");
						runDefaultConfig(conexao);
						refreshTable(conexao,8886);
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
	
	public void runDefaultConfig(TelnetConnection conexao){
		String contemDefaultConfig = conexao.sendCommand("ls");
		
		if(contemDefaultConfig.contains("default_config.sh")){
			Console.print("Executando default config.");
			conexao.sendCommand("./default_config.sh", "(s/n) ");
			conexao.sendCommand("n");
		}
		
	}
	
	public void connect(){
		if(getColetor()==1){
			conexao = Info.getServerOne();
		}else{
			conexao = Info.getServerTwo();
		}
	}
}
