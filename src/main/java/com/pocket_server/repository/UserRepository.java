package com.pocket_server.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pocket_server.model.User;

public interface UserRepository extends JpaRepository<User, String> {
	User getByphoneNumber(String phonenumber);
	Optional<User> findByphoneNumber(String phonenumber);
	boolean existsByphoneNumber(String phoneNumber);
	void saveAndFlush(Optional<User> user);
}
