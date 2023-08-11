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

import net.md_5.bungee.api.ChatColor;
import ru.namerpro.AdvancedNMotd.Configuration.ConfigurationManager;
import ru.namerpro.AdvancedNMotd.Configuration.IConfiguration;
import ru.namerpro.AdvancedNMotd.Universal.Information;
import ru.namerpro.AdvancedNMotd.Universal.UniversalActions;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

// Stated below assumes standard minecraft resource pack
public class FontData {

	// Number next to character is character length in '|' symbols
	// HashMap should only have symbols which length is different to 5 since most symbols length is 5
	// Current map has data for every symbol on english 'qwerty' keyboard
	private static final HashMap<Character, Integer> defaultCharacterLinker = new HashMap<Character, Integer>() {{
		put('f', 4); put('I', 3); put('i', 1); put('k', 4); put('l', 2); put('t', 3);
		put('!', 1); put('@', 6); put('*', 3); put('(', 3); put(')', 3); put('{', 3);
		put('}', 3); put('[', 3); put(']', 3); put(':', 1); put(';', 1); put('"', 3);
		put('\'', 1); put('<', 4); put('>', 4); put('|', 1); put('~', 6); put('№', 9);
		put('`', 2); put('.', 1); put(',', 1); put(' ', 4); // In some amount of minecraft releases size of space is 2
	}};

	private static final HashMap<Character, Double> italicCharacterLinker = new HashMap<Character, Double>() {{

	}};

	public static final int spaceLength = getLength(" ");

	// Every bold character is always one '|' symbol longer than its non-bold equivalent
	// (except space: in some amount of minecraft releases it has same length)
	private static int getBoldFontInfo(char character) {
		return getDefaultFontInfo(character) + 1;
	}

	private static int getDefaultFontInfo(char character) {
		// We consider unspecified character to be of length 5.
		// If that's not true, user must create language file in 'languages' folder
		return defaultCharacterLinker.getOrDefault(character, 5);
	}

	// Get line length in '|' symbols
	public static int getLength(String line) {
		boolean isCode = false;
		boolean isBold = false;
		int stringSize = 0;
		for (char symbol : line.toCharArray()) {
			// Every '§' symbol is hidden in motd
			if (symbol == '§') {
				isCode = true;
				continue;
			}
			if (isCode && symbol == 'l') {
				isBold = true;
				isCode = false;
				continue;
			}
			// Any of these codes removes bold style from text
			if (isCode && ((symbol >= 'a' && symbol <= 'f') || (symbol >= 'A' && symbol <= 'F') || symbol == 'r' || (symbol >= '0' && symbol <= '9'))) {
				isBold = isCode = false;
				continue;
			}
			// Every symbol (no matter whether its right code or not) is hidden if it goes after '§' symbol in motd
			if (!isCode) {
				// After each symbol, but ' ' symbol goes a little indent length exactly 1 '|' symbol.
				stringSize += (isBold ? getBoldFontInfo(symbol) : getDefaultFontInfo(symbol)) + (symbol == ' ' ? 0 : 1);
			}
			isCode = false;
		}
		// We don't need to calculate indent after last symbol, so we need to subtract it if we counted it.
		return stringSize - (line.length() != 0 && line.charAt(line.length() - 1) != ' ' ? 1 : 0);
	}
	
	public static void loadLanguages() throws IOException {
		String languagesFolder = Information.resourceFolderPath + "/languages";
		File[] files = new File(languagesFolder).listFiles((dir, name) -> name.toLowerCase().endsWith(".yml"));
		if (files == null || files.length == 0) {
			return;
		}
		UniversalActions.universalInstance.sendConsoleMessage(ChatColor.GREEN + "AdvancedNMotd: Loading custom languages...");
		for (File concreteFile : files) {
			UniversalActions.universalInstance.sendConsoleMessage(ChatColor.GREEN + "AdvancedNMotd: Loading file \"" + concreteFile.getName() + "\".");
			IConfiguration languageConfiguration = ConfigurationManager.buildConfiguration(concreteFile);
			for (String key : languageConfiguration.getKeys()) {
				defaultCharacterLinker.put(key.charAt(0), languageConfiguration.getInt(key));
			}
		}
		UniversalActions.universalInstance.sendConsoleMessage(ChatColor.GREEN + "AdvancedNMotd: Successfully loaded language configuration files.");
	}

}
