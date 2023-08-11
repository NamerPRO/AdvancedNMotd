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
import ru.namerpro.AdvancedNMotd.MotdRuleParser.RuleException;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.RuleParser;
import ru.namerpro.AdvancedNMotd.Universal.Pair;

public class ParseCommand implements IDiamondCommand {

    private final int index;
    private final String lineToParse;
    private final DiamondVariables variables;
    private final RuleParser.RuleParserData parserData;

    private ParseCommand(int index, String lineToParse, DiamondVariables variables, RuleParser.RuleParserData parserData) {
        this.index = index;
        this.lineToParse = lineToParse;
        this.variables = variables;
        this.parserData = parserData;
    }

    public static final class ParseCommandFactory implements ICommandFactory {

        @Override
        public IDiamondCommand create(int index, String lineToParse, DiamondVariables variables, RuleParser.RuleParserData parserData) {
            return new ParseCommand(index, lineToParse, variables, parserData);
        }

    }

    @Override
    public Pair<String, Integer> execute() throws DiamondException {
        Pair<String, Integer> stringCommandResponse = new StringCommand.StringCommandFactory().create(index, lineToParse, variables, parserData).execute();
        RuleParser parseCommandParser = new RuleParser(stringCommandResponse.getKey());
        String commandResponse;
        try {
            commandResponse = parseCommandParser.apply();
        } catch (RuleException error) {
            throw new DiamondException("Parse command parsing exception! Parsing failed with reason: \"" + error.getMessage() + "\". Line: \"" + lineToParse + "\".");
        }
        return new Pair<>(commandResponse, stringCommandResponse.getValue());
    }

}
