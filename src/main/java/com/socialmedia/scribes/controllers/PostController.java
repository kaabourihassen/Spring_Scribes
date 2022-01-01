package com.socialmedia.scribes.controllers;


import com.mongodb.lang.Nullable;
import com.socialmedia.scribes.entities.Comment;
import com.socialmedia.scribes.entities.Post;
import com.socialmedia.scribes.entities.User;
import com.socialmedia.scribes.repositories.UserRepository;
import com.socialmedia.scribes.sec.jwt.AuthTokenFilter;
import com.socialmedia.scribes.sec.jwt.JwtUtils;
import com.socialmedia.scribes.services.PostService;
import com.socialmedia.scribes.services.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@CrossOrigin()
@RestController
public class PostController {
    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;
    private UserRepository userRepository;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    AuthTokenFilter authTokenFilter;

    @GetMapping(path ="/posts")
    public List<Post> getAllPosts(){
        return postService.getAllPosts();
    }
    @GetMapping(path ="/post/{postId}")
    public Post getPostById(@PathVariable ObjectId postId){
        return postService.getPostById(postId);
    }
    @PostMapping(path ="/post")
    public ResponseEntity createPost(HttpServletRequest request,@RequestPart("post") Post post, @Nullable @RequestParam("file") MultipartFile[] multipartFileLst){
        String token = authTokenFilter.parseJwt(request);
        User user = userService.loadUserByUsername(jwtUtils.getEmailFromJwtToken(token));
        return postService.createPost(user,post,multipartFileLst);
    }
    @PutMapping(path ="/post")
    public ResponseEntity updatePost(HttpServletRequest request,@RequestPart("post") Post post, @Nullable @RequestParam("file") MultipartFile[] multipartFileLst){
        String token = authTokenFilter.parseJwt(request);
        User user = userService.loadUserByUsername(jwtUtils.getEmailFromJwtToken(token));
        return postService.updatePost(user,post,multipartFileLst);
    }
    @DeleteMapping(path ="/post")
    public void deletePost(@RequestParam ObjectId postId){
        postService.deletePost(postId);
    }
    @PostMapping(path ="/post/{postId}")
    public Post createComment(HttpServletRequest request, @PathVariable ObjectId postId , @RequestBody Comment comment){
        String token = authTokenFilter.parseJwt(request);
        User user = userService.loadUserByUsername(jwtUtils.getEmailFromJwtToken(token));
        return postService.createComment(user,postId,comment);
    }
    @PostMapping(path ="/post/like/{postId}")
    public Post createLike(HttpServletRequest request,@PathVariable ObjectId postId){
        String token = authTokenFilter.parseJwt(request);
        User user = userService.loadUserByUsername(jwtUtils.getEmailFromJwtToken(token));
        return postService.createLike(user,postId);
    }
}
