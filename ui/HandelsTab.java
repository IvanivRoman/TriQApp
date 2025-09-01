package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HandelsTab extends JPanel {

	private JTextField queryField;
	private JRadioButton allWordsBtn;
	private JRadioButton anyWordBtn;
	private JRadioButton exactMatchBtn;
	private JComboBox<String> registerTypeBox;
	private JTextField registerNumField;
	private JComboBox<String> courtBox;

	Map<String, String> courtCodes = new HashMap<>();
	{
		courtCodes.put("Alle", "");
		
	}

	public HandelsTab() {
	    setLayout(new BorderLayout());
	    setBorder(new EmptyBorder(20, 40, 20, 40));

	    // Form panel
	    JPanel formPanel = new JPanel(new GridBagLayout());
	    formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
	    GridBagConstraints gbc = new GridBagConstraints();
	    gbc.insets = new Insets(5, 5, 5, 5); 
	    gbc.fill = GridBagConstraints.NONE;
	    gbc.anchor = GridBagConstraints.WEST;

	    int row = 0;

	    // Search query
	    gbc.gridx = 0;
	    gbc.gridy = row;
	    gbc.weightx = 0; 
	    formPanel.add(new JLabel("Search:"), gbc);

	    gbc.gridx = 1;
	    gbc.weightx = 0; 
	    queryField = new JTextField(25);
	    formPanel.add(queryField, gbc);

	    // Search type
	    row++;
	    gbc.gridx = 0;
	    gbc.gridy = row;
	    gbc.weightx = 0;
	    formPanel.add(new JLabel("Search type:"), gbc);

	    gbc.gridx = 1;
	    gbc.weightx = 1.0;
	    JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
	    allWordsBtn = new JRadioButton("All words");
	    anyWordBtn = new JRadioButton("Any word");
	    exactMatchBtn = new JRadioButton("Exact match", true);
	    ButtonGroup group = new ButtonGroup();
	    group.add(allWordsBtn);
	    group.add(anyWordBtn);
	    group.add(exactMatchBtn);
	    radioPanel.add(allWordsBtn);
	    radioPanel.add(anyWordBtn);
	    radioPanel.add(exactMatchBtn);
	    formPanel.add(radioPanel, gbc);

	    // Registry type
	    row++;
	    gbc.gridx = 0;
	    gbc.gridy = row;
	    gbc.weightx = 0;
	    formPanel.add(new JLabel("Registry type:"), gbc);

	    gbc.gridx = 1;
	    gbc.weightx = 1.0;
	    registerTypeBox = new JComboBox<>(new String[] { "", "HRA", "HRB", "GnR", "PR", "VR", "GsR" });
	    formPanel.add(registerTypeBox, gbc);

	    // Registration number
	    row++;
	    gbc.gridx = 0;
	    gbc.gridy = row;
	    gbc.weightx = 0;
	    formPanel.add(new JLabel("Registry number:"), gbc);

	    gbc.gridx = 1;
	    gbc.weightx = 1.0;
	    registerNumField = new JTextField(25);
	    formPanel.add(registerNumField, gbc);

	    // Court
	    row++;
	    gbc.gridx = 0;
	    gbc.gridy = row;
	    gbc.weightx = 0;
	    formPanel.add(new JLabel("Court:"), gbc);

	    gbc.gridx = 1;
	    gbc.weightx = 1.0;
	    courtBox = new JComboBox<>(new String[] { "", "Aachen", "Altenburg", "Amberg", "Ansbach", "Apolda", "Arnsberg", "Arnstadt", "Arnstadt Zweigstelle Ilmenau", "Aschaffenburg", "Augsburg", "Aurich", "Bad Hersfeld", "Bad Homburg v.d.H.", "Bad Kreuznach", "Bad Oeynhausen", "Bad Salzungen", "Bamberg", "Bayreuth", "Berlin (Charlottenburg)", "Bielefeld", "Bochum", "Bonn", "Braunschweig", "Bremen", "Chemnitz", "Coburg", "Coesfeld", "Cottbus", "Darmstadt", "Deggendorf", "Dortmund", "Dresden", "Duisburg", "Düren", "Düsseldorf", "Eisenach", "Erfurt", "Eschwege", "Essen", "Flensburg", "Frankfurt am Main", "Frankfurt/Oder", "Freiburg", "Friedberg", "Fritzlar", "Fulda", "Fürth", "Gelsenkirchen", "Gera", "Gießen", "Gotha", "Göttingen", "Greiz", "Gütersloh", "Hagen", "Hamburg", "Hamm", "Hanau", "Hannover", "Heilbad Heiligenstadt", "Hildburghausen", "Hildesheim", "Hof", "Homburg", "Ingolstadt", "Iserlohn", "Jena", "Kaiserslautern", "Kassel", "Kempten (Allgäu)", "Kiel", "Kleve", "Koblenz", "Köln", "Königstein", "Korbach", "Krefeld", "Landau", "Landshut", "Langenfeld", "Lebach", "Leipzig", "Lemgo", "Limburg", "Lübeck", "Ludwigshafen a.Rhein (Ludwigshafen)", "Lüneburg", "Mainz", "Mannheim", "Marburg", "Meiningen", "Memmingen", "Merzig", "Mönchengladbach", "Montabaur", "Mühlhausen", "München", "Münster", "Neubrandenburg", "Neunkirchen", "Neuruppin", "Neuss", "Nordhausen", "Nürnberg", "Offenbach am Main", "Oldenburg (Oldenburg)", "Osnabrück", "Ottweiler", "Paderborn", "Passau", "Pinneberg", "Pößneck", "Pößneck Zweigstelle Bad Lobenstein", "Potsdam", "Recklinghausen", "Regensburg", "Rostock", "Rudolstadt", "Saarbrücken", "Saarlouis", "Schweinfurt", "Schwerin", "Siegburg", "Siegen", "Sömmerda", "Sondershausen", "Sonneberg", "Stadthagen", "Stadtroda", "Steinfurt", "Stendal", "St. Ingbert (St Ingbert)", "Stralsund", "Straubing", "Stuttgart", "St. Wendel (St Wendel)", "Suhl", "Tostedt", "Traunstein", "Ulm", "Völklingen", "Walsrode", "Weiden i. d. OPf.", "Weimar", "Wetzlar", "Wiesbaden", "Wittlich", "Wuppertal", "Würzburg", "Zweibrücken" });
	    formPanel.add(courtBox, gbc);

	    // Buttons
	    row++;
	    gbc.gridx = 1;              
	    gbc.gridy = row;
	    gbc.gridwidth = 1;          
	    gbc.fill = GridBagConstraints.NONE;
	    gbc.anchor = GridBagConstraints.WEST;
	    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
	    JButton searchBtn = new JButton("Search");
	    searchBtn.setBackground(new Color(46, 139, 87));
	    searchBtn.setForeground(Color.WHITE);
	    searchBtn.setOpaque(false);
	    searchBtn.setContentAreaFilled(true);
	    searchBtn.setBorderPainted(false);
	    searchBtn.setFocusPainted(false); 
	    JButton clearBtn = new JButton("Clear");
	    clearBtn.setBackground(new Color(220, 20, 60));
	    clearBtn.setForeground(Color.WHITE);
	    buttonPanel.add(clearBtn);
	    buttonPanel.add(searchBtn);
	    formPanel.add(buttonPanel, gbc);
	    
	    // Button action
	    searchBtn.addActionListener(e -> openSearchInBrowser());
	    clearBtn.addActionListener(e -> clearForm());

	    add(formPanel, BorderLayout.NORTH);
	    
	    SwingUtilities.invokeLater(() -> {
	        JRootPane rootPane = SwingUtilities.getRootPane(this);
	        if (rootPane != null) {
	            rootPane.setDefaultButton(searchBtn);
	        }
	    });
	}


	private void openSearchInBrowser() {
		try {
			String baseUrl = "put_correct_line";
			String schlagwoerter = encode(queryField.getText());
			String schlagwortOptionen = allWordsBtn.isSelected() ? "1" : anyWordBtn.isSelected() ? "2" : "3";
			String registerArt = encode((String) registerTypeBox.getSelectedItem());
			String registerNummer = encode(registerNumField.getText());
			String selectedCourt = (String) courtBox.getSelectedItem();
			String registergericht = encode(courtCodes.getOrDefault(selectedCourt, ""));

			String url = baseUrl + "?schlagwoerter=" + schlagwoerter + "&schlagwortOptionen=" + schlagwortOptionen
					+ "&registerArt=" + registerArt + "&registerNummer=" + registerNummer + "&registergericht="
					+ registergericht + "&suchOptionenGeloescht=true";

			Desktop.getDesktop().browse(new URI(url));
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
		}
	}

	private void clearForm() {
		queryField.setText("");
		allWordsBtn.setSelected(false);
		anyWordBtn.setSelected(false);
		exactMatchBtn.setSelected(true);
		registerTypeBox.setSelectedIndex(0);
		registerNumField.setText("");
		courtBox.setSelectedIndex(0);
	}

	private String encode(String value) {
		return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
	}
}
