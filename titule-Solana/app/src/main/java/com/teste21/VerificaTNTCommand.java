package com.teste21;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.sql.*;

public class VerificaTNTCommand implements CommandExecutor {

    private final Main plugin;

    public VerificaTNTCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("⚠️ Comando apenas para jogadores.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage("❓ Use: /tnt <nomeDaFabrica>");
            return true;
        }

        String nomeFabrica = args[0];
        Block targetBlock = player.getTargetBlockExact(5);

        if (targetBlock == null) {
            player.sendMessage("❌ Nenhum bloco visado.");
            return true;
        }

        Location tntLocation = targetBlock.getLocation();
        Location observerLocation = calcularObserver(player, tntLocation);
        if (observerLocation == null) {
            player.sendMessage("⚠️ Direção do jogador não suportada.");
            return true;
        }

        World world = player.getWorld();
        Connection conn = plugin.getConnection();

        if (conn == null) {
            player.sendMessage("❌ Banco de dados não está conectado.");
            return true;
        }

        synchronized (conn) {
            try (PreparedStatement select = conn.prepareStatement("SELECT id FROM fabricas WHERE nome = ?")) {
                select.setString(1, nomeFabrica);
                ResultSet result = select.executeQuery();

                if (result.next()) {
                    // Já existe — faz UPDATE
                    try (PreparedStatement update = conn.prepareStatement(
                        "UPDATE fabricas SET world = ?, tnt_x = ?, tnt_y = ?, tnt_z = ?, observer_x = ?, observer_y = ?, observer_z = ? WHERE nome = ?"
                    )) {
                        update.setString(1, world.getName());
                        update.setInt(2, tntLocation.getBlockX());
                        update.setInt(3, tntLocation.getBlockY());
                        update.setInt(4, tntLocation.getBlockZ());
                        update.setInt(5, observerLocation.getBlockX());
                        update.setInt(6, observerLocation.getBlockY());
                        update.setInt(7, observerLocation.getBlockZ());
                        update.setString(8, nomeFabrica);
                        update.executeUpdate();
                        player.sendMessage("🔁 Fábrica '" + nomeFabrica + "' atualizada!");
                    }
                } else {
                    // Novo registro — faz INSERT
                    try (PreparedStatement insert = conn.prepareStatement(
                        "INSERT INTO fabricas (nome, world, tnt_x, tnt_y, tnt_z, observer_x, observer_y, observer_z) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
                    )) {
                        insert.setString(1, nomeFabrica);
                        insert.setString(2, world.getName());
                        insert.setInt(3, tntLocation.getBlockX());
                        insert.setInt(4, tntLocation.getBlockY());
                        insert.setInt(5, tntLocation.getBlockZ());
                        insert.setInt(6, observerLocation.getBlockX());
                        insert.setInt(7, observerLocation.getBlockY());
                        insert.setInt(8, observerLocation.getBlockZ());
                        insert.executeUpdate();
                        player.sendMessage("✅ Fábrica '" + nomeFabrica + "' registrada!");
                    }
                }

                player.sendMessage("📌 TNT: " + formatLoc(tntLocation));
                player.sendMessage("🧿 Observer: " + formatLoc(observerLocation));
            } catch (SQLException e) {
                plugin.getLogger().severe("Erro ao inserir/atualizar fábrica:");
                e.printStackTrace();
                player.sendMessage("❌ Erro ao salvar no banco de dados.");
            }
        }

        return true;
    }

    private Location calcularObserver(Player player, Location tntLoc) {
        BlockFace face = player.getFacing();
        return switch (face) {
            case NORTH -> tntLoc.clone().add(1, 0, 0);
            case SOUTH -> tntLoc.clone().add(-1, 0, 0);
            case EAST  -> tntLoc.clone().add(0, 0, -1);
            case WEST  -> tntLoc.clone().add(0, 0, 1);
            default -> null;
        };
    }

    private String formatLoc(Location loc) {
        return loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ();
    }
}