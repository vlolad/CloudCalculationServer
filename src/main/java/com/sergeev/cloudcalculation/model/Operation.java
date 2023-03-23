package com.sergeev.cloudcalculation.model;

import lombok.AllArgsConstructor;
import lombok.Data;

//Class takes three nodes: two values and operator between them
//After calculation, class return new node, that replaces three old nodes.

@Data
@AllArgsConstructor
public class Operation {
    private Node val1;
    private Node op;
    private Node val2;

    public Node calculate() {
        Double num1 = Double.parseDouble(val1.getValue());
        Double num2 = Double.parseDouble(val2.getValue());
        double result = 0d;
        switch (op.getValue()) {
            case "+":
                result = num1 + num2;
                break;
            case "-":
                result = num1 - num2;
                break;
            case "*":
                result = num1 * num2;
                break;
            case "/":
                result = num1 / num2;
                break;
        }

        return Node.builder()
                .value(Double.toString(result))
                .isOp(false)
                .next(val2.getNext())
                .prev(val1.getPrev())
                .build();
    }
}
