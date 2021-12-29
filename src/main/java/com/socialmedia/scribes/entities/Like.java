package com.socialmedia.scribes.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "likes")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Like {
    @Id
    @JsonSerialize(using= ToStringSerializer.class)
    private ObjectId likeId;
    @Indexed
    @Field(value = "user_id")
    @DBRef
    private User user;
    @Indexed
    @Field(value = "post")
    @DBRef
    private Post post;

    public Like(User user, Post post) {
        this.user = user;
        this.post = post;
    }
}
