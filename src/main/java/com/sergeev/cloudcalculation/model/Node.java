package com.sergeev.cloudcalculation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Node {
    //True if this node contains operator, false - if contains value
    private boolean isOp;
    private String value;
    private Node next;
    private Node prev;

    public Node(boolean isOp, String value) {
        this.isOp = isOp;
        this.value = value;
    }

    public Double getNum() {
        if (!isOp) {
            return Double.parseDouble(value);
        }
        throw new RuntimeException("Node is not the value!");
    }

    public String toString() {
        String s = isOp ? "Operator" : "Value";
        return "[" + s + " " + value + "]";
    }
}
