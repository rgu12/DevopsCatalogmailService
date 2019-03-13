package com.lbg.core.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.stereotype.Service;

import com.lbg.core.model.MailRequest;

@Service
public class MailService {

	

	public boolean doMail(MailRequest request, Properties props) {
		
		boolean	status = false;
		Session session=null;
		try {
			
		 session = Session.getInstance(props);
		
		}catch(Exception e) {
			
			e.printStackTrace();
		}
		
		/*Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(request.getUsername(), request.getPassword());
			}
		});*/

		Message message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress(request.getFromMailId()));
		} catch (MessagingException e) {

			e.printStackTrace();
			return status;
		}
		try {
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(request.getToMailId()));
		} catch (AddressException e1) {

			e1.printStackTrace();
			return status;
		} catch (MessagingException e1) {

			e1.printStackTrace();
			return status;
		}
		try {
			message.setSubject(request.getSubject());
		} catch (MessagingException e) {

			e.printStackTrace();
			return status;
		}

		String msg = request.getMessage();

		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		try {
			mimeBodyPart.setContent(msg, "text/html");
		} catch (MessagingException e) {

			e.printStackTrace();
			return status;
		}

		Multipart multipart = new MimeMultipart();
		try {
			multipart.addBodyPart(mimeBodyPart);
		} catch (MessagingException e) {

			e.printStackTrace();
			return status;
		}

		try {
			message.setContent(multipart);
		} catch (MessagingException e) {

			e.printStackTrace();
			return status;
		}

		try {
			Transport.send(message);
		} catch (MessagingException e) {

			e.printStackTrace();
			return status;
		}
		status = true;
		return status;
	}
}
