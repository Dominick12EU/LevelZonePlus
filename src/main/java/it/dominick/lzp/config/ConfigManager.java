package it.dominick.lzp.config;

import it.dominick.lzp.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
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
            configCache.put(baseDir + fileName, config);
        } else {
            Bukkit.getLogger().warning("File di configurazione vuoto: " + configFile.getPath());
        }
    }

    public String getString(String key) {
        return getString(ConfigFile.MESSAGES, key);
    }

    public String getString(ConfigFile configFile, String key) {
        String string = (String) get(configFile, key);
        String prefix = (String) get(ConfigFile.MESSAGES, "global.prefix");
        return ChatUtils.placeholder(string, "{prefix}", prefix);
    }

    public boolean getBoolean(String key) {
        return getBoolean(ConfigFile.CONFIG, key);
    }

    public boolean getBoolean(ConfigFile configFile, String key) {
        if (configCache.containsKey(configFile.getFilePath())) {
            FileConfiguration config = configCache.get(configFile.getFilePath());
            return config.getBoolean(key);
        }
        return false;
    }

    public Object get(String key) {
        return get(ConfigFile.CONFIG, key);
    }

    public Object get(ConfigFile configFile, String key) {
        if (configCache.containsKey(configFile.getFilePath())) {
            FileConfiguration config = configCache.get(configFile.getFilePath());
            return config.get(key);
        }
        return null;
    }

    public ConfigurationSection getConfigurationSection(ConfigFile configFile, String key) {
        if (configCache.containsKey(configFile.getFilePath())) {
            FileConfiguration config = configCache.get(configFile.getFilePath());
            return config.getConfigurationSection(key);
        }
        return null;
    }

    public void saveConfig(ConfigFile configFile, FileConfiguration config) {
        saveConfig(configFile, config, configFile.getSubDirectory());
    }

    public void saveConfig(ConfigFile configFile, FileConfiguration config, String subDirectory) {
        File configFileObject = new File(pluginFolder + File.separator + subDirectory, configFile.getFileName());
        try {
            config.save(configFileObject);
            loadConfigToCache(configFile.getFileName(), subDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig(ConfigFile configFile) {
        FileConfiguration config = configCache.get(configFile.getFilePath());
        if (config == null) {
            File configFileObject = new File(pluginFolder, configFile.getSubDirectory() + File.separator + configFile.getFileName());

            File parentDir = configFileObject.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }

            if (!configFileObject.exists()) {
                try {
                    configFileObject.createNewFile();
                } catch (IOException e) {
                    Bukkit.getLogger().warning("Impossibile creare il file di configurazione: " + configFileObject.getPath());
                    e.printStackTrace();
                    return null;
                }
            }

            config = YamlConfiguration.loadConfiguration(configFileObject);
            configCache.put(configFile.getFilePath(), config);
        }
        return config;
    }



    public void deleteConfig(ConfigFile configFile) {
        deleteConfig(configFile, configFile.getSubDirectory());
    }

    public void deleteConfig(ConfigFile configFile, String subDirectory) {
        File configFileObject = new File(pluginFolder + File.separator + subDirectory, configFile.getFileName());
        if (configFileObject.exists()) {
            configFileObject.delete();
            configCache.remove(configFile.getFilePath());
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
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
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

    public boolean contains(ConfigFile configFile, String key) {
        if (configCache.containsKey(configFile.getFilePath())) {
            FileConfiguration config = configCache.get(configFile.getFilePath());
            return config.contains(key);
        }
        return false;
    }

    public List<String> getList(String key) {
        return getList(ConfigFile.CONFIG, key);
    }

    public List<String> getList(ConfigFile configFile, String key) {
        if (configCache.containsKey(configFile.getFilePath())) {
            FileConfiguration config = configCache.get(configFile.getFilePath());
            return config.getStringList(key);
        }
        return new ArrayList<>();
    }

    public void set(ConfigFile configFile, String key, Object value) {
        FileConfiguration config;
        if (configCache.containsKey(configFile.getFilePath())) {
            config = configCache.get(configFile.getFilePath());
        } else {
            config = new YamlConfiguration();
        }
        config.set(key, value);
        saveConfig(configFile, config);
    }

    public void printHelp(Player player) {
        List<String> help = Arrays.asList(
                // Main Gradient: #209966 #33F342 #67CB70
                "&r",
                " &#209966&lL&#23A860&le&#26B75A&lv&#2AC654&le&#2DD54E&ll&#30E448&lZ&#33F342&lo&#3CEC4A&ln&#44E651&le&#4DDF59&lP&#56D861&ll&#5ED268&lu&#67CB70&ls &7v" + plugin.getDescription().getVersion(),
                "  &7By Dominick12",
                "&r",
                " &#209966&lC&#25B35C&lo&#2BCC51&lm&#30E647&lm&#3AED49&la&#49E256&ln&#58D663&ld&#67CB70&ls",
                "  &7/lzp help &8- &7Show this menu.",
                "  &7/lzp pos1 &8- &7Set position 1.",
                "  &7/lzp pos2 &8- &7Set position 2.",
                "  &7/lzp create <name> &8- &7Create a region.",
                "  &7/lzp delete <name> &8- &7Remove a region.",
                "&r"
        );

        for (String message : help) {
            ChatUtils.send(player, message);
        }
    }

    public void reloadConfigs() {
        configCache.clear();
        loadAllConfigsToCache(pluginFolder);
    }
}