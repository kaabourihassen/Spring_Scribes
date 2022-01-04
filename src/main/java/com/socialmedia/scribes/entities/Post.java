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

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document(collection = "posts")
@Data
public class Post {
    @Id
    @JsonSerialize(using= ToStringSerializer.class)
    private ObjectId postId;
    @Field(value = "title")
    private String title;
    @Field(value = "content")
    private String content;
    @Field(value = "createDate")
    private LocalDateTime createDate;
    @DBRef
    private String thumbnail;
    @DBRef
    private Set<Comment> comments = new HashSet<>();;
    @DBRef
    @Indexed
    private User user;
    @DBRef
    @Indexed
    private Set<Category> categories = new HashSet<>();
    @DBRef
    private Set<Like> likes = new HashSet<>();



}
