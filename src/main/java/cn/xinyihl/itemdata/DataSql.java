package cn.xinyihl.itemdata;

import cn.xinyihl.utils.Utils;
import cn.xinyihl.database.DatabaseManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DataSql implements IData {
    private final DatabaseManager databaseManager;

    public DataSql(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public void saveItem(Player player, String savename, ItemStack item) {
        String sql = "INSERT INTO saveitem (name, data) VALUES (?, ?) ON DUPLICATE KEY UPDATE data = VALUES(data)";
        try {
            Connection connection = this.databaseManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            setParameters(preparedStatement, savename, Utils.serializer(item));
            preparedStatement.executeUpdate();
            connection.close();
            player.sendMessage("§a物品§7[" + savename + "§7]§a已保存至数据库");
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                player.sendMessage("§c物品§7[" + savename + "§7]§c已存在，无法重复保存");
            } else {
                handleException(player, "保存物品时发生错误", e);
            }
        }
    }

    @Override
    public ItemStack getItem(Player player, String savename) {
        String sql = "SELECT data FROM saveitem WHERE name = ?";
        try {
            Connection connection = this.databaseManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            setParameters(preparedStatement, savename);
            ResultSet resultSet = preparedStatement.executeQuery();
            String itemData = null;
            if (resultSet.next()) {
                itemData = resultSet.getString("data");
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
            return Utils.deserializer(itemData);
        } catch (SQLException e) {
            handleException(player, "获取物品时发生错误", e);
        }
        return null;
    }

    @Override
    public void deleteItem(Player player, String savename) {
        String sql = "DELETE FROM saveitem WHERE name = ?";
        try {
            Connection connection = this.databaseManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            setParameters(preparedStatement, savename);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                player.sendMessage("§a物品§7[" + savename + "§7]§a已删除");
            } else {
                player.sendMessage("§a物品§7[" + savename + "§7]§a不存在");
            }
            connection.close();
        } catch (SQLException e) {
            handleException(player, "删除物品时发生错误", e);
        }
    }

    @Override
    public Optional<List<String>> getAllSavedNames() {
        String sql = "SELECT name FROM saveitem";
        List<String> itemNames = new ArrayList<>();
        try {
            Connection connection = this.databaseManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                itemNames.add(resultSet.getString("name"));
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.of(itemNames);
    }

    private void handleException(CommandSender sender, String message, Exception e) {
        sender.sendMessage("§c" + message + "：" + e.getMessage());
        throw new RuntimeException(e);
    }

    private static void setParameters(PreparedStatement preparedStatement, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i + 1, params[i]);
        }
    }
}
