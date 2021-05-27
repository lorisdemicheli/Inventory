package org.github.lorisdemicheli.inventory.examples;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.github.lorisdemicheli.inventory.entity.EntitySerializable;

public class PlayerType extends EntitySerializable<PlayerType>{

	private static final long serialVersionUID = 7743621606279687246L;
	
	private transient Player player;
	private String uuid;
	
	public PlayerType() {}
	
	public PlayerType(Player player) {
		this.player = player;
		this.uuid = player.getUniqueId().toString();
	}

	public Player getPlayer() {
		if(player == null) {
			player = Bukkit.getPlayer(UUID.fromString(uuid));
		}
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
		this.uuid = player.getUniqueId().toString();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public Class<PlayerType> getComplexType() {
		return PlayerType.class;
	}

	@Override
	public String toString() {
		return "PlayerType [player=" + player + ", uuid=" + uuid + "]";
	}
	
	
}
