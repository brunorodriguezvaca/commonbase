package com.brunorv.commonbase.service;

import com.brunorv.commonbase.LogMovementsRepository;
import com.brunorv.commonbase.entity.LogMovements;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.diff.JsonDiff;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
public class LogMovementService extends BaseService{

    @Autowired
    private LogMovementsRepository logMovementsRepository;


    @Override
    protected Map<String, Object> getModelConfiguration() {
        return null;
    }

    public void saveLogMovement(String lastValue,String actualValue,String operation,String objectModel,String objectId,String userId){
        String changes=this.calculatePropertiesChanged(lastValue,actualValue);
        LogMovements log=new LogMovements(lastValue,actualValue,operation,objectModel,objectId,changes);
        log.setUserCreatedAt(userId);
        this.logMovementsRepository.save(log);
    }

    private String calculatePropertiesChanged(String lastValue,String actualValue) {
        String changes ="";
        try {
            Object lastValueObject = parseJsonString(lastValue);
            Object actualValueObject = parseJsonString(actualValue);
            changes = compareObjects(lastValueObject, actualValueObject);
            return changes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return changes;
    }

    private Object parseJsonString(String jsonString) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonString, Object.class);
    }

    private String compareObjects(Object obj1, Object obj2) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode tree1 = objectMapper.valueToTree(obj1);
        JsonNode tree2 = objectMapper.valueToTree(obj2);

        Map<String, JsonNode> originalValues = new HashMap<>();
        trackOriginalValues("", tree1, originalValues);

        JsonNode diff = JsonDiff.asJson(tree1, tree2);
        String changes = processJsonDiff(diff, originalValues);
        StringBuilder changesBuilder = new StringBuilder(changes);
        if (!changesBuilder.toString().isEmpty() && changesBuilder.charAt(changesBuilder.length() - 1) == ',') {
            changesBuilder.deleteCharAt(changesBuilder.length() - 1);
        }
        changesBuilder.insert(0, '[');
        changesBuilder.append(']');

        return changesBuilder.toString();
    }

    private void trackOriginalValues(String path, JsonNode node, Map<String, JsonNode> originalValues) {
        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String fieldName = entry.getKey();
                JsonNode fieldValue = entry.getValue();

                String fullPath = path + "/" + fieldName;
                originalValues.put(fullPath, fieldValue);
                trackOriginalValues(fullPath, fieldValue, originalValues);
            }
        } else if (node.isArray()) {
            for (int i = 0; i < node.size(); i++) {
                String fullPath = path + "[" + i + "]";
                trackOriginalValues(fullPath, node.get(i), originalValues);
            }
        }
    }

    private String processJsonDiff(JsonNode node, Map<String, JsonNode> originalValues) {
        StringBuilder changesBuilder = new StringBuilder();
        if (node.isArray()) {
            for (JsonNode arrayElement : node) {
                changesBuilder.append(processJsonDiff(arrayElement, originalValues));
            }
        } else if (node.isObject()) {
            String operation = node.path("op").asText();
            String path = node.path("path").asText();
            JsonNode value = node.path("value");

            JsonNode originalValue = originalValues.get(path);


            changesBuilder.append("{\"op\":\"").append(operation)
                    .append("\",\"property\":\"").append(path)
                    .append("\",\"oldValue\":").append(originalValue)
                    .append(",\"newValue\":").append(value).append("},");

        }
        return changesBuilder.toString();
    }


}
