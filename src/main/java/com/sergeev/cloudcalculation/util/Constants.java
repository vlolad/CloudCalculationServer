package com.sergeev.cloudcalculation.util;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Constants {

    //Supported operators
    public static final Set<String> OPERATORS = Stream.of("+", "-", "*", "/")
            .collect(Collectors.toCollection(HashSet::new));
}
