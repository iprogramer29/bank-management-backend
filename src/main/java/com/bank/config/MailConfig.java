package com.bank.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;


@Configuration
public class MailConfig {

	@Bean
	public JavaMailSender getJavaMailSender() {
	    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
	    mailSender.setHost("smtp.gmail.com");
	    mailSender.setPort(587);
	    mailSender.setUsername("prashupatil29@gmail.com");
	    mailSender.setPassword("xmpr zfpa kara bpju");

	    Properties props = mailSender.getJavaMailProperties();
	    props.put("mail.transport.protocol", "smtp");
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.starttls.enable", "true");
	    props.put("mail.smtp.ssl.trust", "*"); // Trusts all certificates
	    props.put("mail.smtp.ssl.checkserveridentity", "false"); // Disables strict checking

	    mailSender.setJavaMailProperties(props);
	    return mailSender;
	}


    
}
