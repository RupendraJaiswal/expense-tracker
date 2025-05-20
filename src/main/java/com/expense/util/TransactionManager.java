package com.expense.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.expense.bean.TransactionBean;
import com.expense.database.DBUtil;

public class TransactionManager {

	public void addTransaction(TransactionBean t) {
		String sql = "INSERT INTO transactions (type, category, amount, date) VALUES (?, ?, ?, ?)";

		try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, t.getType());
			stmt.setString(2, t.getCategory());
			stmt.setDouble(3, t.getAmount());
			stmt.setDate(4, Date.valueOf(t.getDate()));

			stmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<TransactionBean> getTransactions(int year, int month) {
		List<TransactionBean> list = new ArrayList<>();
		String sql = "SELECT * FROM transactions WHERE YEAR(date) = ? AND MONTH(date) = ?";

		try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, year);
			stmt.setInt(2, month);

			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				list.add(new TransactionBean(rs.getString("type"), rs.getString("category"), rs.getDouble("amount"),
						rs.getDate("date").toLocalDate()));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}

	public void printMonthlySummary(int year, int month) {
		List<TransactionBean> transactions = getTransactions(year, month);
		double incomeTotal = 0, expenseTotal = 0;

		System.out.println("---- Monthly Summary ----");
		for (TransactionBean t : transactions) {
			if ("income".equalsIgnoreCase(t.getType())) {
				incomeTotal += t.getAmount();
			} else {
				expenseTotal += t.getAmount();
			}
		}

		System.out.println("Income: " + incomeTotal);
		System.out.println("Expense: " + expenseTotal);
		System.out.println("Balance: " + (incomeTotal - expenseTotal));
	}

	public void loadFromExcelFile(String filename) {
		try (FileInputStream fis = new FileInputStream(new File(filename)); Workbook workbook = new XSSFWorkbook(fis)) {

			Sheet sheet = workbook.getSheetAt(0);
			int count = 0;

			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);

				if (row == null)
					continue;

				String type = row.getCell(0).getStringCellValue().trim().toLowerCase();
				String category = row.getCell(1).getStringCellValue().trim();
				double amount = row.getCell(2).getNumericCellValue();
				LocalDate date = row.getCell(3).getLocalDateTimeCellValue().toLocalDate();

				TransactionBean t = new TransactionBean(type, category, amount, date);
				addTransaction(t);
				System.out.println(t);
				count++;
			}

			System.out.println("Loaded " + count + " transaction(s) from Excel file.");

		} catch (Exception e) {
			System.err.println("Error loading Excel file: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public Map<String, Map<String, Double>> getSummaryByMonth(int year, Month month) {
		Map<String, Map<String, Double>> summary = new HashMap<>();

		String query = "SELECT type, category, SUM(amount) as total " + "FROM transactions "
				+ "WHERE YEAR(date) = ? AND MONTH(date) = ? " + "GROUP BY type, category";

		try (Connection conn = DBUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setInt(1, year);
			stmt.setInt(2, month.getValue());

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					String type = rs.getString("type");
					String category = rs.getString("category");
					double total = rs.getDouble("total");

					summary.computeIfAbsent(type, k -> new HashMap<>()).put(category, total);
				}
			}

		} catch (SQLException e) {
			System.err.println("Error fetching monthly summary: " + e.getMessage());
			e.printStackTrace();
		}

		return summary;
	}

	public List<TransactionBean> getAllTransactions() {
		List<TransactionBean> transactions = new ArrayList<>();
		String query = "SELECT type, category, amount, date FROM transactions";

		try (Connection conn = DBUtil.getConnection();
				PreparedStatement ps = conn.prepareStatement(query);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				String type = rs.getString("type");
				String category = rs.getString("category");
				double amount = rs.getDouble("amount");
				LocalDate date = rs.getDate("date").toLocalDate();

				transactions.add(new TransactionBean(type, category, amount, date));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return transactions;
	}

	public void saveToFile(String filename) {
		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet("Transactions");

			// Header row
			Row header = sheet.createRow(0);
			header.createCell(0).setCellValue("Type");
			header.createCell(1).setCellValue("Category");
			header.createCell(2).setCellValue("Amount");
			header.createCell(3).setCellValue("Date");

			List<TransactionBean> transactions = getAllTransactions();

			int rowNum = 1;
			for (TransactionBean t : transactions) {
				Row row = sheet.createRow(rowNum++);
				row.createCell(0).setCellValue(t.getType());
				row.createCell(1).setCellValue(t.getCategory());
				row.createCell(2).setCellValue(t.getAmount());
				row.createCell(3).setCellValue(t.getDate().toString());
				System.out.println(t.toString());
			}

			// Auto-size columns
			for (int i = 0; i < 4; i++) {
				sheet.autoSizeColumn(i);
			}

			try (FileOutputStream fos = new FileOutputStream(filename)) {
				workbook.write(fos);
			}

			System.out.println("Data saved to Excel: " + filename);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
