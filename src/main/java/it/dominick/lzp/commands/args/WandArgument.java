package it.dominick.lzp.commands.args;

import it.dominick.lzp.LevelZonePlus;
import it.dominick.lzp.config.ConfigManager;
import it.dominick.lzp.utils.ChatUtils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class WandArgument extends Argument {

    @Getter
    private final NamespacedKey key;

    public WandArgument(ConfigManager config) {
        super(config, "/lzp wand", "levelzone.admin");
        String WAND_KEY = "lzp_wand";
        this.key = new NamespacedKey(LevelZonePlus.getInstance(), WAND_KEY);
    }

    @Override
    public void execute(Player player, String[] args) {
        ItemStack wand = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = wand.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatUtils.color("&aLevelZonePlus Wand"));
            meta.setLore(ChatUtils.color(List.of(
                    "&7Left-Click: Set Pos1",
                    "&7Right-Click: Set Pos2"
            )));
            meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);

            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            wand.setItemMeta(meta);
        }
        player.getInventory().addItem(wand);
        ChatUtils.send(player, config.getString("cmd.wand.given"));
    }

    @Override
    public int minimumArgs() {
        return 0;
    }
}
