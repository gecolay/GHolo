package dev.geco.gholo.object.simple;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import dev.geco.gholo.GHoloMain;
import dev.geco.gholo.service.VersionService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;

/**
 * skull:self -> shows the skull of the player who is viewing the hologram
 * skull:eyJ0ZXh0dXJlcyI6... -> shows the skull with the given base64 texture
 * player_skull:nrvkg -> shows the skull of the given player
 * itemstack:diamond_sword -> shows the given item
 */
public class SimpleItem {

    private final static VersionService version = GHoloMain.getInstance().getVersionService();

    @SuppressWarnings("deprecation")
    public static ItemStack getItem(String content, Player player) {
        ItemStack itemStack = null;
        String[] contentSplit = content.split(":", 2);
        if (contentSplit.length < 2) {
            return null;
        }

        String type = contentSplit[0];
        String value = contentSplit[1].trim();
        switch (type.toLowerCase()) {
            case "itemstack" -> {
                Material material = Material.getMaterial(value.toUpperCase());
                if (material != null) {
                    itemStack = new ItemStack(material);
                }
            }
            case "skull" -> {
                itemStack = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
                if (value.equalsIgnoreCase("self")) {
                    skullMeta.setOwningPlayer(player);
                } else {
                    if (version.isNewerOrVersion(1, 20, 1)) {
                        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID(), GHoloMain.NAME);
                        PlayerTextures textures = profile.getTextures();
                        URL url;
                        try {
                            String decoded = new String(Base64.getDecoder().decode(value));
                            String decodedFormatted = decoded.replaceAll("\\s", "");
                            JsonObject jsonObject = new Gson().fromJson(decodedFormatted, JsonObject.class);
                            String urlText = jsonObject.get("textures").getAsJsonObject().get("SKIN").getAsJsonObject().get("url").getAsString();
                            url = new URL(urlText);
                        } catch (Exception error) {
                            return null;
                        }
                        textures.setSkin(url);
                        profile.setTextures(textures);
                        skullMeta.setOwnerProfile(profile);
                    } else {
                        GameProfile profile = new GameProfile(UUID.randomUUID(), "");
                        profile.getProperties().put("textures", new Property("textures", value));

                        try {
                            Field profileField = skullMeta.getClass().getDeclaredField("profile");
                            profileField.setAccessible(true);
                            profileField.set(skullMeta, profile);
                        } catch (IllegalArgumentException | NoSuchFieldException | SecurityException |
                                 IllegalAccessException error) {
                            return null;
                        }
                    }
                }
                itemStack.setItemMeta(skullMeta);
            }
            case "player_skull" -> {
                itemStack = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(value);
                if (offlinePlayer.hasPlayedBefore()) {
                    skullMeta.setOwningPlayer(offlinePlayer);
                } else {
                    skullMeta.setOwner(value);
                }

                itemStack.setItemMeta(skullMeta);
            }
        }
        return itemStack;
    }
}