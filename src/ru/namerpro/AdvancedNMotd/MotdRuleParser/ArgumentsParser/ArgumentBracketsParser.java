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

package ru.namerpro.AdvancedNMotd.MotdRuleParser.ArgumentsParser;

import ru.namerpro.AdvancedNMotd.MotdRuleParser.*;

public final class ArgumentBracketsParser implements IBracketsParser {

    public static final class ArgumentBracketsFactory implements IBracketsParserFactory {

        @Override
        public IBracketsParser create(String lineToParse, int index, RuleParser.RuleParserData parserData) {
            return new ArgumentBracketsParser(lineToParse, index, parserData);
        }

    }

    private int index;
    private final String lineToParse;
    private final RuleParser.RuleParserData parserData;

    private ArgumentBracketsParser(String lineToParse, int index, RuleParser.RuleParserData parserData) {
        this.index = index;
        this.lineToParse = lineToParse;
        this.parserData = parserData;
    }

    @Override
    public void parse() throws RuleException {
        StringBuilder variableName = new StringBuilder();
        StringBuilder variableValue = new StringBuilder();
        for (++index; index < lineToParse.length() && lineToParse.charAt(index) != ']'; ++index) {
            char symbol = lineToParse.charAt(index);
            if (symbol == ' ') {
                if (variableName.length() != 0) {
                    parserData.arguments.put(variableName.toString(), null);
                    variableName.setLength(0);
                }
                continue;
            }
            if (symbol == '=') {
                if (variableName.length() == 0) {
                    throw new RuleException("Empty variable name when should not! Line: \"" + lineToParse + "\". Index: " + index + ". (most common reason: space before '=')");
                }
                ++index;
                if (index == lineToParse.length()) {
                    throw new RuleException("Expected '\"' symbol after '=' symbol, but end of line found!. Line: \"" + lineToParse + "\". Index: " + index + ".");
                }
                if (lineToParse.charAt(index) != '"') {
                    throw new RuleException("No opening quote found when should! Line: \"" + lineToParse + "\". Index: " + index + ". (most common reason: space after '=')");
                }
                for (++index; index < lineToParse.length() && lineToParse.charAt(index) != '"'; ++index) {
                    if (lineToParse.charAt(index) == '|') {
                        ++index;
                        if (index == lineToParse.length()) {
                            break;
                        }
                    }
                    variableValue.append(lineToParse.charAt(index));
                }
                if (index >= lineToParse.length()) {
                    throw new RuleException("Expected closing '\"', but end of line found!. Line: \"" + lineToParse + "\". Index: " + index + ".");
                }
                parserData.arguments.put(variableName.toString(), variableValue.toString());
                variableName.setLength(0);
                variableValue.setLength(0);
                continue;
            }
            if (variableName.length() == 0 && Utility.isNumeric(symbol)) {
                throw new RuleException("Rule variable names cannot start with numbers, but symbol '" + symbol + "' found! Line: \"" + lineToParse + "\". Index: " + lineToParse + ".");
            }
            if (!Utility.isEnglish(symbol) && !Utility.isNumeric(symbol)) {
                throw new RuleException("Rule variable names can only contain english characters and numbers, but symbol '" + symbol + "' found! Line: \"" + lineToParse + "\". Index: " + index + ".");
            }
            variableName.append(symbol);
        }
        if (index >= lineToParse.length()) {
            throw new RuleException("Rule arguments must end with ']' symbol, but end of line found! Line: \"" + lineToParse + "\". Index: " + index + ".");
        }
        if (variableName.length() != 0) {
            parserData.arguments.put(variableName.toString(), null);
        }
    }

}
