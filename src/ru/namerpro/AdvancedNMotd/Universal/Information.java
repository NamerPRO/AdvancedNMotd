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

package ru.namerpro.AdvancedNMotd.Universal;

import java.io.File;

public class Information {

    public static boolean areColorsSupportedByServer;
    public static final String jarPath;
    public static final String resourceFolderPath;
    public static final String pluginVersion;

    static {
        final File jarPathAsFile = new File(Information.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        jarPath = jarPathAsFile.getPath().replace("%20", " ");
        final String pluginsFolderPath = jarPathAsFile.getParentFile().getPath().replace("%20", " ");
        resourceFolderPath = pluginsFolderPath + "/AdvancedNMotd";
        pluginVersion = "10.0.0";
    }

    public static ServerType serverType = null; // This field will be set to true if plugin runs on bukkit/spigot server
    public static int minumalClientVersionThatSupportsColors = 735; // this number refers to 1.16 server

    public enum ServerType {
        BUKKIT,
        BUNGEE
    }

}
