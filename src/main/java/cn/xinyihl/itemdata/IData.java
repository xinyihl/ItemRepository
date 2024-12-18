package cn.xinyihl.itemdata;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;

public interface IData {
    void saveItem(Player player, String savename, ItemStack item);

    ItemStack getItem(Player player, String savename);

    void deleteItem(Player player, String savename);

    Optional<List<String>> getAllSavedNames();
}
