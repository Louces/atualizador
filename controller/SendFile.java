package controller;

import javax.swing.JProgressBar;

import view.Principal;
import connection.TelnetConnection;


public class SendFile {
	private static JProgressBar progressBar = Principal.getProgressBar();
	public static int updateSPLV4Master[] = new int [14];
	public static int updateSPVL4Slave[][] = new int [5][5];
	public static String serialMaster[] = new String[14];
	
	public static void sendMaster(){

		for(int i = 0 ; i < updateSPLV4Master.length; i++){
		
			switch (updateSPLV4Master[i]) {
			case 1:
				ftpget(1, (i+1));
				break;
			case 2:
				
				break;
				
			case 3:
				
				break;

			default:
				break;
			}
		}
		
	}
	
	public static void ftpget(int coletor , int ID){
		int IDcoletor;
		long tamanho = Info.getFileUpgrade().length();
		String name = Info.getFileUpgrade().getName();
		
		TableInfo.refresh(serialMaster[ID-1], 4,"Upload...");
		
		TelnetConnection conexao;
		
		if(coletor==2){
			conexao = Info.getServerTwo();
			IDcoletor = Integer.parseInt(Info.getIDColetorTwo());
		}else{
			conexao = Info.getServerOne();
			IDcoletor = Integer.parseInt(Info.getIDColetorOne());
		}
		
		conexao.connectVlan101("169.254."+(127 + ID)+".1");
		conexao.sendCommand("ftpget -uroot -proot " + "169.254." + (127+IDcoletor) + ".1 " + name + " " + name + " &");
		
		while(true){
			conexao.sendCommand("sleep 5");
			String flag = conexao.sendCommand("ls");
			if(flag.contains(name)){
				break;
			}
		}
		
		progressBar.setValue(0);
		progressBar.setString(0 + "%");
		progressBar.setVisible(true);
		
		while(true){
			long flag = Long.parseLong(FilterCommand.filter(conexao.sendCommand("ls -la "+ name +" | awk '{print $5}'"))); 
			if(flag<tamanho){
				progressBar.setValue((int) ((flag*100/tamanho)));
				progressBar.setString((int) ((flag*100/tamanho)) + "%");
				conexao.sendCommand("sleep 5");
			}else{
				progressBar.setValue((int) ((flag/tamanho)*100));
				progressBar.setString((int) ((flag*100/tamanho)) + "%");
				progressBar.setVisible(false);
				TableInfo.refresh(serialMaster[ID-1], 4,"Aquardando atualização");
				TableInfo.refresh(serialMaster[ID-1], 5,"ATUALIZAR[X]");
				break;
			}
		}
		
		conexao.disconnect();
		
	}
	
}
