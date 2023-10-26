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
    // Operators "sqrt" and "^"
    private int tierIII = 0;
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
        } else if (block.getType().equals(BlockType.SQRT) || block.getType().equals(BlockType.POW)) {
            tierIII++;
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
            //If first block is "-", and second digit is negative, it changes the value...
            if (Double.parseDouble(first.getNext().getValue()) < 0) {
                Block block = first.getNext();
                double newValue = Double.parseDouble(first.getNext().getValue()) * -1; //...right here
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

    //Checking round brackets
    public void checkQuantity() throws Exception { //TODO normal exceptions
        if (tierQ == 0) return;
        if (tierQ % 2 != 0) throw new Exception("Problem with quantity");

        //Search for first "("
        Block open = this.first;
        while (!open.getType().equals(BlockType.QUANTITY)) {
            open = open.getNext();
        }
        if (open.getValue().equals(")")) throw new Exception("Problem with quantity: type 2");
        // If number before the round bracket - add new multiplier
        // Example: 5(2+2) = 20
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
        //If next block after "(" is ")" without expression - adds 0
        //Example: 5() = 5*(0) = 0
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

        int internal = 0; //Count internal quantities
        int count = 0; //Count blocks inside quantity
        //Parsing quantity
        while (!(close.getValue().equals(")") && internal == 0)) {
            if (close.getType().equals(BlockType.QUANTITY)) {
                if (close.getValue().equals("(")) internal++;
                else internal--;
            }
            close = close.getNext();
            count++;
        }
        Block block = open.getNext();
        //Create new expression for quantity
        Expression exp = new Expression();
        for (; count != 0; count--) {
            exp.add(block);
            block = block.getNext();
        }
        //Clean quantity from the main expression
        exp.getFirst().setPrev(null);
        exp.getLast().setNext(null);
        this.tierI -= exp.getTierI();
        this.tierII -= exp.getTierII();
        this.tierIII -= exp.getTierIII();
        this.tierQ -= exp.getTierQ();
        //Search for inside quantities
        exp.checkQuantity();
        //Check if there are real expression or just negative number
        exp.checkFirst();

        Block result = exp.calculate();
        //insert result of the quantity to the main expression
        result.setPrev(open.getPrev());
        result.setNext(close.getNext());
        result = linkBlock(result);
        //If quantity was negative - change to the opposite number
        if (result.getPrev() != null && result.getPrev().getValue().equals("-")) {
            this.checkFirst();
        }
    }

    //Calculate in queue: tierIII operators first, then tierII, tierI the last
    //If there are no operators - return the first Block, which is single in the expression
    public Block calculate() {
        while(tierIII != 0) {
            Block op = findTierIII();
            if (op == null) return null;
            if (op.getType().equals(BlockType.SQRT)) {
                calculateSqrt(op);
            } else {
                calculate(op);
            }
            tierIII--;
        }

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

    //Calculate two numbers and link it's result
    private void calculate(Block op) {
        Block b1 = op.getPrev();
        Block b2 = op.getNext();

        Double p1 = Double.valueOf(b1.getValue());
        Double p2 = Double.valueOf(b2.getValue());

        Double result = 0d;

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
            case "^":
                result = Math.pow(p1, p2);
        }

        Block newBlock = Block.builder()
                .type(BlockType.NUMBER)
                .prev(b1.getPrev())
                .next(b2.getNext())
                .value(result.toString())
                .build();

        linkBlock(newBlock);
    }

    //Find square root and link the result
    private void calculateSqrt(Block op) {
        StringBuilder sb = new StringBuilder();
        int i = 5;
        while(op.getValue().charAt(i) != ')') {
            sb.append(op.getValue().charAt(i));
            i++;
        }
        double num = Double.parseDouble(sb.toString());
        Block newBlock = Block.builder()
                .type(BlockType.NUMBER)
                .prev(op.getPrev())
                .next(op.getNext())
                .value(String.valueOf(Math.sqrt(num)))
                .build();
        linkBlock(newBlock);
    }

    //Search for the next available operators of tierI and tierII
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

    //Search for the next available operators of tierIII
    private Block findTierIII() {
        Block block = first;
        while (block != null) {
            if (block.getType().equals(BlockType.SQRT) || block.getType().equals(BlockType.POW)) {
                return block;
            }
            block = block.getNext();
        }
        return null;
    }

    //Link block into the expression
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