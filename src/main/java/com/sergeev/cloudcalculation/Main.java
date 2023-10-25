package com.sergeev.cloudcalculation;

import com.sergeev.cloudcalculation.server.CalculationService;

public class Main {

    public static void main(String[] args) {
        CalculationService service = new CalculationService();

        String str = "5+(-(3+3))";
        System.out.println(service.calculateFromString(str));
    }
}
