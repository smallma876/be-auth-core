package com.hopeclinic.api.Dtos;
import java.util.List;

public class UserResponseDto {

    private Long id;
    private String username;
    private String profilePictureUrl;
    private List<String> roles;
    private String message;

    public UserResponseDto() {}

    public UserResponseDto(Long id, String username, String profilePictureUrl, List<String> roles, String message) {
        this.id = id;
        this.username = username;
        this.profilePictureUrl = profilePictureUrl;
        this.roles = roles;
        this.message = message;
    }

    public Long getId(){ 
        return id; 
    }
    public void setId(Long id){ 
        this.id = id; 
    }

    public String getUsername(){ 
        return username; 
    }
    public void setUsername(String username){ 
        this.username = username; 
    }

    public String getProfilePictureUrl(){ 
        return profilePictureUrl; 
    }
    public void setProfilePictureUrl(String profilePictureUrl){ 
        this.profilePictureUrl = profilePictureUrl; 
    }

    public List<String> getRoles(){ 
        return roles; 
    }
    public void setRoles(List<String> roles){ 
        this.roles = roles; 
    }

    public String getMessage(){ 
        return message;
     }
    public void setMessage(String message){ 
        this.message = message; 
    }
}
