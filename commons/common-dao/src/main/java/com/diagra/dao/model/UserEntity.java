package com.diagra.dao.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "user")
public class UserEntity {

    @Id
    @MongoId(FieldType.OBJECT_ID)
    private String id;

    @Field
    @Indexed(unique = true)
    private String userName;
    @Field
    @Indexed(unique = true)
    private String email;
    @Field
    private String password;
    @Field
    private List<UserRoles> roles;

    public UserEntity() {
    }

    public UserEntity(String email, String userName, String password, List<UserRoles> roles) {
        this(null, email, userName, password, roles);
    }

    @PersistenceConstructor
    public UserEntity(String id, String email, String userName, String password, List<UserRoles> roles) {
        this.email = email;
        this.userName = userName;
        this.password = password;
        this.roles = new ArrayList<>(roles);
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }

    public List<UserRoles> getRoles() {
        return roles;
    }

    public String getId() {
        return id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRoles(List<UserRoles> roles) {
        this.roles = roles;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
