package connection;

import java.awt.Color;
import java.io.IOException;
import java.net.InetAddress;

import view.Principal;

public class ValidaIP {
	private String addressIP;
	private String localHost = "127.0.0.1";
	private int numeroColetoresValidos;

	public String getAddressIP() {
		return addressIP;
	}

	public void setAddressIP(String addressIP) {
		this.addressIP = addressIP;
	}

	public boolean ping(int coletor) {
		try {
			InetAddress inet;

			if (coletor == 1)
				inet = InetAddress.getByName(Principal.getTxfColetorUm().getText());
			else
				inet = InetAddress.getByName(Principal.getTxfColetorDois().getText());

			if (inet.isReachable(5000)) {
				setAddressIP(inet.toString());
				
				if (getAddressIP().contains(localHost)) {
					return false;
				}

				return true;
			} else
				return false;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void monitoringIP(final int coletor) {
		Thread mon = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(10000);
						if (ping(coletor)) {
							if (coletor == 1)
								Principal.getLbColetorUm().setBackground(Color.green);
							else
								Principal.getLbColetorDois().setBackground(Color.green);
						} else {
							if (coletor == 1) {
								if (!Principal.getTxfColetorUm().getText().isEmpty())
									Principal.getLbColetorUm().setBackground(Color.red);
							} else {
								if (!Principal.getTxfColetorDois().getText().isEmpty())
									Principal.getLbColetorDois().setBackground(Color.red);
							}
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		mon.start();
	}
	
	public int getNumeroColetoresValidos() {
		return numeroColetoresValidos;
	}

	public void setNumeroColetoresValidos() {
		int x=0, y=0;
		if(ping(1)){x=1;}
		if(ping(2)){y=2;}
		numeroColetoresValidos=x+y;
	}
}
