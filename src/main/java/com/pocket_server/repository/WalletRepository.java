package com.pocket_server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pocket_server.model.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Integer> {

}
