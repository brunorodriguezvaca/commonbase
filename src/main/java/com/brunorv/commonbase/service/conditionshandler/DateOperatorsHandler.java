package com.brunorv.commonbase.service.conditionshandler;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

public class DateOperatorsHandler extends ConditionHandler {
    private ConditionHandler nextHandler;

    public DateOperatorsHandler(){
        this.operatorHandler= Arrays.asList("betweenDate","greaterThanDate","lessThanDate");
    }

    @Override
    public void handleCondition(StringBuilder where, String field, String operator, Object valueNode, List<Object> params) throws Exception {

        if(!this.isValidOperator(operator)){
            throw new Exception("the operator '"+operator+"' is not supported");
        }

        if(this.isMyOperator(operator)){



            switch (operator){
                case "betweenDate":
                    String[] parts = valueNode.toString().split("\\|");
                    String init= parts[0];
                    String end = parts[1];
                    Timestamp initTimestamp = convertStringToTimestamp(init);
                    Timestamp endTimestamp = convertStringToTimestamp(end);
                    where.append(field)
                            .append(" ")
                            .append(" BETWEEN ")
                            .append("?")
                            .append(" AND ")
                            .append(" ? ");

                    params.add(initTimestamp);
                    params.add(endTimestamp);
                    break;


                default:
                    Timestamp dateTimestamp = convertStringToTimestamp(valueNode.toString());
                    operator=this.getSqlOperator(operator);
                    where.append(field)
                            .append(" ")
                            .append(operator)
                            .append(" ?");

                    params.add(dateTimestamp);
                    break;
            }




            return;
        }



        if (nextHandler != null) {
            nextHandler.handleCondition(where, field, operator, valueNode,params);
        }

    }

    private String getSqlOperator(String operator){
        String sqlOperator = null;
        switch (operator){
            case "greaterThanDate":
                sqlOperator = ">=";
                break;
                case "lessThanDate":
                    sqlOperator = "<=";
                    break;

        }
        return sqlOperator;
    }

    private static Timestamp convertStringToTimestamp(String str) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date parsedDate = dateFormat.parse(str);
            return new Timestamp(parsedDate.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public ConditionHandler setNextHandler(ConditionHandler nextHandler) {
        this.nextHandler = nextHandler;
        return  this.nextHandler;
    }
}
