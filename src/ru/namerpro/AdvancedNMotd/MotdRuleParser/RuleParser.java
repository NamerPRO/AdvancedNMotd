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

package ru.namerpro.AdvancedNMotd.MotdRuleParser;

import ru.namerpro.AdvancedNMotd.Configuration.ConfigurationManager;
import ru.namerpro.AdvancedNMotd.Extensions.Extension.API.RuleArguments;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.ArgumentsParser.ArgumentBracketsParser;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.DiamondParser.DiamondBracketsParser;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.FormatRules.CalcRule.CalcRule;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.FormatRules.FormatRule.FormatRule;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.FormatRules.IFormatRuleFactory;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.FormatRules.TextRule.TextRule;
import ru.namerpro.AdvancedNMotd.Universal.Pair;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Objects;

public class RuleParser {

    // Creating brackets linker
    private static final HashMap<Character, IBracketsParserFactory> bracketsLinker = new HashMap<Character, IBracketsParserFactory>() {{
        put('<', new DiamondBracketsParser.DiamondBracketsFactory());
        put('[', new ArgumentBracketsParser.ArgumentBracketsFactory());
    }};

    // Creating rules linker
    public static final HashMap<String, IFormatRuleFactory> rulesLinker = new HashMap<String, IFormatRuleFactory>() {{
       put("text", new TextRule.TextRuleFactory());
       put("calc", new CalcRule.CalcRuleFactory());
       put("format", new FormatRule.FormatRuleFactory());
    }};

    // Map of closing symbols
    private static final HashMap<Character, Character> matcher = new HashMap<Character, Character>() {{
        put('<', '>');
        put('[', ']');
    }};

    private int index = 0;
    private String lineToParse;

    // Stores format rule data
    public class RuleParserData {

        public String inputLine;
        public HashMap<String, String> arguments = new HashMap<>();

        public RuleParserData(String inputLine) {
            this.inputLine = inputLine;
        }

    }

    public RuleParser(String lineToParse) {
        this.lineToParse = lineToParse;
    }

    public String apply() throws RuleException {

        // First lets collect information about string
        // and do some basic checks

        boolean isParsingSuccess = false;
        boolean isRuleNameEnded = false;
        char symbol;

        ArrayDeque<Pair<Character, Integer>> bracketsData = new ArrayDeque<>();
        StringBuilder formatRuleName = new StringBuilder();

        for (; index < lineToParse.length(); ++index) {
            symbol = lineToParse.charAt(index);

            // If we reached ':', parsing is finish
            if (symbol == ':') {
                isParsingSuccess = true;
                ++index;

                // Skip first space after ':' if it is in line.
                if (index < lineToParse.length() && lineToParse.charAt(index) == ' ') {
                    ++index;
                }

                break;
            }

            // Save name of format rule.
            if (!isRuleNameEnded && Utility.isEnglish(symbol)) {
                formatRuleName.append(symbol);
                continue;
            }

            // If we reached here, then we have finished getting name
            isRuleNameEnded = true;

            // Check whether lexeme is valid
            if (matcher.get(symbol) == null) {
                throw new RuleException("Unexpected lexeme '" + symbol + "' found! Line: \"" + lineToParse + "\". Index: " + index + ".");
            }

            // Collect lexeme
            bracketsData.offer(new Pair<>(symbol, index));

            // Skipping in content
            for (; index < lineToParse.length() && lineToParse.charAt(index) != matcher.get(symbol); ++index) {
                if (lineToParse.charAt(index) == '|') {
                    if (index + 1 == lineToParse.length()) {
                        throw new RuleException("Unexpected line end after screening symbol! To display '|' you need to screen it like \"||\". Line: \"" + lineToParse + "\". Index: " + index + ".");
                    }
                    if (lineToParse.charAt(index + 1) == matcher.get(symbol)) {
                        lineToParse = lineToParse.substring(0, index) + lineToParse.substring(index + 1);
                    }
                }
            }
        }

        if (!isParsingSuccess) {
            // ERROR!
            throw new RuleException("An error occurred while parsing a rule! Line: \"" + lineToParse + "\". Index: " + index + ".");
        }

        String initialText = "";
        if (index < lineToParse.length()) {
            // We have some initial text after ':', so we need to copy it.
            initialText = lineToParse.substring(index);
        }

        // Initialization of variable for diamond brackets with user's input
        RuleParserData parserData = new RuleParserData(initialText);

        // Now lets parse them all!
        Pair<Character, Integer> concreteBracketData;
        while ((concreteBracketData = bracketsData.poll()) != null) {
            IBracketsParser concreteParser = bracketsLinker.get(concreteBracketData.getKey()).create(lineToParse, concreteBracketData.getValue(), parserData);
            concreteParser.parse();
        }

        // Cheers! We have finished preprocessing string.
        // Let's invoke certain format rule code and pass it our result
        // if format rule was specified.
        String formatRuleNameAsString = formatRuleName.toString().trim().toLowerCase();

        // If format rule name was not specified we will apply "global" format rule that
        // does nothing to text and will not enter if. That's why we need such initialization.
        String formatRuleResponse = parserData.inputLine;
        if (!formatRuleNameAsString.isEmpty()) {
            IFormatRuleFactory formatRuleFactory = rulesLinker.get(formatRuleNameAsString);
            if (formatRuleFactory != null) {
                // Hurray! This is a standard format rule. Here is all simple.
                formatRuleResponse = formatRuleFactory.create(parserData.inputLine, new RuleArguments(parserData.arguments), false).format();
            } else if (ConfigurationManager.rules.contains("Rules." + formatRuleNameAsString)) {
                String customRule = Objects.requireNonNull(ConfigurationManager.rules.getString("Rules." + formatRuleNameAsString)).trim();
                if (customRule.charAt(0) != '<' || customRule.charAt(customRule.length() - 1) != '>') {
                    throw new RuleException("Custom rule needs to start with '<' and end with '>', but was presented in configuration as \"" + customRule + "\". Line: \"" + lineToParse + "\". Index: " + index + ".");
                }
                formatRuleResponse = new RuleParser(customRule + ": " + parserData.inputLine).apply();
            } else {
                throw new RuleException("Unknown format rule \"" + formatRuleNameAsString + "\" detected! Line: \"" + lineToParse + "\". Index: " + index + ".");
            }
        }

        // All done! String is ready to be displayed in motd.
        return formatRuleResponse;
    }

}
