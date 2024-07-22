package com.brunorv.commonbase.service.conditionshandler;

import java.util.Arrays;
import java.util.List;

public abstract class ConditionHandler {
    private ConditionHandler nextHandler;
    protected List<String> operatorHandler;
    public abstract void handleCondition(StringBuilder where, String field, String operator, Object valueNode, List<Object> params) throws Exception;

    public ConditionHandler setNextHandler(ConditionHandler nextHandler) {
        this.nextHandler = nextHandler;
        return this;
    }

    protected boolean isValidOperator(String operator) {
        List<String> validOperators = Arrays.asList("=","!=", "<>", "<", ">", "<=", ">=","in","not in","%like%","like%","%like","between","betweenDate","greaterThanDate","lessThanDate");
        return validOperators.contains(operator);
    }

    protected boolean isMyOperator(String operator) {
        return operatorHandler.contains(operator);
    }

}