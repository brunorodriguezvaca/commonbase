package com.brunorv.commonbase.service.conditionshandler;



import java.util.Arrays;
import java.util.List;

public class NullConditionHandler extends ConditionHandler {
    private ConditionHandler nextHandler;
    public NullConditionHandler(){
        this.operatorHandler= Arrays.asList("isNull","isNotNull");
    }
    @Override
    public void handleCondition(StringBuilder where, String field, String operator, Object valueNode, List<Object> params) throws Exception {

        if(!this.isValidOperator(operator)){
            throw new Exception("the operator '"+operator+"' is not supported");
        }

        if(this.isMyOperator(operator)){
            where.append(field)
                    .append(" ")
                    .append(getParserOperator(valueNode, operator))
                    .append(" ");
          return;
        }

        if (nextHandler != null) {
            nextHandler.handleCondition(where, field, operator, valueNode,params);
        }
    }

    protected String getParserOperator(Object valueNode,String operator) throws Exception {
        if(operator.equals("isNull")) return "is null";
        if(operator.equals("isNotNull")) return "is not null";
        throw new Exception("the operator '"+operator+"' is not supported for the value "+null);
    }

    @Override
    public ConditionHandler setNextHandler(ConditionHandler nextHandler) {
        this.nextHandler = nextHandler;
        return  this.nextHandler;
    }
}
