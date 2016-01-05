package supervisor;

public class Supervisor4Slave extends Supervisor4Master{

	private int idMaster;
	private int idSlave;

	public int getIdMaster() {
		return idMaster;
	}

	public void setIdMaster(int idMaster) {
		this.idMaster = idMaster;
	}

	public int getIdSlave() {
		return idSlave;
	}

	public void setIdSlave(int idSlave) {
		this.idSlave = idSlave;
	}

}
