package me.yuzegod.lobbylevel.Config;

import java.util.logging.*;
import org.bukkit.plugin.*;
import org.yaml.snakeyaml.representer.*;
import org.yaml.snakeyaml.*;
import org.bukkit.configuration.file.*;
import org.yaml.snakeyaml.constructor.*;
import org.apache.commons.lang.*;
import org.bukkit.*;
import com.google.common.base.*;
import org.bukkit.configuration.*;
import com.google.common.io.*;
import java.io.*;

public class FileConfig extends YamlConfiguration
{
    protected File file;
    protected Logger loger;
    protected Plugin plugin;
    protected final DumperOptions yamlOptions;
    protected final Representer yamlRepresenter;
    protected final Yaml yaml;
    
    private FileConfig(final File file) {
        this.yamlOptions = new DumperOptions();
        this.yamlRepresenter = (Representer)new YamlRepresenter();
        this.yaml = new Yaml((BaseConstructor)new YamlConstructor(), this.yamlRepresenter, this.yamlOptions);
        Validate.notNull((Object)file, "File cannot be null");
        this.file = file;
        this.loger = Bukkit.getLogger();
        this.init(file);
    }
    
    private FileConfig(final InputStream stream) {
        this.yamlOptions = new DumperOptions();
        this.yamlRepresenter = (Representer)new YamlRepresenter();
        this.yaml = new Yaml((BaseConstructor)new YamlConstructor(), this.yamlRepresenter, this.yamlOptions);
        this.loger = Bukkit.getLogger();
        this.init(stream);
    }
    
    public FileConfig(final Plugin plugin) {
        this(plugin, "config.yml");
    }
    
    public FileConfig(final Plugin plugin, final File file) {
        this.yamlOptions = new DumperOptions();
        this.yamlRepresenter = (Representer)new YamlRepresenter();
        this.yaml = new Yaml((BaseConstructor)new YamlConstructor(), this.yamlRepresenter, this.yamlOptions);
        Validate.notNull((Object)file, "File cannot be null");
        Validate.notNull((Object)plugin, "Plugin cannot be null");
        this.plugin = plugin;
        this.file = file;
        this.loger = plugin.getLogger();
        this.check(file);
        this.init(file);
    }
    
    public FileConfig(final Plugin plugin, final String filename) {
        this(plugin, new File(plugin.getDataFolder(), filename));
    }
    
    private void check(final File file) {
        final String filename = file.getName();
        final InputStream stream = this.plugin.getResource(filename);
        try {
            if (!file.exists()) {
                if (stream == null) {
                    file.createNewFile();
                    this.loger.info("\u914d\u7f6e\u6587\u4ef6 " + filename + " \u521b\u5efa\u5931\u8d25...");
                }
                else {
                    this.plugin.saveResource(filename, true);
                    this.loger.info("\u914d\u7f6e\u6587\u4ef6 " + filename + " \u4e0d\u5b58\u5728 \u4ece\u63d2\u4ef6\u91ca\u653e...");
                }
            }
            else {
                final FileConfig newcfg = new FileConfig(stream);
                final FileConfig oldcfg = new FileConfig(file);
                final String newver = newcfg.getString("version");
                final String oldver = oldcfg.getString("version");
                if (newver != null && !newver.equalsIgnoreCase(oldver)) {
                    this.loger.warning("\u914d\u7f6e\u6587\u4ef6: " + filename + " \u7248\u672c " + oldver + " \u8fc7\u4f4e \u6b63\u5728\u5347\u7ea7\u5230 " + newver + " ...");
                    try {
                        oldcfg.save(new File(file.getParent(), String.valueOf(filename) + ".backup"));
                        this.loger.warning("\u914d\u7f6e\u6587\u4ef6: " + filename + " \u5df2\u5907\u4efd\u4e3a " + filename + ".backup !");
                    }
                    catch (IOException e) {
                        this.loger.warning("\u914d\u7f6e\u6587\u4ef6: " + filename + " \u5907\u4efd\u5931\u8d25!");
                    }
                    this.plugin.saveResource(filename, true);
                    this.loger.info("\u914d\u7f6e\u6587\u4ef6: " + filename + " \u5347\u7ea7\u6210\u529f!");
                }
            }
        }
        catch (IOException e2) {
            this.loger.info("\u914d\u7f6e\u6587\u4ef6 " + filename + " \u521b\u5efa\u5931\u8d25...");
        }
    }
    
    private void init(final File file) {
        Validate.notNull((Object)file, "File cannot be null");
        try {
            final FileInputStream stream = new FileInputStream(file);
            this.init(stream);
        }
        catch (FileNotFoundException e) {
            this.loger.info("\u914d\u7f6e\u6587\u4ef6 " + file.getName() + " \u4e0d\u5b58\u5728...");
        }
    }
    
    private void init(final InputStream stream) {
        Validate.notNull((Object)stream, "Stream cannot be null");
        try {
            this.load(new InputStreamReader(stream, Charsets.UTF_8));
        }
        catch (IOException ex) {
            this.loger.info("\u914d\u7f6e\u6587\u4ef6 " + this.file.getName() + " \u8bfb\u53d6\u9519\u8bef...");
        }
        catch (InvalidConfigurationException ex2) {
            this.loger.info("\u914d\u7f6e\u6587\u4ef6 " + this.file.getName() + " \u683c\u5f0f\u9519\u8bef...");
        }
    }
    
    public void load(final File file) throws FileNotFoundException, IOException, InvalidConfigurationException {
        Validate.notNull((Object)file, "File cannot be null");
        final FileInputStream stream = new FileInputStream(file);
        this.load(new InputStreamReader(stream, Charsets.UTF_8));
    }
    
    public void load(final Reader reader) throws IOException, InvalidConfigurationException {
        final BufferedReader input = (BufferedReader)((reader instanceof BufferedReader) ? reader : new BufferedReader(reader));
        final StringBuilder builder = new StringBuilder();
        try {
            String line;
            while ((line = input.readLine()) != null) {
                builder.append(line);
                builder.append('\n');
            }
        }
        finally {
            input.close();
        }
        input.close();
        this.loadFromString(builder.toString());
    }
    
    public void reload() {
        this.init(this.file);
    }
    
    public void save() {
        if (this.file == null) {
            this.loger.info("\u672a\u5b9a\u4e49\u914d\u7f6e\u6587\u4ef6\u8def\u5f84 \u4fdd\u5b58\u5931\u8d25!");
        }
        try {
            this.save(this.file);
        }
        catch (IOException e) {
            this.loger.info("\u914d\u7f6e\u6587\u4ef6 " + this.file.getName() + " \u4fdd\u5b58\u9519\u8bef...");
            e.printStackTrace();
        }
    }
    
    public void save(final File file) throws IOException {
        Validate.notNull((Object)file, "File cannot be null");
        Files.createParentDirs(file);
        final String data = this.saveToString();
        final Writer writer = new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8);
        try {
            writer.write(data);
        }
        finally {
            writer.close();
        }
        writer.close();
    }
    
    public String saveToString() {
        this.yamlOptions.setIndent(this.options().indent());
        this.yamlOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        this.yamlRepresenter.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        final String header = this.buildHeader();
        String dump = this.yaml.dump((Object)this.getValues(false));
        if (dump.equals("{}\n")) {
            dump = "";
        }
        return String.valueOf(header) + dump;
    }
}
