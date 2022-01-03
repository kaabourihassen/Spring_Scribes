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
    @JsonSerialize(using= ToStringSerializer.class)
    private ObjectId userId;
    @Indexed
    @Field(value = "post")
    @JsonSerialize(using= ToStringSerializer.class)
    private ObjectId postId;

    public Like(ObjectId userId, ObjectId postId) {
        this.userId = userId;
        this.postId = postId;
    }
}
