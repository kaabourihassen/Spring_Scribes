package com.socialmedia.scribes.services;

import com.socialmedia.scribes.entities.ConfirmationToken;
import com.socialmedia.scribes.entities.User;
import com.socialmedia.scribes.repositories.EmailSender;
import com.socialmedia.scribes.repositories.UserRepository;
import com.socialmedia.scribes.sec.jwt.JwtUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


@Service
public class UserService implements UserDetailsService {

    @Autowired
    private EmailSender emailSender;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtils jwtUtils;

    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private ConfirmationTokenService confirmationTokenService;
    private static final String UPLOAD_DIR =".\\src\\main\\resources\\static\\imagesuploads\\userImages\\";



    public User enableUser(User user) {
        Optional<User> userExists;
        userExists = userRepository.findByEmail(user.getEmail());
        if (userExists.isPresent()) {
            user.setEnabled(true);
            user.setLocked(false);
        }
        return userRepository.save(user);
    }

    public String changePassword(String email,String newPassword) {
        Optional<User> userExists = userRepository.findByEmail(email);
        User user1= userExists.orElseThrow(()-> new UsernameNotFoundException(String.format("user not found",email)));

        String encodedPassword = bCryptPasswordEncoder.encode(newPassword);

        user1.setPassword(encodedPassword);

        userRepository.save(user1);
        return "changed";
    }

    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException(String.format("User not found", email)));
    }

    public String forgetPassword(String email){
        Optional<User> userExists = userRepository.findByEmail(email);

        if (userExists.isEmpty()) {
            throw new IllegalStateException("there is no account with this email");
        }else{
            String token = UUID.randomUUID().toString();
            User user= userExists.orElseThrow(()-> new UsernameNotFoundException(String.format("user not found",email)));

            ConfirmationToken resetPasswordToken = new ConfirmationToken(token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), user );
            confirmationTokenService.saveConfirmationToken(resetPasswordToken);

            String link = "http://localhost:8080/resetPassword/reset?token=" + resetPasswordToken.getToken();
            emailSender.sendForgetPassword(
                    resetPasswordToken.getUser().getEmail(),
                    buildPassword(resetPasswordToken.getUser().getFullName(),link)
            );
            return token;
        }


    }

    public User changeGeneralInfo(User user,ObjectId userId) {
        User user1= userRepository.findById(userId).orElseThrow(()-> new UsernameNotFoundException(String.format("user not found")));

        user1.setFullName(user.getFullName());
        user1.setBio(user.getBio());
        user1.setAddress(user.getAddress());
        user1.setPhone(user.getPhone());

        userRepository.save(user1);
        return user1;
    }
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public User getUserById(ObjectId userId) {
        Optional<User> userExists = userRepository.findById(userId);
        User user1= userExists.orElseThrow(()-> new UsernameNotFoundException(String.format("user not found")));
        return  user1;
    }
    public User follow(User user, ObjectId userId) {
        User user1= userRepository.findById(userId).orElseThrow(()-> new UsernameNotFoundException(String.format("user not found")));
        Set<ObjectId> userFollowings = user.getFollowings();
        Set<ObjectId> userFollowers = user1.getFollowers();
        for (ObjectId id : userFollowings){
            if(id.equals(userId)){
                userFollowings.remove(id);
                for (ObjectId fid : userFollowers){
                    if(fid.equals(user.getUserId())){
                        userFollowers.remove(fid);
                        userRepository.save(user);
                        return userRepository.save(user1);
                    }
                }
            }
        }
        userFollowers.add(user.getUserId());
        userFollowings.add(userId);
        userRepository.save(user);
        return userRepository.save(user1);
    }

    public void deleteUser(ObjectId userId) {
        userRepository.deleteById(userId);
    }

    private String randomName(){
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            int index = (int)(AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
    }
    public ResponseEntity uploadUserProfileImage(User user, MultipartFile file) {

        try {
            String nameR = randomName();
            InputStream inputStream = file.getInputStream();
            OutputStream outputStream = new FileOutputStream(new File(UPLOAD_DIR +nameR+ file.getOriginalFilename()));
            int read = 0;
            byte[] bytes = new byte[1024];

            while((read = inputStream.read(bytes)) != -1 ){
                outputStream.write(bytes,0,read);
            }
            outputStream.flush();
            outputStream.close();

            user.setProfilePic(UPLOAD_DIR +nameR+ file.getOriginalFilename());
            userRepository.save(user);
            System.out.println(user);
            return new ResponseEntity(HttpStatus.OK);
        }catch (IOException e){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

    }
    public ResponseEntity uploadUserCoverImage(User user, MultipartFile file) {

        try {
            String nameR = randomName();
            InputStream inputStream = file.getInputStream();
            OutputStream outputStream = new FileOutputStream(new File(UPLOAD_DIR +nameR+ file.getOriginalFilename()));
            int read = 0;
            byte[] bytes = new byte[1024];

            while((read = inputStream.read(bytes)) != -1 ){
                outputStream.write(bytes,0,read);
            }
            outputStream.flush();
            outputStream.close();



            user.setCoverPic(UPLOAD_DIR +nameR+ file.getOriginalFilename());
            userRepository.save(user);
            return new ResponseEntity(HttpStatus.CREATED);
        }catch (IOException e){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

    }
    public ObjectId findCurrentUsername() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getUserId();
    }


    public String resetPassword(String token) {


        return token;
    }

    private String buildPassword(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Reset Your Password </span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Please click on the below link to Reset your Password : </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }


}