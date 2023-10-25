package com.sergeev.cloudcalculation.controller;

import com.sergeev.cloudcalculation.model.ResultResponse;
import com.sergeev.cloudcalculation.server.CalculationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/calc/string")
@Slf4j
public class StringCalculatorController {

    private final CalculationService calculationService;

    @Autowired
    public StringCalculatorController(CalculationService calculationService) {
        this.calculationService = calculationService;
    }

    //Controller get string as URL parameter "calc"
    @PostMapping()
    public ResultResponse calculate(@RequestParam(name = "calc") String calc) {
        log.info("Request calculation for string: {}", calc);
        String response = calculationService.calculateFromString(calc);
        return new ResultResponse(response);
    }

}
