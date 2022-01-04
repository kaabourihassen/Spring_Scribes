package com.socialmedia.scribes.controllers;

import com.socialmedia.scribes.entities.RegistrationRequest;
import com.socialmedia.scribes.entities.User;
import com.socialmedia.scribes.repositories.UserRepository;
import com.socialmedia.scribes.sec.JwtResponse;
import com.socialmedia.scribes.sec.jwt.AuthTokenFilter;
import com.socialmedia.scribes.sec.jwt.JwtUtils;
import com.socialmedia.scribes.services.RegistrationService;
import com.socialmedia.scribes.services.UserService;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin()
@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    AuthTokenFilter authTokenFilter;
    @Autowired
    private RegistrationService registrationService;

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
    public String resetPassword(@RequestParam("email") String email,@RequestParam("newPassword") String newPassword) {
        return registrationService.changePassword(email,newPassword);
    }
    @PutMapping(path = "/user/changePassword")
    public User changePassword(HttpServletRequest request,@RequestBody String newPassword){
        String token = authTokenFilter.parseJwt(request);
        User user = userService.loadUserByUsername(jwtUtils.getEmailFromJwtToken(token));
        return registrationService.changeloggedPassword(user.getEmail(),newPassword);
    }
    @GetMapping(path = "/users")
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }
    @GetMapping(path = "/user/{userId}")
    public User getUserById(@PathVariable ObjectId userId) throws FileNotFoundException {
        return userService.getUserById(userId);
    }
    @PutMapping(path = "/user/updateUser/{userId}")
    public User UpdateUser(@RequestBody User user,@PathVariable ObjectId userId){
        return userService.changeGeneralInfo(user,userId);
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
    @GetMapping(path = "/user/getProfilePic/{userId}" , produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] getProfilePic(HttpServletRequest request,@PathVariable ObjectId userId) throws IOException {
        String token = authTokenFilter.parseJwt(request);
        User user = userService.loadUserByUsername(jwtUtils.getEmailFromJwtToken(token));
        User user1= userRepository.findById(userId).orElseThrow(()-> new UsernameNotFoundException(String.format("user not found")));
        InputStream in = new FileInputStream(user1.getProfilePic());
        return IOUtils.toByteArray(in);
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
