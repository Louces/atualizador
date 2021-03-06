package connection;

import java.io.InputStream;
import java.io.PrintStream;

import org.apache.commons.net.telnet.TelnetClient;

import controller.Console;


/**
 * Esta classa � usada para conexao telnet com o hosts e suas vlans internas.
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
	 * @param � o IP de DCN.
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
			
			
			if(readUntil("login: ").contains("spvl91#")){
				write("root");
				readUntil("Password: ");
				write("5PV1XCL");
				readUntil(prompt + " ");
			}else{
				write("root");
				readUntil("Password: ");
				write("root");
				readUntil(prompt + " ");
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**Conecta na Vlan101 via Canal de supervis�o.
	 * Regra: 169.254.(127+ID).1 onde o ID � o endere�o do NE no barramento.
	 * @param IP � a endere�o da subrede na VLAN101. 
	 */
	public boolean connectVlan101(String IP) {
		write("telnet " + IP);
		readUntil("login: ");
		write("root");
		readUntil("Password: ");
		write("root");
		if(!readUntil('$' + " ").contains("Login incorrect")){
			return true;
		}else{
			readUntil("login: ");
			write("root");
			readUntil("Password: ");
			write("root");
			readUntil("login: ");
			write("root");
			readUntil("Password: ");
			write("root");
			readUntil(prompt + " ");
			return false;
		}
	}
	
	
	/**Conecta na Vlan102
	 * @param IP � o IP da subrede na Vlan102.
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
	 * @param command � o comando a ser executado.
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
	
	public String sendCommand(String command, String readUntil) {
		try {
			write(command);
			return readUntil(readUntil);
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
				//Tratando login em SPVL-4 antigos
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
				}//Tratando login em SPVL-4 antigos
				
				if(ch == 's'){
					ch = (char) in.read();
					System.out.print(ch);
					sb.append(ch);
					
					if(ch == 'p'){
						ch = (char) in.read();
						System.out.print(ch);
						sb.append(ch);
						
						if(ch == 'v'){
							ch = (char) in.read();
							System.out.print(ch);
							sb.append(ch);
							
							if(ch == 'l'){
								ch = (char) in.read();
								System.out.print(ch);
								sb.append(ch);
								
								if(ch == '9'){
									ch = (char) in.read();
									System.out.print(ch);
									sb.append(ch);
									
									if(ch == '1'){
										ch = (char) in.read();
										System.out.print(ch);
										sb.append(ch);
										
										if(ch == '#'){
											if(!pattern.contains("login: ")){
												pattern = '#' + " ";
												System.out.print(ch);
												sb.append(ch);	
											}
											
										}
										
									}
								}
							}
						}
					}
				}
				
		
				
				if(sb.toString().contains(" root]# ")){
					pattern = '#' + " ";
				}
								
				if (ch == lastChar)
					if (sb.toString().endsWith(pattern) || sb.toString().contains("Login incorrect")){
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
