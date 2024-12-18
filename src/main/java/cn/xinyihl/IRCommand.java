package cn.xinyihl;

import cn.xinyihl.itemdata.IData;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class IRCommand implements TabExecutor {

    private final IData data;

    private final ItemRepository plugin;

    public IRCommand(IData data, ItemRepository plugin) {
        this.plugin = plugin;
        this.data = data;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String s, String[] args) {
        if (args.length == 0 || (args.length == 1 && "help".equalsIgnoreCase(args[0]))) {
            showHelp(sender);
            return true;
        }
        if (args.length == 1 && "reload".equalsIgnoreCase(args[0])) {
            reloadConfig(sender);
            return true;
        }
        if (args.length == 3 && "giveplayer".equalsIgnoreCase(args[0])) {
            giveItemToPlayer(sender, args[1], args[2]);
            return true;
        }
        if ((args.length == 2 && "save".equalsIgnoreCase(args[0])) || "give".equalsIgnoreCase(args[0]) || "delete".equalsIgnoreCase(args[0])) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c此命令仅限玩家使用");
            } else {
                handlePlayerCommand((Player) sender, args[0], args[1]);
            }
            return true;
        }
        if ((args.length == 1 && "save".equalsIgnoreCase(args[0])) || "give".equalsIgnoreCase(args[0])) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§c此命令仅限玩家使用");
            } else {
                sender.sendMessage("§c请输入物品的保存名!");
            }
            return true;
        }
        if (args.length == 1 && "delete".equalsIgnoreCase(args[0]))
            sender.sendMessage("§c请输入你要删除的保存名");
        if (args.length == 1 && "list".equalsIgnoreCase(args[0])) {
            listSavedItems(sender);
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        List<String> tab = new ArrayList<>();
        List<String> options = Arrays.asList("save", "give", "giveplayer", "delete", "reload");
        if (args.length == 1) {
            String input = args[0].toLowerCase();
            for (String option : options) {
                if (option.startsWith(input)) {
                    tab.add(option);
                }
            }
        }
        if ((args.length == 2 && ("give".equalsIgnoreCase(args[0]) || "giveplayer".equalsIgnoreCase(args[0]))) || "delete".equalsIgnoreCase(args[0])) {
            Optional<List<String>> savedItems = data.getAllSavedNames();
            if (savedItems.isPresent() && !(savedItems.get()).isEmpty()) {
                tab.addAll(savedItems.get());
            }
        }
        if (args.length == 3 && "giveplayer".equalsIgnoreCase(args[0])) {
            for (Player onlinePlayer : sender.getServer().getOnlinePlayers()) {
                tab.add(onlinePlayer.getName());
            }
        }
        return tab;
    }

    private void showHelp(CommandSender sender) {
        sender.sendMessage("§a-------§bSaveItem§a-------");
        sender.sendMessage("§a /saveitem save <name> 保存物品");
        sender.sendMessage("§a /saveitem give <name> 给与自己物品");
        sender.sendMessage("§a /saveitem giveplayer <name> <player> 给其他玩家物品");
        sender.sendMessage("§a /saveitem delete <name> 删除物品");
        sender.sendMessage("§a /saveitem list 浏览已保存物品");
        sender.sendMessage("§a /saveitem reload 重载配置文件");
    }

    private void reloadConfig(CommandSender sender) {
        this.plugin.reloadConfigAndDatabase();
        sender.sendMessage("§a配置文件已重新加载！");
    }

    private void handlePlayerCommand(Player player, String command, String savename) {
        switch (command.toLowerCase()) {
            case "save":
                saveItem(player, savename);
                return;
            case "give":
                giveItem(player, savename);
                return;
            case "delete":
                data.deleteItem(player, savename);
                return;
        }
        player.sendMessage("§c未知命令！");
    }

    private void saveItem(Player player, String savename) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() == Material.AIR) {
            player.sendMessage("§c你没有手持物品！");
            return;
        }
        data.saveItem(player, savename, item);
    }

    private void giveItem(Player player, String savename) {
        ItemStack item = data.getItem(player, savename);
        if (item != null) {
            player.getInventory().addItem(item);
            player.sendMessage("§a物品§7[" + savename + "§7]§a已获取");
        } else {
            player.sendMessage("§a物品§7[" + savename + "§7]§a不存在");
        }
    }

    private void listSavedItems(CommandSender sender) {
        Optional<List<String>> savedItems = data.getAllSavedNames();
        if (savedItems.isPresent() && !(savedItems.get()).isEmpty()) {
            sender.sendMessage("§a已保存的物品列表：");
            for (String itemName : savedItems.get())
                sender.sendMessage(" - " + itemName);
        } else {
            sender.sendMessage("§a当前没有已保存的物品。");
        }
    }

    private void giveItemToPlayer(CommandSender sender, String savename, String targetPlayerName) {
        Player targetPlayer = sender.getServer().getPlayer(targetPlayerName);
        if (targetPlayer == null) {
            sender.sendMessage("§c玩家§7[" + targetPlayerName + "§7]§c不在线！");
            return;
        }
        giveItem(targetPlayer, savename);
    }
}
