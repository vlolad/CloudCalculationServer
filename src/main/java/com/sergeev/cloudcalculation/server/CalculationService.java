package com.sergeev.cloudcalculation.server;

import com.sergeev.cloudcalculation.model.Block;
import com.sergeev.cloudcalculation.model.Expression;
import com.sergeev.cloudcalculation.util.BlockType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.sergeev.cloudcalculation.util.Constants.*;

@Service
@Slf4j
public class CalculationService {

    public String calculateFromString(String calc) {
        return calculateExpression(parseLine(calc));
    }

    private String calculateExpression(Expression exp) {
        //check for round brackets first and calculate them
        try { //TODO custom exception
            exp.checkQuantity();
        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }

        exp.checkFirst(); //If first Block is "-", its transforms into Number-Block
        Block result = exp.calculate();

        return result.getValue();
    }

    private Expression parseLine(String line) {
        String[] lines = line.split("");
        int i;
        StringBuilder sb;
        Expression exp = new Expression();

        for (i = 0; i < lines.length; i++) {
            if (OPERATIONS.contains(lines[i])) {
                exp.add(new Block(BlockType.OPERATOR, lines[i]));
            } else if (QUANTITY.contains(lines[i])) {
                exp.add(new Block(BlockType.QUANTITY, lines[i]));
            } else {
                sb = new StringBuilder();
                int move = i;
                for (int j = i; j < lines.length && lines[j].matches(IS_DIGIT); j++) {
                    sb.append(lines[j]);
                    move++;
                }
                i = --move;

                exp.add(new Block(BlockType.NUMBER, sb.toString()));
            }
        }

        return exp;
    }
}
