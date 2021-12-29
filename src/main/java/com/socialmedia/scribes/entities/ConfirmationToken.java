package com.socialmedia.scribes.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "confirmationtokens")
@Getter
@Setter
@NoArgsConstructor
public class ConfirmationToken {


    @Id
    @JsonSerialize(using= ToStringSerializer.class)
    private ObjectId id;
    @Indexed
    @Field(value = "token")
    private String token;
    @Field(value = "createdAt")
    private LocalDateTime createdAt;
    @Field(value = "expiresAt")
    private LocalDateTime expiresAt;
    @Field(value = "confirmedAt")
    private LocalDateTime confirmedAt;
    @DBRef
    @Indexed
    private User user;
    public ConfirmationToken(String token, LocalDateTime createdAt, LocalDateTime expiresAt , User user) {
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.user = user;
    }

}