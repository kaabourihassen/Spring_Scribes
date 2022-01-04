package com.socialmedia.scribes.services;

import com.mongodb.lang.Nullable;
import com.socialmedia.scribes.entities.*;
import com.socialmedia.scribes.repositories.CommentRepository;
import com.socialmedia.scribes.repositories.LikeRepository;
import com.socialmedia.scribes.repositories.PostRepository;
import com.socialmedia.scribes.repositories.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Service
public class PostService {
    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    LikeRepository likeRepository;
    @Autowired
    private UserRepository userRepository;
    private static  String UPLOAD_DIR =".\\src\\main\\resources\\static\\imagesuploads\\postImages\\";
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getPostById(ObjectId postId) {
        Optional<Post> postExists;
        postExists = postRepository.findById(postId);
        Post post = postExists.orElseThrow(()-> new UsernameNotFoundException(String.format("Post not found")));
        return post;
    }
    private String randomName(){
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";

        StringBuilder sb = new StringBuilder(10);

        for (int i = 0; i < 10; i++) {
            int index = (int)(AlphaNumericString.length() * Math.random());

            sb.append(AlphaNumericString
                    .charAt(index));
        }
        return sb.toString();
    }

    public Post createPost(User user, Post post,@Nullable MultipartFile multipartFile) {
        if (multipartFile == null){
            Post post1 =new Post();
            post1.setTitle(post.getTitle());
            post1.setContent(post.getContent());
            post1.setCategories(post.getCategories());
            post1.setCreateDate(LocalDateTime.now());
            post1.setUser(user);
            postRepository.save(post1);
            return postRepository.save(post1);
        }else {
            String nameR = randomName();
            try {
                Post post1 =new Post();
                post1.setTitle(post.getTitle());
                post1.setContent(post.getContent());
                post1.setCategories(post.getCategories());
                post1.setCreateDate(LocalDateTime.now());
                post1.setUser(user);
                InputStream inputStream = multipartFile.getInputStream();
                OutputStream outputStream = new FileOutputStream(new File(UPLOAD_DIR + multipartFile.getOriginalFilename()));
                int read = 0;
                byte[] bytes = new byte[1024];

                while((read = inputStream.read(bytes)) != -1 ){
                    outputStream.write(bytes,0,read);
                }
                outputStream.flush();
                outputStream.close();

                post1.setThumbnail(UPLOAD_DIR + multipartFile.getOriginalFilename());



                return postRepository.save(post1);
            }catch (IOException e){
                return null;
            }
        }
    }

    public Post updatePost(User user,Post post,@Nullable MultipartFile multipartFile) {
        if(user.getEmail().equals(post.getUser().getEmail())){
            if (multipartFile == null){
                Post post1 =new Post();
                post1.setPostId(post.getPostId());
                post1.setTitle(post.getTitle());
                post1.setContent(post.getContent());
                post1.setComments(post.getComments());
                post1.setLikes(post.getLikes());
                post1.setCategories(post.getCategories());
                postRepository.save(post1);
                return postRepository.save(post1);
            }else {
                try {
                    String nameR = randomName();
                    Post post1 =new Post();
                    post1.setPostId(post.getPostId());
                    post1.setTitle(post.getTitle());
                    post1.setContent(post.getContent());
                    post1.setCategories(post.getCategories());
                    InputStream inputStream = multipartFile.getInputStream();
                    OutputStream outputStream = new FileOutputStream(new File(UPLOAD_DIR + nameR + multipartFile.getOriginalFilename()));
                    int read = 0;
                    byte[] bytes = new byte[1024];

                    while((read = inputStream.read(bytes)) != -1 ){
                        outputStream.write(bytes,0,read);
                    }
                    outputStream.flush();
                    outputStream.close();



                    post1.setThumbnail(UPLOAD_DIR +nameR+ multipartFile.getOriginalFilename());

                    return postRepository.save(post1);
                }catch (IOException e){
                    return null;
                }
            }
        }else{
            return null;
        }

    }

    public void deletePost(ObjectId postId) {
        postRepository.deleteById(postId);
    }

    public Post createComment(User user,ObjectId postId, Comment comment){
        Post post = postRepository.findById(postId).orElseThrow(()-> new UsernameNotFoundException(String.format("Post not found")));
        Set<Comment> comments = post.getComments();
        comments.add(comment);
        post.setComments(comments);
        comment.setUser(user);
        commentRepository.save(comment);
        return postRepository.save(post);
    }
    public Post createLike(User user,ObjectId postId){
        Post post = postRepository.findById(postId).orElseThrow(()-> new UsernameNotFoundException(String.format("Post not found")));
        Like like = likeRepository.findLikeByUserIdAndPostId(user.getUserId(),postId);
        if (like != null){
            Set<Like> likes = post.getLikes();
            for (Like l :likes){
                if (l.getLikeId().equals(like.getLikeId())){
                    likes.remove(l);
                    likeRepository.deleteLikeByUserIdAndPostId(user.getUserId(),postId);
                    return postRepository.save(post);
                }
            }
        }else {
            Like like1 = new Like(user.getUserId(),postId);
            likeRepository.save(like1);
            Set<Like> likes = post.getLikes();
            likes.add(like1);
            post.setLikes(likes);
            return  postRepository.save(post);
        }

        return  postRepository.save(post);
    }

    public List<Post> getPostsByUser(ObjectId userId) {
        User user= userRepository.findById(userId).orElseThrow(()-> new UsernameNotFoundException(String.format("user not found")));
        return postRepository.findPostsByUser(user);
    }

    public void deleteComment(User user,ObjectId commentId) {
         Comment comment= commentRepository.findById(commentId).orElseThrow(()-> new UsernameNotFoundException(String.format("comment not found")));
        if(user.getUserId().equals(comment.getUser().getUserId())){
            commentRepository.deleteById(commentId);
        }
    }
}
