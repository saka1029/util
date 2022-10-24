package saka1029.util.calculator;

import java.util.HashMap;
import java.util.Map;

public class Calculator {

    Map<String, Expression> variables = new HashMap<>();
    
    void parse(String line) {
        line = line.replaceFirst("#.*", ""); // remove comment
        String[] statements = line.split(";");
    }
}
