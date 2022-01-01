package com.socialmedia.scribes.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.*;

@Document(collection = "users")
@NoArgsConstructor@AllArgsConstructor
@Getter
@Setter
public class User implements UserDetails {
    @Id
    @JsonSerialize(using= ToStringSerializer.class)
    private ObjectId userId;
    @Field(value = "fullName")
    private String fullName;
    @Field(value = "email")
    @Indexed
    private String email;
    @Field(value = "password")
    private String password;
    @Field(value = "created_at")
    private LocalDateTime created_at;
    @Field(value = "enabled")
    private boolean enabled=false;
    @Field(value = "locked")
    private boolean locked;
    @Field(value = "bio")
    private String bio;
    private String profilePic;
    private String coverPic;
    @Field(value = "address")
    private String address;
    @Field(value = "phone")
    private String phone;

    private Role role;


    @DBRef
    private Set<User> followers = new HashSet<>();

    @DBRef
    private Set<Category> favoriteCategories = new HashSet<>();

    @DBRef
    private Set<Post> posts = new HashSet<>();

    @DBRef
    private Set<Post> post= new HashSet<>();

    @DBRef
    private Set<Comment> comments= new HashSet<>();
    @Field(value = "likes")
    @DBRef
    private List<Like> likes;

    public User(String fullName, String email, String password) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role=Role.USER;
    }

    public User(String fullName, String email, String password, LocalDateTime created_at, boolean enabled, boolean locked, String bio,
                String profilePic, String coverPic, String address, String phone, Set<Role> roles,
                Set<User> followers, Set<Category> favoriteCategories, Set<Post> post) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.created_at = created_at;
        this.enabled = enabled;
        this.locked = locked;
        this.bio = bio;
        this.profilePic = profilePic;
        this.coverPic = coverPic;
        this.address = address;
        this.phone = phone;
        this.role = role;
        this.followers = followers;
        this.favoriteCategories = favoriteCategories;
        this.post = post;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority= new SimpleGrantedAuthority(role.name());
        return Collections.singletonList(authority);
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
