package it.itsuptoyou.utils;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class MailUtils {

	@Value(value="${spring.mail.host}")
	private String hostname;
	
	@Value(value="${spring.mail.port}")
	private int port;
	
	@Value(value="${spring.mail.username}")
	private String username;
	
	@Value(value="${spring.mail.password}")
	private String password;
	
	@Value(value="${spring.mail.protocol}")
	private String protocol;
	
	@Bean
	public JavaMailSender getJavaMailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(hostname);
		mailSender.setUsername(username);
		mailSender.setPassword(password);
		mailSender.setPort(port);
		
		Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", protocol);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.debug", "true");
		
		return mailSender;
	}
	
	public void sendMessage(String toEmail, String subject,String message) throws MessagingException {
		JavaMailSender mailSender = getJavaMailSender();
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
		String msg = message;
		helper.setFrom(username);
		helper.setTo(toEmail);
		helper.setSubject(subject);
		helper.setText(msg, true);
		try {
			mailSender.send(mimeMessage);
		}catch(Exception e) {
			System.out.println("Errore: " + e);
		}
	}
	
	public void sendMessageToSupport(String fromEmail, String subject, String message) throws MessagingException {
		JavaMailSender mailSender = getJavaMailSender();
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
		String msg ="From user <h3> " + fromEmail + "</h3> " + message;
		helper.setFrom(username);
		helper.setTo(username);
		helper.setSubject(subject);
		helper.setText(msg, true);
		try {
			mailSender.send(mimeMessage);
		}catch(Exception e) {
			System.out.println("Errore: " + e);
		}
	}
}
