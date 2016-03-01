package connection;

import java.io.InputStream;
import java.io.PrintStream;

import org.apache.commons.net.telnet.TelnetClient;


/**
 * Esta classa é usada para conexao telnet com o hosts e suas vlans internas.
 * @author Fabiano Louzada Cesario.
 * @version 1.0
 *
 */
public class TelnetConnection {
	private TelnetClient telnet = new TelnetClient();
	private InputStream in;
	private PrintStream out;
	private char prompt = '$';
	private String server;
	
	
	/**
	 * @param é o IP de DCN.
	 */
	public TelnetConnection(String server) {
		setServer(server);
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	
	/**
	 * Conecta na Vlan100 (DCN).
	 * @exception pode gerar uma exception ao conectar.
	 */
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

	/**Conecta na Vlan101 via Canal de supervisão.
	 * Regra: 169.254.(127+ID).1 onde o ID é o endereço do NE no barramento.
	 * @param IP é a endereço da subrede na VLAN101. 
	 */
	public void connectVlan101(String IP) {
		write("telnet " + IP);
		readUntil("login: ");
		write("root");
		readUntil("Password: ");
		write("root");
		readUntil('$' + " ");
	}
	
	
	/**Conecta na Vlan102
	 * @param IP é o IP da subrede na Vlan102.
	 */
	public void connectVlan102(String IP){
		write("telnet " + IP);
		readUntil("login: ");
		write("root");
		readUntil("Password: ");
		write("root");
		readUntil('$' + " ");
	}
	
	
	/**Executa um comando no terminal linux.
	 * @param command é o comando a ser executado.
	 * @return
	 */
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
					if (sb.toString().endsWith(pattern)){
						return sb.toString();
					}
		
				ch = (char) in.read();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public String toString(){
		return getServer();
	}

}
