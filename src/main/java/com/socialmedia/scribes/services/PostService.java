package com.socialmedia.scribes.services;

import com.socialmedia.scribes.entities.*;
import com.socialmedia.scribes.repositories.CommentRepository;
import com.socialmedia.scribes.repositories.LikeRepository;
import com.socialmedia.scribes.repositories.PostThumbnailRepository;
import com.socialmedia.scribes.repositories.PostRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class PostService {
    @Autowired
    PostRepository postRepository;
    @Autowired
    PostThumbnailRepository postImageRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    LikeRepository likeRepository;
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

    public ResponseEntity createPost(User user, Post post, MultipartFile[] multipartFileLst) {
        if (multipartFileLst == null){
            Post post1 =new Post();
            post1.setTitle(post.getTitle());
            post1.setContent(post.getContent());
            post1.setCategories(post.getCategories());
            post1.setCreateDate(LocalDateTime.now());
            post1.setUser(user);
            postRepository.save(post1);
            return new ResponseEntity(HttpStatus.CREATED);
        }else {
            List<PostThumbnail> photos = new ArrayList<PostThumbnail>();
            try {
                Post post1 =new Post();
                post1.setTitle(post.getTitle());
                post1.setContent(post.getContent());
                post1.setCategories(post.getCategories());
                post1.setCreateDate(LocalDateTime.now());
                post1.setUser(user);
                for(MultipartFile multipartFile : multipartFileLst){

                    InputStream inputStream = multipartFile.getInputStream();
                    OutputStream outputStream = new FileOutputStream(new File(UPLOAD_DIR + multipartFile.getOriginalFilename()));
                    int read = 0;
                    byte[] bytes = new byte[1024];

                    while((read = inputStream.read(bytes)) != -1 ){
                        outputStream.write(bytes,0,read);
                    }
                    outputStream.flush();
                    outputStream.close();

                    PostThumbnail photo = new PostThumbnail();
                    photo.setTitle(multipartFile.getOriginalFilename());
                    photo.setFile(multipartFile.getBytes());
                    postRepository.save(post1);
                    photo.setPost(post1);
                    postImageRepository.save(photo);
                    photos.add(photo);
                }

                post1.setPictures(photos);

                postRepository.save(post1);
                return new ResponseEntity(HttpStatus.CREATED);
            }catch (IOException e){
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
        }
    }
    public ResponseEntity updatePost(User user,Post post, MultipartFile[] multipartFileLst) {
        if(user.getEmail().equals(post.getUser().getEmail())){
            if (multipartFileLst == null){
                Post post1 =new Post();
                post1.setPostId(post.getPostId());
                post1.setTitle(post.getTitle());
                post1.setContent(post.getContent());
                post1.setComments(post.getComments());
                post1.setLikes(post.getLikes());
                post1.setCategories(post.getCategories());
                postRepository.save(post1);
                return new ResponseEntity(HttpStatus.CREATED);
            }else {
                List<PostThumbnail> photos = new ArrayList<PostThumbnail>();
                try {
                    Post post1 =new Post();
                    post1.setPostId(post.getPostId());
                    post1.setTitle(post.getTitle());
                    post1.setContent(post.getContent());
                    post1.setCategories(post.getCategories());

                    for(MultipartFile multipartFile : multipartFileLst){

                        InputStream inputStream = multipartFile.getInputStream();
                        OutputStream outputStream = new FileOutputStream(new File(UPLOAD_DIR + multipartFile.getOriginalFilename()));
                        int read = 0;
                        byte[] bytes = new byte[1024];

                        while((read = inputStream.read(bytes)) != -1 ){
                            outputStream.write(bytes,0,read);
                        }
                        outputStream.flush();
                        outputStream.close();

                        PostThumbnail photo = new PostThumbnail();
                        photo.setTitle(multipartFile.getOriginalFilename());
                        photo.setFile(multipartFile.getBytes());
                        postRepository.save(post1);
                        photo.setPost(post1);
                        postImageRepository.save(photo);
                        photos.add(photo);
                    }

                    post1.setPictures(photos);

                    postRepository.save(post1);
                    return new ResponseEntity(HttpStatus.CREATED);
                }catch (IOException e){
                    return new ResponseEntity(HttpStatus.BAD_REQUEST);
                }
            }
        }else{
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

    }

    public void deletePost(ObjectId postId) {
        postRepository.deleteById(postId);
    }

    public Post createComment(User user,ObjectId postId, Comment comment){
        Post post = postRepository.findById(postId).orElseThrow(()-> new UsernameNotFoundException(String.format("Post not found")));
        post.setComments((List<Comment>) comment);
        comment.setUser(user);
        commentRepository.save(comment);
        return postRepository.save(post);
    }
    public Post createLike(User user,ObjectId postId){

        Post post = postRepository.findById(postId).orElseThrow(()-> new UsernameNotFoundException(String.format("Post not found")));
        Like like= likeRepository.findLikeByUserAndPost(user,post);
        if(like==null){
            likeRepository.save(like);
        }
        return  postRepository.save(post);
    }
}
