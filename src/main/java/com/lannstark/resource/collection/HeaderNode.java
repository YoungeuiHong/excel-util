package com.lannstark.resource.collection;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HeaderNode {
    private String type;
    private String columnName;
    private String fieldName;
    private Map<String, Integer> headerRGB;
    private Map<String, Integer> bodyRGB;
    private List<HeaderNode> children;
    @JsonIgnore
    private String fieldPath;

    public HeaderNode(String type, String columnName, String fieldName) {
        this.type = type;
        this.columnName = columnName;
        this.fieldName = fieldName;
        this.headerRGB = new HashMap<>();
        this.bodyRGB = new HashMap<>();
        this.children = new ArrayList<>();
    }

    public List<HeaderNode> getLeafNodes(HeaderNode headerNode) {
        List<HeaderNode> leafNodes = new ArrayList<>();
        if(headerNode.children == null || headerNode.children.size() == 0) {
            leafNodes.add(headerNode);
        } else {
            for(HeaderNode childNode : headerNode.children) {
                leafNodes.addAll(getLeafNodes(childNode));
            }
        }
        return leafNodes;
    }

    public void addChild(HeaderNode child) {
        List<HeaderNode> children = this.children;
        children.add(child);
        this.children = children;
    }

    public int getHeightOfHeaderNode() {
        //
        return this.maxDepth(this) - 1;
    }

    private int maxDepth(HeaderNode root) {
        if (root == null) {
            return 0;
        }
        int maxChildDepth = 0;
        for (HeaderNode child : root.getChildren()) {
            int childDepth = maxDepth(child);
            maxChildDepth = Math.max(maxChildDepth, childDepth);
        }
        return maxChildDepth + 1;
    }

    public void fillFieldPath() {
        this.fieldPath = "/root";
        fillFieldPathByDFS(this);
    }

    private void fillFieldPathByDFS(HeaderNode parent) {
        if (parent.getChildren() == null || parent.getChildren().size() == 0) {
            return;
        }
        for (HeaderNode child : parent.children) {
            child.setFieldPath(parent.getFieldPath() + "/" + child.fieldName);
            fillFieldPathByDFS(child);
        }
    }
}
