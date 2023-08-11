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

import java.util.ArrayList;

public class PlaceholderArguments {

    private final ArrayList<String> arguments;

    public PlaceholderArguments(ArrayList<String> arguments) {
        this.arguments = arguments;
    }

    public ArrayList<String> getAll() {
        return arguments;
    }

    public int getCount() {
        return arguments.size();
    }

    public boolean hasArgument(String argument) {
        return arguments.contains(argument);
    }

    public boolean hasArgumentOnItsPlace(String argument, int argumentIndex) {
        return argumentIndex >= 0 && argumentIndex < arguments.size() && argument.equals(arguments.get(argumentIndex));
    }

    public boolean hasArgumentByIndex(int argumentIndex) {
        return argumentIndex >= 0 && argumentIndex < arguments.size();
    }

    public String getArgument(int argumentIndex) throws IndexOutOfBoundsException {
        return arguments.get(argumentIndex);
    }

}
