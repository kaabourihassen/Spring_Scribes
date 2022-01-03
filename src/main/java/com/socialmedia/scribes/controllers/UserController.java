package com.socialmedia.scribes.controllers;

import com.socialmedia.scribes.entities.RegistrationRequest;
import com.socialmedia.scribes.entities.User;
import com.socialmedia.scribes.sec.JwtResponse;
import com.socialmedia.scribes.sec.jwt.AuthTokenFilter;
import com.socialmedia.scribes.sec.jwt.JwtUtils;
import com.socialmedia.scribes.services.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin()
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    AuthTokenFilter authTokenFilter;

    @CrossOrigin(origins = "*", maxAge = 3600)
    @PostMapping("/auth/signIn")
    public ResponseEntity<?> authenticateUser(@RequestBody RegistrationRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        User userDetails = (User) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getUserId(),
                userDetails.getEmail(),roles,userDetails.getFullName(),true));
    }
    @PostMapping(path = "/forgetPassword")
    public String forgetPassword(@RequestParam("email") String email) {
        System.out.println("1");
        return userService.forgetPassword(email);
    }
    @GetMapping(path = "/resetPassword/reset")
    public String resetPassword(@RequestParam("token")String token) {
        return userService.resetPassword(token);
    }
    @PostMapping(path = "/resetPassword/reset")
    public String changePassword(@RequestParam("email") String email,@RequestParam("newPassword") String newPassword) {
        return userService.changePassword(email,newPassword);
    }
    @GetMapping(path = "/users")
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }
    @GetMapping(path = "/user/{userId}")
    public User getUserById(@PathVariable ObjectId userId){
        return userService.getUserById(userId);
    }
    @PutMapping(path = "/user/updateUser")
    public User UpdateUser(@RequestBody User user){
        return userService.changeGeneralInfo(user);
    }
    @DeleteMapping(path = "/user/{userId}")
    public void deleteUser(@PathVariable ObjectId userId){
        userService.deleteUser(userId);
    }

    @PostMapping(path = "/user/uploadProfilePic", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void uploadUserProfileImage(HttpServletRequest request, @RequestParam("file") MultipartFile file){
        String token = authTokenFilter.parseJwt(request);
        User user = userService.loadUserByUsername(jwtUtils.getEmailFromJwtToken(token));
        userService.uploadUserProfileImage(user,file);
    }
    @GetMapping(path = "/user/getProfilePic")
    public byte[] getProfilePic(HttpServletRequest request) throws IOException {
        String token = authTokenFilter.parseJwt(request);
        User user = userService.loadUserByUsername(jwtUtils.getEmailFromJwtToken(token));
        return Files.readAllBytes(Path.of(user.getProfilePic()));
    }
    @PostMapping(path = "/user/uploadCoverPic", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void uploadUserCoverImage(HttpServletRequest request,@RequestParam("file") MultipartFile file){
        String token = authTokenFilter.parseJwt(request);
        User user = userService.loadUserByUsername(jwtUtils.getEmailFromJwtToken(token));
        userService.uploadUserCoverImage(user,file);
    }
    @PostMapping(path = "/user/follow/{userId}")
    public User follow(HttpServletRequest request,@PathVariable ObjectId userId){
        String token = authTokenFilter.parseJwt(request);
        User user = userService.loadUserByUsername(jwtUtils.getEmailFromJwtToken(token));
        return userService.follow(user,userId);
    }
}
