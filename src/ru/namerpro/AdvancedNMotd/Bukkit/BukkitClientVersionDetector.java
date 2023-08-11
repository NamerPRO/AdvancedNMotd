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

package ru.namerpro.AdvancedNMotd.Bukkit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.PacketType.Protocol;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

public class BukkitClientVersionDetector extends PacketAdapter {
	
	public static int clientVersion;
	
	@Override
    public void onPacketReceiving(final PacketEvent event) {
		try {
	    	final PacketContainer packet = event.getPacket();
	        if(event.getPacketType() == PacketType.Handshake.Client.SET_PROTOCOL) {
	        	if(packet.getProtocols().read(0) == Protocol.STATUS) {
	        		clientVersion = packet.getIntegers().read(0);
	        	}
	        }
		} catch(Exception e) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Could not get version of a client viewing motd! Continue with version 735 (1.16).");
			clientVersion = 735;
		}
    }

	public BukkitClientVersionDetector(Plugin plugin, ListenerPriority listenerPriority, PacketType setProtocol) {
		super(plugin, listenerPriority, setProtocol);
	}

}
