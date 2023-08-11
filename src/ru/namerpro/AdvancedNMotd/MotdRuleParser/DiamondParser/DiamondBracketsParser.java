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

package ru.namerpro.AdvancedNMotd.MotdRuleParser.DiamondParser;

import ru.namerpro.AdvancedNMotd.MotdRuleParser.DiamondParser.Commands.*;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.IBracketsParser;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.IBracketsParserFactory;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.RuleParser;
import ru.namerpro.AdvancedNMotd.Universal.Pair;

import java.util.HashMap;

public final class DiamondBracketsParser implements IBracketsParser {

    private static final HashMap<String, ICommandFactory> vocabulary = new HashMap<String, ICommandFactory>() {{
        put("return", new ReturnCommand.ReturnCommandFactory());
        put("parse", new ParseCommand.ParseCommandFactory());
        put("string", new StringCommand.StringCommandFactory());
        put("substring", new SubstringCommand.SubstringCommandFactory());
        put("file", new FileCommand.FileCommandFactory());
    }};

    private int index;
    private final String lineToParse;
    private final DiamondVariables variables;
    private final RuleParser.RuleParserData parserData;

    public static final class DiamondBracketsFactory implements IBracketsParserFactory {

        @Override
        public IBracketsParser create(String lineToParse, int index, RuleParser.RuleParserData parserData) {
            return new DiamondBracketsParser(lineToParse.trim(), index, parserData);
        }

    }

    private DiamondBracketsParser(String lineToParse, int index, RuleParser.RuleParserData parserData) {
        this(lineToParse, index, new DiamondVariables(), parserData);
    }

    public DiamondBracketsParser(String lineToParse, int index, DiamondVariables variables, RuleParser.RuleParserData parserData) {
        this.index = index;
        this.lineToParse = lineToParse;
        this.variables = variables;
        this.parserData = parserData;

        variables.setVariable("this", parserData.inputLine);
    }

    @Override
    public void parse() throws DiamondException {

        if (lineToParse.isEmpty()) {
            // NOT CRITICAL! If line is empty there is nothing to execute.
            return;
        }

        char symbol;
        StringBuilder command = new StringBuilder();

        while (lineToParse.charAt(++index) != '>') {

            if (skipSpaces()) {
                --index;
                continue;
            }

            if ((symbol = lineToParse.charAt(index)) == '>') {
                break;
            }

            while (symbol != ' ' && symbol != ';') {
                command.append(symbol);
                ++index;
                if (index == lineToParse.length()) {
                    throw new DiamondException("Expected either ';' or ' ' after command name! Line: \"" + lineToParse + "\". Index: " + index + ".");
                }
                symbol = lineToParse.charAt(index);
            }

            if (skipSpaces()) {
                throw new DiamondException("Expected either ';' or '=' or arguments after command name, but end of line found! Line: \"" + lineToParse + "\". Index: " + index + ".");
            }

            // Now lets call command.
            String token = command.toString();

            if (vocabulary.get(token) == null) {
                throw new DiamondException("Expected a command name, but \"" + command + "\" found! Line: \"" + lineToParse + "\". Index: " + index + ".");
            }

            Pair<String, Integer> commandResponse = vocabulary.get(token).create(index, lineToParse, variables, parserData).execute();

            index = commandResponse.getValue() + 1;

            skipSpaces();
            symbol = lineToParse.charAt(index);

            if (symbol != ';') {

                if (symbol != '=') {
                    throw new DiamondException("Expected symbol '=' when save command output to variable, but '" + symbol + "' found! If you do not want to save output to specific variable, place ';' at the end of the statement. Line: \"" + lineToParse + "\". Index: " + index + ".");
                }

                ++index;

                if (skipSpaces()) {
                    throw new DiamondException("Expected symbol '\"' when save command output to variable, but end of line found! Line: \"" + lineToParse + "\". Index: " + index + ".");
                }

                if (lineToParse.charAt(index++) != '"') {
                    throw new DiamondException("Expected symbol '\"' when save command output to variable, but '" + lineToParse.charAt(index - 1) + "' found! Line: \"" + lineToParse + "\". Index: " + index + ".");
                }

                StringBuilder variableName = new StringBuilder();

                while (index < lineToParse.length() && (symbol = lineToParse.charAt(index)) != '"') {
                    if (symbol == '|') {
                        index += 2;
                    }
                    variableName.append(symbol);
                    ++index;
                }

                if (symbol != '"') {
                    throw new DiamondException("Expected closing symbol '\"' when save command output to variable, but end of line found! Line: \"" + lineToParse + "\". Index: " + index + ".");
                }

                String variableNameStr = variableName.toString();

                if (variableName.charAt(0) != '_') {
                    throw new DiamondException("Expected variable name to start with '_' symbol to avoid possible conflicts with plugin/extensions based placeholders! Line: \"" + lineToParse + "\". Index: " + index + ".");
                }

                ++index;

                if (index >= lineToParse.length()) {
                    throw new DiamondException("Expected ';' at the end of a statement, but end of line found! Line \"" + lineToParse + "\". Index: " + index + ".");
                }

                if (lineToParse.charAt(index) != ';') {
                    throw new DiamondException("Expected ';' at the end of a statement, but symbol '" + lineToParse.charAt(index) + "' found! Line \"" + lineToParse + "\". Index: " + index + ".");
                }

                variables.setVariable(variableNameStr, commandResponse.getKey());
            } else {
                variables.setVariable("this", commandResponse.getKey());
            }

            command.setLength(0);
        }
    }

    private boolean skipSpaces() {
        while ((lineToParse.charAt(index) == ' ' || lineToParse.charAt(index) == '\t') && index + 1 != lineToParse.length()) {
            ++index;
        }
        return index + 1 == lineToParse.length();
    }

}
