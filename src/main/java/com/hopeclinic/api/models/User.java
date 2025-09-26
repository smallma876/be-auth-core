package com.hopeclinic.api.models;
import java.util.List;
import org.hibernate.validator.constraints.Length;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 15)
    @NotBlank
    @Length(min = 8, max = 15)
    private String username;

    @NotBlank
    @Length(min = 8)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password_hash" , length = 255, nullable = false)
    private String password;

    @Column(name = "profile_picture_url", length = 255, nullable = true)
    private String profilePictureUrl;

    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<String> rolesName;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_role",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns =  @JoinColumn(name = "role_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role_id"})
    )
    private List<Role> roles;
}
