package dev.geco.gholo;

import dev.geco.gholo.api.event.GHoloLoadedEvent;
import dev.geco.gholo.api.event.GHoloReloadEvent;
import dev.geco.gholo.cmd.GHoloCommand;
import dev.geco.gholo.cmd.GHoloReloadCommand;
import dev.geco.gholo.cmd.tab.EmptyTabComplete;
import dev.geco.gholo.cmd.tab.GHoloTabComplete;
import dev.geco.gholo.event.PlayerEventHandler;
import dev.geco.gholo.metric.BStatsMetric;
import dev.geco.gholo.service.ConfigService;
import dev.geco.gholo.service.DataService;
import dev.geco.gholo.service.HoloAnimationService;
import dev.geco.gholo.service.HoloImportService;
import dev.geco.gholo.service.HoloService;
import dev.geco.gholo.service.MessageService;
import dev.geco.gholo.service.PermissionService;
import dev.geco.gholo.service.TaskService;
import dev.geco.gholo.service.UpdateService;
import dev.geco.gholo.service.VersionService;
import dev.geco.gholo.service.message.PaperMessageService;
import dev.geco.gholo.service.message.SpigotMessageService;
import dev.geco.gholo.util.FormatUtil;
import dev.geco.gholo.util.IEntityUtil;
import dev.geco.gholo.util.ImageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class GHoloMain extends JavaPlugin {

    public static final String NAME = "GHolo";
    public static final String RESOURCE_ID = "121144";

    private final int BSTATS_RESOURCE_ID = 24075;
    private static GHoloMain gHoloMain;
    private ConfigService configService;
    private MessageService messageService;
    private UpdateService updateService;
    private PermissionService permissionService;
    private TaskService taskService;
    private DataService dataService;
    private VersionService versionService;
    private HoloService holoService;
    private HoloAnimationService holoAnimationService;
    private HoloImportService holoImportService;
    private FormatUtil formatUtil;
    private IEntityUtil entityUtil;
    private boolean placeholderAPILink = false;
    private boolean supportsPaperFeature = false;
    private boolean supportsTaskFeature = false;

    public static GHoloMain getInstance() { return gHoloMain; }

    public ConfigService getConfigService() { return configService; }

    public MessageService getMessageService() { return messageService; }

    public UpdateService getUpdateService() { return updateService; }

    public PermissionService getPermissionService() { return permissionService; }

    public TaskService getTaskService() { return taskService; }

    public DataService getDataService() { return dataService; }

    public VersionService getVersionService() { return versionService; }

    public HoloService getHoloService() { return holoService; }

    public HoloAnimationService getHoloAnimationService() { return holoAnimationService; }

    public HoloImportService getHoloImportService() { return holoImportService; }

    public FormatUtil getFormatUtil() { return formatUtil; }

    public IEntityUtil getEntityUtil() { return entityUtil; }

    public boolean hasPlaceholderAPILink() { return placeholderAPILink; }

    public boolean supportsPaperFeature() { return supportsPaperFeature; }

    public boolean supportsTaskFeature() { return supportsTaskFeature; }

    public void onLoad() {
        gHoloMain = this;

        configService = new ConfigService(this);
        updateService = new UpdateService(this);
        permissionService = new PermissionService();
        taskService = new TaskService(this);
        dataService = new DataService(this);
        versionService = new VersionService(this);
        holoService = new HoloService(this);
        holoAnimationService = new HoloAnimationService(this);
        holoImportService = new HoloImportService(this);

        formatUtil = new FormatUtil(this);

        loadFeatures();

        messageService = supportsPaperFeature ? new PaperMessageService(this) : new SpigotMessageService(this);
    }

    public void onEnable() {
        if(!versionCheck()) return;

        entityUtil = (IEntityUtil) versionService.getPackageObjectInstance("util.EntityUtil");

        loadPluginDependencies();
        loadSettings(Bukkit.getConsoleSender());

        setupCommands();
        setupEvents();
        setupBStatsMetric();

        Bukkit.getPluginManager().callEvent(new GHoloLoadedEvent(this));

        messageService.sendMessage(Bukkit.getConsoleSender(), "Plugin.plugin-enabled");

        printPluginLinks(Bukkit.getConsoleSender());
        updateService.checkForUpdates();
    }

    public void onDisable() {
        unload();
        messageService.sendMessage(Bukkit.getConsoleSender(), "Plugin.plugin-disabled");
    }

    private void loadSettings(CommandSender sender) {
        if(!connectDatabase(sender)) return;
        holoAnimationService.loadHoloAnimations();
        holoService.createTables();
        holoService.loadHolos();
        ImageUtil.generateFolder();
    }

    public void reload(CommandSender sender) {
        GHoloReloadEvent reloadEvent = new GHoloReloadEvent(this);
        Bukkit.getPluginManager().callEvent(reloadEvent);
        if(reloadEvent.isCancelled()) return;

        unload();
        configService.reload();
        messageService.loadMessages();
        loadPluginDependencies();
        loadSettings(sender);
        printPluginLinks(sender);
        updateService.checkForUpdates();

        Bukkit.getPluginManager().callEvent(new GHoloLoadedEvent(this));
    }

    private void unload() {
        dataService.close();
        holoService.unloadHolos();
        holoAnimationService.stopHoloAnimations();
    }

    private void setupCommands() {
        getCommand("gholo").setExecutor(new GHoloCommand(this));
        getCommand("gholo").setTabCompleter(new GHoloTabComplete(this));
        getCommand("gholo").setPermissionMessage(messageService.getMessage("Messages.command-permission-error"));
        getCommand("gholoreload").setExecutor(new GHoloReloadCommand(this));
        getCommand("gholoreload").setTabCompleter(new EmptyTabComplete());
        getCommand("gholoreload").setPermissionMessage(messageService.getMessage("Messages.command-permission-error"));
    }

    private void setupEvents() {
        getServer().getPluginManager().registerEvents(new PlayerEventHandler(this), this);
    }

    private boolean versionCheck() {
        if(versionService.isNewerOrVersion(19, 4) && versionService.isAvailable()) return true;
        messageService.sendMessage(Bukkit.getConsoleSender(), "Plugin.plugin-version", "%Version%", versionService.getServerVersion());
        updateService.checkForUpdates();
        Bukkit.getPluginManager().disablePlugin(this);
        return false;
    }

    private boolean connectDatabase(CommandSender sender) {
        boolean connected = dataService.connect();
        if(connected) return true;
        messageService.sendMessage(sender, "Plugin.plugin-data");
        Bukkit.getPluginManager().disablePlugin(this);
        return false;
    }

    private void loadFeatures() {
        try {
            Class.forName("io.papermc.paper.event.entity.EntityMoveEvent");
            supportsPaperFeature = true;
        } catch(ClassNotFoundException ignored) { supportsPaperFeature = false; }

        try {
            Class.forName("io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler");
            supportsTaskFeature = true;
        } catch(ClassNotFoundException ignored) { supportsTaskFeature = false; }
    }

    private void loadPluginDependencies() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
        placeholderAPILink = plugin != null && plugin.isEnabled();
    }

    private void printPluginLinks(CommandSender sender) {
        if(placeholderAPILink) messageService.sendMessage(sender, "Plugin.plugin-link", "%Link%", Bukkit.getPluginManager().getPlugin("PlaceholderAPI").getName());
    }

    private void setupBStatsMetric() {
        BStatsMetric bStatsMetric = new BStatsMetric(this, BSTATS_RESOURCE_ID);

        bStatsMetric.addCustomChart(new BStatsMetric.SimplePie("plugin_language", () -> configService.L_LANG));
        bStatsMetric.addCustomChart(new BStatsMetric.AdvancedPie("minecraft_version_player_amount", () -> Map.of(versionService.getServerVersion(), Bukkit.getOnlinePlayers().size())));
        bStatsMetric.addCustomChart(new BStatsMetric.SingleLineChart("holo_count", () -> holoService.getHoloCount()));
        bStatsMetric.addCustomChart(new BStatsMetric.SingleLineChart("holo_row_count", () -> holoService.getHoloRowCount()));
    }

}