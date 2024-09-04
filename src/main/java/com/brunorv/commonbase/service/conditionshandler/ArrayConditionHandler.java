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
            List<?> list = (List<?>) valueNode;
            if(list.isEmpty()){
                throw new Exception("the list of values must not be empty");
            }


            if ( list.get(0) instanceof String) {
                handleArrayCondition(where, field, operator, (List<String>) valueNode, params);
            }

            if ( list.get(0) instanceof Boolean) {
                handleArrayBooleanCondition(where, field, operator, (List<Boolean>) valueNode, params);
            }

            return;
        }

        if (nextHandler != null) {
            nextHandler.handleCondition(where, field, operator, valueNode,params);
        }
    }

    private void handleArrayBooleanCondition(StringBuilder where, String field, String operator, List<Boolean> arrayValues, List<Object> params) throws Exception {
        where.append(field)
                .append(" ")
                .append(operator)
                .append(" ("+this.getBooleanValues(arrayValues,operator,params)+") ");
    }

  private void handleArrayCondition(StringBuilder where, String field, String operator, List<String> arrayValues, List<Object> params) throws Exception {




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

    protected String getBooleanValues(List<Boolean> arrayValues, String operator, List<Object> params) throws Exception {

        StringBuilder arrayString = new StringBuilder();

        for (int i = 0; i < arrayValues.size(); i++) {
            if (arrayValues.get(i) instanceof Boolean) {
                arrayString.append(arrayValues.get(i));
            }else{
                arrayString.append(arrayValues.get(i));
            }

            if(arrayValues.size()>1 && i+1<arrayValues.size()){
                arrayString.append(",");
            }

        }

        return arrayString.toString();



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