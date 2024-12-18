package cn.xinyihl.itemdata;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DataYml implements IData {

    private final YamlConfiguration config;

    private final File file;

    public DataYml(File file) {
        this.file = file;
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }

    @Override
    public void saveItem(Player player, String savename, ItemStack item) {
        this.config.set("items." + savename, item);
        try {
            this.config.save(this.file);
            player.sendMessage("§a物品§7[" + savename + "§7]§a已保存");
        } catch (IOException e) {
            handleException(player, "物品保存时出错", e);
        }
    }

    @Override
    public ItemStack getItem(Player player, String savename) {
        return this.config.getItemStack("items." + savename);
    }

    @Override
    public void deleteItem(Player player, String savename) {
        if (this.config.contains("items." + savename)) {
            this.config.set("items." + savename, null);
            try {
                this.config.save(this.file);
                player.sendMessage("§a物品§7[" + savename + "§7]§a已删除");
            } catch (IOException e) {
                handleException(player, "物品删除时出错", e);
            }
        } else {
            player.sendMessage("§a物品§7[" + savename + "§7]§a不存在");
        }
    }

    @Override
    public Optional<List<String>> getAllSavedNames() {
        return Optional.of(new ArrayList<>(this.config.getConfigurationSection("items").getKeys(false)));
    }

    private void handleException(CommandSender sender, String message, Exception e) {
        sender.sendMessage("§c" + message + "：" + e.getMessage());
        e.printStackTrace();
    }
}
