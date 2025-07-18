package com.teste21;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.block.BlockFace;

import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockRedstoneEvent;

import org.bukkit.event.player.PlayerJoinEvent;

import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import java.util.function.Consumer;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block; // Already imported, good
import org.bukkit.command.ConsoleCommandSender; // Already imported, good
import org.bukkit.entity.EntityType; // Already imported, good
import org.bukkit.entity.TNTPrimed; // Already imported, good
import org.bukkit.event.EventHandler; // Already imported, good
import org.bukkit.event.Listener; // Already imported, good
import org.bukkit.event.entity.EntityExplodeEvent; // Ensure this is imported
import org.bukkit.inventory.ItemStack; // Import for ItemStack
import org.bukkit.inventory.meta.ItemMeta; // Import for ItemMeta
import org.bukkit.enchantments.Enchantment; // Import for Enchantment
import net.md_5.bungee.api.ChatColor; // Import for ChatColor
import java.util.ArrayList; // Import for ArrayList
import java.util.List; // Import for List
import org.bukkit.Particle; // Import for Particle
import org.bukkit.Sound; // Import for Sound

import java.util.HashMap;
import java.util.Map;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.entity.Player;



import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.UUID;

public class Main extends JavaPlugin implements Listener {
    private Connection connection;
    private AgendadorTNT agendadorTNT;
    private Solana solana; // Classe Solana que gerencia a conexão com o config.yml Solana.java
    private FileConfiguration config; // Armazena o config.yml Solana.java
    private final Map<Location, UUID> tntColocada = new HashMap<>();


@Override
public void onEnable() {
    getLogger().info("✅ Plugin iniciado com configurações!");

    // Banco e configs
    connectDatabase();
    carregarFabricas();
    createConfig();

    // Eventos
    saveDefaultConfig(); // Garante que o config.yml seja carregado
    this.config = getConfig();
    this.solana = new Solana(this, config, connection);
    

    getServer().getPluginManager().registerEvents(this, this);
    Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);

    config = getConfig(); // Inicializa config.yml corretamente Solana.java
    solana = new Solana(this, config, connection); // Passa config.yml e conexão para Solana Solana.java
    


    

    // Comandos
    getCommand("titulo").setExecutor(new TitleCommand(this));
    getCommand("verificatnt").setExecutor(new ExecutarFabricaCommand(this));
    getCommand("tnt").setExecutor(new VerificaTNTCommand(this));
    getCommand("resetarfabricas").setExecutor(new ResetarFabricasCommand(this));
    getCommand("listfabric").setExecutor(new ListarFabricasCommand(this));
    getCommand("ativarfabrica").setExecutor(new AtivarFabricaCommand(this));
    getCommand("desativarfabrica").setExecutor(new DesativarFabricaCommand(this));

    // Inicializa e agenda as fábricas no Folia
    this.agendadorTNT = new AgendadorTNT(this, solana);
    Bukkit.getGlobalRegionScheduler().runDelayed(this, task -> {
        agendadorTNT.agendarTodasFabricas();
        getLogger().info("🔁 Agendador de TNT iniciado com sucesso!");
    }, 1L);
}





// Getter público para usar em comandos
public AgendadorTNT getAgendadorTNT() {
    return this.agendadorTNT;
}




@Override
public void onDisable() {
    try {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            getLogger().info("🔌 Conexão com o banco de dados encerrada.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


public Connection getConnection() {
    return this.connection;
}

public Solana getSolana() {
    return this.solana;
}

@EventHandler
public void onTNTPlace(BlockPlaceEvent event) {
    if (event.getBlock().getType() == Material.TNT) {
        Player player = event.getPlayer();
        Location loc = event.getBlock().getLocation();
        tntColocada.put(loc, player.getUniqueId());
    }
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
        if (!dbFile.getParentFile().exists()) dbFile.getParentFile().mkdirs();

        connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
        Statement statement = connection.createStatement();

        // Tabela de títulos dos jogadores
        statement.execute("""
            CREATE TABLE IF NOT EXISTS players (
                uuid TEXT PRIMARY KEY,
                title TEXT
            )
        """);

        // Tabela das fábricas
        statement.execute("""
            CREATE TABLE IF NOT EXISTS fabricas (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                world TEXT,
                tnt_x INTEGER, tnt_y INTEGER, tnt_z INTEGER,
                observer_x INTEGER, observer_y INTEGER, observer_z INTEGER,
                ativa_agendamento INTEGER DEFAULT 1
            )
        """);

        statement.execute("""
            CREATE TABLE IF NOT EXISTS banco (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        jogador TEXT UNIQUE,
        saldo DECIMAL(10,2) DEFAULT 500,
        divida DECIMAL(10,2) DEFAULT 0,
        investimento DECIMAL(10,2) DEFAULT 0
    )
        """);

        statement.execute("""
            CREATE TABLE IF NOT EXISTS jogadores (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        nome TEXT UNIQUE NOT NULL
    )
        """);

        statement.execute("""
            CREATE TABLE IF NOT EXISTS carteiras (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        jogador_id INTEGER NOT NULL,
        endereco TEXT UNIQUE NOT NULL,
        chave_privada TEXT NOT NULL,
        frase_secreta TEXT NOT NULL,
        FOREIGN KEY (jogador_id) REFERENCES jogadores(id) ON DELETE CASCADE
    )
        """);

        statement.execute("""
            CREATE TABLE IF NOT EXISTS livro_caixa (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        jogador TEXT NOT NULL,
        tipo_transacao TEXT NOT NULL,
        valor FLOAT NOT NULL,
        moeda TEXT NOT NULL,
        assinatura TEXT NOT NULL,
        data_hora DATETIME DEFAULT CURRENT_TIMESTAMP
    )
        """);

        statement.execute("""
            CREATE TABLE IF NOT EXISTS airdrops (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                jogador TEXT NOT NULL,
                valor FLOAT NOT NULL,
                data_hora DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """);

        getLogger().info("🗃️ Banco de dados SQLite conectado e tabelas criadas/verificadas com sucesso!");
    } catch (SQLException e) {
        getLogger().severe("❌ Erro ao conectar ao banco de dados!");
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

@EventHandler
public void onTNTExplode(EntityExplodeEvent event) {
    if (event.getEntityType() != EntityType.TNT) return;





        if (event.getEntityType() == EntityType.TNT) {
            Location loc = event.getLocation();
            World world = loc.getWorld();

            // Você tinha um comando 'verificatnt' aqui, que deve estar na sua classe CommandExecutor
            // Se você ainda precisa executá-lo no evento de explosão, mantenha a linha abaixo:
            // ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
            // Bukkit.dispatchCommand(console, "verificatnt");

            // Dropar 64 madeiras
            world.dropItemNaturally(loc, new ItemStack(Material.OAK_LOG, 64));

            // Dropar 64 frascos de XP
            world.dropItemNaturally(loc, new ItemStack(Material.EXPERIENCE_BOTTLE, 64));

            // --- Criando e Dropando a Espada Evolution ---
            ItemStack espadaEvolution = new ItemStack(Material.DIAMOND_SWORD);
            ItemMeta espadaMeta = espadaEvolution.getItemMeta();

            if (espadaMeta != null) {
                espadaMeta.setDisplayName(ChatColor.YELLOW + "Espada Evolution");

                List<String> espadaLore = new ArrayList<>();
                espadaLore.add("Feito Para Guerras");
                espadaLore.add("Ritizin00");
                espadaMeta.setLore(espadaLore);

                espadaMeta.addEnchant(Enchantment.getByName("BANE_OF_ARTHROPODS"), 10, true);
                espadaMeta.addEnchant(Enchantment.getByName("FIRE_ASPECT"), 10, true);
                espadaMeta.addEnchant(Enchantment.getByName("KNOCKBACK"), 2, true);
                espadaMeta.addEnchant(Enchantment.getByName("LOOTING"), 10, true);
                espadaMeta.addEnchant(Enchantment.getByName("SHARPNESS"), 10, true);
                espadaMeta.addEnchant(Enchantment.getByName("SMITE"), 2, true);
                espadaMeta.addEnchant(Enchantment.getByName("SWEEPING_EDGE"), 10, true);

                espadaMeta.setUnbreakable(true);

                espadaEvolution.setItemMeta(espadaMeta);
            }
            world.dropItemNaturally(loc, espadaEvolution);
            // --- Fim da Criação e Drop da Espada ---

            // --- Criando e Dropando a Picareta Evolution ---
            ItemStack picaretaEvolution = new ItemStack(Material.DIAMOND_PICKAXE);
            ItemMeta picaretaMeta = picaretaEvolution.getItemMeta();

            if (picaretaMeta != null) {
                picaretaMeta.setDisplayName(ChatColor.YELLOW + "Picareta Evolution");

                List<String> picaretaLore = new ArrayList<>();
                picaretaLore.add("Feita para Minerar");
                picaretaLore.add("Ritizin00");
                picaretaMeta.setLore(picaretaLore);

                // Encantamentos da Picareta
                picaretaMeta.addEnchant(Enchantment.getByName("EFFICIENCY"), 10, true);
                picaretaMeta.addEnchant(Enchantment.getByName("FORTUNE"), 10, true);

                picaretaMeta.setUnbreakable(true);

                picaretaEvolution.setItemMeta(picaretaMeta);
            }
            world.dropItemNaturally(loc, picaretaEvolution);
            // --- Fim da Criação e Drop da Picareta ---

            // --- Criando e Dropando o Arco Evolution ---
            ItemStack arcoEvolution = new ItemStack(Material.BOW);
            ItemMeta arcoMeta = arcoEvolution.getItemMeta();

            if (arcoMeta != null) {
                arcoMeta.setDisplayName(ChatColor.YELLOW + "Arco Evolution");

                List<String> arcoLore = new ArrayList<>();
                arcoLore.add("Alvo em Mira");
                arcoMeta.setLore(arcoLore);

                // Encantamentos do Arco
                arcoMeta.addEnchant(Enchantment.getByName("FLAME"), 1, true);
                arcoMeta.addEnchant(Enchantment.getByName("INFINITY"), 1, true);
                arcoMeta.addEnchant(Enchantment.getByName("POWER"), 10, true);

                arcoMeta.setUnbreakable(true);

                arcoEvolution.setItemMeta(arcoMeta);
            }
            world.dropItemNaturally(loc, arcoEvolution);
            // --- Fim da Criação e Drop do Arco ---
            
            // Efeito visual e sonoro
            world.spawnParticle(Particle.EXPLOSION_EMITTER, loc, 1);
            world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
        }
    }



    private void carregarFabricas() {
    try {
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM fabricas");

        while (rs.next()) {
            String nome = rs.getString("nome");
            String worldName = rs.getString("world");
            World world = Bukkit.getWorld(worldName);
            if (world == null) continue;

            Location tntLoc = new Location(world, rs.getInt("tnt_x"), rs.getInt("tnt_y"), rs.getInt("tnt_z"));
            Location observerLoc = new Location(world, rs.getInt("observer_x"), rs.getInt("observer_y"), rs.getInt("observer_z"));

            // Aqui você pode armazenar essas fábricas em um mapa:
            // fabricasMap.put(nome, new Fábrica(tntLoc, observerLoc));
            getLogger().info("✅ Fábrica carregada: " + nome);
        }
    } catch (SQLException e) {
        getLogger().severe("Erro ao carregar fábricas do banco de dados!");
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