package com.mbpx.baubiblio.email;


import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EmailService implements EmailSender {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);
	
	private final JavaMailSender mailSender;

	@Async
	@Override
	public void send(String to, String subject, String content) {
		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
			helper.setText(content, true);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setFrom("ejemplo@ejemplo.com");
			mailSender.send(mimeMessage);
		}catch (MessagingException e) {
			LOGGER.error("failed to send email", e);
			throw new IllegalStateException("failed to send email");
		}
		
	}

}
