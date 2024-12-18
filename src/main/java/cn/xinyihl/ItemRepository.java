package cn.xinyihl;


import cn.xinyihl.database.DatabaseManager;
import cn.xinyihl.itemdata.DataSql;
import cn.xinyihl.itemdata.DataYml;
import cn.xinyihl.itemdata.IData;
import de.tr7zw.changeme.nbtapi.NBT;
import org.bukkit.Bukkit;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class ItemRepository extends JavaPlugin {

    private DatabaseManager databaseManager;
    private IData data;

    @Override
    public void onEnable() {
        if (!NBT.preloadApi()) {
            getLogger().warning("NBT-API 未正确初始化，ItemRepository 已禁用");
            getPluginLoader().disablePlugin(this);
            return;
        }
        saveDefaultConfig();
        initializeDatabaseManager();
        initCommand();
    }

    @Override
    public void onDisable() {
        if (this.databaseManager != null) {
            this.databaseManager.disconnect();
        }
    }

    private void initCommand() {
        TabExecutor command = new IRCommand(this.data, this);
        Bukkit.getPluginCommand("saveitem").setExecutor(command);
        Bukkit.getPluginCommand("saveitem").setTabCompleter(command);
    }

    public void reloadConfigAndDatabase() {
        reloadConfig();
        initializeDatabaseManager();
        initCommand();
    }

    private void initializeDatabaseManager() {
        if (this.databaseManager != null) {
            this.databaseManager.disconnect();
        }
        if (getConfig().getBoolean("mysql.enable")) {
            this.databaseManager = new DatabaseManager(getConfig());
            this.databaseManager.createDatabase();
            getLogger().info("数据库已连接并初始化");
            this.data = new DataSql(this.databaseManager);
        } else {
            this.databaseManager = null;
            getLogger().info("使用YAML存储，未启用数据库连接");
            this.data = new DataYml(new File(this.getDataFolder(), "saveitem.yml"));
        }
    }
}
