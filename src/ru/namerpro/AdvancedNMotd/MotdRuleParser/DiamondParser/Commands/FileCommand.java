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

import ru.namerpro.AdvancedNMotd.MotdRuleParser.DiamondParser.*;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.RuleParser;
import ru.namerpro.AdvancedNMotd.Universal.Pair;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class FileCommand implements IDiamondCommand {

    private final int index;
    private final String lineToParse;
    private final DiamondVariables variables;
    private final RuleParser.RuleParserData parserData;

    private FileCommand(int index, String lineToParse, DiamondVariables variables, RuleParser.RuleParserData parserData) {
        this.index = index;
        this.lineToParse = lineToParse;
        this.variables = variables;
        this.parserData = parserData;
    }

    public static final class FileCommandFactory implements ICommandFactory {

        @Override
        public IDiamondCommand create(int index, String lineToParse, DiamondVariables variables, RuleParser.RuleParserData parserData) {
            return new FileCommand(index, lineToParse, variables, parserData);
        }

    }

    @Override
    public Pair<String, Integer> execute() throws DiamondException {
        Pair<String, Integer> stringCommandResponse = new StringCommand.StringCommandFactory().create(index, lineToParse, variables, parserData).execute();
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(stringCommandResponse.getKey()));
            String fileLineToParse = new String(encoded, Charset.defaultCharset()).replaceAll(Pattern.quote(System.getProperty("line.separator")), "").trim();
            if (fileLineToParse.charAt(0) != '<' || fileLineToParse.charAt(fileLineToParse.length() - 1) != '>') {
                throw new DiamondException("File command inside file exception! File with diamond brackets must begin with '<' and end with '>', but provided one does not. Line: \"" + fileLineToParse + "\".");
            }
            parserData.inputLine = variables.getVariable("%this%");
            DiamondBracketsParser fileCommandParser = new DiamondBracketsParser(fileLineToParse, 0, variables, parserData);
            fileCommandParser.parse();
        } catch (StackOverflowError ignored) {
            throw new DiamondException("File command parsing exception! Detected infinite recursion. Check whether 2 files include each other somewhere or similar. Line: \"" + lineToParse + "\". Index: " + index + ".");
        } catch (IOException ignored) {
            throw new DiamondException("An error occurred while reading data from file at path: " + stringCommandResponse.getKey());
        }
        return new Pair<>(parserData.inputLine, stringCommandResponse.getValue());
    }

}
