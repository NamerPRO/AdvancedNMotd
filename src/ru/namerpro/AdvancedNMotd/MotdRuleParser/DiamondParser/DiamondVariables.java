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

package ru.namerpro.AdvancedNMotd.MotdRuleParser.DiamondParser;

import java.util.HashMap;

public class DiamondVariables {

    private final HashMap<String, String> variables = new HashMap<>();

    public void setVariable(String name, String value) {
        if (!name.equals("this")) {
            variables.put(name, value);
        }
        variables.put("this", value);
    }

    public String getVariable(String name) {
        String extractedName = name.substring(1, name.length() - 1);
        return variables.getOrDefault(extractedName, name);
    }

}
