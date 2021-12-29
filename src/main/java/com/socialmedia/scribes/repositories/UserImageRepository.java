package com.socialmedia.scribes.repositories;

import com.socialmedia.scribes.entities.UserImage;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface UserImageRepository extends MongoRepository<UserImage, ObjectId> {
}
