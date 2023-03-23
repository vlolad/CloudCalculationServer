package com.sergeev.cloudcalculation.model;

import lombok.Data;
import lombok.NoArgsConstructor;


//Custom variation of LinkedList
@Data
@NoArgsConstructor
public class NodeList {
    private int size = 0;
    private Node header;
    private Node tail;
    //Less prior operators counting, such as + and -
    private int tierI = 0;
    //More prior operators counting, such as * and /
    private int tierII = 0;

    public boolean add(Node node) {
        if (node.isOp()) {
            //Update operators count fields
            addOperator(node);
        }
        if (size == 0) {
            this.header = node;
            this.tail = node;
            size++;
            return true;
        }
        node.setPrev(this.tail);
        this.tail.setNext(node);
        this.tail = node;
        size++;
        return true;
    }

    //Method search next available operation and calculate it
    public void calculateNext() {
        if (size == 1) {
            return;
        }
        Node node;
        if (tierII > 0) {
            node = getOperation(findTierII()).calculate();
            tierII--;
        } else if (tierI > 0) {
            node = getOperation(findTierI()).calculate();
            tierI--;
        } else {
            return;
        }
        //Updating new node statement
        if (node.getPrev() == null) {
            header = node;
        } else {
            node.getPrev().setNext(node);
        }
        if (node.getNext() == null) {
            tail = node;
        } else {
            node.getNext().setPrev(node);
        }
        //Calculated operation converts two values and one operator in single result value node
        //So we need to update the size of this list
        size -= 2;
    }

    public Double getAnswer() {
        if (size != 1) {
            return null;
        }
        return header.getNum();
    }

    //Search starts from the beginning
    //TODO implement Node<E> node(int index) method from LinkedList
    private Node findTierII() {
        Node node = header;
        while (node != null) {
            if (node.isOp() && (node.getValue().equals("*") || node.getValue().equals("/"))) {
                return node;
            }
            node = node.getNext();
        }
       return null;
    }

    private Node findTierI() {
        Node node = header;
        while (node != null) {
            if (node.isOp() && (node.getValue().equals("+") || node.getValue().equals("-"))) {
                return node;
            }
            node = node.getNext();
        }
        return null;
    }

    private Operation getOperation(Node node) {
        return new Operation(node.getPrev(), node, node.getNext());
    }

    private void addOperator(Node node) {
        if (node.getValue().equals("*") || node.getValue().equals("/")) {
            tierII++;
            return;
        }
        tierI++;
    }

    public String toString() {
        if (header == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Node node = header;
        while (node != null) {
            sb.append(node);
            node = node.getNext();
        }
        return sb.toString();
    }
}
