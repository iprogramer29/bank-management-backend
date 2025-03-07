package com.bank.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import com.bank.entity.Account;
import com.bank.entity.Transaction;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;

import jakarta.transaction.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Date;

@Service
public class AccountService {

	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private TransactionRepository transactionRepository;

	// new account
	public Account createAccount(Account account) {
		account.setAccountNumber(generateAccountNumber()); // Set 10-digit account number
		return accountRepository.save(account);
	}

	// generate the 10 digit account number
	private String generateAccountNumber() {
		SecureRandom secureRandom = new SecureRandom();
		long number = 1000000000L + (long) (secureRandom.nextDouble() * 9000000000L);
		return String.valueOf(number);
	}

	// get account by account number
	public Account getAccountByNumber(String accountNumber) {
		return accountRepository.findByAccountNumber(accountNumber);
	}

	// deposit amount
	@Transactional
	public Account deposit(String accountNumber, double amount) {
		Account account = accountRepository.findByAccountNumber(accountNumber);
		if (account == null) {
			throw new RuntimeException("Account not found");
		}

		// Update balance
		account.setBalance(account.getBalance() + amount);
		accountRepository.save(account); // ✅ Save the updated balance

		// Save transaction record
		Transaction transaction = new Transaction();
		transaction.setAccountNumber(accountNumber);
		transaction.setAmount(amount);
		transaction.setType("Deposit");
		transaction.setTransactionDate(LocalDateTime.now());

		transactionRepository.save(transaction); // ✅ Save transaction record

		return accountRepository.save(account);
	}

	// Withdraw amount
	@Transactional
	public Account withdraw(String accountNumber, double amount) {
		Account account = accountRepository.findByAccountNumber(accountNumber);

		if (account == null) {
			throw new RuntimeException("Account not found");
		}

		if (account.getBalance() < amount) {
			throw new RuntimeException("Insufficient balance");
		}

		// Deduct amount
		account.setBalance(account.getBalance() - amount);

		// Save withdrawal transaction
		Transaction transaction = new Transaction();
		transaction.setAccountNumber(accountNumber);
		transaction.setAmount(amount);
		transaction.setType("Withdraw");
		transaction.setTransactionDate(LocalDateTime.now());

		transactionRepository.save(transaction);

		return accountRepository.save(account);
	}

	// transfer money

	@Transactional // Ensures rollback in case of failure
	public void transfer(String fromAccountNumber, String toAccountNumber, double amount) {
		// Find sender's account
		Account sender = accountRepository.findByAccountNumber(fromAccountNumber);
		if (sender == null) {
			throw new RuntimeException("Sender account not found");
		}

		// Find receiver's account
		Account receiver = accountRepository.findByAccountNumber(toAccountNumber);
		if (receiver == null) {
			throw new RuntimeException("Receiver account not found");
		}

		// Check balance
		if (sender.getBalance() < amount) {
			throw new RuntimeException("Insufficient balance");
		}

		// Perform transfer
		sender.setBalance(sender.getBalance() - amount);
		receiver.setBalance(receiver.getBalance() + amount);

		// Save updated accounts
		accountRepository.save(sender);
		accountRepository.save(receiver);

		// Log the transaction for sender
		Transaction senderTransaction = new Transaction();
		senderTransaction.setAccountNumber(fromAccountNumber);
		senderTransaction.setType("DEBIT");
		senderTransaction.setAmount(amount);
		senderTransaction.setTransactionDate(LocalDateTime.now());
		transactionRepository.save(senderTransaction);

		// Log the transaction for receiver
		Transaction receiverTransaction = new Transaction();
		receiverTransaction.setAccountNumber(toAccountNumber);
		receiverTransaction.setType("CREDIT");
		receiverTransaction.setAmount(amount);
		receiverTransaction.setTransactionDate(LocalDateTime.now());
		transactionRepository.save(receiverTransaction);
	}

}