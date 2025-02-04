package dev.geco.gholo.util;

import dev.geco.gholo.GHoloMain;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public static BufferedImage getBufferedImage(String url) { try { return ImageIO.read(new URL(url)); } catch(Throwable e) { return null; } }

    public static BufferedImage getBufferedImage(org.bukkit.OfflinePlayer offlinePlayer, boolean useOverlay) { try { return ImageIO.read(new URL((useOverlay ? AVATAR_URL_WITH_OVERLAY : AVATAR_URL_WITHOUT_OVERLAY) + offlinePlayer.getName() + "/" + DEFAULT_AVATAR_SIZE)); } catch(Throwable e) { return null; } }

    public static BufferedImage getBufferedImage(File file) { try { return ImageIO.read(file); } catch(Throwable e) { return null; } }

    public ImageUtil(BufferedImage image) { lines = toLines(toColorArray(image, image.getWidth(), image.getHeight())); }

    public ImageUtil(BufferedImage image, int size) {
        if(size <= 0) size = 1;
        lines = toLines(toColorArray(image, size, size));
    }

    public ImageUtil(BufferedImage image, int width, int height) {
        if(width <= 0 ) width = 1;
        if(height <= 0 ) height = 1;
        lines = toLines(toColorArray(image, width, height));
    }

    private String[] toLines(String[][] content) {
        String[] lines = new String[content[0].length];
        String transparencyColor = DEFAULT_TRANSPARENCY_COLOR.toString();
        String emptyPlaceholder = " [|] ";
        String fillPlaceholder = "[X]";
        for(int y = 0; y < content[0].length; y++) {
            StringBuilder line = new StringBuilder();
            String resetColor = org.bukkit.ChatColor.RESET.toString();
            for(String[] color : content) {
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

    private String[][] toColorArray(BufferedImage image, int width, int height) {
        BufferedImage resizedImage = width != image.getWidth() && height != image.getHeight() ? resizeImage(image, width, height) : image;
        String[][] colorArray = new String[resizedImage.getWidth()][resizedImage.getHeight()];
        for(int x = 0; x < resizedImage.getWidth(); x++) for(int y = 0; y < resizedImage.getHeight(); y++) colorArray[x][y] = getColor(new java.awt.Color(resizedImage.getRGB(x, y), true));
        return colorArray;
    }

    private BufferedImage resizeImage(BufferedImage image, int width, int height) {
        AffineTransform affineTransform = new AffineTransform();
        affineTransform.scale(width / (double) image.getWidth(), height / (double) image.getHeight());
        AffineTransformOp operation = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return operation.filter(image, null);
    }

    private String getColor(java.awt.Color color) {
        if(color.getAlpha() < 128) return null;
        return net.md_5.bungee.api.ChatColor.of(color).toString();
    }

}