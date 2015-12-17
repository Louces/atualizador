package supervisor;

import connection.TelnetConnection;
import controller.Console;
import controller.FilterCommand;
import controller.TableInfo;

public class Supervisor4Master implements Supervisor {

	private String serialNumber;
	private String id;
	private String idVlan201;
	private String versaoAplicacao;
	private int numeroScravos;
	private int numeroSPVL90;
	//private String ipVLAN100;
	private String status;
	
	//private TelnetConnection conexao;
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	/*public String getIpVLAN100() {
		return ipVLAN100;
	}

	public void setIpVLAN100(String ipVLAN100) {
		this.ipVLAN100 = ipVLAN100;
	}*/

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
	
	@Override
	public boolean atualizarAplicacao() {
		// Implementar
		return false;
	}

}
