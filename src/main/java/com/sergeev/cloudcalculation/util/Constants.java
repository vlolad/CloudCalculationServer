package com.sergeev.cloudcalculation.util;

import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Constants {

    public static final String IS_DIGIT = "\\d+";
    public static final String TIER_I = "[+#-]+";
    public static final String TIER_II = "[*#/]+";
    public static final HashSet<String> OPERATIONS = Stream.of("+", "-", "*", "/")
            .collect(Collectors.toCollection(HashSet::new));
    public static final HashSet<String> QUANTITY = Stream.of("(", ")")
            .collect(Collectors.toCollection(HashSet::new));
}
