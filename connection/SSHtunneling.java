package connection;

import com.jcraft.jsch.*;

public class SSHtunneling {

	private String host;
	private String user;
	private String password;
	private Session session;
	

	public Session getSession() {
		return session;
	}

	public SSHtunneling(String host, String user, String password) throws JSchException {
		this.host = host;
		this.user = user;
		this.password = password;
		JSch jsch = new JSch();
		session = jsch.getSession(user, host, 22);
		session.setPassword(password);
		localUserInfo lui = new localUserInfo();
		session.setUserInfo(lui);
	}

/*	public boolean go() throws Exception {
		int port = 22;

		
		JSch jsch = new JSch();
		Session session = 
		session.setPassword(password);
		localUserInfo lui = new localUserInfo();
		session.setUserInfo(lui);
		session.connect();
		if(session.isConnected()){
			return true;
		}
		
	return false;
	
	}*/

	class localUserInfo implements UserInfo {
		String passwd;

		public String getPassword() {
			return passwd;
		}

		public boolean promptYesNo(String str) {
			return true;
		}

		public String getPassphrase() {
			return null;
		}

		public boolean promptPassphrase(String message) {
			return true;
		}

		public boolean promptPassword(String message) {
			return true;
		}

		public void showMessage(String message) {
		}
	}

}
