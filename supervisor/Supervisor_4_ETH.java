package supervisor;

public class Supervisor_4_ETH implements Supervisor {

	private String serialNumber;
	private String id;
	private String idVlan201;
	private String versaoAplicacao;
	private int numeroScravos;
	private int numeroSPVL90;
	private String ipVLAN100;
	
	

	public String getIpVLAN100() {
		return ipVLAN100;
	}

	public void setIpVLAN100(String ipVLAN100) {
		this.ipVLAN100 = ipVLAN100;
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
    
	
	@Override
	public boolean atualizarAplicacao() {
		// Implementar
		return false;
	}

}
