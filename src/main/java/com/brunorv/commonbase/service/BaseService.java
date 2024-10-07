package com.brunorv.commonbase.service;




import com.brunorv.commonbase.dto.filter.FieldFilter;
import com.brunorv.commonbase.dto.filter.ListDataResponse;
import com.brunorv.commonbase.dto.filter.ListFilterDto;
import com.brunorv.commonbase.exception.FieldsNotFoundException;
import com.brunorv.commonbase.service.conditionshandler.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public abstract  class BaseService<Entity,T> {

    @Autowired
    protected RestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    protected String baseUri;

    protected String baseTable;
    protected String uniqueAttributeCounterBaseTable = "id";
    protected String distinctActivated = "distinct";
    protected abstract Map<String, Object> getModelConfiguration();
    private static HttpHeaders createHeaders(String bearerToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", bearerToken);
        return headers;
    }



    public  <T> ResponseEntity<T> apiRequest(
            String url,
            HttpMethod httpMethod,
            String bearerToken,
            Object requestBody,
            ParameterizedTypeReference<T> responseType
    ) {
        HttpHeaders headers = createHeaders(bearerToken);
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, headers);

        if (responseType == null) {
            responseType = new ParameterizedTypeReference<>() {};
        }

        return this.restTemplate.exchange(url, httpMethod, entity, responseType);
    }


    public Object filter(ListFilterDto obj, List<List<ListFilterDto.Condition>> extraConditions) {
        ListFilterDto filterDto = ((ListFilterDto) obj);
        List<Map<String, Object>> data = null;
        int count =0;
        try {
            data = (List<Map<String, Object>>) this.dynamicQueryModelList(filterDto, filterDto.getPagination().getPage(), filterDto.getPagination().getSize(),false,extraConditions);
            count = (int) this.dynamicQueryModelList(filterDto, filterDto.getPagination().getPage(), filterDto.getPagination().getSize(),true,extraConditions);
            filterDto.getPagination().setTotalRows(count);
        } catch (FieldsNotFoundException e) {
            return new ResponseEntity(e.getErrorObject(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new ResponseEntity(new ListDataResponse(filterDto.getPagination(),filterDto.getSorting(),data), HttpStatus.OK).getBody();
    }

    protected Map<String, String> getModelConfiguration(ListFilterDto filterDto,List<List<ListFilterDto.Condition>> extraConditions,List<Object> params) throws Exception {

        Map<String, Object>modelConfiguration=this.getModelConfiguration();
        Map<String, Object> fieldsMapping = (Map<String, Object>) modelConfiguration.get("fieldsMapping");
        Map<String, Object> joinQueryMapping = (Map<String, Object>) modelConfiguration.get("joinQueryMapping");
        Map<String, String> selectMapping = new HashMap<>();
        Map<String, String> tableMapping = new HashMap<>();
        Map<String, String> resultMapping = new HashMap<>();
        StringBuilder select= new StringBuilder();
        StringBuilder groupBy= new StringBuilder();
        StringBuilder join= new StringBuilder();
        StringBuilder joinCounter= new StringBuilder();

        StringBuilder where = new StringBuilder();
        List<String> fieldsNotFound=new ArrayList<>();
        List<String> forcingGroupingFields=new ArrayList<>();

        if (!filterDto.getFields().isEmpty()) {
            for (String field : filterDto.getFields()) {
                FieldFilter fieldFilter= (FieldFilter) fieldsMapping.get(field);

                if (fieldFilter!=null && !selectMapping.containsKey(field)) {
                    selectMapping.put(field,fieldFilter.getParseableSelect());
                    select.append(fieldFilter.getParseableSelect()).append(",");

                    if(fieldFilter.isForcingGrouping()){
                        forcingGroupingFields.add(fieldFilter.getDatabaseField());
                    }

                }



                if (fieldFilter!=null &&  joinQueryMapping.containsKey(fieldFilter.getTable()) && !tableMapping.containsKey(fieldFilter.getTable())) {
                    tableMapping.put(fieldFilter.getTable(), (String) joinQueryMapping.get(fieldFilter.getTable()));
                    join.append(joinQueryMapping.get(fieldFilter.getTable()));
                    joinCounter.append(joinQueryMapping.get(fieldFilter.getTable()));
                }

                if(fieldFilter==null){
                    fieldsNotFound.add(field);
                }


            }
        }


        for (List<ListFilterDto.Condition> conditionSet : filterDto.getConditions()) {

            for (ListFilterDto.Condition condition : conditionSet) {

                String fieldName = condition.getField();
                FieldFilter fieldFilter= (FieldFilter) fieldsMapping.get(fieldName);

                if (fieldFilter!=null &&  !tableMapping.containsKey(fieldFilter.getTable()) && joinQueryMapping.containsKey(fieldFilter.getTable())) {
                    tableMapping.put(fieldFilter.getTable(), (String) joinQueryMapping.get(fieldFilter.getTable()));
                    join.append(joinQueryMapping.get(fieldFilter.getTable()));
                }

                if(fieldFilter==null){
                    fieldsNotFound.add(fieldName);
                }

            }

        }


        if(!fieldsNotFound.isEmpty()) throw new FieldsNotFoundException("There are  fields not found at the request", fieldsNotFound);


        for (List<ListFilterDto.Condition> conditionSet : filterDto.getConditions()) {
            if (conditionSet.isEmpty()) {
                continue;
            }
            buildWhereClause(conditionSet, fieldsMapping, where,"OR",params);
            where.insert(0, "(");
            where.append(")");
        }


        for (List<ListFilterDto.Condition> conditionSet : extraConditions) {
            if (conditionSet.isEmpty()) {
                continue;
            }

            buildWhereClause(conditionSet, fieldsMapping, where,"AND",params);
        }


        if(forcingGroupingFields.size()>0){
            for (String field : filterDto.getFields()) {
                FieldFilter fieldFilter= (FieldFilter) fieldsMapping.get(field);
                if(!fieldFilter.isForcingGrouping()){
                    groupBy.append(fieldFilter.getDatabaseField()).append(",");
                }
            }
        }


        select = this.cleanStringBuilder(select);
        groupBy = this.cleanStringBuilder(groupBy);

        FieldFilter fieldFilter= (FieldFilter) fieldsMapping.get(filterDto.getSorting().getField());
        String sorting=fieldFilter!=null?fieldFilter.getSortingDatabaseField()+" "+filterDto.getSorting().getOrderDirection():"";

        resultMapping.put("select",select.toString());
        resultMapping.put("joins",join.toString());
        resultMapping.put("joinsCounter",joinCounter.toString());
        resultMapping.put("where",where.toString());
        resultMapping.put("orderBy",sorting);
        resultMapping.put("groupBy",groupBy.toString());
        resultMapping.put("base_table",this.baseTable);
        return resultMapping;
    }

    public void buildWhereClause(List<ListFilterDto.Condition> conditionSet, Map<String, Object> fieldsMapping, StringBuilder where,String firstAppend,List<Object> params) throws Exception {

            if (where.length() > 0) {
                where.append(" "+firstAppend+" ");
            }

            if(conditionSet.size()>1){
                where.append("(");
            }


            boolean isFirstCondition = true;

            for (ListFilterDto.Condition innerCondition : conditionSet) {
                if (!isFirstCondition) {
                    where.append(" AND ");
                }

                FieldFilter fieldFilter = (FieldFilter) fieldsMapping.get(innerCondition.getField());
                String field = fieldFilter.getDatabaseField();
                String operator = innerCondition.getOperator();
                Object valueNode = innerCondition.getValue();
                this.appendCondition(where, field, operator, valueNode,params,fieldFilter);
                isFirstCondition = false;
            }

        if(conditionSet.size()>1){
            where.append(")");
        }

    }

    public StringBuilder cleanStringBuilder(StringBuilder data){

        if (data.toString().endsWith(",")) {
            data = new StringBuilder(data.toString().substring(0, data.toString().length() - 1));
        }
        return data;
    }


    public void appendCondition(StringBuilder where, String field, String operator, Object valueNode,List<Object> params,FieldFilter fieldFilter) throws Exception {
        // PrimitiveDataTypeHandler must be the last element in the chain
        ConditionHandler conditionHandlerChain = new NullConditionHandler();
        ConditionHandler arrayConditionHandlerChain = new ArrayConditionHandler(fieldFilter);
        ConditionHandler likeOperatorHandlerChain = new LikeOperatorHandler();
        ConditionHandler DateOperatorHandlerChain = new DateOperatorsHandler();
        BetweenOperatorHandler BetweenOperatorHandler = new BetweenOperatorHandler();
        ConditionHandler primitiveDataConditionHandlerChain = new PrimitiveDataTypeHandler();

        //build chain
        conditionHandlerChain.setNextHandler(arrayConditionHandlerChain)
                .setNextHandler(likeOperatorHandlerChain)
                .setNextHandler(DateOperatorHandlerChain)
                .setNextHandler(BetweenOperatorHandler)
                .setNextHandler(primitiveDataConditionHandlerChain);

        conditionHandlerChain.handleCondition(where, field, operator, valueNode,params);
    }

    protected String parserSqlQuery(ListFilterDto filterDto, int pageNumber, int pageSize, boolean count,List<List<ListFilterDto.Condition>> extraConditions,List<Object> params) throws Exception {
        Map<String, String> queryModelConfiguration = getModelConfiguration(filterDto,extraConditions,params);

        int offset = (pageNumber - 1) * pageSize;
        String limitClause = String.format("LIMIT %d OFFSET %d", pageSize, offset);

        String baseSql;
        if (count) {
            baseSql = String.format("SELECT COUNT( "+this.distinctActivated+" "+this.baseTable+"."+this.uniqueAttributeCounterBaseTable+") \n FROM %s \n %s \n WHERE  %s  ",
                    queryModelConfiguration.get("base_table"),
                    queryModelConfiguration.get("joinsCounter"),
                    queryModelConfiguration.get("where")
                    );
        } else {

            baseSql = String.format("SELECT "+this.distinctActivated+" "+ " %s \n FROM %s \n %s \n WHERE %s \n GROUP BY %s \n ORDER BY %s \n %s",
                    queryModelConfiguration.get("select"),
                    queryModelConfiguration.get("base_table"),
                    queryModelConfiguration.get("joins"),
                    queryModelConfiguration.get("where"),
                    queryModelConfiguration.get("groupBy"),
                    queryModelConfiguration.get("orderBy"),
                    limitClause);

        }

        if (queryModelConfiguration.get("where").isEmpty()){
            baseSql=baseSql.replace("WHERE","");
        }

        if (queryModelConfiguration.get("groupBy").isEmpty()){
            baseSql=baseSql.replace("GROUP BY","");
        }

        if (queryModelConfiguration.get("orderBy").isEmpty()){
            baseSql=baseSql.replace("ORDER BY","");
        }

        return baseSql;
    }

    public Object dynamicQueryModelList(ListFilterDto filterDto, int pageNumber, int pageSize,boolean count,List<List<ListFilterDto.Condition>> extraConditions) throws Exception {
        List<Object> params = new ArrayList<>();
        String sql = this.parserSqlQuery(filterDto,pageNumber,pageSize,count,extraConditions,params);
        if (count) {
            return jdbcTemplate.queryForObject(sql, Integer.class,params.toArray());
        } else {
            return jdbcTemplate.queryForList(sql,params.toArray());
        }
    }


    public String getUserLogged(){

       HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
       String bearerToken=request.getHeader("Authorization");

        String keycloakBaseUrl = this.baseUri + "/auth/users/user-by-token";
        Map data= apiRequest(
                keycloakBaseUrl,
                HttpMethod.GET,
                bearerToken,
                null,
                new ParameterizedTypeReference<Map>() {}
        ).getBody();

        return (String) data.get("sub");

    }

    public void loadAuditableFields(Map<String, FieldFilter> fieldsMapping,String tableName){
        fieldsMapping.put("createdAt", new FieldFilter(tableName, "createdAt","created_at",false));
        fieldsMapping.put("deletedAt", new FieldFilter(tableName, "deletedAt","deleted_at",false));
        fieldsMapping.put("updatedAt", new FieldFilter(tableName, "updatedAt","updated_at",false));
        fieldsMapping.put("userCreatedAt", new FieldFilter(tableName, "userCreated","user_created_at",false));
        fieldsMapping.put("userUpdatedAt", new FieldFilter(tableName, "userUpdated","user_deleted_at",false));
        fieldsMapping.put("userDeletedAt", new FieldFilter(tableName, "userDeleted","user_updated_at",false));
    }


    protected String convertEntityToJson(Object myEntity) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(myEntity);
    }
}
