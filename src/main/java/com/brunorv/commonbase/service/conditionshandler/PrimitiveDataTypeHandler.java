package com.brunorv.commonbase.service.conditionshandler;

import java.util.List;

public class PrimitiveDataTypeHandler extends ConditionHandler {
    private ConditionHandler nextHandler;

    @Override
    public void handleCondition(StringBuilder where, String field, String operator, Object valueNode, List<Object> params) throws Exception {

        if(!this.isValidOperator(operator)){
            throw new Exception("the operator '"+operator+"' is not supported");
        }

        where.append(field)
                .append(" ")
                .append(operator)
                .append("?");
        params.add(valueNode);


    }

    @Override
    public ConditionHandler setNextHandler(ConditionHandler nextHandler) {
        this.nextHandler = nextHandler;
        return  this.nextHandler;
    }
}
