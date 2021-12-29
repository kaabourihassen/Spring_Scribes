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


@Document(collection = "postimages")
@Data @AllArgsConstructor @NoArgsConstructor @Getter @Setter
@JsonIgnoreProperties({"hibernateLazyInitializer","handler","user"})
public class PostThumbnail {

    @Id
    @JsonSerialize(using= ToStringSerializer.class)
    private ObjectId imageid;
    @Field(value = "content")
    private String title;
    @Field(value = "imageArray")
    @JsonIgnore
    private byte[] file;

    @DBRef
    @Indexed
    @JsonIgnore
    private Post post;

}