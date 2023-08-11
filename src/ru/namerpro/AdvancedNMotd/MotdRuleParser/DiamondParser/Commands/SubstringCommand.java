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

package ru.namerpro.AdvancedNMotd.MotdRuleParser.DiamondParser.Commands;

import ru.namerpro.AdvancedNMotd.MotdRuleParser.DiamondParser.DiamondException;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.DiamondParser.DiamondVariables;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.DiamondParser.ICommandFactory;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.DiamondParser.IDiamondCommand;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.FormatRules.CalcRule.CalculateException;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.FormatRules.CalcRule.Calculator;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.RuleParser;
import ru.namerpro.AdvancedNMotd.Universal.Pair;

public class SubstringCommand implements IDiamondCommand {

    private int index;
    private final String lineToParse;
    private final DiamondVariables variables;
    private final RuleParser.RuleParserData parserData;

    private SubstringCommand(int index, String lineToParse, DiamondVariables variable, RuleParser.RuleParserData parserData) {
        this.index = index;
        this.lineToParse = lineToParse;
        this.variables = variable;
        this.parserData = parserData;
    }

    public static final class SubstringCommandFactory implements ICommandFactory {

        @Override
        public IDiamondCommand create(int index, String lineToParse, DiamondVariables variables, RuleParser.RuleParserData parserData) {
            return new SubstringCommand(index, lineToParse, variables, parserData);
        }

    }

    @Override
    public Pair<String, Integer> execute() throws DiamondException {
        if (lineToParse.charAt(index) != '[') {
            throw new DiamondException("Substring command arguments syntax exception! Expected '[', but '" + lineToParse.charAt(index) + "' found. Line: \"" + lineToParse + "\". Index: " + index + ".");
        }
        StringBuilder leftLimit = new StringBuilder();
        StringBuilder rightLimit = new StringBuilder();
        StringBuilder contentToSubstring = new StringBuilder();
        int fillType = 0;
        char symbol;
        for (++index; index < lineToParse.length(); ++index) {
            if ((symbol = lineToParse.charAt(index)) == ']') {
                break;
            }
            if (symbol != ',') {
                if (fillType == 0) {
                    leftLimit.append(symbol);
                } else if (fillType == 1){
                    rightLimit.append(symbol);
                } else {
                    contentToSubstring.append(symbol);
                }
            } else {
                ++fillType;
            }
        }
        if (index == lineToParse.length()) {
            throw new DiamondException("Substring command arguments syntax exception! Line: \"" + lineToParse + "\". Index: " + index + ".");
        }
        Calculator calculator = new Calculator();
        int leftLimitAsInt;
        int rightLimitAsInt;
        try {
            leftLimitAsInt = Integer.parseInt(calculator.calculate(leftLimit.toString(), Calculator.ExpressionResultType.INTEGER));
            rightLimitAsInt = Integer.parseInt(calculator.calculate(rightLimit.toString(), Calculator.ExpressionResultType.INTEGER));
        } catch (CalculateException | NumberFormatException error) {
            throw new DiamondException("Failed to calculate segment bounds! Calculator returned exception: \"" + error.getMessage() + "\". Line: \"" + lineToParse + "\". Index: " + index + ".");
        }
        Pair<String, Integer> stringCommandResponse = new StringCommand.StringCommandFactory().create(0, contentToSubstring.toString().trim(), variables, parserData).execute();
        String commandResponse;
        try {
            commandResponse = stringCommandResponse.getKey().substring(leftLimitAsInt, rightLimitAsInt + 1);
        } catch (StringIndexOutOfBoundsException ignored) {
            throw new DiamondException("Cannot get a substring [" + leftLimitAsInt + ", " + rightLimitAsInt + "] from line \"" + stringCommandResponse.getKey() + "\", because index out of bounds! Line: \"" + lineToParse + "\". Index: " + index + ".");
        }
        return new Pair<>(commandResponse, index);
    }

}
