/*
 * AdvancedNMotd - Bukkit / Bungeecord plugin that provides advanced ways to manage minecraft server motd
 * Copyright (C) 2023  NamerPRO
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package ru.namerpro.AdvancedNMotd.MotdRuleParser.FormatRules.CalcRule;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.regex.Pattern;

public class Calculator implements ICalculator {

    private static final Pattern DOUBLE_PATTERN = Pattern.compile(
            "[\\x00-\\x20]*[+-]?(NaN|Infinity|((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)" +
                    "([eE][+-]?(\\p{Digit}+))?)|(\\.((\\p{Digit}+))([eE][+-]?(\\p{Digit}+))?)|" +
                    "(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))" +
                    "[pP][+-]?(\\p{Digit}+)))[fFdD]?))[\\x00-\\x20]*");

    public enum ExpressionResultType {
        ADAPTABLE, // Returns result as integer if fractional part is 0 else as double
        INTEGER, // Discards the fractional part of the number
        DOUBLE, // Returns result with fractional part even if it is 0
        BOOLEAN // Returns 0 if calculation result equals to 0 else 1
    }

    private boolean canBeUnary(char symbol) {
        return symbol == '-' || symbol == '!' || symbol == '~';
    }

    private boolean isPartOfArithmetic(char symbol) {
        char[] symbols = new char[] { '+', '-', '*', '/', '%', '>', '<', '=', '&', '|', '^' };
        for (char concreteSymbol : symbols) {
            if (concreteSymbol == symbol) {
                return true;
            }
        }
        return false;
    }

    private boolean isLeftAssociative(String operator) {
        String[] operators = new String[] { "+", "-", "*", "/", "%", ">", "<", "=", ">=", "<=", "&",
                                            "|", "&&", "||", ">>", "<<", "!=", "^", "//" };
        for (String concreteOperator : operators) {
            if (concreteOperator.equals(operator)) {
                return true;
            }
        }
        return false;
    }

    private boolean isArithmetic(String operator) {
        String[] operators = new String[] { "+", "-", "*", "/", "%", ">", "<", "=", ">=", "<=", "&",
                                            "|", "&&", "||", ">>", "<<", "!=", "^", "//" };
        for (String concreteOperator : operators) {
            if (concreteOperator.equals(operator)) {
                return true;
            }
        }
        return false;
    }

    private int getPriority(String symbol) throws CalculateException {
        HashMap<String, Integer> priorityLinker = new HashMap<String, Integer>() {{
            put("||", 1); put("&&", 2); put("|", 3); put("^", 4); put("&", 5);
            put("!=", 6); put("=", 6); put(">=", 7); put(">", 7); put("<=", 7);
            put("<", 7); put("<<", 8); put(">>", 8); put("+", 9); put("-", 9);
            put("%", 10); put("/", 10); put("*", 10); put("//", 10);
        }};
        if (!priorityLinker.containsKey(symbol)) {
            throw new CalculateException("Unexpected value: " + symbol);
        }
        return priorityLinker.get(symbol);
    }

    private boolean isEnglish(char symbol) {
        return (symbol >= 'a' && symbol <= 'z') || (symbol >= 'A' && symbol <= 'Z');
    }

    private boolean isEnglishString(String str) {
        if (str.isEmpty()) {
            return false;
        }
        for (char symbol : str.toCharArray()) {
            if (!isEnglish(symbol)) {
                return false;
            }
        }
        return true;
    }

    private boolean isInteger(char symbol) {
        return symbol >= '0' && symbol <= '9';
    }

    private double evaluate(double a, double b, String operation) throws CalculateException {
        if ((operation.equals("/") || operation.equals("%")) && b == 0) {
            throw new CalculateException("Division by zero is not allowed!");
        }
        if (operation.equals("&") || operation.equals("|") || operation.equals(">>") ||
                operation.equals("<<") || operation.equals("//") || operation.equals("%")) {
            if ((int) a != a || (int) b != b) {
                throw new CalculateException("Operator '" + operation + "' requires integer operands!");
            }
        }
        switch (operation) {
            case "+":
                return a + b;
            case "-":
                return a - b;
            case "*":
                return a * b;
            case "/":
                return a / b;
            case "//":
                return (int) a / (int) b;
            case ">":
                return (a > b) ? 1.0 : 0.0;
            case ">=":
                return (a >= b) ? 1.0 : 0.0;
            case "<":
                return (a < b) ? 1.0 : 0.0;
            case "<=":
                return (a <= b) ? 1.0 : 0.0;
            case "&":
                return (int) a & (int) b;
            case "|":
                return (int) a | (int) b;
            case "^":
                return (int) a ^ (int) b;
            case "&&":
                return (a == 1) ? (b == 1) ? 1 : 0 : 0;
            case "||":
                return (a == 0) ? (b == 0) ? 0 : 1 : 1;
            case "%":
                return (int) a % (int) b;
            case ">>":
                return (int) a >> (int) b;
            case "<<":
                return (int) a << (int) b;
            case "=":
                return (a == b) ? 1.0 : 0.0;
            case "!=":
                return (a != b) ? 1.0 : 0.0;
            default:
                throw new CalculateException("Unexpected operation '" + operation + "'! Allowed ones are '+', '-', '*', '/'");
        }
    }

    private boolean isDoubleString(String str) {
        return DOUBLE_PATTERN.matcher(str).matches();
    }

    @Override
    public String calculate(String expression, ExpressionResultType type) throws CalculateException {
        expression = expression.trim();
        //Empty expression is not allowed
        if (expression.length() == 0) {
            throw new CalculateException("Empty expression is not allowed!");
        }
        StringBuilder operand = new StringBuilder(); //number
        StringBuilder operator = new StringBuilder(); //plus minus
        boolean isOperator = false; //Even if we have unary operator, this needs to be false for synchronization
        boolean isOperatorUnary = canBeUnary(expression.charAt(0)); //If we have unary operator flag it
        boolean metSeparator = false;
        Deque<String> operatorsStack = new ArrayDeque<>();
        Deque<Double> operandsStack = new ArrayDeque<>();
        Deque<Character> unaryOperationsStack = new ArrayDeque<>();
        for (int i = 0; i < expression.length(); ++i) {
            char symbol = expression.charAt(i);
            //Space contains no meaning, so let's skip it
            if (symbol == ' ') {
                //Only thing we should not forget to do is toggle separator flag
                if (operand.length() != 0 || operator.length() != 0) {
                    metSeparator = true;
                }
                continue;
            }
            //If there is '(' then lets always add it to stack of operators
            //Important to have this if before next one!
            //Example why: 1+(-2) //will work only is isOperatorUnary remains true
            if (symbol == '(' && operator.length() == 0) {
                operatorsStack.push(String.valueOf(symbol));
                continue;
            }
            //If there is a possibility of having a unary operator
            if (isOperatorUnary) {
                //If it sure is unary operator
                if (canBeUnary(symbol)) {
                    //Let's add it to unary operators stack
                    unaryOperationsStack.push(symbol);
                    continue;
                }
                //If we meet not unary operator when only
                //unary ones are allowed signal
                if (isPartOfArithmetic(symbol)) {
                    throw new CalculateException("Unexpected occurrence '" + symbol + "' found! Expected unary operator.");
                }
                //Else it is not a unary operator, and we do not
                //expect one next for now.
                isOperatorUnary = false;
                //Let's handle what we have (no continue!)
            }
            //If symbol is ')' then lets push everything out of stack
            //till we get '(' that will be also popped out
            if (symbol == ')' && operand.length() == 0) {
                //If operator buffer is not empty, then expression has
                //an operator right before closing bracket what is wrong
                //Example: (1 + ) + 3
                if (operator.length() != 0) {
                    throw new CalculateException("Unfinished expression found. Expression cannot finish with '" + operator + "'.");
                }
                while (!operatorsStack.isEmpty() && !operatorsStack.getFirst().equals("(")) {
                    String op = operatorsStack.pop();
                    double argument1 = operandsStack.pop();
                    if (operandsStack.isEmpty()) {
                        throw new CalculateException("Stack of numbers is empty when should not! Check if expression is correct.");
                    }
                    double argument2 = operandsStack.pop();
                    operandsStack.push(evaluate(argument2, argument1, op));
                }
                if (operatorsStack.isEmpty()) {
                    throw new CalculateException("Empty operation stack when should not found! Check if expression is correct. Seems like you missed '(' somewhere.");
                }
                operatorsStack.pop();

                operator.setLength(0);
                operand.setLength(0);
                metSeparator = false;

                continue;
            }
            if (isOperator) {
                //If symbol can be a part of operator
                //But:
                //If we meet '-', '!' or '~' when buffer is not empty, then these are unary
                //operations as there is no operation of size > 1 with '-', '!' or '~'
                if (metSeparator && isPartOfArithmetic(symbol) && !canBeUnary(symbol)) {
                    throw new CalculateException("Unexpected occurrence! Expected to see unary operator or operand, but symbol '" + symbol + "' found.");
                }
                if (isPartOfArithmetic(symbol) && !(canBeUnary(symbol) && operator.length() > 0)) {
                    //And length we have is not bigger, then maximum length of operator
                    if (operator.length() > 1) {
                        throw new CalculateException("Unexpected operator found! Operator length is no more than 2, but length 3 already found.");
                    }
                    //Let's add this symbol to operator string
                    operator.append(symbol);
                    //And move to next symbol
                    continue;
                }
                //If we skipped if above because of wrong character in expression signal!
                if (!isInteger(symbol) && !isEnglish(symbol) && symbol != '(' && symbol != '.' && !canBeUnary(symbol)) {
                    throw new CalculateException("Unexpected symbol '" + symbol + "' found! Expected number or opening bracket or space.");
                }
                //If symbol we have does not belong to operator, check whether we have is operator
                if (!isArithmetic(operator.toString())) {
                    throw new CalculateException("Expected operator but '" + operator + "' found!");
                }
                //We have valid operator. Let's execute it, we need to move backwards,
                //so we do not miss possible meaningful symbol of expression
                --i;
            } else {
                //If we met separator on our way but still here, signal
                if (metSeparator && !isPartOfArithmetic(symbol) && symbol != ')') {
                    throw new CalculateException("Unexpected occurrence! Expected to see operator, but symbol '" + symbol + "' found.");
                }
                //We may have a valid symbol. Collecting it.
                if (isEnglish(symbol) || isInteger(symbol) || symbol == '.') {
                    operand.append(symbol);
                    continue;
                }
                //If we skipped if above because of unsupported character signal!
                if (!isPartOfArithmetic(symbol) && symbol != ')') {
                    throw new CalculateException("Unexpected symbol '" + symbol + "' found! Expected binary operation or space.");
                }
                //If we have neither number nor variable, this is an error
                if (!isDoubleString(operand.toString()) && !isEnglishString(operand.toString())) {
                    throw new CalculateException("Expected operand but '" + operand + "' found!");
                }
                //We have valid operand. Let's execute it, we need to move backwards
                //so we do not miss possible meaningful symbol of expression
                --i;
            }
            //If we reach this part then we either have VALID operator or operand.
            //(valid means no violations in variable name or number format)
            //We also have isOperator flag that determines whether we have operator or operand
            //Let's use Dijkstra's algorithm to evaluate the expression.

            //If we have a number let's apply
            //its unary operators to it (functions will be added later)
            if (!isOperator) {
                double value; //Result of stated above this if will be stored here
                //Check if operand is a number
                if (isDoubleString(operand.toString())) {
                    value = Double.parseDouble(operand.toString());
                } else {
                    throw new CalculateException("Unexpected occurrence '" + operand + "' found!");
                }
                //After we got a value we can apply unary operations to it
                while (!unaryOperationsStack.isEmpty()) {
                    switch (unaryOperationsStack.pop()) {
                        case '-':
                            value = -value;
                            break;
                        case '!':
                            value = (value == 0) ? 1.0 : 0.0;
                            break;
                        case '~':
                            //This operation only supports integer argument
                            if ((int) value != value) {
                                throw new CalculateException("Operator '~' can only be applied to integer argument, but double '" + operand + "' found!");
                            }
                            value = ~(int) value;
                            break;
                        //Stack of unary operations cannot contain anything but unary operations, so default branch is useless
                    }
                }
                //We got a value that needs to be pushed on operands stack
                operandsStack.push(value);
            } else {
                //Here we handle operators. If operators stack has '(' at its top push operator to it
                if (operatorsStack.isEmpty() || operatorsStack.getFirst().equals("(")) {
                    operatorsStack.push(operator.toString());

                    //Update settings (more explanation at the bottom of method)
                    isOperatorUnary = true;
                    isOperator = false;
                    operator.setLength(0);
                    metSeparator = false;

                    continue;
                }
                //Else let's do calculations according to algorithm
                if (isLeftAssociative(operator.toString())) {
                    while (!operatorsStack.isEmpty() && !operatorsStack.getFirst().equals("(") && getPriority(operator.toString()) <= getPriority(operatorsStack.getFirst())) {
                        String op = operatorsStack.pop();
                        double argument1 = operandsStack.pop();
                        if (operandsStack.isEmpty()) {
                            throw new CalculateException("Stack of numbers is empty when should not! Check if expression is correct.");
                        }
                        double argument2 = operandsStack.pop();
                        operandsStack.push(evaluate(argument2, argument1, op));
                    }
                    operatorsStack.push(operator.toString());
                } else {
                    while (!operatorsStack.isEmpty() && !operatorsStack.getFirst().equals("(") && getPriority(operator.toString()) < getPriority(operatorsStack.getFirst())) {
                        String op = operatorsStack.pop();
                        double argument1 = operandsStack.pop();
                        if (operandsStack.isEmpty()) {
                            throw new CalculateException("Stack of numbers is empty when should not! Check if expression is correct.");
                        }
                        double argument2 = operandsStack.pop();
                        operandsStack.push(evaluate(argument2, argument1, op));
                    }
                    operatorsStack.push(operator.toString());
                }
            }

            //Last thing to do is update parameters as done below

            //If we saw an operator this time then if we see it
            //again just next it can only be unary or an expression mistake
            isOperatorUnary = isOperator;
            //Toggle expectations
            isOperator = !isOperator;
            //Set operator and operand to defaults (make both empty)
            operator.setLength(0);
            operand.setLength(0);
            metSeparator = false;
        }
        //If we have an operator in memory after finished loop
        //then expression has error as correct ones cannot end with operators
        if (operator.length() != 0) {
            throw new CalculateException("Unfinished expression found. Expression cannot finish with '" + operator + "'.");
        }
        //If we did not add operand because string finished, add it
        if (operand.length() != 0) {
            //Code exactly same as in loop (commented there)
            double value;
            if (isDoubleString(operand.toString())) {
                value = Double.parseDouble(operand.toString());
            } else {
                throw new CalculateException("Unexpected occurrence '" + operand + "' found!");
            }
            while (!unaryOperationsStack.isEmpty()) {
                switch (unaryOperationsStack.pop()) {
                    case '-':
                        value = -value;
                        break;
                    case '!':
                        value = (value == 0) ? 1.0 : 0.0;
                        break;
                    case '~':
                        if ((int) value != value) {
                            throw new CalculateException("Operator '~' can only be applied to integer argument, but double '" + operand + "' found!");
                        }
                        value = ~(int) value;
                        break;
                }
            }
            operandsStack.push(value);
        }
        //When there is nothing in a string
        //If unary operations stack is not empty this is error
        if (!unaryOperationsStack.isEmpty()) {
            throw new CalculateException("Unapplied unary operator found! Check expression.");
        }
        //Let's pop everything out of stacks
        while (!operatorsStack.isEmpty()) {
            String op = operatorsStack.pop();
            if (op.equals("(")) {
                throw new CalculateException("Unexpected extra '(' symbol found when should not! Check if expression is correct. You seem to forget ')' somewhere.");
            }
            double argument1 = operandsStack.pop();
            if (operandsStack.isEmpty()) {
                throw new CalculateException("Stack of numbers is empty when should not! Check if expression is correct.");
            }
            double argument2 = operandsStack.pop();
            operandsStack.push(evaluate(argument2, argument1, op));
        }
        //Let's return response as it was requested
        switch (type) {
            case ADAPTABLE:
                if (operandsStack.getFirst() == operandsStack.getFirst().intValue()) {
                    return String.valueOf(operandsStack.getFirst().intValue());
                } else {
                    return String.valueOf(operandsStack.getFirst());
                }
            case INTEGER:
                return String.valueOf(operandsStack.getFirst().intValue());
            case DOUBLE:
                return String.valueOf(operandsStack.getFirst());
            case BOOLEAN:
                if (operandsStack.getFirst() == 0) {
                    return "0";
                } else {
                    return "1";
                }
            default:
                throw new CalculateException("Unexpected type '" + type + "' found! Check syntax.");
        }
    }

}
