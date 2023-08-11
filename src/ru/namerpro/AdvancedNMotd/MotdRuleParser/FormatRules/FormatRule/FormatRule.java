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

package ru.namerpro.AdvancedNMotd.MotdRuleParser.FormatRules.FormatRule;

import ru.namerpro.AdvancedNMotd.Extensions.Extension.API.RuleArguments;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.FormatRules.IFormatRule;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.FormatRules.IFormatRuleFactory;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.FormatRules.TextRule.TextRule;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.RuleException;

public class FormatRule implements IFormatRule {

    private final IFormatRule textRule;

    // Motd size in '|' symbols
    private static final int motdSize = 253;

    private final RuleArguments arguments;
    private final boolean isScreeningSmart;
    private String lineToParse;
    private int index;

    public static final class FormatRuleFactory implements IFormatRuleFactory {

        @Override
        public IFormatRule create(String lineToParse, RuleArguments ruleArguments, boolean isScreeningSmart) {
            return new FormatRule(lineToParse, ruleArguments, isScreeningSmart);
        }

    }

    private FormatRule(String lineToParse, RuleArguments ruleArguments, boolean isScreeningSmart) {
        this.arguments = ruleArguments;
        this.lineToParse = lineToParse;
        this.isScreeningSmart = isScreeningSmart;
        // isScreeningSmart set to true due to we have an ability remove screening symbols in this rule
        this.textRule = new TextRule.TextRuleFactory().create(lineToParse, RuleArguments.getEmptyArguments(), true);
    }

    private void parseLine() throws RuleException {
        char symbol;
        StringBuilder leftAlignText = new StringBuilder();
        StringBuilder rightAlignText = new StringBuilder();
        StringBuilder centerAlignText = new StringBuilder();
        for (; index < lineToParse.length(); ++index) {
            symbol = lineToParse.charAt(index);
            if (symbol != 'l' && symbol != 'c' && symbol != 'r') {
                continue;
            }
            if (index + 1 < lineToParse.length() && lineToParse.charAt(index + 1) != '{') {
                continue;
            }
            for (index += 2; index < lineToParse.length() && lineToParse.charAt(index) != '}'; ++index) {
                if (lineToParse.charAt(index) == '|') {
                    if (!isScreeningSmart) {
                        lineToParse = lineToParse.substring(0, index) + lineToParse.substring(index + 1);
                    } else {
                        ++index;
                    }
                }
                switch (symbol) {
                    case 'l':
                        leftAlignText.append(lineToParse.charAt(index));
                        break;
                    case 'r':
                        rightAlignText.append(lineToParse.charAt(index));
                        break;
                    case 'c':
                        centerAlignText.append(lineToParse.charAt(index));
                        break;
                }
            }
            if (index == lineToParse.length()) {
                throw new RuleException("Unexpected line end! Expected '}' symbol instead. Line: \"" + lineToParse + "\". Index: " + index + ".");
            }
        }
        // Previous blocks can affect next with their color codes (example: l{...&l} c{bold text})
        // It is important to avoid this and add '§r' at the END of each block, NOT at the beginning,
        // because if certain block ended with applied bold code, then size of spaces
        // between blocks will be 1 '|' symbol more (length 5), but we perform calculations
        // in so-called non-bold spaces (length 4).
        leftAlignText.append("§r");
        rightAlignText.append("§r");
        centerAlignText.append("§r");

        int centerTextIndent = (motdSize - FontData.getLength(centerAlignText.toString())) / 2;

        int unitsBeforeCenteredText;
        int unitsAfterCenteredText;
        if (centerAlignText.length() != 0) {
            unitsBeforeCenteredText = centerTextIndent - FontData.getLength(leftAlignText.toString());
            unitsAfterCenteredText = centerTextIndent - FontData.getLength(rightAlignText.toString());
        } else {
            unitsBeforeCenteredText = 0;
            unitsAfterCenteredText = motdSize - FontData.getLength(leftAlignText.toString()) - FontData.getLength(rightAlignText.toString());
        }

        int state = 0;
        while (state < unitsBeforeCenteredText) {
            leftAlignText.append(" ");
            state += FontData.spaceLength;
        }
        leftAlignText.append(centerAlignText);

        state = 0;
        while (state < unitsAfterCenteredText) {
            leftAlignText.append(" ");
            state += FontData.spaceLength;
        }
        leftAlignText.append(rightAlignText);

        lineToParse = leftAlignText.toString();
    }

    @Override
    public String format() throws RuleException {
        if (arguments.getCount() != 0) {
            throw new RuleException("Format rule \"format\" does not support arguments, but \"" + arguments.getCount() + "\" were provided! Line: \"" + lineToParse + "\". Index: " + index + ".");
        }
        lineToParse = textRule.format();
        parseLine();
        return lineToParse;
    }

}
