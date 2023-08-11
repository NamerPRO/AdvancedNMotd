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

package ru.namerpro.AdvancedNMotd.MotdRuleParser.FormatRules.TextRule.Placeholders;

import net.md_5.bungee.api.ChatColor;
import ru.namerpro.AdvancedNMotd.Configuration.ConfigurationManager;
import ru.namerpro.AdvancedNMotd.Extensions.Extension.API.Limits;
import ru.namerpro.AdvancedNMotd.Extensions.Extension.API.PlaceholderArguments;
import ru.namerpro.AdvancedNMotd.Universal.Information;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.FormatRules.IPlaceholder;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.RuleException;
import ru.namerpro.AdvancedNMotd.Universal.Pair;
import ru.namerpro.AdvancedNMotd.Universal.UniversalActions;

import java.awt.*;
import java.util.ArrayList;

public class ColorPlaceholder implements IPlaceholder {

    private static final ArrayList<Pair<Color, ChatColor>> legacyColors = new ArrayList<Pair<Color, ChatColor>>() {{
        add(new Pair<>(new Color(0,0,0), ChatColor.BLACK));
        add(new Pair<>(new Color(0,0,170), ChatColor.DARK_BLUE));
        add(new Pair<>(new Color(0,170,0), ChatColor.DARK_GREEN));
        add(new Pair<>(new Color(0,170,170), ChatColor.DARK_AQUA));
        add(new Pair<>(new Color(170,0,0), ChatColor.DARK_RED));
        add(new Pair<>(new Color(170,0,170), ChatColor.DARK_PURPLE));
        add(new Pair<>(new Color(255,170,0), ChatColor.GOLD));
        add(new Pair<>(new Color(170,170,170), ChatColor.GRAY));
        add(new Pair<>(new Color(85,85,85), ChatColor.DARK_GRAY));
        add(new Pair<>(new Color(85,85,255), ChatColor.BLUE));
        add(new Pair<>(new Color(85,255,85), ChatColor.GREEN));
        add(new Pair<>(new Color(85,255,255), ChatColor.AQUA));
        add(new Pair<>(new Color(255,85,85), ChatColor.RED));
        add(new Pair<>(new Color(255,85,255), ChatColor.LIGHT_PURPLE));
        add(new Pair<>(new Color(255,255,85), ChatColor.YELLOW));
        add(new Pair<>(new Color(255,255,255), ChatColor.WHITE));
    }};

    @Override
    public Pair<String, Integer> apply(String lineToParse, Limits limits, PlaceholderArguments arguments, boolean isScreeningSmart) throws RuleException {
        if (arguments != null && arguments.getCount() != 1) {
            throw new RuleException("Placeholder \"%color: ...%\" must have one argument or its name must be omitted, but it is presented in string as \"" + lineToParse.substring(limits.start, limits.end + 1) + "\". Line: \"" + lineToParse + "\". Index: " + limits.start + ".");
        }
        int index = limits.start + (arguments == null ? 1 : 7);
        int clientVersion = UniversalActions.universalInstance.getClientVersion();
        StringBuilder fromColor = new StringBuilder();
        StringBuilder toColor = new StringBuilder();
        StringBuilder placeholderText = new StringBuilder();
        for (; index < lineToParse.length() && lineToParse.charAt(index) != '%' && lineToParse.charAt(index) != '-'; ++index) {
            fromColor.append(lineToParse.charAt(index));
        }
        if (index == lineToParse.length()) {
            throw new RuntimeException("Color placeholder must end with '%' symbol, but end of line found! Line: \"" + lineToParse + "\". Index: " + index + ".");
        }
        int startR, startG, startB;
        String fromColorAsString = fromColor.toString().trim();
        try {
            startR = Integer.valueOf(fromColorAsString.substring(1,3),16);
            startG = Integer.valueOf(fromColorAsString.substring(3,5),16);
            startB = Integer.valueOf(fromColorAsString.substring(5,7),16);
        } catch (StringIndexOutOfBoundsException | NumberFormatException ignored) {
            throw new RuleException("Wrong color code given! \"" + fromColorAsString + "\" is not a color. Line: \"" + lineToParse + "\". Index: " + index + ".");
        }
        if (lineToParse.charAt(index) == '%') {
            // Color placeholder found: %#COLOR%. Let's parse it.
            String parsedLine;
            if (clientVersion >= Information.minumalClientVersionThatSupportsColors && Information.areColorsSupportedByServer) {
                parsedLine = lineToParse.substring(0, limits.start) + ChatColor.of(fromColorAsString) + lineToParse.substring(limits.end + 1);
            } else {
                if (ConfigurationManager.config.getBoolean("AdvancedNMotd.Configuration.Downsample") && ConfigurationManager.downsample.contains("Downsample.Colors." + fromColorAsString)) {
                    parsedLine = lineToParse.substring(0, limits.start) + ConfigurationManager.downsample.getString("Downsample.Colors." + fromColorAsString) + lineToParse.substring(limits.end + 1);
                } else {
                    parsedLine = lineToParse.substring(0, limits.start) + getClosestLegacyColor(startR, startG, startB) + lineToParse.substring(limits.end + 1);
                }
            }
            return new Pair<>(parsedLine, limits.end - (lineToParse.length() - parsedLine.length()));
        }
        // If we reach this line, then we definitely deal with gradient placeholder.
        if (index + 1 == lineToParse.length() || lineToParse.charAt(++index) != '>') {
            throw new RuntimeException("Expected symbol '>' in gradient placeholder! Line: \"" + lineToParse + "\". Index: " + index + ".");
        }
        for (++index; index < lineToParse.length() && lineToParse.charAt(index) != ':'; ++index) {
            toColor.append(lineToParse.charAt(index));
        }
        if (index == lineToParse.length()) {
            throw new RuntimeException("Gradient placeholder must have syntax: %#fromColor->#toColor:text;colorCodes (optional)%! Line: \"" + lineToParse + "\". Index: " + index + ".");
        }
        if (index + 1 < lineToParse.length() && lineToParse.charAt(index + 1) == ' ') {
            ++index;
        }
        int symbolsToExclude = 0;
        for (++index; index < lineToParse.length() && lineToParse.charAt(index) != ';' && lineToParse.charAt(index) != '%'; ++index) {
            if (lineToParse.charAt(index) == '|') {
                ++symbolsToExclude;
                ++index;
                if (isScreeningSmart) {
                    placeholderText.append("|");
                }
                if (index == lineToParse.length()) {
                    throw new RuntimeException("Gradient placeholder must have syntax: %#fromColor->#toColor:text;colorCodes (optional)%! Line: \"" + lineToParse + "\". Index: " + index + ".");
                }
                placeholderText.append(lineToParse.charAt(index));
                continue;
            }
            placeholderText.append(lineToParse.charAt(index));
        }
        if (index >= lineToParse.length()) {
            throw new RuntimeException("Gradient placeholder must have syntax: %#fromColor->#toColor:text;colorCodes (optional)%! Line: \"" + lineToParse + "\". Index: " + index + ".");
        }
        ArrayList<Character> colorCodes = new ArrayList<>();
        if (lineToParse.charAt(index) == ';') {
            for (++index; index < lineToParse.length() && lineToParse.charAt(index) != '%'; ++index) {
                if (lineToParse.charAt(index) == ';' || lineToParse.charAt(index) == '&' || lineToParse.charAt(index) == ' ') {
                    continue;
                }
                if (index - 1 < 0 || lineToParse.charAt(index - 1) != '&') {
                    throw new RuleException("Expected '&' before color code in gradient placeholder! Line: \"" + lineToParse + "\". Index: " + index + ".");
                }
                colorCodes.add(lineToParse.charAt(index));
            }
            if (index == lineToParse.length()) {
                throw new RuleException("Expected ending '%' in gradient placeholder, but end of line found! Line: \"" + lineToParse + "\". Index: " + limits.start + ".");
            }
        }
        StringBuilder coloredPlaceholderText = new StringBuilder();
        int endR, endG, endB;
        try {
            endR = Integer.valueOf(toColor.substring(1,3),16);
            endG = Integer.valueOf(toColor.substring(3,5),16);
            endB = Integer.valueOf(toColor.substring(5,7),16);
        } catch (StringIndexOutOfBoundsException | NumberFormatException ignored) {
            throw new RuleException("Wrong color code given in end color in gradient placeholder! \"" + toColor + "\" is not a color. Line: \"" + lineToParse + "\". Index: " + index + ".");
        }
        if (clientVersion >= Information.minumalClientVersionThatSupportsColors && Information.areColorsSupportedByServer) {
            char[] gradientTextLetters = placeholderText.toString().toCharArray();
            double interval = 1.0 / (placeholderText.length() - (isScreeningSmart ? symbolsToExclude : 0));
            int characterIndex = 0;
            for(double step = 0; step < 1 && characterIndex < gradientTextLetters.length; step += interval) {
                int concreteR = (int) (startR * (1 - step) + endR * step);
                int concreteG = (int) (startG * (1 - step) + endG * step);
                int concreteB = (int) (startB * (1 - step) + endB * step);
                coloredPlaceholderText.append(net.md_5.bungee.api.ChatColor.of(String.format("#%02X%02X%02X", concreteR, concreteG, concreteB)));
                for (Character colorCode : colorCodes) {
                    coloredPlaceholderText.append("§").append(colorCode);
                }
                coloredPlaceholderText.append(gradientTextLetters[characterIndex]);
                if (gradientTextLetters[characterIndex] == '|' && isScreeningSmart) {
                    ++characterIndex;
                    if (characterIndex < gradientTextLetters.length) {
                        coloredPlaceholderText.append(gradientTextLetters[characterIndex]);
                    }
                }
                ++characterIndex;
            }
        } else {
            if (ConfigurationManager.config.getBoolean("AdvancedNMotd.Configuration.Downsample") && ConfigurationManager.downsample.contains("Downsample.Gradient." + fromColorAsString + "->" + toColor)) {
                coloredPlaceholderText.append(ConfigurationManager.downsample.getString("Downsample.Gradient." + fromColorAsString + "->" + toColor));
            } else {
                coloredPlaceholderText.append(getClosestLegacyColor(startR, startG, startB));
            }
            for (Character colorCode : colorCodes) {
                coloredPlaceholderText.append("&").append(colorCode);
            }
            coloredPlaceholderText.append(placeholderText);
        }
        String parsedLine = lineToParse.substring(0, limits.start) + coloredPlaceholderText + lineToParse.substring(limits.end + 1);
        return new Pair<>(parsedLine, limits.end - (lineToParse.length() - parsedLine.length()));
    }

    ChatColor getClosestLegacyColor(int startR, int startG, int startB) {
        int minimalDistance = Integer.MAX_VALUE;
        int minimalColorIndex = 0;
        for (int i = 0; i < legacyColors.size(); ++i) {
            Color color = legacyColors.get(i).getKey();
            int distance = (int) (Math.pow(color.getRed() - startR, 2) + Math.pow(color.getGreen() - startG, 2) + Math.pow(color.getBlue() - startB, 2));
            if (distance < minimalDistance) {
                minimalDistance = distance;
                minimalColorIndex = i;
            }
        }
        return legacyColors.get(minimalColorIndex).getValue();
    }

}
