package com.lbg.core.props;

import java.util.Properties;

import org.springframework.stereotype.Component;

import com.lbg.core.model.MailRequest;
@Component
public class ConfigureProperties {
	Properties prop = new Properties();

	public  Properties getProp(MailRequest mailRequest) {
		
	//	prop.put("mail.smtp.auth", true);
		prop.put("mail.smtp.starttls.enable", "true");
		prop.put("mail.smtp.host", mailRequest.getHost());
		prop.put("mail.smtp.port", mailRequest.getPort());
	//	prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		return prop;
	}

	public void setProp() {

		
	
	}

}
