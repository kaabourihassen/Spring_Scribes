package com.socialmedia.scribes.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "comments")
@Data
public class Comment {
    @Id
    @JsonSerialize(using= ToStringSerializer.class)
    private ObjectId commentId;
    @Field(value = "content")
    private String content;

    @DBRef
    @Indexed
    @JsonIgnore
    private  Post post;
    @DBRef
    private User user;
}
