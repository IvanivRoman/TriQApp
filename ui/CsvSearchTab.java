package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Company;
import model.CompanyManager;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.List;

public class CsvSearchTab extends JPanel {
    private CompanyManager manager;
    private JTextField searchField;
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private FooterUpdater footerUpdater;


    public CsvSearchTab(CompanyManager manager) {
        this.manager = manager;
        setLayout(new BorderLayout(5,5));

        // Search panel
        JPanel topPanel = new JPanel(new BorderLayout(5,5));
        searchField = new JTextField();
        JButton searchButton = new JButton("Search");
        topPanel.add(searchField, BorderLayout.CENTER);
        topPanel.add(searchButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Results table
        String[] columnNames = {"Old name", "New name", "Registration", "Tr-ID"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultsTable = new JTable(tableModel);
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(resultsTable), BorderLayout.CENTER);
        
        resultsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && resultsTable.getSelectedRow() != -1) {
                    int row = resultsTable.getSelectedRow();
                    int column = resultsTable.getSelectedColumn();
                    Object value = resultsTable.getValueAt(row, column);
                    if (value != null) {
                        StringSelection selection = new StringSelection(value.toString());
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
                        JOptionPane.showMessageDialog(
                                CsvSearchTab.this,
                                "Copied: " + value,
                                "Information",
                                JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                }
            }
        });


        // buttons
        addButton = new JButton("\u271A");
        addButton.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 16));
        addButton.setForeground(Color.WHITE);
        addButton.setBackground(new Color(66, 133, 244));

        editButton = new JButton("\u270F");
        deleteButton = new JButton("\u274C");
        editButton.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 16));
        deleteButton.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 16));
        editButton.setForeground(Color.WHITE);
        deleteButton.setForeground(Color.WHITE);
        editButton.setBackground(new Color(0, 128, 0));   
        deleteButton.setBackground(Color.RED);
        
        editButton.setToolTipText("Edit");
        deleteButton.setToolTipText("Delete");
        
        editButton.setVisible(false);
        deleteButton.setVisible(false);

        // Dynamic button panel
        JPanel floatingPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        floatingPanel.setOpaque(false);
        floatingPanel.add(editButton);
        floatingPanel.add(deleteButton);
        floatingPanel.add(addButton);
        add(floatingPanel, BorderLayout.SOUTH);

        // Table listener
        resultsTable.getSelectionModel().addListSelectionListener(e -> {
            boolean rowSelected = resultsTable.getSelectedRow() != -1;
            editButton.setVisible(rowSelected);
            deleteButton.setVisible(rowSelected);
        });

        // Event handlers
        searchButton.addActionListener(e -> search());
        searchField.addActionListener(e -> search());
        addButton.addActionListener(e -> addNewCompany());
        editButton.addActionListener(e -> editSelectedCompany());
        deleteButton.addActionListener(e -> deleteSelectedCompany());
    }
    
    public void setFooterUpdater(FooterUpdater footerUpdater) {
        this.footerUpdater = footerUpdater;
    }

    public void setCompanyManager(CompanyManager manager) {
        this.manager = manager;
    }

    private void search() {
        String query = searchField.getText().trim();
        tableModel.setRowCount(0);

        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter the company name to search.");
            return;
        }

        List<Company> found = manager.searchByOldName(query);
        if (found.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Company not found: " + query);
        } else {
            for (Company c : found) {
                tableModel.addRow(new Object[]{c.getOldName(), c.getNewName(), c.getRegister(), c.getTrId()});
            }
        }
    }

    private void editSelectedCompany() {
        int selectedRow = resultsTable.getSelectedRow();
        if (selectedRow == -1) return;

        String oldName = (String) tableModel.getValueAt(selectedRow, 0);
        String newName = (String) tableModel.getValueAt(selectedRow, 1);
        String register = (String) tableModel.getValueAt(selectedRow, 2);
        String trId = (String) tableModel.getValueAt(selectedRow, 3);

        JTextField oldNameField = new JTextField(oldName, 35);
        JTextField newNameField = new JTextField(newName, 35);
        JTextField registerField = new JTextField(register, 35);
        JTextField trIdField = new JTextField(trId, 35);

        JPanel panel = new JPanel(new GridLayout(0,1));
        panel.add(new JLabel("Old name:")); panel.add(oldNameField);
        panel.add(new JLabel("New name:")); panel.add(newNameField);
        panel.add(new JLabel("Registration:")); panel.add(registerField);
        panel.add(new JLabel("Tr-ID:")); panel.add(trIdField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Company edit",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            Company updated = new Company(
                    oldNameField.getText().trim(),
                    newNameField.getText().trim(),
                    registerField.getText().trim(),
                    trIdField.getText().trim()
            );
            manager.editCompany(oldName, updated);
            tableModel.setValueAt(updated.getOldName(), selectedRow, 0);
            tableModel.setValueAt(updated.getNewName(), selectedRow, 1);
            tableModel.setValueAt(updated.getRegister(), selectedRow, 2);
            tableModel.setValueAt(updated.getTrId(), selectedRow, 3);
        }
    }

    private void deleteSelectedCompany() {
        int selectedRow = resultsTable.getSelectedRow();
        if (selectedRow == -1) return;

        String oldName = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete company: " + oldName + "?", "Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            manager.deleteCompany(oldName);
            tableModel.removeRow(selectedRow);
            if (footerUpdater != null) footerUpdater.updateFooter();
        }
    }

    private void addNewCompany() {
        JTextField oldNameField = new JTextField(35);
        JTextField newNameField = new JTextField(35);
        JTextField registerField = new JTextField(35);
        JTextField trIdField = new JTextField(35);

        JPanel panel = new JPanel(new GridLayout(0,1));
        panel.add(new JLabel("Old name:")); panel.add(oldNameField);
        panel.add(new JLabel("New name:")); panel.add(newNameField);
        panel.add(new JLabel("Registration:")); panel.add(registerField);
        panel.add(new JLabel("Tr-ID:")); panel.add(trIdField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add new company",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String oldName = oldNameField.getText().trim();

            // Check for duplicates
            boolean exists = manager.getAllCompanies().stream()
                    .anyMatch(c -> c.getOldName().equalsIgnoreCase(oldName));

            if (exists) {
                JOptionPane.showMessageDialog(this,
                        "A company with such name already exists!",
                        "Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Company newCompany = new Company(
                    oldName,
                    newNameField.getText().trim(),
                    registerField.getText().trim(),
                    trIdField.getText().trim()
            );
            manager.addCompany(newCompany);
            if (footerUpdater != null) footerUpdater.updateFooter();
            search();
        }
    }
}
