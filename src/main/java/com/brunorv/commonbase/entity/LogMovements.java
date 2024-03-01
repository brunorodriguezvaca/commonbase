package com.brunorv.commonbase.entity;


import javax.persistence.Column;
import javax.persistence.Entity;



@Entity
public class LogMovements extends BaseEntity{

    @Column(nullable = false)
    private String objectId;

    @Column(nullable = false)
    private String objectModel;

    @Column(nullable = false,columnDefinition = "TEXT")
    private String lastValue;

    @Column(nullable = false,columnDefinition = "TEXT")
    private String actualValue;

    @Column(nullable = false)
    private String operation;

    @Column(columnDefinition = "TEXT")
    private String propertiesChanged;


    public LogMovements() {
    }

    public LogMovements(String lastValue, String actualValue, String operation,String objectModel,String objectId,String changes) {
        this.lastValue = lastValue;
        this.actualValue = actualValue;
        this.operation = operation;
        this.objectModel=objectModel;
        this.objectId=objectId;
        this.propertiesChanged=changes;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getObjectModel() {
        return objectModel;
    }

    public void setObjectModel(String objectModel) {
        this.objectModel = objectModel;
    }

    public String getLastValue() {
        return lastValue;
    }

    public void setLastValue(String lastValue) {
        this.lastValue = lastValue;
    }

    public String getActualValue() {
        return actualValue;
    }

    public void setActualValue(String actualValue) {
        this.actualValue = actualValue;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getPropertiesChanged() {
        return propertiesChanged;
    }

    public void setPropertiesChanged(String propertiesChanged) {
        this.propertiesChanged = propertiesChanged;
    }
}
