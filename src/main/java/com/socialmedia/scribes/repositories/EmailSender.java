package com.socialmedia.scribes.repositories;

import org.springframework.stereotype.Repository;

@Repository
public interface EmailSender {
    void send(String to, String email);
    void sendForgetPassword(String to, String email);
}