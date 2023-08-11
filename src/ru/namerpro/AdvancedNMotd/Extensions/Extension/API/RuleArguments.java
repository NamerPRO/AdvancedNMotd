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

import java.util.HashMap;

public class RuleArguments {

    private final HashMap<String, String> arguments;

    public RuleArguments(HashMap<String, String> arguments) {
        this.arguments = arguments;
    }

    public static RuleArguments getEmptyArguments() {
        return new RuleArguments(new HashMap<>());
    }

    public boolean hasFlag(String flagName) {
        return arguments.getOrDefault(flagName, "") == null;
    }

    public boolean hasArgument(String argumentName) {
        return arguments.get(argumentName) != null;
    }

    public String getArgumentValue(String argumentName) {
        return arguments.get(argumentName);
    }

    public HashMap<String, String> getAll() {
        return arguments;
    }

    public int getCount() {
        return arguments.size();
    }

}
