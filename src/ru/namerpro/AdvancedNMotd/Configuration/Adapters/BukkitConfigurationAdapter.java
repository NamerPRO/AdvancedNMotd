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

package ru.namerpro.AdvancedNMotd.Configuration.Adapters;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import ru.namerpro.AdvancedNMotd.Configuration.IConfiguration;
import ru.namerpro.AdvancedNMotd.Configuration.IConfigurationFactory;

import java.io.File;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;

public class BukkitConfigurationAdapter implements IConfiguration {

    private final FileConfiguration configuration;

    public static class BukkitConfigurationAdapterFactory implements IConfigurationFactory {

        @Override
        public IConfiguration loadConfiguration(File file) {
            return file.getName().endsWith(".yml") ? new BukkitConfigurationAdapter(YamlConfiguration.loadConfiguration(file)) : null;
        }

        @Override
        public IConfiguration loadConfiguration(InputStreamReader streamReader) {
            return new BukkitConfigurationAdapter(YamlConfiguration.loadConfiguration(streamReader));
        }

    }

    public BukkitConfigurationAdapter(FileConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Object get(String path) {
        return configuration.get(path);
    }

    @Override
    public String getString(String path) {
        return configuration.getString(path);
    }

    @Override
    public int getInt(String path) {
        return configuration.getInt(path);
    }

    @Override
    public boolean getBoolean(String path) {
        return configuration.getBoolean(path);
    }

    @Override
    public double getDouble(String path) {
        return configuration.getDouble(path);
    }

    @Override
    public List<String> getStringList(String path) {
        return configuration.getStringList(path);
    }

    @Override
    public List<Boolean> getBooleanList(String path) {
        return configuration.getBooleanList(path);
    }

    @Override
    public List<Byte> getByteList(String path) {
        return configuration.getByteList(path);
    }

    @Override
    public List<Character> getCharacterList(String path) {
        return configuration.getCharacterList(path);
    }

    @Override
    public List<Double> getDoubleList(String path) {
        return configuration.getDoubleList(path);
    }

    @Override
    public List<Float> getFloatList(String path) {
        return configuration.getFloatList(path);
    }

    @Override
    public List<Integer> getIntegerList(String path) {
        return configuration.getIntegerList(path);
    }

    @Override
    public List<?> getList(String path) {
        return configuration.getList(path);
    }

    @Override
    public boolean contains(String path) {
        return configuration.contains(path);
    }

    @Override
    public Collection<String> getKeys() {
        return configuration.getKeys(false);
    }

    @Override
    public void set(String path, Object value) {
        configuration.set(path, value);
    }

    @Override
    public void remove(String path) {
        configuration.set(path, null);
    }

}
