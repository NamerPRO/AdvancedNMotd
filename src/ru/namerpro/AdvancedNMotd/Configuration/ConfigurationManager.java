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

import ru.namerpro.AdvancedNMotd.Universal.Information;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class ConfigurationManager {

	public static final ArrayList<IConfiguration> motds = new ArrayList<>();
	public static IConfiguration placeholders;
	public static IConfiguration rules;
	public static IConfiguration data;
	public static IConfiguration downsample;
	public static IConfiguration config;

	private static IConfigurationFactory factory = null;

	public static void setConfigurationFactory(IConfigurationFactory factory) {
		ConfigurationManager.factory = factory;
	}

	public static IConfiguration buildConfiguration(File file) throws NullPointerException, IOException {
		if (factory == null) {
			throw new NullPointerException("You must initialize factory 'factory' in ConfigurationManager class with setConfigurationFactory method!");
		}
		return factory.loadConfiguration(file);
	}

	public static IConfiguration buildConfiguration(InputStreamReader streamReader) throws NullPointerException, IOException {
		if (factory == null) {
			throw new NullPointerException("You must initialize factory 'factory' in ConfigurationManager class with setConfigurationFactory method!");
		}
		return factory.loadConfiguration(streamReader);
	}

	private static IConfiguration createAdvancedNMotdConfigurationFile(String fileName, String filePath, boolean isLegacy) throws IOException {
		return createConfigurationFile(fileName, filePath, isLegacy, Information.jarPath, Information.resourceFolderPath);
	}

	public static IConfiguration createConfigurationFile(String fileName, String filePath, boolean isLegacy, String jarPath, String resourceFolderPath) throws IOException {
		URL url = new URL("jar:file:/" + jarPath + "!/" + filePath + (isLegacy ? "legacy_" : "") + fileName);
		InputStream in = url.openStream();
		File outDirectory = new File(resourceFolderPath + "/" + filePath);
		if (!outDirectory.exists()) {
			outDirectory.mkdirs();
		}
		File outFile = new File(outDirectory.getPath() + "/" + fileName);
		if (!outFile.exists()) {
			OutputStream out = new FileOutputStream(outFile);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = in.read(buffer)) != -1) {
				out.write(buffer, 0, length);
			}
			out.close();
		}
		in.close();
		return buildConfiguration(outFile);
	}

	private static void createIconsFolder() throws IOException {
		File iconsDirectory = new File(Information.resourceFolderPath + "/icons");
		if (!iconsDirectory.exists()) {
			for(int index = 1; index < 6; ++index) {
				String concreteIconName = "icon" + index +".png";
				createAdvancedNMotdConfigurationFile(concreteIconName, "icons/", false);
			}
		}
		createAdvancedNMotdConfigurationFile("ReadME.txt", "icons/", false);
	}

	private static void createMotdsFolder() throws IOException {
		File motdsDirectory = new File(Information.resourceFolderPath + "/motds");
		if (!motdsDirectory.exists()) {
			for (int index = 1; index < 6; ++index) {
				String concreteMotdFileName = "motd" + index + ".yml";
				createAdvancedNMotdConfigurationFile(concreteMotdFileName, "motds/", !Information.areColorsSupportedByServer);
			}
		}
		createAdvancedNMotdConfigurationFile("ReadME.txt", "motds/", false);
	}

	private static void initializeMotds() throws IOException {
		createMotdsFolder();
		motds.clear();
		File[] motdsAsFile = new File(Information.resourceFolderPath + "/motds").listFiles((dir, name) -> name.toLowerCase().endsWith(".yml"));
		if (motdsAsFile == null || motdsAsFile.length == 0) {
			throw new NoSuchElementException("Folder \"motds\" at path \"AdvancedNMotd/motds\" must contain at least one \"*.yml\" motd configuration, but 0 found!");
		}
		for (File concreteMotd : motdsAsFile) {
			motds.add(buildConfiguration(concreteMotd));
		}
	}
	
	public static void createConfigs() throws IOException {
		placeholders = createAdvancedNMotdConfigurationFile("placeholders.yml", "aliases/", false);
		rules = createAdvancedNMotdConfigurationFile("rules.yml", "aliases/", false);
		data = createAdvancedNMotdConfigurationFile("data.yml", "", false);
		if (Information.areColorsSupportedByServer) {
			downsample = createAdvancedNMotdConfigurationFile("downsample.yml", "", false);
		}
		config = createAdvancedNMotdConfigurationFile("config.yml", "", !Information.areColorsSupportedByServer);

		createAdvancedNMotdConfigurationFile("ReadME.txt", "aliases/", false);
		createAdvancedNMotdConfigurationFile("ReadME.txt", "languages/", false);
		createAdvancedNMotdConfigurationFile("ReadME.txt", "extensions/", false);

		initializeMotds();
		createIconsFolder();
	}

}
