package com.socialmedia.scribes.repositories;

import com.socialmedia.scribes.entities.Like;
import com.socialmedia.scribes.entities.Post;
import com.socialmedia.scribes.entities.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends MongoRepository<Like, ObjectId> {
    Like findLikeByUserAndPost(User user, Post post);
}
