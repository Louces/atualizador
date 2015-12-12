package connection;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.swing.JProgressBar;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.io.CopyStreamAdapter;

import controller.Console;
import view.Principal;

public class FtpPutColetor {

	private String user = "root";
	private String password = "root";
	private JProgressBar progressBar = Principal.getProgressBar();
	private File file = Principal.getFileUpgrade();

	public FtpPutColetor(String server) {
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

			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			Console.print("Transmitindo dados...");
			ftp.storeFile(file.getName(), is);
			Console.print("Transmissão concluida");
			System.out.print(ftp.getReplyString());

			ftp.logout();
			Console.print("Conexão FTP fechada com " + server);
			ftp.disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void setCopyStreamListener(CopyStreamAdapter streamListener) {
		throw new UnsupportedOperationException("Not supported yet."); 
	}

	void storeFile(String name, FileInputStream fis) {
		throw new UnsupportedOperationException("Not supported yet."); 
	}

}
