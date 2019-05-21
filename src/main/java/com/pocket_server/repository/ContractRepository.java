package com.pocket_server.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.pocket_server.model.Contract;

public interface ContractRepository extends JpaRepository<Contract, String>  {
	@Query("SELECT c FROM Contract c WHERE c.user1_id = :user_id")
	List<Contract> findAllByuser1_id(String user_id)
	;
	@Query("SELECT c FROM Contract c WHERE c.user2_id = :user_id")
	List<Contract> findAllByuser2_id(String user_id);
}
