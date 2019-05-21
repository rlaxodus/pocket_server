package com.pocket_server.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pocket_server.model.Contract;
import com.pocket_server.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
	List<Transaction> findAllByorigin(String origin);
	List<Transaction> findAllBydestination(String origin);
}
