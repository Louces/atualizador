package connection;

import java.awt.Color;
import java.io.IOException;
import java.net.InetAddress;

import controller.Console;
import controller.Info;
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
				inet = InetAddress.getByName(Info.getColetorOne());
			else
				inet = InetAddress.getByName(Info.getColetorTwo());

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
								if (!Info.getColetorOne().isEmpty())
									Principal.getLbColetorUm().setBackground(Color.red);
							} else {
								if (!Info.getColetorTwo().isEmpty())
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
		
		if(Info.getColetorOne().equals(Info.getColetorTwo())){
			if(ping(1)){x=1;}
		}else{
			if(ping(1)){x=1;}
			if(ping(2)){y=2;}	
		}
		
		
		if((x+y)==3){
			Console.print("Nº de coletores válidos : 2");	
		}else if((x+y)!=0){
			Console.print("Nº de coletores válidos : 1");
		}else{
			Console.print("Nº de coletores válidos : 0");
		}
				
		numeroColetoresValidos=x+y;
		Info.setnColetoresValidos(numeroColetoresValidos);
		Principal.configColetores(numeroColetoresValidos);
	}
}
