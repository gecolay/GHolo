package dev.geco.gholo.util;

import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.imageio.*;

import dev.geco.gholo.GHoloMain;

public class ImageUtil {

    public static final org.bukkit.ChatColor DEFAULT_TRANSPARENCY_COLOR = org.bukkit.ChatColor.GRAY;
    public static final List<String> IMAGE_TYPES = Arrays.asList("avatar", "helm", "file", "url");
    public static final File IMAGE_FOLDER = new File(GHoloMain.getInstance().getDataFolder(), "images/");

    private static final String AVATAR_URL_WITH_OVERLAY = "https://minotar.net/helm/";
    private static final String AVATAR_URL_WITHOUT_OVERLAY = "https://minotar.net/avatar/";
    private static final int DEFAULT_AVATAR_SIZE = 32;
    private final String[] lines;

    public List<String> getLines() { return new ArrayList<>(Arrays.asList(lines)); }

    public static void generateFolder() { if(!IMAGE_FOLDER.exists()) IMAGE_FOLDER.mkdir(); }

    public static BufferedImage getBufferedImage(String URL) { try { return ImageIO.read(new URL(URL)); } catch (Exception e) { return null; } }

    public static BufferedImage getBufferedImage(org.bukkit.OfflinePlayer Player, boolean Overlay) { try { return ImageIO.read(new URL((Overlay ? AVATAR_URL_WITH_OVERLAY : AVATAR_URL_WITHOUT_OVERLAY) + Player.getName() + "/" + DEFAULT_AVATAR_SIZE)); } catch (Exception e) { return null; } }

    public static BufferedImage getBufferedImage(File File) { try { return ImageIO.read(File); } catch (Exception e) { return null; } }

    public ImageUtil(BufferedImage Image) { lines = toLines(toColorArray(Image, Image.getWidth(), Image.getHeight())); }

    public ImageUtil(BufferedImage Image, int Size) {
        if(Size <= 0) Size = 1;
        lines = toLines(toColorArray(Image, Size, Size));
    }

    public ImageUtil(BufferedImage Image, int Width, int Height) {
        if(Width <= 0 ) Width = 1;
        if(Height <= 0 ) Height = 1;
        lines = toLines(toColorArray(Image, Width, Height));
    }

    private String[] toLines(String[][] Colors) {
        String[] lines = new String[Colors[0].length];
        String transparencyColor = DEFAULT_TRANSPARENCY_COLOR.toString();
        String emptyPlaceholder = " [|] ";
        String fillPlaceholder = "[X]";
        for(int y = 0; y < Colors[0].length; y++) {
            StringBuilder line = new StringBuilder();
            String resetColor = org.bukkit.ChatColor.RESET.toString();
            for(String[] color : Colors) {
                String currentColor = color[y];
                if(currentColor == null) {
                    if(!resetColor.equals(transparencyColor)) {
                        line.append(transparencyColor);
                        resetColor = transparencyColor;
                    }
                    line.append(emptyPlaceholder);
                } else {
                    if(!resetColor.equals(currentColor)) {
                        line.append(currentColor);
                        resetColor = currentColor;
                    }
                    line.append(fillPlaceholder);
                }
            }
            lines[y] = line.toString();
        }
        return lines;
    }

    private String[][] toColorArray(BufferedImage Image, int Width, int Height) {
        BufferedImage image = Width != Image.getWidth() && Height != Image.getHeight() ? resizeImage(Image, Width, Height) : Image;
        String[][] colorArray = new String[image.getWidth()][image.getHeight()];
        for(int x = 0; x < image.getWidth(); x++) for(int y = 0; y < image.getHeight(); y++) colorArray[x][y] = getColor(new java.awt.Color(image.getRGB(x, y), true));
        return colorArray;
    }

    private BufferedImage resizeImage(BufferedImage Image, int Width, int Height) {
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.scale(Width / (double) Image.getWidth(), Height / (double) Image.getHeight());
        AffineTransformOp operation = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return operation.filter(Image, null);
    }

    private String getColor(java.awt.Color Color) {
        if(Color.getAlpha() < 128) return null;
        return net.md_5.bungee.api.ChatColor.of(Color).toString();
    }

}