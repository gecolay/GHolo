package dev.geco.gholo.util;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class LocationUtil {

    public World parseLocationWorldInput(String input, World base) { return input.equalsIgnoreCase("~") ? Bukkit.getWorld(input) : base; }

    public double parseLocationInput(String input, double base) throws NumberFormatException { return !input.startsWith("~") ? Double.parseDouble(input) : (base + (input.length() > 1 ? Double.parseDouble(input.substring(1)) : 0)); }

    public float parseLocationInput(String input, float base)  throws NumberFormatException { return !input.startsWith("~") ? Float.parseFloat(input) : (base + (input.length() > 1 ? Float.parseFloat(input.substring(1)) : 0)); }

}