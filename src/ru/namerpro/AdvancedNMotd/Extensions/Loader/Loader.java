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

package ru.namerpro.AdvancedNMotd.Extensions.Loader;

import net.md_5.bungee.api.ChatColor;
import ru.namerpro.AdvancedNMotd.Configuration.ConfigurationManager;
import ru.namerpro.AdvancedNMotd.Configuration.IConfiguration;
import ru.namerpro.AdvancedNMotd.Universal.Information;
import ru.namerpro.AdvancedNMotd.Universal.UniversalActions;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

public final class Loader implements ILoader {

    private static Loader instance;
    private final URLClassLoader loader;

    private Loader() throws MalformedURLException {
        final File[] extensions = new File(Information.resourceFolderPath + "/extensions").listFiles((dir, name) -> name.toLowerCase().endsWith(".jar"));
        if (extensions == null || extensions.length == 0) {
            loader = null;
        } else {
            URL[] extensionsAsURLArray = new URL[extensions.length];
            for (int i = 0; i < extensions.length; ++i) {
                extensionsAsURLArray[i] = extensions[i].toURI().toURL();
            }

            loader = new URLClassLoader(
                    extensionsAsURLArray,
                    Loader.class.getClassLoader()
            );
        }
    }

    private URLClassLoader getLoader() {
        return loader;
    }

    public static Loader getLoaderInstance() throws MalformedURLException {
        if (instance == null) {
            instance = new Loader();
        }
        return instance;
    }

    @Override
    public void load(boolean isEnable) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException, LoaderException, URISyntaxException {
        if (getLoader() == null) {
            // User did not install any extensions and thus there is nothing to load
            return;
        }
        Enumeration<URL> passportsEnumeration = getLoader().getResources("passport.yml");
        int amountOfExtensions = 0;
        while (passportsEnumeration.hasMoreElements()) {
            URL concretePassportURL = passportsEnumeration.nextElement();
            InputStream concretePassportStream = concretePassportURL.openStream();
            InputStreamReader concretePassportReader = new InputStreamReader(concretePassportStream, StandardCharsets.UTF_8);
            IConfiguration concretePassport = ConfigurationManager.buildConfiguration(concretePassportReader);

            if (!concretePassport.contains("author") || !concretePassport.contains("name") || !concretePassport.contains("main") || !concretePassport.contains("version") || !concretePassport.contains("description")) {
                throw new LoaderException("Invalid passport of extension at path \"" + concretePassportURL.getFile() + "\". Passport must contain \"name\", \"main\", \"author\", \"version\" and \"description\" fields.");
            }

            if (isEnable) {
                UniversalActions.universalInstance.sendConsoleMessage(ChatColor.GREEN + "AdvancedNMotd: Loading extension \"" + concretePassport.getString("name") + "\" v" + concretePassport.getString("version") + " by " + concretePassport.getString("author") + "...");
            } else {
                UniversalActions.universalInstance.sendConsoleMessage(ChatColor.RED + "AdvancedNMotd: Disabling extension \"" + concretePassport.getString("name") + "\"...");
            }

            Class<?> classToLoad = Class.forName(concretePassport.getString("main"), true, loader);
            Method method = classToLoad.getDeclaredMethod(isEnable ? "onExtensionEnable" : "onExtensionDisable");
            Object instance = classToLoad.newInstance();
            method.invoke(instance);

            concretePassportReader.close();
            concretePassportStream.close();
            ++amountOfExtensions;
        }

        if (isEnable) {
            UniversalActions.universalInstance.sendConsoleMessage(ChatColor.GREEN + "AdvancedNMotd: Successfully loaded " + amountOfExtensions + " extension(s)!");
        } else {
            UniversalActions.universalInstance.sendConsoleMessage(ChatColor.RED + "AdvancedNMotd: Successfully disabled " + amountOfExtensions + " extension(s)!");
        }
    }

}
