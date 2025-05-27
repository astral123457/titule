package com.teste21;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.UUID;

public class Main extends JavaPlugin {
    private Connection connection;

    @Override
    public void onEnable() {
        getLogger().info("Plugin iniciado com configurações!");

        createConfig();
        connectDatabase();

        getCommand("titulo").setExecutor(new TitleCommand(this));

        // ✅ Registrar evento para restaurar títulos ao entrar
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);
    }

    private void createConfig() {
        File configFile = new File(getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            config.set("titulo_padrao", "[Jogador]");
            config.set("mensagem_entrada", "&aBem-vindo ao servidor!");

            try {
                config.save(configFile);
                getLogger().info("Arquivo config.yml gerado com sucesso!");
            } catch (IOException e) {
                getLogger().severe("Erro ao criar config.yml!");
            }
        }
    }

    private void connectDatabase() {
        try {
            File dbFile = new File(getDataFolder(), "database.db");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());

            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS players (uuid TEXT PRIMARY KEY, title TEXT)");
            getLogger().info("Banco de dados SQLite conectado!");
        } catch (SQLException e) {
            getLogger().severe("Erro ao conectar ao banco de dados!");
            e.printStackTrace();
        }
    }

    public void savePlayerTitle(UUID uuid, String title) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT OR REPLACE INTO players (uuid, title) VALUES (?, ?)");
            statement.setString(1, uuid.toString());
            statement.setString(2, title);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getPlayerTitle(UUID uuid) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT title FROM players WHERE uuid = ?");
            statement.setString(1, uuid.toString());
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return result.getString("title");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}