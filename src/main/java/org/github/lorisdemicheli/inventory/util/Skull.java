package org.github.lorisdemicheli.inventory.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

@SuppressWarnings("deprecation")
public class Skull {

	private static CacheLoader<String, String> loader;
	private static LoadingCache<String, String> cache;

	private static final String FIRMA = "GyKw/U2RGpIwt1fsS5YHrv0wxwUqzFoulK0sm8U3cOeJHTBAXHvO0GPm4o/G27/J0SlMR60y9aR0L1ys2Ddq+tQ7MTXdWh3v2WkqeRh/7W0nj+yOYT7N3bqKHJVR//aw3d1yOCEzh1eAtbcxRhFh4hhut/5SqjKmVD5tzuIrKKHNvpJ/ZsJ8Y+8qkEUpBavDxi7LEjlxuRypgyQpK9Mmhtj9fML7bfsOMLyBmQ5bB/kuhGOtC1myrODoZN4uzblBaJQiNslkjDqlED7+5Oe2sX9i5R2RNvzSMiMBrP/joofsBg0HW8DX8zs2VKZMSKlI6fZJXzpSSdZU18XIMl11SkIMiw9iw49mqPOtqDrlDOLke7NLZW195Bpm3BXMP4EEnknJbrh+2ESkjmTEqnzqZP9OgMfW2lNnHYidXR1iqsmQkX3qLBQKaHbLtG0DajWN7bAHST4mP8ozb3eZrcHGVhsxu4Iuu9do5bLpMiP6ATLTbssakAsAqOAtGaAuUT/d5W4SgZbMBihHpNeQjiUAnKG0OHjFvHwRueef5y4qo67N3XfDRwKfHq+/DxJ5zsHGeQ49rSB+pI2jjM4u03N4wbRjds2DS03na/+WVQL2P5mwVHgW7Y0tse6weU5/+ganNNEMwO+oYaMefsrWSdhRE8cFbR42m+3BgFtgjPJTq7s=";

	static {
		loader = new CacheLoader<String, String>() {
			@Override
			public String load(String key) {
				return key.toUpperCase();
			}
		};
		cache = CacheBuilder.newBuilder().maximumSize(10000).build(loader);
	}

	public static ItemStack create(String signature, String value,String displayName) {
		if (signature == null || signature.isEmpty()) {
			signature = "K9P4tCIENYbNpDuEuuY0shs1x7iIvwXi4jUUVsATJfwsAIZGS+9OZ5T2HB0tWBoxRvZNi73Vr+syRdvTLUWPusVXIg+2fhXmQoaNEtnQvQVGQpjdQP0TkZtYG8PbvRxE6Z75ddq+DVx/65OSNHLWIB/D+Rg4vINh4ukXNYttn9QvauDHh1aW7/IkIb1Bc0tLcQyqxZQ3mdglxJfgIerqnlA++Lt7TxaLdag4y1NhdZyd3OhklF5B0+B9zw/qP8QCzsZU7VzJIcds1+wDWKiMUO7+60OSrIwgE9FPamxOQDFoDvz5BOULQEeNx7iFMB+eBYsapCXpZx0zf1bduppBUbbVC9wVhto/J4tc0iNyUq06/esHUUB5MHzdJ0Y6IZJAD/xIw15OLCUH2ntvs8V9/cy5/n8u3JqPUM2zhUGeQ2p9FubUGk4Q928L56l3omRpKV+5QYTrvF+AxFkuj2hcfGQG3VE2iYZO6omXe7nRPpbJlHkMKhE8Xvd1HP4PKpgivSkHBoZ92QEUAmRzZydJkp8CNomQrZJf+MtPiNsl/Q5RQM+8CQThg3+4uWptUfP5dDFWOgTnMdA0nIODyrjpp+bvIJnsohraIKJ7ZDnj4tIp4ObTNKDFC/8j8JHz4VCrtr45mbnzvB2DcK8EIB3JYT7ElJTHnc5BKMyLy5SKzuw=";
		}
		ItemStack is = getSkull();
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(displayName);
		is.setItemMeta(im);
		SkullMeta sm = (SkullMeta) is.getItemMeta();
		GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
		gameProfile.getProperties().put("textures", new Property("textures", value, signature));
		try {
			Field profileField = sm.getClass().getDeclaredField("profile");
			profileField.setAccessible(true);
			profileField.set(sm, gameProfile);
		} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
			e1.printStackTrace();
		}
		is.setItemMeta((ItemMeta) sm);
		return is;
	}

	private static ItemStack getSkull() {
		ItemStack skull;
		if (getVersion() > 12) {
			skull = new ItemStack(Material.valueOf("PLAYER_HEAD"));
		} else {
			skull = new ItemStack(Material.getMaterial("SKULL_ITEM"), 1,
					(short) SkullType.PLAYER.ordinal());
		}
		return skull;
	}

	private static int getVersion() {
		String name = Bukkit.getServer().getClass().getPackage().getName();
		name = (name.substring(name.lastIndexOf('.') + 1) + ".").substring(3);
		return Integer.parseInt(name.substring(0, name.length() - 4));
	}

	public static ItemStack getHead(Player player) {
		String signature;
		String value;
		try {
			Object entityPlayer = ReflectionUtils.callMethod(player, "getHandle");
			GameProfile gameProfile = (GameProfile) ReflectionUtils.callMethod(entityPlayer, "getProfile");
			Property property = gameProfile.getProperties().get("textures").iterator().next();
			signature = property.getSignature();
			value = property.getValue();
			cache.put(player.getName(), property.getValue());
		} catch (Exception e) {
			signature = FIRMA;
			value = cache.getIfPresent(player.getName());
			if(value == null) {
				value = getHeadValue(player.getName());
				if(value == null) {
					return null;
				}
			}
		}
		return create(signature, value,player.getName());
	}

	private static String getHeadValue(String name) {
		try {
			String result = getURLContent("https://api.mojang.com/users/profiles/minecraft/" + name);
			Gson g = new Gson();
			JsonObject obj = (JsonObject) g.fromJson(result, JsonObject.class);
			String uid = obj.get("id").toString().replace("\"", "");
			String signature = getURLContent("https://sessionserver.mojang.com/session/minecraft/profile/" + uid);
			obj = (JsonObject) g.fromJson(signature, JsonObject.class);
			String value = obj.getAsJsonArray("properties").get(0).getAsJsonObject().get("value").getAsString();
			String decoded = new String(Base64.getDecoder().decode(value));
			obj = (JsonObject) g.fromJson(decoded, JsonObject.class);
			String skinURL = obj.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
			byte[] skinByte = ("{\"textures\":{\"SKIN\":{\"url\":\"" + skinURL + "\"}}}").getBytes();
			String encoded = new String(Base64.getEncoder().encode(skinByte));
			cache.put(name, encoded);
			return encoded;
		} catch (Exception exception) {
			return null;
		}
	}

	private static String getURLContent(String urlStr) {
		BufferedReader in = null;
		StringBuilder sb = new StringBuilder();
		try {
			URL url = new URL(urlStr);
			in = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
			String str;
			while ((str = in.readLine()) != null)
				sb.append(str);
		} catch (Exception exception) {
			try {
				if (in != null)
					in.close();
			} catch (IOException iOException) {
			}
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException iOException) {
			}
		}
		return sb.toString();
	}
}