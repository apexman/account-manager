package com.maksimov.accountManager.controller;

import com.maksimov.accountManager.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;

@RestController
@RequestMapping(value = "/test")
public class TestController {
    private Logger logger = LoggerFactory.getLogger(TestController.class);
    private EmailService emailService;

    @Autowired
    public TestController(EmailService emailService) {
        this.emailService = emailService;
    }

    @RequestMapping
    public String sayHello() {
        return "HELLO";
    }

    @RequestMapping(value = "/send")
    public String sendEmail() {
        String result = null;
        try {
            emailService.sendEmail("asdf");
            result = "OK";
        } catch (MessagingException e) {
            logger.error(e.getMessage());
            result = e.getMessage();
        }

        return result;
    }
}
