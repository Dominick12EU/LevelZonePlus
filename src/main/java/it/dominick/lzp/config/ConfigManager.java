package it.dominick.lzp.config;

import it.dominick.lzp.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;

public class ConfigManager {

    private final JavaPlugin plugin;
    private final File pluginFolder;
    private final Map<String, FileConfiguration> configCache = new HashMap<>();

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.pluginFolder = plugin.getDataFolder();
        loadAllConfigsToCache(pluginFolder);
    }

    public void loadAllConfigsToCache(File folder) {
        loadConfigsFromFolder(folder, "");
    }

    private void loadConfigsFromFolder(File folder, String baseDir) {
        if (folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                if (file.isFile() && file.getName().endsWith(".yml")) {
                    loadConfigToCache(file.getName(), baseDir);
                } else if (file.isDirectory()) {
                    loadConfigsFromFolder(file, baseDir + file.getName() + "/");
                }
            }
        }
    }

    public void loadConfigToCache(String fileName, String baseDir) {
        File configFile = new File(pluginFolder, baseDir + File.separator + fileName);
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        if (!config.getKeys(true).isEmpty()) {
            System.out.println(config.getKeys(true).toString());
            configCache.put(fileName, config);
        } else {
            Bukkit.getLogger().warning("File di configurazione vuoto: " + configFile.getPath());
        }
    }

    public String getString(String key) {
        return (String) get("messages.yml", key);
    }

    public Object get(String key) {
        return get("config.yml", key);
    }

    public Object get(String fileName, String key) {
        if (configCache.containsKey(fileName)) {
            FileConfiguration config = configCache.get(fileName);
            return config.get(key);
        }
        return null;
    }

    public void saveConfig(String fileName, FileConfiguration config) {
        saveConfig(fileName, config, "");
    }

    public void saveConfig(String fileName, FileConfiguration config, String subDirectory) {
        File configFile = new File(pluginFolder + File.separator + subDirectory, fileName);
        try {
            config.save(configFile);
            loadConfigToCache(fileName, subDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteConfig(String fileName) {
        deleteConfig(fileName, "");
    }

    public void deleteConfig(String fileName, String subDirectory) {
        File configFile = new File(pluginFolder + File.separator + subDirectory, fileName);
        if (configFile.exists()) {
            configFile.delete();
            configCache.remove(fileName);
        }
    }

    public void createFolderIfNotExists(String folderName) {
        File folder = new File(pluginFolder, folderName);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public void createAndCopyResource(String resourceName, String targetPath) {
        File targetFile = new File(pluginFolder, targetPath);

        if (!targetFile.exists()) {
            try {
                targetFile.getParentFile().mkdirs();

                InputStream inputStream = plugin.getResource(resourceName);
                if (inputStream == null) {
                    Bukkit.getLogger().warning("Resource non trovato: " + resourceName);
                    return;
                }

                OutputStream outputStream = new FileOutputStream(targetFile);
                byte[] buffer = new byte[1024];
                int lenght;
                while ((lenght = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, lenght);
                }

                inputStream.close();
                outputStream.close();

                reloadConfigs();
                Bukkit.getLogger().info("File resource '" + resourceName + "' copiato.");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public List<String> getFilesInFolder(String folderName) {
        File folder = new File(pluginFolder, folderName);
        List<String> fileNames = new ArrayList<>();

        if (folder.exists() && folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                if (file.isFile()) {
                    fileNames.add(file.getName());
                }
            }
        }

        return fileNames;
    }

    public boolean contains(String key) {
        return contains("config.yml", key);
    }

    public boolean contains(String fileName, String key) {
        if (configCache.containsKey(fileName)) {
            FileConfiguration config = configCache.get(fileName);
            return config.contains(key);
        }
        return false;
    }

    public List<String> getList(String key) {
        return getList("config.yml", key);
    }

    public List<String> getList(String fileName, String key) {
        if (configCache.containsKey(fileName)) {
            FileConfiguration config = configCache.get(fileName);
            return config.getStringList(key);
        }
        return new ArrayList<>();
    }

    public void set(String key, Object value) {
        set("config.yml", key, value);
    }

    public void set(String fileName, String key, Object value) {
        FileConfiguration config;
        if (configCache.containsKey(fileName)) {
            config = configCache.get(fileName);
        } else {
            config = new YamlConfiguration();
        }
        config.set(key, value);
        saveConfig(fileName, config);
    }

    public void reloadConfigs() {
        configCache.clear();
        loadAllConfigsToCache(pluginFolder);
    }

    public void printHelp(Player player) {
        List<String> help = Arrays.asList(
                // Main Gradient: #18D63C #75DB80 #18D63C
                // Second Gradient: #A5B1C2
                // Command Gradient: #BEBBB4
                "&r",
                " &#18D63C&lL&#28D747&le&#37D853&lv&#47D95E&le&#56D969&ll&#66DA75&lZ&#75DB80&lo&#66DA75&ln&#56D969&le&#47D95E&lP&#37D853&ll&#28D747&lu&#18D63C&ls &#A5B1C2v" + plugin.getDescription().getVersion(),
                "  &#A5B1C2By Dominick12",
                "&r",
                " &#18D63C&lC&#33D74F&lo&#4DD963&lm&#68DA76&lm&#68DA76&la&#4DD963&ln&#33D74F&ld&#18D63C&ls",
                "  &#BEBBB4/lzp help &#A5B1C2- &#BEBBB4Show this menu.",
                "&r"
        );

        for (String message : help) {
            ChatUtils.send(player, message);
        }
    }
}

