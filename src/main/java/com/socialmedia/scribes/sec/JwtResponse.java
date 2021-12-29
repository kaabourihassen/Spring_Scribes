package com.socialmedia.scribes.sec;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;
import org.bson.types.ObjectId;

import java.util.List;

@Data@NoArgsConstructor@AllArgsConstructor@Getter@Setter
public class JwtResponse {
	private String token;
	private String type = "Bearer";
	@JsonSerialize(using= ToStringSerializer.class)
	private ObjectId id;
	private String email;
	private List<String> roles;
	private String fullName;
	private Boolean active;

	public JwtResponse(String token, ObjectId id, String email, List<String> roles, String fullName, Boolean active) {
		this.token = token;
		this.id = id;
		this.email = email;
		this.roles = roles;
		this.fullName = fullName;
		this.active = active;
	}





}
