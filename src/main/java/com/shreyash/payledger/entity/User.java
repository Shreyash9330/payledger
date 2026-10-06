package com.shreyash.payledger.entity;



import com.shreyash.payledger.enums.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
	@Table(name = "users")   // "user" Postgres mein reserved word hai
	@Getter @Setter @NoArgsConstructor
	public class User {
	    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(nullable = false)
	    private String name;

	    @Column(nullable = false, unique = true)
	    private String email;

	    @Column(nullable = false)
	    private String passwordHash;

	    @Enumerated(EnumType.STRING)
	    @Column(nullable = false)
	    private Role role = Role.USER;
	}

