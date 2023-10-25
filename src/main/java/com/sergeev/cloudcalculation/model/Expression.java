package com.sergeev.cloudcalculation.model;

import com.sergeev.cloudcalculation.util.BlockType;
import lombok.Data;

import static com.sergeev.cloudcalculation.util.Constants.*;

@Data
public class Expression {
    private Block first;
    private Block last;
    // Operations "+" and "-"
    private int tierI = 0;
    // Operations "*" and "/"
    private int tierII = 0;
    //Quantity (round brackets)
    private int tierQ = 0;

    public Expression() {
    }

    //Fill this Expression
    public boolean add(Block block) {

        if (block.getType().equals(BlockType.OPERATOR)) {
            if (block.getValue().matches(TIER_II)) {
                tierII++;
            } else if (block.getValue().matches(TIER_I)) {
                tierI++;
            } else {
                return false;
            }
        } else if (block.getType().equals(BlockType.QUANTITY)) {
            tierQ++;
        }

        if (first == null) {
            first = block;
        } else {
            last.setNext(block);
            block.setPrev(last);
        }
        last = block;

        return true;
    }

    //If first block is "-", next number becomes
    public void checkFirst() {
        if (first.getType().equals(BlockType.OPERATOR) && first.getValue().equals("-")) {

            if (Double.parseDouble(first.getNext().getValue()) < 0) {
                Block block = first.getNext();
                Double newValue = Double.parseDouble(first.getNext().getValue()) * -1;
                block.setValue(Double.toString(newValue));
                tierI--;
                first = block;
                block.setPrev(null);
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append(first.getValue());
            Block block = first.getNext();
            do {
                sb.append(block.getValue());
                block = block.getNext();
            } while (block != null && block.getType().equals(BlockType.NUMBER));

            Block newBlock = new Block(BlockType.NUMBER, sb.toString());
            first = newBlock;
            if (block != null) block.setPrev(newBlock);
            newBlock.setNext(block);
            tierI--;
        }
    }

    public void checkQuantity() throws Exception { //TODO normal exceptions
        if (tierQ == 0) return;
        if (tierQ % 2 != 0) throw new Exception("Problem with quantity");

        Block open = this.first;
        while (!open.getType().equals(BlockType.QUANTITY)) {
            open = open.getNext();
        }
        if (open.getValue().equals(")")) throw new Exception("Problem with quantity: type 2");
        if (open.getPrev() != null && open.getPrev().getType().equals(BlockType.NUMBER)) {
            Block dummy = Block.builder()
                    .value("*")
                    .type(BlockType.OPERATOR)
                    .next(open)
                    .prev(open.getPrev())
                    .build();
            open.getPrev().setNext(dummy);
            open.setPrev(dummy);
            this.tierII++;
        }

        Block close = open.getNext();

        if (close.getValue().equals(")")) {
            Block dummy = Block.builder()
                    .value("0")
                    .prev(open)
                    .next(close)
                    .type(BlockType.NUMBER)
                    .build();
            open.setNext(dummy);
            close.setPrev(dummy);
            close = dummy;
        }

        int internal = 0;
        int count = 0;

        while (!(close.getValue().equals(")") && internal == 0)) {
            if (close.getType().equals(BlockType.QUANTITY)) {
                if (close.getValue().equals("(")) internal++;
                else internal--;
            }
            close = close.getNext();
            count++;
        }
        Block block = open.getNext();

        Expression exp = new Expression();
        for (; count != 0; count--) {
            exp.add(block);
            block = block.getNext();
        }

        exp.getFirst().setPrev(null);
        exp.getLast().setNext(null);
        this.tierI -= exp.getTierI();
        this.tierII -= exp.getTierII();
        this.tierQ -= exp.getTierQ();
        exp.checkQuantity();
        exp.checkFirst();
        Block result = exp.calculate();

        result.setPrev(open.getPrev());
        result.setNext(close.getNext());

        result = linkBlock(result);
        if (result.getPrev() != null && result.getPrev().getValue().equals("-")) {
            this.checkFirst();
        }
    }

    public Block calculate() {
        while (tierII != 0) {
            Block op = findTierII();
            if (op == null) return null;
            calculate(op);
            tierII--;
        }

        while ((tierI != 0)) {
            Block op = findTierI();
            if (op == null) return null;
            calculate(op);
            tierI--;
        }

        return first;
    }

    private void calculate(Block op) {
        Block b1 = op.getPrev();
        Block b2 = op.getNext();

        Double p1 = Double.valueOf(b1.getValue());
        Double p2 = Double.valueOf(b2.getValue());

        Double result = 0d;

        //System.out.println("Operation: " + p1 + " " + op.getValue() + " " + p2);

        switch (op.getValue()) {
            case "+":
                result = p1 + p2;
                break;
            case "-":
                result = p1 - p2;
                break;
            case "*":
                result = p1 * p2;
                break;
            case "/":
                result = p1 / p2;
                break;
        }

        //System.out.println("Result: " + result + "\n");

        Block newBlock = Block.builder()
                .type(BlockType.NUMBER)
                .prev(b1.getPrev())
                .next(b2.getNext())
                .value(result.toString())
                .build();

        linkBlock(newBlock);
    }

    private Block findTierII() {
        return getBlock(TIER_II);
    }

    private Block findTierI() {
        return getBlock(TIER_I);
    }

    private Block getBlock(String tier) {
        Block block = first.getNext();
        while (block != null) {
            if (block.getType().equals(BlockType.OPERATOR) && block.getValue().matches(tier)) {
                return block;
            } else {
                block = block.getNext();
            }
        }

        return null;
    }

    private Block linkBlock(Block block) {
        if (block.getNext() == null) {
            this.last = block;
        } else {
            block.getNext().setPrev(block);
        }
        if (block.getPrev() == null) {
            this.first = block;
        } else {
            block.getPrev().setNext(block);
        }

        return block;
    }
}