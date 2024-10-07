package com.brunorv.commonbase.entity;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Id;


@MappedSuperclass
public abstract class BaseEntity {

    @Id
    private String id;

    @Temporal(TemporalType.TIMESTAMP)
    protected Date createdAt;

    protected String userCreatedAt;


    @PrePersist
    protected void onCreate() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserCreatedAt() {
        return userCreatedAt;
    }

    public void setUserCreatedAt(String userCreatedAt) {
        this.userCreatedAt = userCreatedAt;
    }
}
