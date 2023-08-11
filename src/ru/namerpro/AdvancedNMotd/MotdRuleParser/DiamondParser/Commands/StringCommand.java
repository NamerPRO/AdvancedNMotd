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
import ru.namerpro.AdvancedNMotd.MotdRuleParser.RuleParser;
import ru.namerpro.AdvancedNMotd.Universal.Pair;

public class StringCommand implements IDiamondCommand {

    private int index;
    private final String lineToParse;
    private final DiamondVariables variables;

    private StringCommand(int index, String lineToParse, DiamondVariables variables) {
        this.index = index;
        this.lineToParse = lineToParse;
        this.variables = variables;
    }

    public static final class StringCommandFactory implements ICommandFactory {

        @Override
        public IDiamondCommand create(int index, String lineToParse, DiamondVariables variables, RuleParser.RuleParserData parserData) {
            return new StringCommand(index, lineToParse, variables);
        }

    }

    @Override
    public Pair<String, Integer> execute() throws DiamondException {
        if (lineToParse.charAt(index) != '"') {
            throw new DiamondException("String command arguments syntax exception! Expected '\"', but '" + lineToParse.charAt(index) + "' found. Line: \"" + lineToParse + "\". Index: " + index + ".");
        }
        char symbol;
        boolean isScreened = false;
        StringBuilder string = new StringBuilder();
        for (++index; index < lineToParse.length(); ++index) {
            if ((symbol = lineToParse.charAt(index)) == '|' && !isScreened) {
                isScreened = true;
                continue;
            }
            if (symbol == '"' && !isScreened) {
                break;
            }
            if (symbol == '%' && !isScreened) {
                StringBuilder variableName = new StringBuilder("%");
                while (index + 1 < lineToParse.length() && (symbol = lineToParse.charAt(++index)) != '%') {
                    if (symbol == '|') {
                        ++index;
                        variableName.append(lineToParse.charAt(index));
                        continue;
                    }
                    variableName.append(symbol);
                }
                if (symbol != '%') {
                    throw new DiamondException("String command variable parsing exception! Could not find closing '%', but opening '%' exists. If you did not intent to get variable value, but wanted to place '%' symbol, screen it by placing '|' symbol before '%'. Line: \"" + lineToParse + "\". Index: " + index + ".");
                }
                variableName.append('%');
                string.append(variables.getVariable(variableName.toString()));
                continue;
            }
            isScreened = false;
            string.append(symbol);
        }
        if (index == lineToParse.length()) {
            throw new DiamondException("String command arguments syntax exception! Line: \"" + lineToParse + "\". Index: " + index + ".");
        }
        return new Pair<>(string.toString(), index);
    }

}
