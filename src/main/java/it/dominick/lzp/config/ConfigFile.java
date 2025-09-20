package it.dominick.lzp.config;

import lombok.Getter;

@Getter
public class ConfigFile {
    public static final ConfigFile CONFIG = new ConfigFile("config.yml", "");
    public static final ConfigFile MESSAGES = new ConfigFile("messages.yml", "");

    private final String fileName;
    private final String subDirectory;

    public ConfigFile(String fileName, String subDirectory) {
        this.fileName = fileName;
        this.subDirectory = subDirectory;
    }

    public String getFilePath() {
        return subDirectory.isEmpty() ? fileName : subDirectory + "/" + fileName;
    }
}
