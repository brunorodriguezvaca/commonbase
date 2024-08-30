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
    private String sortingDatabaseField;


    public boolean isSubQuery;
    public String subQueryFieldDestiny;
    public String subQueryField;
    public String subQuery;


    public FieldFilter() {

    }

    public FieldFilter(String table, String responseAttributeName,String databaseAttributeName,boolean isForcingGrouping) {
        this.table = table;
        this.parseableSelect = this.table+"."+databaseAttributeName+" as "+responseAttributeName;
        this.databaseField=this.table+"."+databaseAttributeName;
        this.responseAttributeName=responseAttributeName;
        this.isForcingGrouping =isForcingGrouping;
        this.sortingDatabaseField=this.databaseField;
    }


    public FieldFilter(String table,String specialFunction) {
        this.table = table;
        this.parseableSelect = specialFunction;
        this.isForcingGrouping =true;
    }

    public FieldFilter(String table,String specialFunction,String databaseField,String sorting) {
        this.table = table;
        this.parseableSelect = specialFunction;
        this.isForcingGrouping =true;
        this.databaseField=databaseField;
        this.sortingDatabaseField=sorting;
    }


    public FieldFilter(String table,String specialFunction,String databaseField,String sorting,String subQuery,String subQueryFieldDestiny,String subQueryField) {
        this.table = table;
        this.parseableSelect = specialFunction;
        this.isForcingGrouping =true;
        this.databaseField=databaseField;
        this.sortingDatabaseField=sorting;
        this.subQuery=subQuery;
        this.subQueryFieldDestiny=subQueryFieldDestiny;
        this.subQueryField=subQueryField;
        this.isSubQuery=true;
    }

}
