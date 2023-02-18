package com.lannstark.utils;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;

public final class JsonUtils {
    public static final Gson gson = new Gson();
    public static final ObjectMapper mapper = new ObjectMapper();

    public static void addJsonNode(ObjectNode rootNode, String fieldSimpleName, String jsonPointer, String value) {
        ValueNode node = rootNode.nullNode();
        String numericValue = value.replaceAll("[^0-9.]", "");

        switch (fieldSimpleName) {
            case "byte":
            case "short":
                node = new ShortNode(Short.valueOf(numericValue));
                break;
            case "int":
                node = new IntNode(Integer.valueOf(numericValue));
                break;
            case "long":
                node = new LongNode(Long.valueOf(numericValue));
                break;
            case "float":
                node = new FloatNode(Float.valueOf(numericValue));
                break;
            case "double":
                node = new DoubleNode(Double.valueOf(numericValue));
                break;
            case "boolean":
                node = BooleanNode.valueOf(Boolean.valueOf(value));
                break;
            case "char":
            case "String":
                node = new TextNode(value);
                break;
            default:
                node = new TextNode(value);
                break;
        }

        setJsonPointerValue(rootNode, JsonPointer.compile(jsonPointer), node);
    }

    private static void setJsonPointerValue(ObjectNode node, JsonPointer pointer, JsonNode value) {
        JsonPointer parentPointer = pointer.head();
        JsonNode parentNode = node.at(parentPointer);
        String fieldName = pointer.last().toString().substring(1);

        if (parentNode.isMissingNode() || parentNode.isNull()) {
            parentNode = StringUtils.isNumeric(fieldName) ? mapper.createArrayNode() : mapper.createObjectNode();
            setJsonPointerValue(node, parentPointer, parentNode); // recursively reconstruct hierarchy
        }

        if (parentNode.isArray()) {
            ArrayNode arrayNode = (ArrayNode) parentNode;
            int index = Integer.valueOf(fieldName);
            // expand array in case index is greater than array size (like JavaScript does)
            for (int i = arrayNode.size(); i <= index; i++) {
                arrayNode.addNull();
            }
            arrayNode.set(index, value);
        } else if (parentNode.isObject()) {
            ((ObjectNode) parentNode).set(fieldName, value);
        } else {
            throw new IllegalArgumentException("`" + fieldName + "` can't be set for parent node `"
                    + parentPointer + "` because parent is not a container but " + parentNode.getNodeType().name());
        }
    }
}
