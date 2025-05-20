package com.expense.bean;

import java.time.LocalDate;

public class TransactionBean {
	private String type; // income or expense
	private String category;
	private double amount;
	private LocalDate date;

	public TransactionBean() {
		super();
	}

	public TransactionBean(String type, String category, double amount, LocalDate date) {
		this.type = type;
		this.category = category;
		this.amount = amount;
		this.date = date;
	}

	public String getType() {
		return type;
	}

	public String getCategory() {
		return category;
	}

	public double getAmount() {
		return amount;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return type + "," + category + "," + amount + "," + date;
	}
}
