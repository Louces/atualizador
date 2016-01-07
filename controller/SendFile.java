package controller;

import javax.swing.JProgressBar;

import view.Principal;
import connection.TelnetConnection;


public class SendFile {
	private static JProgressBar progressBar = Principal.getProgressBar();
	private static String name;
	public static int updateSPLV4Master[] = new int [14];
	public static int updateSPVL4Slave[][] = new int [5][5];
	public static String serialMaster[] = new String[14];
	public static String serialSlave[][] = new String [5][5];
	
	
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
		name = Info.getFileUpgrade().getName();
		
		int row = TableInfo.getRow(serialMaster[ID-1]);
		boolean send = Principal.getTabela().getValueAt(row, 5).equals("ENVIAR[X]");
		
		if(!send){
		return;	
		}
		
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
			long flag = Long.parseLong(FilterCommand.filter(conexao.sendCommand("ls -la *supervisor_upgrade* | awk '{print $5}'"))); 
			if(flag<tamanho){
				progressBar.setValue((int) ((flag*100/tamanho)));
				progressBar.setString((int) ((flag*100/tamanho)) + "%");
				conexao.sendCommand("sleep 5");
			}else{
				progressBar.setValue((int) ((flag/tamanho)*100));
				progressBar.setString((int) ((flag*100/tamanho)) + "%");
				progressBar.setVisible(false);
				String md5 = conexao.sendCommand("md5sum "+ Info.getFileUpgrade().getName() + "| awk '{print $1}'");
				
				if(md5.contains(Principal.getMd5())){
					TableInfo.refresh(serialMaster[ID-1], 4,"Aquardando atualização");
					TableInfo.refresh(serialMaster[ID-1], 5,"ATUALIZAR[X]");
					updateSPLV4Master[ID-1]=-1;	
				}else{
					TableInfo.refresh(serialMaster[ID-1], 4,"Descoberto");
				}
				
				break;
			}
		}
		
		
		for (int j = 0; j < 5; j++) {
			if (updateSPVL4Slave[ID - 1][j] == 1) {
				ftpgetSlave(conexao, ID - 1, j);
			}
		}

		conexao.disconnect();
	}
	
	public static void ftpgetSlave(TelnetConnection conexao,int i,int j){
		TableInfo.refresh(serialSlave[i][j], 4,"Upload...");
		name = Info.getFileUpgrade().getName();
		long tamanho = Info.getFileUpgrade().length();
		progressBar.setValue(0);
		progressBar.setString(0 + "%");
		progressBar.setVisible(true);
		
		conexao.connectVlan102("169.254."+(j+1)+".37");
		conexao.sendCommand("ftpget -uroot -proot " + "169.254.0.37 " + name + " " + name + " &");
		
		while(true){
			conexao.sendCommand("sleep 5");
			String flag = conexao.sendCommand("ls");
			if(flag.contains(name)){
				break;
			}
		}
		
		while(true){
			long flag = Long.parseLong(FilterCommand.filter(conexao.sendCommand("ls -la *supervisor_upgrade* | awk '{print $5}'"))); 
			if(flag<tamanho){
				progressBar.setValue((int) ((flag*100/tamanho)));
				progressBar.setString((int) ((flag*100/tamanho)) + "%");
				conexao.sendCommand("sleep 5");
			}else{
				progressBar.setValue((int) ((flag/tamanho)*100));
				progressBar.setString((int) ((flag*100/tamanho)) + "%");
				progressBar.setVisible(false);
				String md5 = conexao.sendCommand("md5sum "+ Info.getFileUpgrade().getName() + "| awk '{print $1}'");
				
				if(md5.contains(Principal.getMd5())){
					TableInfo.refresh(serialSlave[i][j], 4,"Aquardando atualização");
					TableInfo.refresh(serialSlave[i][j], 5,"ATUALIZAR[X]");
					updateSPVL4Slave[i][j]=-1;
				}else{
					TableInfo.refresh(serialSlave[i][j], 4,"Descoberto");
				}
				
				break;
			}
		}
		conexao.disconnect();
	}
	
}
