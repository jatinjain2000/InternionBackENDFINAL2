package com.internevaluation.formfiller.email;

import javax.mail.MessagingException;

public interface EmailSender {
    void send(String to, String email) throws MessagingException;
}
