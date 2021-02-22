package io.github.taixue.plugin.customcommands.config;

import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public abstract class Config {
    protected FileConfiguration fileConfiguration;
    protected MemorySection configSection;
    protected File file;

    public Config(File file, String head) {
        setFileConfiguration(YamlConfiguration.loadConfiguration(file));
        setFile(file);
        configSection = Objects.requireNonNull(((MemorySection) fileConfiguration.get(head)));
    }

    public FileConfiguration getFileConfiguration() {
        return fileConfiguration;
    }

    public MemorySection getConfigSection() {
        return configSection;
    }

    public void setConfigSection(MemorySection configSection) {
        this.configSection = configSection;
    }

    public void setFileConfiguration(FileConfiguration fileConfiguration) {
        this.fileConfiguration = fileConfiguration;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void save() throws IOException {
        fileConfiguration.save(file);
    }

    public Object get(String path) {
        return configSection.get(path);
    }

    public Object get(String path, Object defaultValue) {
        return configSection.get(path, defaultValue);
    }

    public void set(String path, Object value) {
        configSection.set(path, value);
    }
}
