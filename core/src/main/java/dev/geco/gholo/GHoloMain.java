package dev.geco.gholo;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.*;

import dev.geco.gholo.api.event.*;
import dev.geco.gholo.cmd.*;
import dev.geco.gholo.cmd.tab.*;
import dev.geco.gholo.events.*;
import dev.geco.gholo.link.*;
import dev.geco.gholo.manager.*;
import dev.geco.gholo.manager.mm.*;
import dev.geco.gholo.util.*;

public class GHoloMain extends JavaPlugin {

    private SVManager svManager;
    public SVManager getSVManager() { return svManager; }

    private CManager cManager;
    public CManager getCManager() { return cManager; }

    private DManager dManager;
    public DManager getDManager() { return dManager; }

    private HoloManager holoManager;
    public HoloManager getHoloManager() { return holoManager; }

    private HoloAnimationManager holoAnimationManager;
    public HoloAnimationManager getHoloAnimationManager() { return holoAnimationManager; }

    private HoloImportManager holoImportManager;
    public HoloImportManager getHoloImportManager() { return holoImportManager; }

    private UManager uManager;
    public UManager getUManager() { return uManager; }

    private PManager pManager;
    public PManager getPManager() { return pManager; }

    private TManager tManager;
    public TManager getTManager() { return tManager; }

    private MManager mManager;
    public MManager getMManager() { return mManager; }

    private FormatUtil formatUtil;
    public FormatUtil getFormatUtil() { return formatUtil; }

    private IEntityUtil entityUtil;
    public IEntityUtil getEntityUtil() { return entityUtil; }

    private boolean placeholderAPILink;
    public boolean hasPlaceholderAPILink() { return placeholderAPILink; }

    private boolean supportsPaperFeature = false;
    public boolean supportsPaperFeature() { return supportsPaperFeature; }

    private boolean supportsTaskFeature = false;
    public boolean supportsTaskFeature() { return supportsTaskFeature; }

    public final String NAME = "GHolo";

    public final String RESOURCE = "000000";

    private static GHoloMain GPM;

    public static GHoloMain getInstance() { return GPM; }

    private void loadSettings(CommandSender Sender) {

        if(!connectDatabase(Sender)) return;

        getHoloAnimationManager().loadHoloAnimations();
        getHoloManager().createTables();
        getHoloManager().loadHolos();
        ImageUtil.generateFolder();
    }

    private void linkBStats() {

        BStatsLink bstats = new BStatsLink(getInstance(), 24037);

        bstats.addCustomChart(new BStatsLink.SimplePie("plugin_language", () -> getCManager().L_LANG));
        bstats.addCustomChart(new BStatsLink.SingleLineChart("holo_count", () -> getHoloManager().getHoloCount()));
        bstats.addCustomChart(new BStatsLink.SingleLineChart("holo_row_count", () -> getHoloManager().getHoloRowCount()));
    }

    public void onLoad() {

        GPM = this;

        svManager = new SVManager(getInstance());
        cManager = new CManager(getInstance());
        dManager = new DManager(getInstance());
        uManager = new UManager(getInstance());
        pManager = new PManager(getInstance());
        tManager = new TManager(getInstance());
        holoManager = new HoloManager(getInstance());
        holoAnimationManager = new HoloAnimationManager(getInstance());
        holoImportManager = new HoloImportManager(getInstance());

        formatUtil = new FormatUtil(getInstance());

        loadFeatures();

        mManager = supportsPaperFeature() ? new MPaperManager(getInstance()) : new MSpigotManager(getInstance());
    }

    public void onEnable() {

        if(!versionCheck()) return;

        entityUtil = (IEntityUtil) getSVManager().getPackageObject("util.EntityUtil", getInstance());

        loadPluginDependencies();
        loadSettings(Bukkit.getConsoleSender());

        setupCommands();
        setupEvents();
        linkBStats();

        getMManager().sendMessage(Bukkit.getConsoleSender(), "Plugin.plugin-enabled");

        printPluginLinks(Bukkit.getConsoleSender());
        getUManager().checkForUpdates();
    }

    public void onDisable() {

        unload();
        getMManager().sendMessage(Bukkit.getConsoleSender(), "Plugin.plugin-disabled");
    }

    private void unload() {

        getDManager().close();
        getHoloManager().unloadHolos();
        getHoloAnimationManager().stopHoloAnimations();
    }

    private void setupCommands() {

        getCommand("gholo").setExecutor(new GHoloCommand(getInstance()));
        getCommand("gholo").setTabCompleter(new GHoloTabComplete(getInstance()));
        getCommand("gholo").setPermissionMessage(getMManager().getMessage("Messages.command-permission-error"));
        getCommand("gholoreload").setExecutor(new GHoloReloadCommand(getInstance()));
        getCommand("gholoreload").setTabCompleter(new EmptyTabComplete());
        getCommand("gholoreload").setPermissionMessage(getMManager().getMessage("Messages.command-permission-error"));
    }

    private void setupEvents() {

        getServer().getPluginManager().registerEvents(new PlayerEvents(getInstance()), getInstance());
    }

    private void loadFeatures() {

        try {
            Class.forName("io.papermc.paper.event.entity.EntityMoveEvent");
            supportsPaperFeature = true;
        } catch (ClassNotFoundException ignored) { supportsPaperFeature = false; }

        try {
            Class.forName("io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler");
            supportsTaskFeature = true;
        } catch (ClassNotFoundException ignored) { supportsTaskFeature = false; }
    }

    private void loadPluginDependencies() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
        placeholderAPILink = plugin != null && plugin.isEnabled();
    }

    private void printPluginLinks(CommandSender Sender) {
        if(placeholderAPILink) getMManager().sendMessage(Sender, "Plugin.plugin-link", "%Link%", Bukkit.getPluginManager().getPlugin("PlaceholderAPI").getName());
    }

    public void reload(CommandSender Sender) {
        Bukkit.getPluginManager().callEvent(new GHoloReloadEvent(getInstance()));
        unload();
        getCManager().reload();
        getMManager().loadMessages();
        loadPluginDependencies();
        loadSettings(Sender);
        printPluginLinks(Sender);
        getUManager().checkForUpdates();
    }

    private boolean connectDatabase(CommandSender Sender) {
        boolean connect = getDManager().connect();
        if(connect) return true;
        getMManager().sendMessage(Sender, "Plugin.plugin-data");
        Bukkit.getPluginManager().disablePlugin(getInstance());
        return false;
    }

    private boolean versionCheck() {
        if(!getSVManager().isNewerOrVersion(19, 4) || !getSVManager().isAvailable()) {
            getMManager().sendMessage(Bukkit.getConsoleSender(), "Plugin.plugin-version", "%Version%", getSVManager().getServerVersion());
            getUManager().checkForUpdates();
            Bukkit.getPluginManager().disablePlugin(getInstance());
            return false;
        }
        return true;
    }

}