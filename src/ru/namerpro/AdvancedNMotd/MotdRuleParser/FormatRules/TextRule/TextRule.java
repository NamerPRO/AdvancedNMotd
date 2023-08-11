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

package ru.namerpro.AdvancedNMotd.MotdRuleParser.FormatRules.TextRule;

import net.md_5.bungee.api.ChatColor;
import ru.namerpro.AdvancedNMotd.Configuration.ConfigurationManager;
import ru.namerpro.AdvancedNMotd.Extensions.Extension.API.Limits;
import ru.namerpro.AdvancedNMotd.Extensions.Extension.API.PlaceholderArguments;
import ru.namerpro.AdvancedNMotd.Extensions.Extension.API.RuleArguments;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.FormatRules.IFormatRule;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.FormatRules.IFormatRuleFactory;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.FormatRules.IPlaceholder;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.FormatRules.TextRule.Placeholders.*;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.RuleException;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.RuleParser;
import ru.namerpro.AdvancedNMotd.Universal.Pair;

import java.util.*;

public class TextRule implements IFormatRule {

    private final RuleArguments arguments;
    private final boolean isScreeningSmart;
    private String lineToParse;
    private int index;

    // Placeholder syntax: %placeholderName: placeholderArguments%
    private static final HashMap<String, IPlaceholder> placeholders = new HashMap<String, IPlaceholder>() {{
       put("online", new OnlinePlaceholder());
       put("maxPlayers", new MaximumPlayersPlaceholder());
       put("fakeOnline", new FakeOnlinePlaceholder());
       put("fakeMaxPlayers", new FakeMaximumPlayersPlaceholder());
       put("s", new SpacePlaceholder());
       put("color", new ColorPlaceholder());
       put("q", new SingleQuotePlaceholder());
       put("qq", new DoubleQuotePlaceholder());
    }};

    public static final class TextPlaceholders {

        public static void registerPlaceholder(String placeholderName, IPlaceholder placeholder) throws RuleException {
            if (placeholders.containsKey(placeholderName)) {
                throw new RuleException("Placeholder with name \"%" + placeholderName + "%\" already exists! Extensions must expand functionality, not modify the current one.");
            }
            placeholders.put(placeholderName, placeholder);
        }

    }

    public static final class TextRuleFactory implements IFormatRuleFactory {

        @Override
        public IFormatRule create(String lineToParse, RuleArguments ruleArguments, boolean isScreeningSmart) {
            return new TextRule(lineToParse, ruleArguments, isScreeningSmart);
        }

    }

    private TextRule(String lineToParse, RuleArguments ruleArguments, boolean isScreeningSmart) {
        this.lineToParse = lineToParse;
        this.arguments = ruleArguments;
        this.isScreeningSmart = isScreeningSmart;
    }

    private void formatPlaceholders() throws RuleException {
        char symbol;
        StringBuilder placeholderName = new StringBuilder();
        for (; index < lineToParse.length(); ++index) {
            symbol = lineToParse.charAt(index);
            if (symbol == '|') {
                if (index + 1 == lineToParse.length()) {
                    throw new RuleException("Unexpected line end after screening symbol! To display '|' you need to screen it like \"||\". Line: \"" + lineToParse + "\". Index: " + index + ".");
                }
                if (!isScreeningSmart) {
                    lineToParse = lineToParse.substring(0, index) + lineToParse.substring(index + 1);
                } else {
                    ++index;
                }
                continue;
            }
            if (symbol != '%') {
                continue;
            }
            int startIndex = index;
            if (index + 1 < lineToParse.length() && lineToParse.charAt(index + 1) == '#') {
                for (++index; index < lineToParse.length() && lineToParse.charAt(index) != '%'; ++index) {
                    if (lineToParse.charAt(index) == '|') {
                        ++index;
                    }
                }
                if (index >= lineToParse.length()) {
                    throw new RuleException("Unexpected line end! Expected '%' symbol, but end of line found. Line: \"" + lineToParse + "\". Index: " + startIndex + ".");
                }
                Pair<String, Integer> placeholderResponse = placeholders.get("color").apply(lineToParse, new Limits(startIndex, index), null, isScreeningSmart);
                index = placeholderResponse.getValue();
                lineToParse = placeholderResponse.getKey();
                continue;
            }
            for (++index; index < lineToParse.length() && lineToParse.charAt(index) != '%' && lineToParse.charAt(index) != ':'; ++index) {
                placeholderName.append(lineToParse.charAt(index));
            }
            if (index == lineToParse.length()) {
                throw new RuleException("Unexpected line end! Expected '%' or ':' symbol, but end of line found. Line: \"" + lineToParse + "\". Index: " + startIndex + ".");
            }
            ArrayList<String> arguments = new ArrayList<>();
            if (lineToParse.charAt(index) == ':') {
                StringBuilder argument = new StringBuilder(" ");
                for (++index; index < lineToParse.length() && lineToParse.charAt(index) != '%'; ++index) {
                    symbol = lineToParse.charAt(index);
                    if (symbol == '|') {
                        ++index;
                        if (index < lineToParse.length()) {
                            argument.append(lineToParse.charAt(index));
                        }
                        continue;
                    }
                    if (symbol == ',') {
                        String argumentToAdd = argument.toString().trim();
                        if (argumentToAdd.isEmpty()) {
                            throw new RuleException("Placeholder cannot have empty arguments! Line: \"" + lineToParse + "\". Index: " + index + ".");
                        }
                        arguments.add(argumentToAdd);
                        argument.setLength(0);
                        continue;
                    }
                    argument.append(symbol);
                }
                if (index >= lineToParse.length()) {
                    throw new RuleException("Unexpected line end found, but symbol '%' expected! Line: \"" + lineToParse + "\". Index: " + index + ".");
                }
                if (argument.length() != 0) {
                    String argumentToAdd = argument.toString().trim();
                    if (argumentToAdd.isEmpty()) {
                        throw new RuleException("Placeholder cannot have empty arguments! Line: \"" + lineToParse + "\". Index: " + index + ".");
                    }
                    arguments.add(argumentToAdd);
                }
            }
            String placeholderNameAsString = placeholderName.toString();
            IPlaceholder executor = placeholders.get(placeholderNameAsString);
            if (executor != null) {
                Pair<String, Integer> placeholderResponse = executor.apply(lineToParse, new Limits(startIndex, index), new PlaceholderArguments(arguments), isScreeningSmart);
                index = placeholderResponse.getValue();
                lineToParse = placeholderResponse.getKey();
            } else if (ConfigurationManager.placeholders.contains("Placeholders." + placeholderNameAsString)) {
                // We captured user created placeholder.
                if (!arguments.isEmpty()) {
                    // These placeholders cannot have arguments
                    throw new RuleException("Placeholder \"%" + placeholderNameAsString + "%\" cannot have arguments, but presented in line as \"" + lineToParse.substring(startIndex, index + 1) + "\". Line: \"" + lineToParse + "\". Index: " + startIndex + ".");
                }
                String parsedLine = lineToParse.substring(0, startIndex) + new RuleParser(ConfigurationManager.placeholders.getString("Placeholders." + placeholderNameAsString)).apply() + lineToParse.substring(index + 1);
                index = index - (lineToParse.length() - parsedLine.length());
                lineToParse = parsedLine;
            } else {
                throw new RuleException("Unknown placeholder \"" + lineToParse.substring(startIndex, index + 1) + "\" found! If you wanted to see such text, then screen '%' symbol with '|' symbol like this: \"|%\". Line: \"" + lineToParse + "\". Index: " + startIndex + ".");
            }
            placeholderName.setLength(0);
        }
    }

    @Override
    public String format() throws RuleException {
        if (arguments.getCount() != 0) {
            throw new RuleException("Format rule \"text\" does not support arguments, but \"" + arguments.getCount() + "\" were provided! Line: \"" + lineToParse + "\". Index: " + index + ".");
        }
        formatPlaceholders();
        return ChatColor.translateAlternateColorCodes('&', lineToParse);
    }

}
