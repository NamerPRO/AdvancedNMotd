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
import ru.namerpro.AdvancedNMotd.MotdRuleParser.FormatRules.IPlaceholder;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.RuleException;
import ru.namerpro.AdvancedNMotd.Universal.Pair;
import ru.namerpro.AdvancedNMotd.Universal.UniversalActions;

public class FakeOnlinePlaceholder implements IPlaceholder {

    @Override
    public Pair<String, Integer> apply(String lineToParse, Limits limits, PlaceholderArguments arguments, boolean isScreeningSmart) throws RuleException {
        if (arguments.getCount() != 0) {
            throw new RuleException("Placeholder \"%fakeOnline%\" cannot have arguments, but presented in line as \"" + lineToParse.substring(limits.start, limits.end + 1) + "\". Line: \"" + lineToParse + "\". Index: " + limits.start + ".");
        }
        if(!ConfigurationManager.config.getBoolean("AdvancedNMotd.Configuration.Data")) {
            UniversalActions.universalInstance.sendConsoleMessage(ChatColor.RED + "AdvancedNMotd: Detected \"%fakeOnline%\" placeholder, but it was disabled in configuration! To enable it set \"AdvancedNMotd.Configuration.Data\" field to true in \"config.yml\". To dismiss this text and get the same result, screen both '%' symbols by placing '|' before each like this: \"|%\". Line: \"" + lineToParse + "\". Index: " + limits.start + ".");
            return new Pair<>(lineToParse, limits.end);
        }
        int online = UniversalActions.universalInstance.getOnlinePlayers() * ConfigurationManager.data.getInt("Data.PlayerSpace") + ConfigurationManager.data.getInt("Data.ExtraOnlinePlayers");
        int maxPlayers = ConfigurationManager.data.getInt("Data.FakeMaxPlayers");
        if((ConfigurationManager.data.getBoolean("Data.OnlineIsBiggerThenSlotsProtection")) && (online > maxPlayers)) {
            online = maxPlayers;
        }
        String parsedLine = lineToParse.substring(0, limits.start) + online + lineToParse.substring(limits.end + 1);
        return new Pair<>(parsedLine, limits.end - (lineToParse.length() - parsedLine.length()));
    }

}
