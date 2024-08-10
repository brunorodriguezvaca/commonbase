package com.brunorv.commonbase.service.conditionshandler;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

public class BetweenOperatorHandler extends ConditionHandler {
    private ConditionHandler nextHandler;

    public BetweenOperatorHandler(){
        this.operatorHandler= Arrays.asList("between");
    }

    @Override
    public void handleCondition(StringBuilder where, String field, String operator, Object valueNode, List<Object> params) throws Exception {

        if(!this.isValidOperator(operator)){
            throw new Exception("the operator '"+operator+"' is not supported");
        }

        if(this.isMyOperator(operator)){
            String[] parts = valueNode.toString().split("\\|");
            String init= parts[0];
            String end = parts[1];
            Number initTimestamp = convertStringToNumber(init,"double");
            Number endTimestamp =convertStringToNumber(end,"double");
            where.append(field)
                    .append(" ")
                    .append(" BETWEEN ")
                    .append("?")
                    .append(" AND ")
                    .append(" ? ");

            params.add(initTimestamp);
            params.add(endTimestamp);
            return;
            }




        if (nextHandler != null) {
            nextHandler.handleCondition(where, field, operator, valueNode,params);
        }

    }



    private static Number convertStringToNumber(String str, String type) {
        try {
            switch (type.toLowerCase()) {
                case "int":
                    return Integer.parseInt(str);
                case "float":
                    return Float.parseFloat(str);
                case "double":
                    return Double.parseDouble(str);
                case "long":
                    return Long.parseLong(str);
                case "short":
                    return Short.parseShort(str);
                case "byte":
                    return Byte.parseByte(str);
                case "bigdecimal":
                    return new java.math.BigDecimal(str);
                default:
                    throw new IllegalArgumentException("Unsupported number type: " + type);
            }
        } catch (NumberFormatException e) {
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
