package ui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

public class PdfFinderTab extends JPanel {
	private JTextField numberField;
	private JComboBox<String> typeBox;
	private JTextField cityField;
	private JButton searchButton, deleteButton, clearButton;

	private JTable resultTable;
	private DefaultTableModel tableModel;

	private File currentFolder;
	private FooterUpdater footerUpdater;

	private final List<File> foundFiles = new ArrayList<>();

	public PdfFinderTab() {
		setLayout(new BorderLayout());

		add(createInputPanel(), BorderLayout.NORTH);
		add(new JScrollPane(createResultTable()), BorderLayout.CENTER);

		currentFolder = new File(System.getProperty("user.home"), "Downloads");
	}

	/** Top panel with inputs and buttons */
	private JPanel createInputPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		numberField = new JTextField(10);
		typeBox = new JComboBox<>(new String[] { "-", "HRA", "HRB", "GnR", "PR", "VR", "GsR" });
		cityField = new JTextField(10);

		searchButton = new JButton("Search");
		searchButton.setContentAreaFilled(true);
		searchButton.setBackground(new Color(46, 139, 87));
		searchButton.setForeground(Color.WHITE);
		deleteButton = new JButton("Delete");
		deleteButton.setContentAreaFilled(true);
		deleteButton.setForeground(Color.WHITE);
		deleteButton.setBackground(Color.RED);
		clearButton = new JButton("Clear");
		clearButton.setForeground(Color.WHITE);
		clearButton.setContentAreaFilled(true);
		clearButton.setBackground(new Color(66, 133, 244));

		panel.add(new JLabel("Number:"));
		panel.add(numberField);
		panel.add(new JLabel("Type:"));
		panel.add(typeBox);
		panel.add(new JLabel("City:"));
		panel.add(cityField);
		panel.add(searchButton);
		panel.add(clearButton);
		panel.add(deleteButton);

		// Button logic
		searchButton.addActionListener(e -> searchFiles());
		clearButton.addActionListener(e -> clearFields());
		deleteButton.addActionListener(e -> deleteSelectedFile());

		// Enter у numberField / cityField starts a search
		Action searchAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				searchFiles();
			}
		};
		numberField.addActionListener(searchAction);
		cityField.addActionListener(searchAction);

		return panel;
	}

	/** Table of results */
	private JTable createResultTable() {
		String[] columnNames = { "File Name", "Creation Date", "Size" };
		tableModel = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		resultTable = new JTable(tableModel);
		resultTable.setRowHeight(22);
		resultTable.setFont(new Font("SansSerif", Font.PLAIN, 13));

		// zebra renderer
		DefaultTableCellRenderer zebraRenderer = new DefaultTableCellRenderer() {
			private final Color evenColor = new Color(245, 245, 245);

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (!isSelected) {
					c.setBackground(row % 2 == 0 ? evenColor : Color.WHITE);
				}
				return c;
			}
		};
		for (int i = 0; i < resultTable.getColumnCount(); i++) {
			resultTable.getColumnModel().getColumn(i).setCellRenderer(zebraRenderer);
		}

		// double-click to open a file
		resultTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && resultTable.getSelectedRow() != -1) {
					openSelectedFile();
				}
			}
		});

		return resultTable;
	}

	/** Get selected file */
	private File getSelectedFile() {
		int row = resultTable.getSelectedRow();
		if (row == -1)
			return null;
		return foundFiles.get(row);
	}

	/** Set directory */
	public void setFolder(File folder) {
		if (folder != null && folder.isDirectory()) {
			currentFolder = folder;
		}
	}

	/** Search for PDF files */
	private void searchFiles() {
		tableModel.setRowCount(0);
		foundFiles.clear();

		if (currentFolder == null) {
			JOptionPane.showMessageDialog(this, "No folder selected!");
			return;
		}

		String number = numberField.getText().trim();
		String type = (String) typeBox.getSelectedItem();
		String city = cityField.getText().trim();

		if (number.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Enter number!");
			return;
		}

		Pattern pattern = buildFilePattern(city, type, number);

		File[] files = currentFolder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
		if (files == null) {
			JOptionPane.showMessageDialog(this, "No files found.");
			return;
		}

		List<File> matches = new ArrayList<>();
		for (File file : files) {
			if (pattern.matcher(file.getName()).matches()) {
				matches.add(file);
			}
		}

		if (matches.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Nothing found.");
			notifyFooter();
		} else {
			for (File f : matches) {
				foundFiles.add(f);

				String cleanName = cleanFileName(f.getName());
				String dateCreated = formatDate(f);
				String size = formatSize(f.length());

				tableModel.addRow(new Object[] { cleanName, dateCreated, size });
			}
		}

		notifyFooter();
	}

	/** Field cleaning */
	private void clearFields() {
		numberField.setText("");
		typeBox.setSelectedIndex(0);
		cityField.setText("");
		tableModel.setRowCount(0);
		foundFiles.clear();
		notifyFooter();
	}

	/** Delete the selected file */
	private void deleteSelectedFile() {
		File selectedFile = getSelectedFile();
		if (selectedFile == null) {
			JOptionPane.showMessageDialog(this, "Select the file to delete.");
			return;
		}

		int confirm = JOptionPane.showConfirmDialog(this, "Delete file:\n" + selectedFile.getName() + " ?",
				"Confirmation", JOptionPane.YES_NO_OPTION);

		if (confirm == JOptionPane.YES_OPTION) {

			int selectedRow = resultTable.getSelectedRow();
			if (selectedFile.delete()) {
				// видаляємо з обох структур
				foundFiles.remove(selectedRow);
				tableModel.removeRow(selectedRow);

				JOptionPane.showMessageDialog(this, "File deleted.");
				notifyFooter();
			} else {
				JOptionPane.showMessageDialog(this, "Failed to delete file.");
			}

		}
	}

	/** Open selected file */
	private void openSelectedFile() {
		File selectedFile = getSelectedFile();
		if (selectedFile != null && selectedFile.exists()) {
			try {
				Desktop.getDesktop().open(selectedFile);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Could not open file: " + ex.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private Pattern buildFilePattern(String city, String type, String number) {
		StringBuilder regex = new StringBuilder(".*");

		if (!city.isEmpty()) {
			regex.append(Pattern.quote(city)).append(".*");
		}
		if (!"-".equals(type)) {
			
			regex.append("_").append(Pattern.quote(type));
		}

		regex.append("_").append(Pattern.quote(number)).append("\\+.*\\.pdf");

		return Pattern.compile(regex.toString(), Pattern.CASE_INSENSITIVE);
	}

	private String cleanFileName(String fileName) {
		return fileName.replaceFirst("^.*?-(.*?)?-?\\d{14}(?=\\.pdf)", "$1");
	}

	private String formatDate(File f) {
		try {
			BasicFileAttributes attr = Files.readAttributes(f.toPath(), BasicFileAttributes.class);
			return new SimpleDateFormat("yyyy-MM-dd").format(new Date(attr.creationTime().toMillis()));
		} catch (IOException ex) {
			return "N/A";
		}
	}

	private String formatSize(long bytes) {
		if (bytes < 1024)
			return bytes + " B";
		double kb = bytes / 1024.0;
		if (kb < 1024)
			return new DecimalFormat("#.##").format(kb) + " KB";
		double mb = kb / 1024.0;
		return new DecimalFormat("#.##").format(mb) + " MB";
	}

	/** Footer */
	public void setFooterUpdater(FooterUpdater footerUpdater) {
		this.footerUpdater = footerUpdater;
	}

	private void notifyFooter() {
		if (footerUpdater != null) {
			footerUpdater.updateFooter();
		}
	}

	public int getFoundFilesCount() {
		return foundFiles.size();
	}
}
