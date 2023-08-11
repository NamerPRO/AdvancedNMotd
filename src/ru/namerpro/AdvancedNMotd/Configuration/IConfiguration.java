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

package ru.namerpro.AdvancedNMotd.Configuration;

import java.util.Collection;
import java.util.List;

public interface IConfiguration {

    Object get(String path);

    String getString(String path);

    int getInt(String path);

    boolean getBoolean(String path);

    double getDouble(String path);

    List<String> getStringList(String path);

    List<Boolean> getBooleanList(String path);

    List<Byte> getByteList(String path);

    List<Character> getCharacterList(String path);

    List<Double> getDoubleList(String path);

    List<Float> getFloatList(String path);

    List<Integer> getIntegerList(String path);

    List<?> getList(String path);

    boolean contains(String path);

    Collection<String> getKeys();

    void set(String path, Object value);

    void remove(String path);

}
