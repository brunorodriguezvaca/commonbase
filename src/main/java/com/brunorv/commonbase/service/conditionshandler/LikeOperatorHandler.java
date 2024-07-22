package com.brunorv.commonbase.service.conditionshandler;

import java.util.Arrays;
import java.util.List;

public class LikeOperatorHandler extends ConditionHandler {
    private ConditionHandler nextHandler;

    public LikeOperatorHandler(){
        this.operatorHandler= Arrays.asList("%like%","like%","%like");
    }

    @Override
    public void handleCondition(StringBuilder where, String field, String operator, Object valueNode, List<Object> params) throws Exception {

        if(!this.isValidOperator(operator)){
            throw new Exception("the operator '"+operator+"' is not supported");
        }

        if(this.isMyOperator(operator)){

            valueNode = operator.replace("like", valueNode.toString());
            where.append(field)
                    .append(" ")
                    .append("ILIKE ")
                    .append("?");
            params.add(valueNode);
            return;
        }



        if (nextHandler != null) {
            nextHandler.handleCondition(where, field, operator, valueNode,params);
        }

    }

    @Override
    public ConditionHandler setNextHandler(ConditionHandler nextHandler) {
        this.nextHandler = nextHandler;
        return  this.nextHandler;
    }
}
