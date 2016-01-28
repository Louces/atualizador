package view;

import java.awt.Button;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import connection.ValidaIP;
import controller.Console;
import controller.DiscoveryNetwork;
import controller.FileChooser;
import controller.GenerateMD5;
import controller.Info;
import controller.SendFile;
import controller.StoreUpgradeToColetor;
import controller.UpdateSupervisor;

@SuppressWarnings("serial")
public class Principal extends JFrame {
	private JPanel contentPane;
	private static TextArea textAreaConsole;
	private static Label lbColetorUm;
	private static TextField txfColetorDois;
	private static Label lbColetorDois;
	private static TextField txfColetorUm;
	public static Label lbTypeColetor;
	private static JTable table;
	private static DefaultTableModel tabela;
	private static Button btnDescobrir;
	private static Button btnEviarScript;
	private static Button btnSelecionarScript;
	private static Button btnCarregarScript;
	private static Button btnAtualizar;
	private static JProgressBar progressBar;
	private static String md5;
	
	long inicio,fim;
	
	public static Label getLbTypeColetor() {
		return lbTypeColetor;
	}

	public static void setLbTypeColetor(Label lbTypeColetor) {
		Principal.lbTypeColetor = lbTypeColetor;
	}

	public static DefaultTableModel getTabela() {
		return tabela;
	}

	public static void setTabela(DefaultTableModel tabela) {
		Principal.tabela = tabela;
	}
	
	public static String getMd5() {
		return md5;
	}

	public static void setMd5(String md5) {
		Principal.md5 = md5;
	}

	public static void recordTable(String[] row) {
		tabela.addRow(row);
	}

	public static void eraseTable() {
		if (tabela.getRowCount() > 0){
			for (int i = tabela.getRowCount() - 1; i > -1; i--)
				tabela.removeRow(i);
		}
	}

	public static Label getLbColetorUm() {
		return lbColetorUm;
	}

	public static TextField getTxfColetorDois() {
		return txfColetorDois;
	}

	public static Label getLbColetorDois() {
		return lbColetorDois;
	}

	public static TextField getTxfColetorUm() {
		return txfColetorUm;
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Principal frame = new Principal();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Principal() {
		initComponents();
		monitoringIP();
	}

	private void initComponents() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(Principal.class.getResource("/padtec_icone.png")));
		
		setResizable(false);
		setTitle("Padtec S/A - V1.7.1 RC5");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 527, 578);
		contentPane = new JPanel();
		contentPane.setBorder(null);
		setContentPane(contentPane);
		contentPane.setLayout(null);

		lbColetorUm = new Label("Coletor 1");
		lbColetorUm.setBounds(131, 10, 57, 22);
		contentPane.add(lbColetorUm);

		lbColetorDois = new Label("Coletor 2");
		lbColetorDois.setBounds(305, 10, 57, 22);
		contentPane.add(lbColetorDois);

		txfColetorDois = new TextField();
		txfColetorDois.setBounds(373, 10, 105, 22);
		contentPane.add(txfColetorDois);

		txfColetorUm = new TextField();
		txfColetorUm.setBounds(194, 10, 105, 22);
		contentPane.add(txfColetorUm);
		//txfColetorUm.setText("172.30.0.236");

		btnDescobrir = new Button("Descobrir");
		btnDescobrir.setBounds(10, 10, 105, 23);
		contentPane.add(btnDescobrir);

		btnEviarScript = new Button("Enviar Script");
		btnEviarScript.setBounds(10, 97, 105, 23);
		contentPane.add(btnEviarScript);

		btnAtualizar = new Button("Atualizar");
		btnAtualizar.setBounds(10, 126, 105, 23);
		contentPane.add(btnAtualizar);

		progressBar = new JProgressBar();
		progressBar.setEnabled(true);
		progressBar.setStringPainted(true);
		progressBar.setFont(new Font("Arial Black", Font.BOLD, 14));
		progressBar.setBorder(new LineBorder(new Color(0, 0, 0)));
		progressBar.setBounds(131, 68, 122, 52);
		contentPane.add(progressBar);
		progressBar.setForeground(Color.GRAY);
		progressBar.setVisible(false);

		btnSelecionarScript = new Button("Selecionar Script");
		btnSelecionarScript.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				selectFile();
			}
		});
		btnSelecionarScript.setBounds(10, 39, 105, 23);
		contentPane.add(btnSelecionarScript);

		btnCarregarScript = new Button("Carregar Script");
		btnCarregarScript.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				 new Thread(new Runnable() {
			            @Override
			            public void run() {
			            btnCarregarScriptRun();
			            }
			        }).start();
			}
		});
		btnCarregarScript.setBounds(10, 68, 105, 23);
		contentPane.add(btnCarregarScript);

		lbTypeColetor = new Label("Tipo de Coletor : ");
		lbTypeColetor.setBounds(131, 38, 331, 21);
		contentPane.add(lbTypeColetor);

		textAreaConsole = new TextArea();
		textAreaConsole.setBounds(10, 373, 501, 165);
		contentPane.add(textAreaConsole);

		Panel panel = new Panel();
		panel.setFocusTraversalKeysEnabled(false);
		panel.setFocusable(false);
		panel.setBounds(10, 155, 501, 212);
		contentPane.add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.addMouseListener(new MouseAdapter() {

		});
		panel.add(scrollPane);

		table = new JTable();
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				int i = table.getSelectedRow();
				int j = table.getSelectedColumn();

				if (table.getValueAt(i, j).equals("ENVIAR[X]") && j == 5) {
					table.setValueAt("ENVIAR[ ]", i, j);
				} else if (table.getValueAt(i, j).equals("ENVIAR[ ]") && j == 5) {
					table.setValueAt("ENVIAR[X]", i, j);
				}else if(table.getValueAt(i, j).equals("ATUALIZAR[X]") && j == 5){
					table.setValueAt("ATUALIZAR[ ]", i, j);
				}else if(table.getValueAt(i, j).equals("ATUALIZAR[ ]") && j == 5){
					table.setValueAt("ATUALIZAR[X]", i, j);
				}

			}
		});
		scrollPane.setViewportView(table);
		table.setModel(new DefaultTableModel(new Object[][] {}, new String[] {
				"Site", "S/N", "M/E", "Versão", "Status",
				"Check" }));
		table.getColumnModel().getColumn(0).setPreferredWidth(50);
		table.getColumnModel().getColumn(1).setPreferredWidth(10);  
		table.getColumnModel().getColumn(2).setPreferredWidth(40);  
		table.getColumnModel().getColumn(3).setPreferredWidth(50);  
		table.getColumnModel().getColumn(4).setPreferredWidth(110);  
		table.getColumnModel().getColumn(5).setPreferredWidth(60);  
		tabela = (DefaultTableModel) table.getModel();

		btnAtualizar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
		            @Override
		            public void run() {
		            btnAtualizar();
		            }
		        }).start();
			}
		});
		btnEviarScript.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				 new Thread(new Runnable() {
			            @Override
			            public void run() {
			            btnEnviar();
			            }
			        }).start();
			}
		});
		btnDescobrir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				 new Thread(new Runnable() {
			            @Override
			            public void run() {
			            btnDescobrir();
			            }
			        }).start();
			}
		});
	setDisableAll();
	setEnableBtn(1);
	}
	
	public void btnAtualizar(){
		getTextAreaConsole().setText("");
		UpdateSupervisor atualizar = new UpdateSupervisor();
		atualizar.update();
	}
	
	public void btnDescobrir(){
		getTextAreaConsole().setText("");
		setDisable(1);
		DiscoveryNetwork descobrir = new DiscoveryNetwork();
		descobrir.network();
	}

	public void btnCarregarScriptRun(){
		getTextAreaConsole().setText("");
		StoreUpgradeToColetor.store();
		
		if(StoreUpgradeToColetor.isSucess()){
			
		}else{
		
		}
	}
	
	public void btnEnviar(){
		getTextAreaConsole().setText("");
		
		SendFile.sendMaster();
	}

	public void monitoringIP() {
		ValidaIP coletorUm;
		ValidaIP coletorDois;
		coletorUm = new ValidaIP();
		coletorUm.monitoringIP(1);
		coletorDois = new ValidaIP();
		coletorDois.monitoringIP(2);
	}

	public static void configBtn(int config, boolean opcao) {
		if(opcao){
			setEnableBtn(config);
		}else{
			setDisable(config);
		}
	}

	public static void setDisableAll() {
		getBtnDescobri().setEnabled(false);
		getBtnCarregarScript().setEnabled(false);
		getBtnSelecionarScript().setEnabled(false);
		getBtnEviarScript().setEnabled(false);
		getBtnAtualizar().setEnabled(false);
	}
	
	public static void  setEnableBtn(int config){
		switch (config) {
		case 1:
			getBtnDescobri().setEnabled(true);
			break;
		case 2:
			getBtnSelecionarScript().setEnabled(true);
			break;	
		case 3:
			getBtnCarregarScript().setEnabled(true);
			break;
		case 4:
			getBtnEviarScript().setEnabled(true);
			break;
		case 5:
			getBtnAtualizar().setEnabled(true);
			break;	
		default:
		break;
		}
	}
	
	public static void setDisable(int config){
		switch (config) {
		case 1:
			getBtnDescobri().setEnabled(false);
			break;
		case 2:
			getBtnSelecionarScript().setEnabled(false);
			break;	
		case 3:
			getBtnCarregarScript().setEnabled(false);
			break;
		case 4:
			getBtnEviarScript().setEnabled(false);
			break;
		case 5:
			getBtnAtualizar().setEnabled(false);
			break;	
		default:
		break;
		}
	}

	public static Button getBtnDescobri() {
		return btnDescobrir;
	}

	public static Button getBtnEviarScript() {
		return btnEviarScript;
	}

	public static Button getBtnSelecionarScript() {
		return btnSelecionarScript;
	}

	public static Button getBtnCarregarScript() {
		return btnCarregarScript;
	}

	public static Button getBtnAtualizar() {
		return btnAtualizar;
	}

	public static JProgressBar getProgressBar() {
		return progressBar;
	}
	
	public File selectFile() {
		textAreaConsole.setText("");
		FileChooser fileChooser = new FileChooser(null);
		fileChooser.show("Select file (.run)", false);

		File arquivo = fileChooser.getSelectedFile();
		
		if (!(arquivo == null) && arquivo.exists() && arquivo.getName().contains(".run")) {
			Info.setFileUpgrade(arquivo);
			configBtn(3, true);

			try {
				setMd5(GenerateMD5.getMD5Checksum(Info.getFileUpgrade().getAbsolutePath()));
			} catch (Exception e) {
				e.printStackTrace();
				Console.print("O arquivo selecionado não é valido!");
				return null;
			}
			return arquivo;
		} else {
			Console.print("O arquivo selecionado não é valido!");
			configBtn(3, false);
			return null;
		}
	}
	public static TextArea getTextAreaConsole() {
		return textAreaConsole;
	}
	
	public static void configColetores(int coletores){
		switch (coletores) {
		case 1:
			lbColetorDois.setVisible(false);
			txfColetorDois.setVisible(false);
			break;
		case 2:
			lbColetorUm.setVisible(false);
			txfColetorUm.setVisible(false);
			break;
		case 3:
			lbColetorUm.setVisible(true);
			txfColetorUm.setVisible(true);
			lbColetorDois.setVisible(true);
			txfColetorDois.setVisible(true);
			break;
			

		default:
			break;
		}
	}
}
