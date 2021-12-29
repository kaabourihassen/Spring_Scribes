package com.socialmedia.scribes.repositories;

import com.socialmedia.scribes.entities.PostThumbnail;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostThumbnailRepository extends MongoRepository<PostThumbnail, ObjectId> {
}
