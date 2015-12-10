package view;

import java.awt.Button;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
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
import controller.DiscoveryNetwork;
import controller.FileChooser;
import controller.GenerateMD5;
import controller.StoreUpgradeToColetor;
import controller.UpdateSupervisor;

@SuppressWarnings("serial")
public class Principal extends JFrame {
	private JPanel contentPane;
	public static TextArea textAreaConsole;
	private static Label lbColetorUm;
	private static TextField txfColetorDois;
	private static Label lbColetorDois;
	private static TextField txfColetorUm;
	public static Label lbTypeColetor;
	private static JTable table;
	private static DefaultTableModel tabela;
	private Button btnDescobri;
	private Button btnEviarScript;
	private Button btnSelecionarScript;
	private Button btnCarregarScript;
	private Button btnAtualizar;
	private static JProgressBar progressBar;
	private static File fileUpgrade;
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

	public static File getFileUpgrade() {
		return fileUpgrade;
	}

	public static void setFileUpgrade(File fileUpgrade) {
		Principal.fileUpgrade = fileUpgrade;
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
		// configBtn(1);

	}

	private void initComponents() {

		setResizable(false);
		setTitle("Padtec");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 518, 650);
		contentPane = new JPanel();
		contentPane.setBorder(null);
		setContentPane(contentPane);
		contentPane.setLayout(null);

		lbColetorUm = new Label("Coletor 1");
		lbColetorUm.setBounds(21, 10, 65, 21);
		contentPane.add(lbColetorUm);

		lbColetorDois = new Label("Coletor 2");
		lbColetorDois.setBounds(21, 39, 65, 21);
		contentPane.add(lbColetorDois);

		txfColetorDois = new TextField();
		txfColetorDois.setBounds(95, 40, 133, 21);
		contentPane.add(txfColetorDois);

		txfColetorUm = new TextField();
		txfColetorUm.setBounds(95, 10, 133, 21);
		contentPane.add(txfColetorUm);
		txfColetorUm.setText("172.30.0.235");

		btnDescobri = new Button("Descobrir");
		btnDescobri.setBounds(300, 10, 86, 23);
		contentPane.add(btnDescobri);

		btnEviarScript = new Button("Enviar Script");
		btnEviarScript.setBounds(300, 39, 86, 23);
		contentPane.add(btnEviarScript);

		btnAtualizar = new Button("Atualizar");
		btnAtualizar.setBounds(300, 68, 86, 23);
		contentPane.add(btnAtualizar);

		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setFont(new Font("Arial Black", Font.BOLD, 14));
		progressBar.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		progressBar.setBounds(300, 108, 191, 41);
		contentPane.add(progressBar);
		progressBar.setForeground(Color.GRAY);

		btnSelecionarScript = new Button("Selecionar Script");
		btnSelecionarScript.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				selectFile();
			}
		});
		btnSelecionarScript.setBounds(21, 97, 120, 23);
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
		btnCarregarScript.setBounds(21, 126, 120, 23);
		contentPane.add(btnCarregarScript);

		lbTypeColetor = new Label("Tipo de Coletor :");
		lbTypeColetor.setBounds(21, 64, 273, 21);
		contentPane.add(lbTypeColetor);
		lbTypeColetor.setEnabled(false);

		textAreaConsole = new TextArea();
		textAreaConsole.setBounds(21, 410, 470, 202);
		contentPane.add(textAreaConsole);

		Panel panel = new Panel();
		panel.setBounds(21, 175, 470, 229);
		contentPane.add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.addMouseListener(new MouseAdapter() {

		});
		panel.add(scrollPane);

		table = new JTable();
		table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				int i = table.getSelectedRow();
				int j = table.getSelectedColumn();

				if (table.getValueAt(i, j).equals("ENVIAR[X]") && j == 5) {
					table.setValueAt("ENVIAR[ ]", i, j);
				} else if (table.getValueAt(i, j).equals("ENVIAR[ ]") && j == 5) {
					table.setValueAt("ENVIAR[X]", i, j);
				}

			}
		});
		scrollPane.setViewportView(table);
		table.setModel(new DefaultTableModel(new Object[][] {}, new String[] {
				"Site", "S/N", "Mestre/Scravo", "Vers�o Atual", "Status",
				"Check" }));
		table.getColumnModel().getColumn(0).setPreferredWidth(3);
		table.getColumnModel().getColumn(1).setPreferredWidth(10);  
		table.getColumnModel().getColumn(2).setPreferredWidth(55);  
		table.getColumnModel().getColumn(3).setPreferredWidth(55);  
		table.getColumnModel().getColumn(4).setPreferredWidth(110);  
		table.getColumnModel().getColumn(5).setPreferredWidth(60);  
		tabela = (DefaultTableModel) table.getModel();

		btnAtualizar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			
				UpdateSupervisor atualizar = new UpdateSupervisor();
				atualizar.update();
				
				fim = System.currentTimeMillis();
				textAreaConsole.setText((fim - inicio) / 1000d + "");
			}
		});
		btnEviarScript.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnDescobri.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				inicio = System.currentTimeMillis();

				DiscoveryNetwork t = new DiscoveryNetwork();

				t.network();
				
				
			}
		});

	}
	
	
	public void btnCarregarScriptRun(){
		StoreUpgradeToColetor.store();
		
		if(StoreUpgradeToColetor.isSucess()){
			
		}else{
		
		}
	}

	public void monitoringIP() {
		ValidaIP coletorUm;
		ValidaIP coletorDois;
		coletorUm = new ValidaIP();
		coletorUm.monitoringIP(1);
		coletorDois = new ValidaIP();
		coletorDois.monitoringIP(2);
	}

	public void configBtn(int config) {
		switch (config) {
		case 1:
			configOne();
			break;

		default:
			break;
		}
	}

	public void configOne() {
		getBtnCarregarScript().setEnabled(false);
		getBtnSelecionarScript().setEnabled(false);
		getBtnEviarScript().setEnabled(false);
		getBtnAtualizar().setEnabled(false);
	}

	public Button getBtnDescobri() {
		return btnDescobri;
	}

	public Button getBtnEviarScript() {
		return btnEviarScript;
	}

	public Button getBtnSelecionarScript() {
		return btnSelecionarScript;
	}

	public Button getBtnCarregarScript() {
		return btnCarregarScript;
	}

	public Button getBtnAtualizar() {
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
			setFileUpgrade(arquivo);

			try {
				setMd5(GenerateMD5.getMD5Checksum(getFileUpgrade().getAbsolutePath()));
				textAreaConsole.setText("MD5 = " + md5);
			} catch (Exception e) {
				e.printStackTrace();
				textAreaConsole.setText("O arquivo selecionado n�o � valido!");
				return null;
			}
			return arquivo;
		} else {
			textAreaConsole.setText("O arquivo selecionado n�o � valido!");
			return null;
		}
	}
}
