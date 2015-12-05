package view;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import java.awt.Color;

import connection.ValidaIP;
import controller.DiscoveryTypeColetor;

import java.awt.Button;
import java.awt.TextField;
import java.awt.Label;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JProgressBar;

import java.awt.TextArea;
import java.awt.Toolkit;
import java.awt.Font;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;

import java.awt.Panel;

import javax.swing.BoxLayout;


public class Principal extends JFrame {

	private JPanel contentPane;
	public static TextArea textAreaConsole;
	
	private static Label lbColetorUm;
	private static TextField txfColetorDois;
	private static Label lbColetorDois;
	private static TextField txfColetorUm;
	public static  Label lbTypeColetor; 
	private JTable table;
	
	private DefaultTableModel tabela; 
	
		
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
	
	private void initComponents(){
		
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
		txfColetorDois.setBounds(95, 40, 133, 19);
		contentPane.add(txfColetorDois);
		
		txfColetorUm = new TextField();
		txfColetorUm.setBounds(95, 10, 133, 19);
		contentPane.add(txfColetorUm);
		txfColetorUm.setText("192.168.0.11");
		
		Button btnDescobri = new Button("Descobrir");
		btnDescobri.setBounds(300, 10, 86, 23);
		contentPane.add(btnDescobri);
		
		Button btnEviarScript = new Button("Enviar Script");
		btnEviarScript.setBounds(300, 39, 86, 23);
		contentPane.add(btnEviarScript);
		
		Button btnAtualizar = new Button("Atualizar");
		btnAtualizar.setBounds(300, 68, 86, 23);
		contentPane.add(btnAtualizar);
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBar.setFont(new Font("Arial Black", Font.BOLD, 13));
		progressBar.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		progressBar.setBounds(300, 108, 191, 41);
		contentPane.add(progressBar);
		progressBar.setForeground(Color.ORANGE);
		
		Button btnSelecionarScript = new Button("Selecionar Script");
		btnSelecionarScript.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnSelecionarScript.setBounds(21, 97, 120, 23);
		contentPane.add(btnSelecionarScript);
		
		Button btnCarregarScript = new Button("Carregar Script");
		btnCarregarScript.setBounds(21, 126, 120, 23);
		contentPane.add(btnCarregarScript);
		
		lbTypeColetor= new Label("Tipo de Coletor :");
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
		panel.add(scrollPane);
		
		table = new JTable();
		scrollPane.setViewportView(table);
		table.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Site", "S/N", "Mestre/Scravo", "Versão Atual", "Status", "Check"
			}
		));
		
		tabela =  (DefaultTableModel) table.getModel();
		
		btnAtualizar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		btnEviarScript.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnDescobri.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			
				long inicio = System.currentTimeMillis();
				
				DiscoveryTypeColetor teste = new DiscoveryTypeColetor();
				teste.validate();
				
				long fim  = System.currentTimeMillis();  
				textAreaConsole.setText((fim-inicio)/1000d+"");
			}
		});
		
	}
	
	public void monitoringIP(){
		ValidaIP coletorUm,coletorDois;
	
		coletorUm= new ValidaIP();
		coletorUm.monitoringIP(1);
		
		coletorDois = new ValidaIP();
		coletorDois.monitoringIP(2);
	}
}
