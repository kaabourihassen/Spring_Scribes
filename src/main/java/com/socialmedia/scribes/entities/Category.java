package com.socialmedia.scribes.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashSet;
import java.util.Set;

@Document(collection = "categories")
@Data
@AllArgsConstructor@NoArgsConstructor@Getter@Setter
public class Category {
    @Id
    @JsonSerialize(using= ToStringSerializer.class)
    private ObjectId categoryId;
    @Field(value = "categoryName")
    @Indexed
    private String CategoryName;


    public Category(String categoryName) {
        CategoryName = categoryName;
    }
}
