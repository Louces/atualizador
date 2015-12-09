import controller.DiscoveryTypeColetor;

public class Teste {

	public static void main(String[] args) {
		
		long inicio = System.currentTimeMillis();  
		
		//TelnetConnection conexao = new TelnetConnection("172.30.0.235");
		
		/*conexao.connectVlan100();
		
		conexao.sendCommand("rm informacoes");
		conexao.sendCommand("touch informacoes");
		conexao.sendCommand("echo 'Tipo coletor' | ifconfig | grep eth0. | wc | awk '{print $1}' >> informacoes");
		conexao.sendCommand("cat /proc/cmdline | awk '{print $1}' >> informacoes");
		conexao.sendCommand("./supervisor -v | awk '{print $2}' >> informacoes");
		//conexao.sendCommand("cat config/srouter_info.conf | grep -m 1 id |awk -F \"=\" '{print $2}' >> informacoes");
*/		
		DiscoveryTypeColetor t = new DiscoveryTypeColetor();
		
		t.discoveryType("172.30.0.162");
		
		long fim  = System.currentTimeMillis();  
		
		System.out.println("\nTempo de execução =  " + ( fim - inicio) );
		


	}

}
