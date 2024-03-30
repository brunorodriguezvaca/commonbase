package com.brunorv.commonbase.entity;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
public abstract class AuditableEntity extends BaseEntity{

    @Temporal(TemporalType.TIMESTAMP)
    protected Date updatedAt;

    protected String userUpdatedAt;


    @Temporal(TemporalType.TIMESTAMP)
    protected Date deletedAt;

    protected String userDeletedAt;


    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUserUpdatedAt() {
        return userUpdatedAt;
    }

    public void setUserUpdatedAt(String userUpdatedAt) {
        this.userUpdatedAt = userUpdatedAt;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getUserDeletedAt() {
        return userDeletedAt;
    }

    public void setUserDeletedAt(String userDeletedAt) {
        this.userDeletedAt = userDeletedAt;
    }
}
