package supervisor;

import connection.TelnetConnection;
import controller.Console;
import controller.FilterCommand;
import controller.TableInfo;

public class Supervisor4Master implements Supervisor {

	protected final String msgEndUpgrade = "Fim do processo de upgrade";
	protected final String msnNoSpace = "nao possui espaco suficiente para realizar esta atualizacao";
	protected final String msgSyslogChange = "Syslog modificado";
	protected final String msgSyslognNoChange = "Syslog nao precisa de modificacao";
	protected final String msgPing = "0 packets received";
	protected String status;
	protected String nameScript;
	protected boolean flag;
	private String serialNumber;
	private String id;
	private String idVlan201;
	private String versaoAplicacao;
	private int numeroScravos;
	private int numeroSPVL90;
	private int coletor;
	private static final int maxSites = 14;
	private int sroutersUp[] = new int[maxSites];
	private boolean srouters[] = new boolean[maxSites];
	//private TelnetConnection conexao;
	
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

	public int getNumeroScravos() {
		return numeroScravos;
	}

	public void setNumeroScravos(int numeroScravos) {
		this.numeroScravos = numeroScravos;
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
	
	public void refreshTable(TelnetConnection conexao) {
		String NEWversion = 
		FilterCommand.filter(conexao.sendCommand(
		"./supervisor -v | awk '{print $2}'").replace("V", ""));
		TableInfo.refresh(getSerialNumber(), 3, NEWversion);
		TableInfo.refresh(getSerialNumber(), 4, "Unidade reinicializada");
		TableInfo.refresh(getSerialNumber(), 5, "");
	}
	
	public void telnet0900(TelnetConnection conexao) {

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
		// Implementar
		return false;
	}

	public static int getMaxsites() {
		return maxSites;
	}
}
