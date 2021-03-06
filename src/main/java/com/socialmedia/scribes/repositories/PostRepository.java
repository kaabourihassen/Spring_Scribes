package com.socialmedia.scribes.repositories;

import com.socialmedia.scribes.entities.Post;
import com.socialmedia.scribes.entities.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface PostRepository extends MongoRepository<Post, ObjectId> {
    Optional<Post> findById(ObjectId postId);
    List<Post> findPostsByUser(User user);
}
