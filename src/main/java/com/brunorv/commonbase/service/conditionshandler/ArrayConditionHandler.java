package com.brunorv.commonbase.service.conditionshandler;

import com.brunorv.commonbase.dto.filter.FieldFilter;

import java.util.ArrayList;
import java.util.List;

public class ArrayConditionHandler extends ConditionHandler {
    private ConditionHandler nextHandler;
    FieldFilter fieldFilter;

    public ArrayConditionHandler(FieldFilter fieldFilter) {
        this.fieldFilter = fieldFilter;
    }

    @Override
    public void handleCondition(StringBuilder where, String field, String operator, Object valueNode, List<Object> params) throws Exception {

        if(!this.isValidOperator(operator)){
            throw new Exception("the operator '"+operator+"' is not supported");
        }

        if (valueNode instanceof List) {
            handleArrayCondition(where, field, operator, (List<String>) valueNode, params);
            return;
        }

        if (nextHandler != null) {
            nextHandler.handleCondition(where, field, operator, valueNode,params);
        }
    }

    private void handleArrayCondition(StringBuilder where, String field, String operator, List<String> arrayValues, List<Object> params) throws Exception {

       if(arrayValues.isEmpty()){
           throw new Exception("the list of values must not be empty");
       }


       if(this.fieldFilter.isSubQuery){
           where.append(this.fieldFilter.subQueryFieldDestiny)
                   .append(" ")
                   .append(operator)
                   .append(" (")
                   .append(this.fieldFilter.subQuery+" where "+this.fieldFilter.subQueryField + " in"+ " ("+this.getValues(arrayValues,operator,params)+")) ");
       return;
       }




        where.append(field)
                .append(" ")
                .append(operator)
                .append(" ("+this.getValues(arrayValues,operator,params)+") ");


    }

    protected String getValues(List<String> arrayValues, String operator, List<Object> params) throws Exception {

            StringBuilder arrayString = new StringBuilder();

            for (int i = 0; i < arrayValues.size(); i++) {
                if (arrayValues.get(i) instanceof String) {
                    arrayString.append("'"+arrayValues.get(i)+"'");
                }else{
                    arrayString.append(arrayValues.get(i));
                }

                if(arrayValues.size()>1 && i+1<arrayValues.size()){
                    arrayString.append(",");
                }

            }

            return arrayString.toString();



    }

    @Override
    public ConditionHandler setNextHandler(ConditionHandler nextHandler) {
        this.nextHandler = nextHandler;
        return  this.nextHandler;
    }

    }