package com.socialmedia.scribes.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Document(collection = "userimages")
@Data@AllArgsConstructor@NoArgsConstructor@Getter@Setter
public class UserImage implements Serializable {

    @Id
    @JsonSerialize(using= ToStringSerializer.class)
    private ObjectId imageId;
    @Field(value = "title")
    private String title;
    @Field(value = "imageArray")
    @JsonIgnore
    private byte[] file;

    @DBRef
    @Indexed
    private User user;

    @DBRef
    private User usercover;

}