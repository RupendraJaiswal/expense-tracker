package com.expense.main;

import com.expense.bean.TransactionBean;
import com.expense.util.TransactionManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.Month;
import java.util.Map;

public class ExpenseTrackerSwingApp extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TransactionManager manager = new TransactionManager();

	private JComboBox<String> typeBox;
	private JComboBox<String> categoryBox;
	private JTextField amountField;
	private JTextField yearField, monthField;
	private JTable summaryTable;
	private JTextField fileLoadField, fileSaveField;

	public ExpenseTrackerSwingApp() {
		setTitle("Expense Tracker");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(700, 500);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());

		JTabbedPane tabs = new JTabbedPane();
		tabs.add("Add Transaction", getAddPanel());
		tabs.add("Monthly Summary", getSummaryPanel());
		tabs.add("Load/Save", getFilePanel());

		add(tabs, BorderLayout.CENTER);
	}

	private JPanel getAddPanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		String[] types = { "income", "expense" };
		typeBox = new JComboBox<>(types);
		typeBox.setPreferredSize(new Dimension(150, 25));
		typeBox.addActionListener(e -> updateCategoryBox());

		categoryBox = new JComboBox<>();
		categoryBox.setPreferredSize(new Dimension(150, 25));
		updateCategoryBox();

		amountField = new JTextField();
		amountField.setPreferredSize(new Dimension(150, 25));
		JButton addBtn = new JButton("Add Transaction");

		addBtn.addActionListener(e -> {
			try {
				String type = (String) typeBox.getSelectedItem();
				String category = (String) categoryBox.getSelectedItem();
				double amount = Double.parseDouble(amountField.getText());
				manager.addTransaction(new TransactionBean(type, category, amount, LocalDate.now()));

				JOptionPane.showMessageDialog(this, "Transaction Added!");

				// Reset the fields
				amountField.setText("");
				categoryBox.setSelectedIndex(0);
				typeBox.setSelectedIndex(0);
				updateCategoryBox();
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Invalid input!");
			}
		});

		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(new JLabel("Type:"), gbc);
		gbc.gridx = 1;
		panel.add(typeBox, gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
		panel.add(new JLabel("Category:"), gbc);
		gbc.gridx = 1;
		panel.add(categoryBox, gbc);
		gbc.gridx = 0;
		gbc.gridy = 2;
		panel.add(new JLabel("Amount:"), gbc);
		gbc.gridx = 1;
		panel.add(amountField, gbc);
		gbc.gridx = 1;
		gbc.gridy = 3;
		panel.add(addBtn, gbc);

		return panel;
	}

	private JPanel getSummaryPanel() {
		JPanel panel = new JPanel(new BorderLayout());
		JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));

		yearField = new JTextField(5);
		monthField = new JTextField(5);
		JButton viewSummaryBtn = new JButton("View Summary");

		summaryTable = new JTable();
		JScrollPane scrollPane = new JScrollPane(summaryTable);

		viewSummaryBtn.addActionListener(e -> {
			try {
				int year = Integer.parseInt(yearField.getText());
				int month = Integer.parseInt(monthField.getText());
				Month m = Month.of(month);

				Map<String, Map<String, Double>> summary = manager.getSummaryByMonth(year, m);
				DefaultTableModel model = new DefaultTableModel(new String[] { "Type", "Category", "Amount" }, 0);

				summary.forEach((type, map) -> {
					map.forEach((cat, amt) -> {
						model.addRow(new Object[] { type, cat, String.format("%.2f", amt) });
					});
				});

				summaryTable.setModel(model);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Invalid year or month!");
			}
		});

		top.add(new JLabel("Year:"));
		top.add(yearField);
		top.add(new JLabel("Month (1-12):"));
		top.add(monthField);
		top.add(viewSummaryBtn);

		panel.add(top, BorderLayout.NORTH);
		panel.add(scrollPane, BorderLayout.CENTER);
		return panel;
	}

	private JPanel getFilePanel() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();

		fileLoadField = new JTextField(15);
		JButton loadBtn = new JButton("Load");
		JButton browseLoadBtn = new JButton("Browse");

		fileSaveField = new JTextField(15);
		JButton saveBtn = new JButton("Save");
		JButton browseSaveBtn = new JButton("Browse");

		// Browse for Load File
		browseLoadBtn.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser();
			if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				fileLoadField.setText(fileChooser.getSelectedFile().getAbsolutePath());
			}
		});

		// Browse for Save File
		browseSaveBtn.addActionListener(e -> {
			JFileChooser fileChooser = new JFileChooser();
			if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				fileSaveField.setText(fileChooser.getSelectedFile().getAbsolutePath());
			}
		});

		// Load Button Action
		loadBtn.addActionListener(e -> {
			manager.loadFromExcelFile(fileLoadField.getText());
			JOptionPane.showMessageDialog(this, "Data Loaded");
		});

		// Save Button Action
		saveBtn.addActionListener(e -> {
			manager.saveToFile(fileSaveField.getText());
			JOptionPane.showMessageDialog(this, "Data Saved");
		});

		gbc.insets = new Insets(10, 10, 10, 10);

		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(new JLabel("Load File:"), gbc);
		gbc.gridx = 1;
		panel.add(fileLoadField, gbc);
		gbc.gridx = 2;
		panel.add(browseLoadBtn, gbc);
		gbc.gridx = 3;
		panel.add(loadBtn, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		panel.add(new JLabel("Save File:"), gbc);
		gbc.gridx = 1;
		panel.add(fileSaveField, gbc);
		gbc.gridx = 2;
		panel.add(browseSaveBtn, gbc);
		gbc.gridx = 3;
		panel.add(saveBtn, gbc);

		return panel;
	}


	private void updateCategoryBox() {
		categoryBox.removeAllItems();
		if ("income".equals(typeBox.getSelectedItem())) {
			categoryBox.addItem("salary");
			categoryBox.addItem("business");
		} else {
			categoryBox.addItem("food");
			categoryBox.addItem("rent");
			categoryBox.addItem("travel");
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new ExpenseTrackerSwingApp().setVisible(true));
	}
}
