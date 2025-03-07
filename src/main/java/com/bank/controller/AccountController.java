package com.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bank.entity.Account;
import com.bank.repository.AccountRepository;
import com.bank.service.AccountService;
import com.bank.service.EmailService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "http://localhost:3000")
public class AccountController {

	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private AccountService accountService;

	@Autowired
	private EmailService emailService;

	@GetMapping
	public List<Account> getAllAccounts() {
		return accountRepository.findAll();
	}

	@PostMapping("/login")
	public ResponseEntity<Map<String, Object>> login(@RequestBody Account loginRequest) {
		Account account = accountRepository.findByAccountNumber(loginRequest.getAccountNumber());

		if (account == null) {
			return new ResponseEntity<>(Map.of("success", false, "message", "Account not found"), HttpStatus.NOT_FOUND);
		}
		if (!account.getPassword().equals(loginRequest.getPassword())) {
			return new ResponseEntity<>(Map.of("success", false, "message", "Invalid password"),
					HttpStatus.UNAUTHORIZED);
		}

		// Send account details on successful login
		Map<String, Object> response = new HashMap<>();
		response.put("success", true);
		response.put("message", "Login successful");
		response.put("accountNumber", account.getAccountNumber());
		response.put("balance", account.getBalance());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping("/{accountNumber}")
	public ResponseEntity<Account> getAccountByNumber(@PathVariable String accountNumber) {
		Account account = accountRepository.findByAccountNumber(accountNumber);
		if (account == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Return 404 if not found
		}
		return new ResponseEntity<>(account, HttpStatus.OK);
	}

	@PostMapping("/signup")
	public ResponseEntity<?> createAccount(@RequestBody Account account) {
		System.out.println("Received Account Data: " + account); // Debugging

		try {
			// Check if account already exists
			if (accountRepository.findByAccountNumber(account.getAccountNumber()) != null) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(Map.of("message", "Account number already exists"));
			}

			if (accountRepository.existsByEmail(account.getEmail())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(Map.of("message", "Email is already registered"));
			}

			// Hash password manually before saving
			account.setPassword(account.getPassword());

			// Save account using service layer
			Account createdAccount = accountService.createAccount(account);

			// ✅ Send Welcome Email
			String subject = "Welcome To Our Trust Bank, From Branch  " + account.getBranchName();
			String emailBody = "<h2>Hi " + account.getAccountHolderName() + ",</h2>"
					+ "<p>Thank you for signing up! Here are your details:</p>" + "<ul>"
					+ "<li><b>Account holder name:</b> " + createdAccount.getAccountHolderName()
					+ "<li><b>Account Number:</b> " + createdAccount.getAccountNumber()
					+ "<li><b>Account password:</b> " + createdAccount.getPassword() + "<li><b>Branch name:</b> "
					+ createdAccount.getBranchName() + "<li><b>Your gender:</b> " + createdAccount.getGender() + "</li>"
					+ "<li><b>Account type :</b> " + createdAccount.getAccountType() + "</li>" + "</li>"
					+ "<li><b>Mobile Number :</b> " + createdAccount.getMobile() + "</li>" + "<li><b>Address:</b> "
					+ createdAccount.getAddress() + "</li>" + "<li><b>Account creation date :</b> "
					+ createdAccount.getAccountCreationDate() + "</li>"

					+ "</ul>" + "<p>We are happy to have you with us! 🚀</p>";

			emailService.sendEmail(createdAccount.getEmail(), subject, emailBody);

			return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message",
					"Account created successfully. Email sent!", "accountNumber", createdAccount.getAccountNumber()));
		} catch (Exception e) {
			System.err.println("Error creating account: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("message", "An error occurred while creating the account"));
		}
	}

	@PostMapping("/{accountNumber}/deposit")
	public ResponseEntity<Account> deposit(@PathVariable String accountNumber,
			@RequestBody Map<String, Double> request) {
		double amount = request.get("amount");
		Account updatedAccount = accountService.deposit(accountNumber, amount);
		return ResponseEntity.ok(updatedAccount);
	}

	@PostMapping("/{accountNumber}/withdraw")
	public ResponseEntity<Account> withdraw(@PathVariable String accountNumber,
			@RequestBody Map<String, Double> request) {
		double amount = request.get("amount");

		Account updatedAccount = accountService.withdraw(accountNumber, amount);
		return ResponseEntity.ok(updatedAccount);
	}

	@PostMapping("/{accountNumber}/transfer")
	public ResponseEntity<String> transfer(@PathVariable String accountNumber,
			@RequestBody Map<String, Object> request) {

		String toAccountNumber = (String) request.get("toAccountNumber");
		double amount = Double.parseDouble(request.get("amount").toString());

		accountService.transfer(accountNumber, toAccountNumber, amount);
		return ResponseEntity.ok("Transfer successful!");
	}

	@PutMapping("/{id}")
	public ResponseEntity<Account> updateUser(@PathVariable Long id, @RequestBody Account updatedUser) {
		return accountRepository.findById(id).map(user -> {
			user.setAccountNumber(updatedUser.getAccountNumber());
			user.setAccountHolderName(updatedUser.getAccountHolderName());
			user.setMobile(updatedUser.getMobile());
			user.setGender(updatedUser.getGender());
			user.setBalance(updatedUser.getBalance());
			user.setEmail(updatedUser.getEmail());
			user.setAddress(updatedUser.getAddress());
			user.setPassword(updatedUser.getPassword());

			Account savedUser = accountRepository.save(user); // Save the updated user
			return ResponseEntity.ok(savedUser);
		}).orElse(ResponseEntity.notFound().build()); // Return 404 if user not found
	}

}