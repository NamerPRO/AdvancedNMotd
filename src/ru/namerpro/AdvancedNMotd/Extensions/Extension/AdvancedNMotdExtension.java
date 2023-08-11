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

package ru.namerpro.AdvancedNMotd.Extensions.Extension;

import ru.namerpro.AdvancedNMotd.Configuration.ConfigurationManager;
import ru.namerpro.AdvancedNMotd.Configuration.IConfiguration;
import ru.namerpro.AdvancedNMotd.Universal.Pair;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public abstract class AdvancedNMotdExtension {

    private String name = null;
    private String jarPath = null;
    private String extensionFolderPath = null;

    private static final HashMap<String, AdvancedNMotdExtension> linker = new HashMap<>();
    private IConfiguration passport = null;

    {
        try {
            File extensionFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
            jarPath = extensionFile.getPath().replace("%20", " ");
            jarPath = (jarPath.charAt(0) == '/') ? jarPath.substring(1) : jarPath;
            String jarFolderPath = extensionFile.getParentFile().getPath().replace("%20", " ");

            InputStream passportStream = new URL("jar:file:/" + jarPath + "!/passport.yml").openStream();
            InputStreamReader passportReader = new InputStreamReader(passportStream, StandardCharsets.UTF_8);
            passport = ConfigurationManager.buildConfiguration(passportReader);

            name = passport.getString("name");
            extensionFolderPath = jarFolderPath + File.separator + name;

            passportStream.close();
            passportReader.close();

            linker.put(name, this);
        } catch (IOException error) {
            error.printStackTrace();
        }
    }

    public IConfiguration getPassport() {
        return passport;
    }

    public String getName() {
        return name;
    }

    public AdvancedNMotdExtension getCurrentExtension() {
        return this;
    }

    public static AdvancedNMotdExtension getExtension(String extensionName) {
        return linker.get(extensionName);
    }

    private Pair<String, String> preparePath(String pathToResource) {
        int i = pathToResource.length() - 1;
        for (; i >= 0; --i) {
            if (pathToResource.charAt(i) == '\\' || pathToResource.charAt(i) == '/') {
                break;
            }
        }
        String path = i == -1 ? "" : pathToResource.substring(0, i) + "/";
        String name = pathToResource.substring(i + 1);
        return new Pair<>(path, name);
    }

    public IConfiguration saveResource(String pathToResource) throws IOException {
        Pair<String, String> pathAndName = preparePath(pathToResource);
        return ConfigurationManager.createConfigurationFile(pathAndName.getValue(), pathAndName.getKey(), false, jarPath, extensionFolderPath);
    }

    public String getResourceFolder() {
        return extensionFolderPath;
    }

    public abstract void onExtensionEnable();
    public void onExtensionDisable() {}

}
