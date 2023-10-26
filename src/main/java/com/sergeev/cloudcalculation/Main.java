package com.sergeev.cloudcalculation;

import com.sergeev.cloudcalculation.server.CalculationService;

public class Main {

    //Testing
    public static void main(String[] args) {
        CalculationService service = new CalculationService();

        String str = "(2+3)^2-sqrt(25)";
        System.out.println(service.calculateFromString(str));
    }
}
