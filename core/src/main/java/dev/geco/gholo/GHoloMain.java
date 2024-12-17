package dev.geco.gholo;

import java.util.*;

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

    public static final String NAME = "GHolo";
    public static final String RESOURCE = "121144";

    private static GHoloMain GPM;
    private CManager cManager;
    private MManager mManager;
    private UManager uManager;
    private PManager pManager;
    private TManager tManager;
    private DManager dManager;
    private SVManager svManager;
    private HoloManager holoManager;
    private HoloAnimationManager holoAnimationManager;
    private HoloImportManager holoImportManager;
    private FormatUtil formatUtil;
    private IEntityUtil entityUtil;
    private boolean placeholderAPILink;
    private boolean supportsPaperFeature = false;
    private boolean supportsTaskFeature = false;

    public static GHoloMain getInstance() { return GPM; }

    public CManager getCManager() { return cManager; }

    public MManager getMManager() { return mManager; }

    public UManager getUManager() { return uManager; }

    public PManager getPManager() { return pManager; }

    public TManager getTManager() { return tManager; }

    public DManager getDManager() { return dManager; }

    public SVManager getSVManager() { return svManager; }

    public HoloManager getHoloManager() { return holoManager; }

    public HoloAnimationManager getHoloAnimationManager() { return holoAnimationManager; }

    public HoloImportManager getHoloImportManager() { return holoImportManager; }

    public FormatUtil getFormatUtil() { return formatUtil; }

    public IEntityUtil getEntityUtil() { return entityUtil; }

    public boolean hasPlaceholderAPILink() { return placeholderAPILink; }

    public boolean supportsPaperFeature() { return supportsPaperFeature; }

    public boolean supportsTaskFeature() { return supportsTaskFeature; }

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

        Bukkit.getPluginManager().callEvent(new GHoloLoadedEvent(getInstance()));

        getMManager().sendMessage(Bukkit.getConsoleSender(), "Plugin.plugin-enabled");

        printPluginLinks(Bukkit.getConsoleSender());
        getUManager().checkForUpdates();
    }

    public void onDisable() {

        unload();
        getMManager().sendMessage(Bukkit.getConsoleSender(), "Plugin.plugin-disabled");
    }

    private void loadSettings(CommandSender Sender) {

        if(!connectDatabase(Sender)) return;

        getHoloAnimationManager().loadHoloAnimations();
        getHoloManager().createTables();
        getHoloManager().loadHolos();
        ImageUtil.generateFolder();
    }

    public void reload(CommandSender Sender) {
        GHoloReloadEvent reloadEvent = new GHoloReloadEvent(getInstance());
        Bukkit.getPluginManager().callEvent(reloadEvent);
        if(reloadEvent.isCancelled()) return;
        unload();
        getCManager().reload();
        getMManager().loadMessages();
        loadPluginDependencies();
        loadSettings(Sender);
        printPluginLinks(Sender);
        getUManager().checkForUpdates();
        Bukkit.getPluginManager().callEvent(new GHoloLoadedEvent(getInstance()));
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

    private boolean versionCheck() {
        if(getSVManager().isNewerOrVersion(19, 4) && getSVManager().isAvailable()) return true;
        getMManager().sendMessage(Bukkit.getConsoleSender(), "Plugin.plugin-version", "%Version%", getSVManager().getServerVersion());
        getUManager().checkForUpdates();
        Bukkit.getPluginManager().disablePlugin(getInstance());
        return false;
    }

    private boolean connectDatabase(CommandSender Sender) {
        boolean connect = getDManager().connect();
        if(connect) return true;
        getMManager().sendMessage(Sender, "Plugin.plugin-data");
        Bukkit.getPluginManager().disablePlugin(getInstance());
        return false;
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

    private void linkBStats() {

        BStatsLink bstats = new BStatsLink(getInstance(), 24075);

        bstats.addCustomChart(new BStatsLink.SimplePie("plugin_language", () -> getCManager().L_LANG));
        bstats.addCustomChart(new BStatsLink.AdvancedPie("minecraft_version_player", () -> Map.of(GPM.getSVManager().getServerVersion(), Bukkit.getOnlinePlayers().size())));
        bstats.addCustomChart(new BStatsLink.SingleLineChart("holo_count", () -> getHoloManager().getHoloCount()));
        bstats.addCustomChart(new BStatsLink.SingleLineChart("holo_row_count", () -> getHoloManager().getHoloRowCount()));
    }

}