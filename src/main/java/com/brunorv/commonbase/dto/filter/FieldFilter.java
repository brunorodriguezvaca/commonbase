package com.brunorv.commonbase.dto.filter;

import lombok.Data;

@Data
public class FieldFilter {
    private String table;
    private String databaseAttributeName;
    private String responseAttributeName;

    private String joinTableName;



    private boolean isForcingGrouping;

    private String parseableSelect;
    private String databaseField;

    public FieldFilter() {

    }

    public FieldFilter(String table, String responseAttributeName,String databaseAttributeName,boolean isForcingGrouping) {
        this.table = table;
        this.parseableSelect = this.table+"."+databaseAttributeName+" as "+responseAttributeName;
        this.databaseField=this.table+"."+databaseAttributeName;
        this.responseAttributeName=responseAttributeName;
        this.isForcingGrouping =isForcingGrouping;
    }


    public FieldFilter(String table,String specialFunction) {
        this.table = table;
        this.parseableSelect = specialFunction;
        this.isForcingGrouping =true;
    }

    public FieldFilter(String table,String specialFunction,String databaseField) {
        this.table = table;
        this.parseableSelect = specialFunction;
        this.isForcingGrouping =true;
        this.databaseField=databaseField;
    }

}
