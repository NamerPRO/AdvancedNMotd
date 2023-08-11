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

package ru.namerpro.AdvancedNMotd.MotdRuleParser.FormatRules;

import ru.namerpro.AdvancedNMotd.Extensions.Extension.API.Limits;
import ru.namerpro.AdvancedNMotd.Extensions.Extension.API.PlaceholderArguments;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.RuleException;
import ru.namerpro.AdvancedNMotd.Universal.Pair;

public interface IPlaceholder {

    // Function must return a pair of elements:
    // 1) String after parsing placeholder.
    // 2) Index right at the last character of the text inserted instead of placeholder.
    Pair<String, Integer> apply(String lineToParse, Limits limits, PlaceholderArguments arguments, boolean isScreeningSmart) throws RuleException;

}
