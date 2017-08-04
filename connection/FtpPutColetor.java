package connection;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.swing.JProgressBar;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.io.CopyStreamAdapter;

import controller.Console;
import controller.Info;
import view.Principal;


/**
 * Esta classe é usada para transferencia de arquivos via FTP. 
 * @author Fabiano Louzada Cesario.
 * @version 1.0
 *
 */
public class FtpPutColetor {

	private String user = "root";
	private String password = "root";
	private JProgressBar progressBar = Principal.getProgressBar();
	private File file = Info.getFileUpgrade();

	
	/**
	 * @param server é o endereço de DCN do host.
	 */
	public FtpPutColetor(String server) {
		progressBar.setValue(0);
		progressBar.setString(0 + "%");
		progressBar.setVisible(true);
		try {
			final FTPClient ftp = new FTPClient();
			ftp.connect(server);
			Console.print("Conexão FTP aberta com " + server);
			ftp.login(user, password);
			System.out.println("Connected to " + server);
			System.out.print(ftp.getReplyString());
			
			InputStream is = new FileInputStream(file.getAbsolutePath());
			
			CopyStreamAdapter streamListener = new CopyStreamAdapter() {
				
				@Override
				public void bytesTransferred
				(long totalBytesTransferred,int bytesTransferred, long streamSize) {
					
					int percent = (int) (totalBytesTransferred * 100 / file.length());
					progressBar.setValue(percent);
					progressBar.setString(percent + "%");
				}
			};

			ftp.setCopyStreamListener(streamListener);
			ftp.enterLocalPassiveMode();
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			Console.print("Transmitindo " + file.getName() +" para " + server);
			//ftp.storeFile("/root/config/"+file.getName(), is);
			ftp.storeFile(file.getName(), is);
			Console.print("Transmissão concluida");
			System.out.print(ftp.getReplyString());

			ftp.logout();
			Console.print("Conexão FTP fechada com " + server);
			ftp.disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}
		progressBar.setVisible(false);	
	}

}
