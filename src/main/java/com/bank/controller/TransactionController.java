package com.bank.controller;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bank.entity.Transaction;
import com.bank.service.TransactionService;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "http://localhost:3000")
public class TransactionController {

	@Autowired
	private TransactionService transactionService;

	@GetMapping("/{accountId}")
	public ResponseEntity<List<Transaction>> getTransactions(@PathVariable String accountId) {
		return ResponseEntity.ok(transactionService.getTransactionsByAccount(accountId));
	}
}
