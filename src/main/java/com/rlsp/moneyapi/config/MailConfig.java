package com.rlsp.moneyapi.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.rlsp.moneyapi.config.property.RlspMoneyApiProperty;

@Configuration
public class MailConfig {

	@Autowired
	private RlspMoneyApiProperty property;
	
	@Bean
	public JavaMailSender javaMailSender() {
	
		Properties properties = new Properties();
		properties.put("mail.transport.protocol", "smtp");
		properties.put("mail.smtp.auth", true);
		properties.put("mail.smtp.starttls.enable", true);
		properties.put("mail.smtp.connectiontimeout", 10000); // 10 miliseconds = 10s
		
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		
		mailSender.setJavaMailProperties(properties);
		mailSender.setHost(property.getMail().getHost());		
		mailSender.setPort(property.getMail().getPort());
		mailSender.setUsername(property.getMail().getUsername());
		mailSender.setPassword(property.getMail().getPassword());
		
		return mailSender;
	}
}
