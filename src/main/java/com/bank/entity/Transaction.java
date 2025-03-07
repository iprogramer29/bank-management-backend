package com.bank.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
public class Transaction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String accountNumber;
	private String type; // "Deposit", "Withdraw", "Transfer"
	private double amount;
	private LocalDateTime transactionDate;

	public Transaction() {

	}

	public Transaction(String accountNumber, String type, double amount, LocalDateTime date) {
		this.accountNumber = accountNumber;
		this.type = type;
		this.amount = amount;
		this.transactionDate = date;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public LocalDateTime getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(LocalDateTime localDateTime) {
		this.transactionDate = localDateTime;
	}

}