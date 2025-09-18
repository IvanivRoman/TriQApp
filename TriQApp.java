import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;

import javax.swing.*;
import javax.swing.JPanel;
import java.awt.FlowLayout;

import model.CompanyManager;
import ui.CsvSearchTab;
import ui.HandelsTab;
import ui.PdfFinderTab;

public class TriQApp {

	private JFrame frame;
	private CsvSearchTab csvTab;
	private PdfFinderTab pdfTab;
	private HandelsTab handelTab;
	private CompanyManager companyManager;

	private JPanel footerPanel;
	private JLabel footerLabel;
	private JPanel statusCircle;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(TriQApp::new);
	}

	public TriQApp() {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}

		// By default, we search for CSV in the current folder
		File defaultFile = new File("companies.csv");
		companyManager = new CompanyManager(defaultFile.getAbsolutePath());

		frame = new JFrame("TriQ");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 600);
		frame.setLocationRelativeTo(null);

		// tabs
		JTabbedPane tabs = new JTabbedPane();
		csvTab = new CsvSearchTab(companyManager);
		csvTab.setFooterUpdater(this::updateFooterCsv);
		pdfTab = new PdfFinderTab();
		pdfTab.setFooterUpdater(this::updateFooterPdf);
		handelTab = new HandelsTab();
		tabs.addTab("CSV Search", csvTab);
		tabs.addTab("PDF Finder", pdfTab);
		tabs.addTab("Handelsregister", handelTab);

		frame.add(tabs, BorderLayout.CENTER);

		// footer
		footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		footerPanel.setBackground(new Color(245, 245, 245));
		footerPanel
				.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY),
						BorderFactory.createEmptyBorder(5, 10, 5, 10)));
		statusCircle = new JPanel();
		statusCircle.setPreferredSize(new Dimension(16, 16));
		footerLabel = new JLabel("Status info");
		footerPanel.add(statusCircle);
		footerPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		footerPanel.add(footerLabel);
		frame.add(footerPanel, BorderLayout.SOUTH);

		// ChangeListener to update the footer when switching tabs
		tabs.addChangeListener(e -> {
			int index = tabs.getSelectedIndex();
			switch (index) {
			case 0 -> updateFooterCsv();
			case 1 -> updateFooterPdf();
			case 2 -> updateFooterHandels();
			}
		});

		// Initial footer update
		updateFooterCsv();

		// menu
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");

		JMenuItem openCsvItem = new JMenuItem("Open CSV...");
		openCsvItem.addActionListener(e -> chooseCsvFile());

		JMenuItem chooseFolderItem = new JMenuItem("Choose PDF Folder...");
		chooseFolderItem.addActionListener(e -> {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
				pdfTab.setFolder(chooser.getSelectedFile());
			}
		});

		fileMenu.add(openCsvItem);
		fileMenu.add(chooseFolderItem);
		menuBar.add(fileMenu);
		frame.setJMenuBar(menuBar);

		frame.setVisible(true);
        ImageIcon icon = new ImageIcon(TriQApp.class.getResource("/TriQ.png"));
        frame.setIconImage(icon.getImage());
	}

	private void chooseCsvFile() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Select CSV file");
		int result = fileChooser.showOpenDialog(frame);

		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			companyManager = new CompanyManager(selectedFile.getAbsolutePath());
			csvTab.setCompanyManager(companyManager);
			JOptionPane.showMessageDialog(frame, "Loaded: " + selectedFile.getName());
		}

		updateFooterCsv();
	}

	// Footer for CSV tabs
	private void updateFooterCsv() {
		boolean loaded = !companyManager.getAllCompanies().isEmpty();
		statusCircle.setBackground(loaded ? Color.GREEN : Color.RED);
		int rowCount = companyManager.getAllCompanies().size();
		footerLabel.setText(loaded ? "Rows: " + rowCount : "");
		footerPanel.repaint();
	}

	// Footer for PDF tabs
	private void updateFooterPdf() {
		int count = pdfTab.getFoundFilesCount();
		statusCircle.setBackground(Color.BLUE);
		footerLabel.setText("Find: " + count);
		footerPanel.repaint();
	}

	// Footer for Handelsregister tabs
	private void updateFooterHandels() {
		statusCircle.setBackground(Color.ORANGE);
		footerLabel.setText("Handelsregister active");
		footerPanel.repaint();
	}

}