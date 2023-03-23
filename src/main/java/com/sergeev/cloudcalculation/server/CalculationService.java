package com.sergeev.cloudcalculation.server;

import com.sergeev.cloudcalculation.model.Node;
import com.sergeev.cloudcalculation.model.NodeList;
import com.sergeev.cloudcalculation.model.ResultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;

import static com.sergeev.cloudcalculation.util.Constants.OPERATORS;

@Service
@Slf4j
public class CalculationService {

    public ResultResponse calculateFromString(String calc) {
        //Split input string on single String array
        String[] symbols = calc.split("");
        //If string starts from negative value - add 0 as first element
        if (symbols[0].equals("-")) {
            String[] newArr = Arrays.copyOf(symbols, symbols.length + 1);
            newArr[0] = "0";
            System.arraycopy(symbols, 0, newArr, 1, symbols.length);
            symbols = newArr;
        }
        //Create NodeList from single String array
        NodeList list = packArray(symbols);
        //Calculating operations in list until there is only one node left. This node is a final result.
        while (list.getSize() != 1) {
            list.calculateNext();
        }

        log.info("The answer is {}", list.getAnswer().toString());
        //Return answer in simple json format
        return new ResultResponse(list.getAnswer().toString());
    }

    // Test code
//    public static void main(String[] args) {
//        String calc = "-5+4*2";
//        String[] symbols = calc.split("");
//
//        if (symbols[0].equals("-")) {
//            String[] newArr = Arrays.copyOf(symbols, symbols.length + 1);
//            newArr[0] = "0";
//            System.arraycopy(symbols, 0, newArr, 1, symbols.length);
//            symbols = newArr;
//        }
//
//        NodeList list = packArray(symbols);
//        System.out.println(list);
//
//        while (list.getSize() != 1) {
//            list.calculateNext();
//        }
//
//        System.out.println(list.getAnswer());
//    }

    private static NodeList packArray(String[] symbols) {
        NodeList list = new NodeList();

        for (int i = 0; i < symbols.length; i++) {
            if (OPERATORS.contains(symbols[i])) {
                list.add(new Node(true, symbols[i]));
            } else {
                StringBuilder s = new StringBuilder();
                int move = i;
                for (int j = i; (j < symbols.length && !OPERATORS.contains(symbols[j])); j++) {
                    s.append(symbols[j]);
                    move++;
                }
                i = move - 1;
                list.add(new Node(false, s.toString()));
            }
        }

        return list;
    }
}
