package com.wallet_api_clane.services.serviceImpl;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.wallet_api_clane.dtos.MailDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.mail.MessagingException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MailServiceTest {

    @RegisterExtension
    static GreenMailExtension greenmail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("duke", "springboot"))
            .withPerMethodLifecycle(false);

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void shouldSendEmailWithSpecifiedPayload() throws MessagingException {

        MailDto mailDto =  new MailDto("innocentekweogu@gmail.com", "Test Mail", "This is a test Mail");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MailDto> request = new HttpEntity<>(mailDto, headers);

        ResponseEntity<Void> response = this.testRestTemplate.postForEntity("/send-notification", request, Void.class);

        assertEquals(200, response.getStatusCodeValue());

    }
}