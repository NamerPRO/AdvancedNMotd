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

import ru.namerpro.AdvancedNMotd.Extensions.Extension.API.RuleArguments;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.FormatRules.IFormatRule;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.FormatRules.IFormatRuleFactory;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.FormatRules.TextRule.TextRule;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.RuleException;

public class CalcRule implements IFormatRule {

    private final IFormatRule textRule;

    private final RuleArguments arguments;
    private String lineToParse;

    public static final class CalcRuleFactory implements IFormatRuleFactory {

        @Override
        public IFormatRule create(String lineToParse, RuleArguments ruleArguments, boolean isScreeningSmart) {
            return new CalcRule(lineToParse, ruleArguments);
        }

    }

    private CalcRule(String lineToParse, RuleArguments ruleArguments) {
        this.textRule = new TextRule.TextRuleFactory().create(lineToParse, RuleArguments.getEmptyArguments(), false);
        this.lineToParse = lineToParse;
        this.arguments = ruleArguments;
    }

    @Override
    public String format() throws RuleException {
        int index = 0;

        if (arguments.getCount() > 1) {
            throw new RuleException("Format rule \"calc\" cannot have more than one argument! Line: \"" + lineToParse + "\". Index: " + index + ".");
        }

        lineToParse = textRule.format(); // First let's apply text rule

        // Now we can do calculations
        ICalculator calculator = new Calculator();

        String resultTypeAsString;
        if (!arguments.hasArgument("returnType")) {
            if (arguments.getCount() != 0) {
                throw new RuleException("Format rule \"calc\" can only contain one argument \"returnType\", but it has another argument instead. Line: \"" + lineToParse + "\". Index: " + index + ".");
            } else {
                resultTypeAsString = "adaptable";
            }
        } else {
            resultTypeAsString = arguments.getArgumentValue("returnType");
        }

        Calculator.ExpressionResultType resultType;
        switch (resultTypeAsString) {
            case "adaptable":
                resultType = Calculator.ExpressionResultType.ADAPTABLE;
                break;
            case "integer":
                resultType = Calculator.ExpressionResultType.INTEGER;
                break;
            case "double":
                resultType = Calculator.ExpressionResultType.DOUBLE;
                break;
            case "boolean":
                resultType = Calculator.ExpressionResultType.BOOLEAN;
                break;
            default:
                throw new RuleException("Unexpected return type found! Expected \"adaptable\", \"integer\", \"double\" or \"boolean\", but \"" + resultTypeAsString + "\" found. Line: \"" + lineToParse + "\". Index: " + index + ".");
        }

        try {
            return calculator.calculate(lineToParse, resultType);
        } catch (CalculateException error) {
            throw new RuleException("Calculation failed with reason: \"" + error.getMessage() + "\". Line: \"" + lineToParse + "\". Index: " + index + ".");
        }
    }

}
