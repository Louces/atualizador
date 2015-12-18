import connection.TelnetConnection;
import supervisor.Supervisor4Master;


public class Teste {

	public static void main(String[] args) {
		
		Supervisor4Master t = new Supervisor4Master();
		
		TelnetConnection con = new TelnetConnection("172.30.0.236");
		con.connectVlan100();
		
		t.telnet0900(con);
		
		t.setSroutersUp(con, 1);
		
		int[] aux = t.getSroutersUp();
		
		for(int i = 0 ; i < 14 ; i++){
			
			System.out.print(aux[i] + " ");
		}
		
		con.closeSession();

	}

}
