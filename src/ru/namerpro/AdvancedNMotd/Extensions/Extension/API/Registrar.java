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

package ru.namerpro.AdvancedNMotd.Extensions.Extension.API;

import net.md_5.bungee.api.ChatColor;
import ru.namerpro.AdvancedNMotd.Extensions.Extension.AdvancedNMotdExtension;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.FormatRules.IFormatRuleFactory;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.FormatRules.IPlaceholder;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.FormatRules.TextRule.TextRule;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.RuleException;
import ru.namerpro.AdvancedNMotd.MotdRuleParser.RuleParser;
import ru.namerpro.AdvancedNMotd.Universal.UniversalActions;

public final class Registrar {

    private final AdvancedNMotdExtension extension;

    public Registrar(AdvancedNMotdExtension extension) {
        this.extension = extension;
    }

    public void registerPlaceholder(String placeholderName, IPlaceholder placeholder) {
        try {
            TextRule.TextPlaceholders.registerPlaceholder(placeholderName, placeholder);
        } catch (RuleException error) {
            UniversalActions.universalInstance.sendConsoleMessage(ChatColor.RED + "AdvancedNMotd: Extension \"" + extension.getName() + "\" made an attempt to register placeholder \"%" + placeholderName + "%\", but such placeholder was already registered! An error is below:");
        }
    }

    public void registerRule(String ruleName, IFormatRuleFactory ruleFactory) {
        if (RuleParser.rulesLinker.containsKey(ruleName)) {
            UniversalActions.universalInstance.sendConsoleMessage(ChatColor.RED + "AdvancedNMotd: Extension \"" + extension.getName() + "\" made an attempt to register format rule \"" + ruleName + "\", but such format rule was already registered! An error is below:");
            return;
        }
        RuleParser.rulesLinker.put(ruleName, ruleFactory);
    }

}
