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

package ru.namerpro.AdvancedNMotd.Updater;

import net.md_5.bungee.api.ChatColor;
import ru.namerpro.AdvancedNMotd.Universal.Information;
import ru.namerpro.AdvancedNMotd.Universal.UniversalActions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class UpdateChecker {

    private static boolean startUpAlert = true;
    private static boolean alertAboutUpdate = true;

    // Method must be run asynchronously!
    public static void check() {
        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL("https://60d0f9a77de0b20017109e1b.mockapi.io/api/v1/info").openConnection();
            if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                String reply = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
                String latestVersion = reply.split("\"version\":\"")[1].split("\"")[0];
                if (latestVersion.equals(Information.pluginVersion)) {
                    if (startUpAlert) {
                        UniversalActions.universalInstance.sendConsoleMessage("");
                        UniversalActions.universalInstance.sendConsoleMessage(ChatColor.GRAY + "------------ " + ChatColor.WHITE + "Advanced" + ChatColor.GRAY + "NMotd" + ChatColor.WHITE + " Updater" + ChatColor.GRAY + " ----------");
                        UniversalActions.universalInstance.sendConsoleMessage(ChatColor.GREEN + "You have the latest version of AdvancedNMotd!");
                        UniversalActions.universalInstance.sendConsoleMessage(ChatColor.WHITE + "Current version: " + ChatColor.AQUA + Information.pluginVersion + ".");
                        UniversalActions.universalInstance.sendConsoleMessage(ChatColor.GRAY + "---------------------------------------------");
                        UniversalActions.universalInstance.sendConsoleMessage("");
                        startUpAlert = false;
                    }
                } else if (startUpAlert || alertAboutUpdate) {
                    UniversalActions.universalInstance.sendConsoleMessage("");
                    UniversalActions.universalInstance.sendConsoleMessage(ChatColor.GRAY + "------------ " + ChatColor.WHITE + "Advanced" + ChatColor.GRAY + "NMotd" + ChatColor.WHITE + " Updater" + ChatColor.GRAY + " ----------");
                    UniversalActions.universalInstance.sendConsoleMessage(ChatColor.RED + "You have the outdated version of AdvancedNMotd!");
                    UniversalActions.universalInstance.sendConsoleMessage(ChatColor.RED + "           Current version: " + ChatColor.YELLOW + Information.pluginVersion + ".");
                    UniversalActions.universalInstance.sendConsoleMessage(ChatColor.RED + "           Latest version: " + ChatColor.YELLOW + latestVersion + ".");
                    UniversalActions.universalInstance.sendConsoleMessage(ChatColor.GRAY + "---------------------------------------------");
                    UniversalActions.universalInstance.sendConsoleMessage("");
                    startUpAlert = false;
                    alertAboutUpdate = false;
                }
            } else {
                if (startUpAlert) {
                    UniversalActions.universalInstance.sendConsoleMessage("");
                    UniversalActions.universalInstance.sendConsoleMessage(ChatColor.GRAY + "------------ " + ChatColor.WHITE + "Advanced" + ChatColor.GRAY + "NMotd" + ChatColor.WHITE + " Updater" + ChatColor.GRAY + " ----------");
                    UniversalActions.universalInstance.sendConsoleMessage(ChatColor.RED + "Could not check for updates because of no internet access!");
                    UniversalActions.universalInstance.sendConsoleMessage(ChatColor.RED + "(this may also appear, because the service is offline for maintenance)");
                    UniversalActions.universalInstance.sendConsoleMessage(ChatColor.GRAY + "---------------------------------------------");
                    UniversalActions.universalInstance.sendConsoleMessage("");
                    startUpAlert = false;
                }
            }
        } catch(IOException exception) {
            if (startUpAlert) {
                UniversalActions.universalInstance.sendConsoleMessage("");
                UniversalActions.universalInstance.sendConsoleMessage(ChatColor.GRAY + "------------ " + ChatColor.WHITE + "Advanced" + ChatColor.GRAY + "NMotd" + ChatColor.WHITE + " Updater" + ChatColor.GRAY + " ----------");
                UniversalActions.universalInstance.sendConsoleMessage(ChatColor.RED + "Could not check for updates because of no internet access!");
                UniversalActions.universalInstance.sendConsoleMessage(ChatColor.RED + "(this may also appear, because the service is offline for maintenance)");
                UniversalActions.universalInstance.sendConsoleMessage(ChatColor.GRAY + "---------------------------------------------");
                UniversalActions.universalInstance.sendConsoleMessage("");
                startUpAlert = false;
            }
        }
    }

}
