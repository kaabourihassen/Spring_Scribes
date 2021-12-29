package com.socialmedia.scribes.services;

import com.socialmedia.scribes.entities.ConfirmationToken;
import com.socialmedia.scribes.repositories.ConfirmationTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {
    private final ConfirmationTokenRepository confirmationTokenRepository;

    public void saveConfirmationToken(ConfirmationToken token) {
        confirmationTokenRepository.save(token);
    }

    public Optional<ConfirmationToken> getToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }

    public ConfirmationToken setConfirmedAt(String token) {
        Optional<ConfirmationToken> confirmationToken = confirmationTokenRepository.findByToken(token);
        ConfirmationToken confirmationToken1=confirmationToken.orElseThrow(()-> new UsernameNotFoundException(String.format("token not found")));
        if (confirmationToken!= null){
            confirmationToken1.setConfirmedAt(LocalDateTime.now());
        }
        return confirmationToken1;
    }
}