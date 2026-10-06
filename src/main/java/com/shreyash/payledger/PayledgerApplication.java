package com.shreyash.payledger;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PayledgerApplication {
	
	public static void main(String[] args) {
	    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
	    SpringApplication.run(PayledgerApplication.class, args);
	}

	
}
