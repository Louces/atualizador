package supervisor;

import connection.TelnetConnection;
import controller.Console;
import controller.FilterCommand;
import controller.Info;
import controller.SendFile;
import controller.TableInfo;

public class Supervisor4Master implements Supervisor {

	protected final String msgEndUpgrade = "Fim do processo de upgrade";
	protected final String msnNoSpace = "nao possui espaco suficiente para realizar esta atualizacao";
	protected final String msgSyslogChange = "Syslog modificado";
	protected final String msgSyslognNoChange = "Syslog nao precisa de modificacao";
	protected final String msgPing = "0 packets received";
	protected String status;
	protected String nameScript;
	private String nameNE;
	protected boolean flag;
	private String serialNumber;
	private String id;
	private String idVlan201;
	private String versaoAplicacao;
	private boolean containsSlave;
	private int numeroSPVL90;
	private int[] slaves = new int[5];
	private Supervisor4Slave[] escravo = new Supervisor4Slave[5];
	private int coletor;
	private static final int maxSites = 14;
	private int sroutersUp[] = new int[maxSites];
	private boolean srouters[] = new boolean[maxSites];
	private TelnetConnection conexaoColetor;
	private boolean isUpdate;
	
	
	public Supervisor4Slave[] getEscravo() {
		return escravo;
	}

	public void setEscravo(Supervisor4Slave[] escravo) {
		this.escravo = escravo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIdVlan201() {
		return idVlan201;
	}

	public void setIdVlan201(String idVlan201) {
		this.idVlan201 = idVlan201;
	}

	public String getVersaoAplicacao() {
		return versaoAplicacao;
	}

	public void setVersaoAplicacao(String versaoAplicacao) {
		this.versaoAplicacao = versaoAplicacao;
	}

	public int getNumeroSPVL90() {
		return numeroSPVL90;
	}

	public void setNumeroSPVL90(int numeroSPVL90) {
		this.numeroSPVL90 = numeroSPVL90;
	}
    
	public void stopSupervisor(TelnetConnection conexao){
		Console.print("Preparando atualização...");
		for(int i = 0 ; i<3 ; i++)
		 conexao.sendCommand("./stopsupervisor.sh");
	}
	
	public void sleep(TelnetConnection conexao,int i){
		conexao.sendCommand("sleep " + i);
	}
	
	public void clear(TelnetConnection conexao){
		conexao.sendCommand("rm *upgrade*");
	    conexao.sendCommand("rm -rf *bkp*");
	}
	
	public void refreshTable(TelnetConnection conexao, int tipo) {
		String NEWversion = 
		FilterCommand.filter(conexao.sendCommand(
		"./supervisor -v | awk '{print $2}'").replace("V", ""));
		TableInfo.refresh(getSerialNumber(), 3, NEWversion);
		if(tipo==8886){
			TableInfo.refresh(getSerialNumber(), 4, "Unidade reinicializada");
			TableInfo.refresh(getSerialNumber(), 5, "");	
		}
		
	}
	
	public void telnet0900(TelnetConnection conexao, int server) {

		String command = conexao.sendCommand("cat config/srouter_info.conf | grep -m 1 ne | awk '{print $3}'");
		String ID =FilterCommand.filter(command);
		srouters[Integer.parseInt(ID)-1]=true;
		conexao.write("telnet 0 9000");
		conexao.readUntil("SROUTER NE ID [#" + ID + "]>");
		conexao.write("6");
		String srouter = conexao.readUntil("SROUTER NE ID [#" + ID + "]>");
		conexao.write("9");
		conexao.readUntil('$' + " ");
		setSrouters(srouter);
		
		if(server==1){
			Info.setIDColetorOne(ID);
		}else{
			Info.setIDColetorTwo(ID);
		}
		
	}
	
	public void setSrouters(String srouter) {
		for (int i = 0; i < srouters.length ; i++) {
			if (srouter.contains("169.254." + (128+i)+".1")) {
				srouters[i] = true;
			}
		}
	}
	
	public  boolean[] getSrouters(){
		return srouters;
	}
	
	public void setSroutersUp(TelnetConnection conexao, int coletor){
		for(int i = 0 ; i < srouters.length ; i++){
			if(srouters[i]){
				boolean flag = conexao.sendCommand
				("ping -c 1 169.254." + (128 + i) + ".1").contains(msgPing);
				if(!flag){
					sroutersUp[i]= coletor;
				}
			}
		}
	}
	
	public int[] getSroutersUp() {
		return sroutersUp;
	}
	
	public int getColetor() {
		return coletor;
	}

	public void setColetor(int coletor) {
		this.coletor = coletor;
	}
	
	@Override
	public boolean update() {
		
		TelnetConnection conexao = getConexaoColetor();
		conexao.connectVlan101("169.254."+(Integer.parseInt(getId())+127)+".1");
		
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
					conexao.disconnect();
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
						setUpdate(true);
						Console.print("Atualizando tabela");
						refreshTable(conexao,8887);
						rebootNotColetor(conexao);
						conexao.disconnect();
						return true;
					} else if (status.contains(msgSyslognNoChange)) {
						Console.print("Atualizando tabela");
						refreshTable(conexao,8887);
						setUpdate(true);
						rebootNotColetor(conexao);
						conexao.disconnect();
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

	public static int getMaxsites() {
		return maxSites;
	}
	
	public void discoverySlave(TelnetConnection conexao){
		Console.print("Descobrindo placas escravas...");
		String comando =
		conexao.sendCommand("cat config/srouter_spvls_ips.conf");
		
		for (int i = 1; i <= 5; i++) {

			if(comando.contains("169.254."+(i)+".37")){
			 	String ping = 
			 	conexao.sendCommand("ping -c 1 " + "169.254."+(i)+".37");
			 	if(ping.contains("1 packets received")){
			 		slaves[i-1]=1;
			 	}
			}
		}
		
		for(int i = 0 ; i < slaves.length; i++ ){
			if(slaves[i]==1){
				conexao.connectVlan102("169.254."+(i+1)+".37");
				Supervisor4Slave supervisor = new Supervisor4Slave();
				Console.print("Apagando arquivos remanescentes de "+"169.254."+(i+1)+".37");
				conexao.sendCommand("rm *upgrade*");
			    conexao.sendCommand("rm -rf *bkp*");
			    Console.print("Obtendo dados.");
			    supervisor.setId(getId());
			    supervisor.setIdMaster(Integer.parseInt(getId()));
			    supervisor.setIdSlave(i+1);
			    comando = conexao.sendCommand
			    ("cat /proc/cmdline | awk '{print $1}'");
			    supervisor.setSerialNumber(FilterCommand.filter(comando).replaceAll("sn=", ""));
			    comando = conexao.sendCommand
			    ("./supervisor -v | awk '{print $2}'");
			    supervisor.setVersaoAplicacao(FilterCommand.filter(comando).replaceFirst("V", ""));
			    supervisor.setConexaoColetorSlave(conexao);
			    conexao.disconnect();
			    supervisor.setStatus("Descoberto");
			    escravo[i]=supervisor;
			    SendFile.updateSPVL4Slave[Integer.parseInt(supervisor.getId())-1][i]=1;
			    SendFile.serialSlave[Integer.parseInt(supervisor.getId())-1][i]=supervisor.getSerialNumber();
			    setContainsSlave(true);
			}
		}
	}

	public boolean isContainsSlave() {
		return containsSlave;
	}

	public void setContainsSlave(boolean containsSlave) {
		this.containsSlave = containsSlave;
	}

	public TelnetConnection getConexaoColetor() {
		return conexaoColetor;
	}

	public void setConexaoColetor(TelnetConnection conexaoColetor) {
		this.conexaoColetor = conexaoColetor;
	}
	
	public void rebootNotColetor(TelnetConnection conexao){
		boolean flagOne =  getSerialNumber().equals(Info.getSnColetorOne());
		boolean flagTwo =  getSerialNumber().equals(Info.getSnColetorTwo());
		
		if(!(flagOne||flagTwo)){
			Console.print("Reiniciando a unidade");
			conexao.sendCommand("reboot");
			TableInfo.refresh(getSerialNumber(), 4, "Unidade reinicializada.");
			TableInfo.refresh(getSerialNumber(), 5, "-");
		}else{
			TableInfo.refresh(getSerialNumber(), 4, "Aguardando Reboot.");
			TableInfo.refresh(getSerialNumber(), 5, "-");
		}
		
	}

	public boolean isUpdate() {
		return isUpdate;
	}

	public void setUpdate(boolean isUpdate) {
		this.isUpdate = isUpdate;
	}

	public String getNameNE() {
		return nameNE;
	}

	public void setNameNE(String nameNE) {
		this.nameNE = nameNE;
	}
}
