package com.brunorv.commonbase.service.conditionshandler;



import java.util.List;

public class NullConditionHandler extends ConditionHandler {
    private ConditionHandler nextHandler;

    @Override
    public void handleCondition(StringBuilder where, String field, String operator, Object valueNode, List<Object> params) throws Exception {

        if(!this.isValidOperator(operator)){
            throw new Exception("the operator '"+operator+"' is not supported");
        }

        if (valueNode == null) {
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

        if(valueNode==null){
            if(operator.equals("=")) return "is null";
            if(operator.equals("!=")) return "is not null";
            throw new Exception("the operator '"+operator+"' is not supported for the value "+null);
        }

        return operator;

    }

    @Override
    public ConditionHandler setNextHandler(ConditionHandler nextHandler) {
        this.nextHandler = nextHandler;
        return  this.nextHandler;
    }
}
