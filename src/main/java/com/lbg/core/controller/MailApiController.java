package com.lbg.core.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.lbg.core.model.MailRequest;
import com.lbg.core.props.ConfigureProperties;
import com.lbg.core.service.MailService;
import com.lbg.core.service.impl.MailServiceImpl;

@RestController
public class MailApiController {
	
	@Autowired
    private MailServiceImpl mailServiceImpl;
	
	@Autowired
    private ConfigureProperties configureProperties;
	
	
	@org.springframework.web.bind.annotation.GetMapping("/")
    public ResponseEntity<String> checkHealth() {
        return ResponseEntity.status(HttpStatus.OK).body("Mail API is up");
    }

	@org.springframework.web.bind.annotation.PostMapping("/mailto")
	 public ResponseEntity<String> mail(@org.springframework.web.bind.annotation.RequestBody MailRequest request) throws IOException {
		
		
	       Boolean  response =	mailServiceImpl.doMail(request,configureProperties.getProp(request));
	      
	       if(response) {
	        return ResponseEntity.status(HttpStatus.OK).body("Mail Sent Succesfully");
	       }
	       else
	    	 return ResponseEntity.status(HttpStatus.OK).body("Mail Not Sent Succesfully");
	        	
	    }
	
}
