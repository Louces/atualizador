package connection;

import org.apache.commons.net.telnet.*;
import java.io.*;

public class TelnetConnection {

	private TelnetClient telnet = new TelnetClient();

	private InputStream in;
	private PrintStream out;
	private char prompt = '$';
	private String server;
	

	public TelnetConnection(String server) {
		setServer(server);
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}
	
	
	public void connectVlan100() {

		try {

			telnet.connect(getServer(), 23);

			in = telnet.getInputStream();
			out = new PrintStream(telnet.getOutputStream());

			readUntil("login: ");

			write("root");

			readUntil("Password: ");

			write("root");

			readUntil(prompt + " ");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void connectVlan101(String IP) {

		write("telnet " + IP);

		readUntil("login: ");

		write("root");

		readUntil("Password: ");

		write("root");

		readUntil('$' + " ");

	}

	public String telnet0900(String ID) {

		write("telnet 0 9000");

		readUntil("SROUTER NE ID [#" + ID + "]>");

		write("6");

		String Srouter = readUntil("SROUTER NE ID [#" + ID + "]>");

		write("9");

		readUntil('$' + " ");

		return Srouter;

	}
	
	public String sendCommand(String command) {
		try {
			write(command);
			return readUntil(prompt + " ");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void disconnect() {
		sendCommand("exit");
	}

	public void closeSession() {
		try {
			telnet.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void write(String value) {
		try {
			out.println(value);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String readUntil(String pattern) {

		try {
			char lastChar = pattern.charAt(pattern.length() - 1);

			StringBuffer sb = new StringBuffer();
			char ch = (char) in.read();

			while (true) {
				System.out.print(ch);
				sb.append(ch);
				
				if (ch == '5') {
					ch = (char) in.read();
					System.out.print(ch);
					sb.append(ch);
					
					if (ch == 'b') {
						ch = (char) in.read();
						System.out.print(ch);
						sb.append(ch);
						
						if (ch == '#') {
							pattern = '#' + " ";
							System.out.print(ch);
							sb.append(ch);
						}
					}

				}

				if (ch == lastChar) 
					if (sb.toString().endsWith(pattern))
						return sb.toString();
				
				ch = (char) in.read();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
