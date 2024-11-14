package dev.geco.gholo.util;

import java.awt.Color;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.imageio.*;

import dev.geco.gholo.GHoloMain;

public class ImageUtil {

    private static final String AVATAR_URL_WITH_OVERLAY = "https://minotar.net/helm/";

    private static final String AVATAR_URL_WITHOUT_OVERLAY = "https://minotar.net/avatar/";

    private static final int DEFAULT_SIZE = 16;

    public static final org.bukkit.ChatColor DEFAULT_TRANSPARENCY_COLOR = org.bukkit.ChatColor.GRAY;

    private final boolean HEX_COLORS = Arrays.stream(net.md_5.bungee.api.ChatColor.class.getMethods()).filter(m -> "of".equals(m.getName())).findFirst().orElse(null) != null;

    private final int MAX_SIZE = 500;

    public static final List<String> IMAGE_TYPES = Arrays.asList("avatar", "helm", "file", "url");

    public static final File IMAGE_FOLDER = new File(GHoloMain.getInstance().getDataFolder(), "images/");

    private final Color[] colors = {

            new Color(0, 0, 0),
            new Color(0, 0, 170),
            new Color(0, 170, 0),
            new Color(0, 170, 170),
            new Color(170, 0, 0),
            new Color(170, 0, 170),
            new Color(255, 170, 0),
            new Color(170, 170, 170),
            new Color(85, 85, 85),
            new Color(85, 85, 255),
            new Color(85, 255, 85),
            new Color(85, 255, 255),
            new Color(255, 85, 85),
            new Color(255, 85, 255),
            new Color(255, 255, 85),
            new Color(255, 255, 255)
    };

    private final String[] lines;

    public List<String> getLines() { return new ArrayList<>(Arrays.asList(lines)); }

    public static void generateFolder() { if(!IMAGE_FOLDER.exists()) IMAGE_FOLDER.mkdir(); }

    public static BufferedImage getBufferedImage(String URL) { try { return ImageIO.read(new URL(URL)); } catch (Exception e) { return null; } }

    public static BufferedImage getBufferedImage(org.bukkit.OfflinePlayer Player, boolean Overlay) { try { return ImageIO.read(new URL((Overlay ? AVATAR_URL_WITH_OVERLAY : AVATAR_URL_WITHOUT_OVERLAY) + Player.getName() + "/" + DEFAULT_SIZE)); } catch (Exception e) { return null; } }

    public static BufferedImage getBufferedImage(File File) { try { return ImageIO.read(File); } catch (Exception e) { return null; } }

    public ImageUtil(BufferedImage Image) { lines = toLines(toColorArray(Image, Image.getWidth(), Image.getHeight())); }

    public ImageUtil(BufferedImage Image, int Size) {

        if(Size <= 0) throw new NumberFormatException();
        if(Size > MAX_SIZE) Size = MAX_SIZE;
        lines = toLines(toColorArray(Image, Size, Size));
    }

    public ImageUtil(BufferedImage Image, int Width, int Height) {

        if(Width <= 0 || Height <= 0) throw new NumberFormatException();
        if(Width > MAX_SIZE) Width = MAX_SIZE;
        if(Height > MAX_SIZE) Height = MAX_SIZE;
        lines = toLines(toColorArray(Image, Width, Height));
    }

    private String[][] toColorArray(BufferedImage Image, int Width, int Height) {

        BufferedImage image = Width != Image.getWidth() && Height != Image.getHeight() ? resizeImage(Image, Width, Height) : Image;

        String[][] colorArray = new String[image.getWidth()][image.getHeight()];

        for(int x = 0; x < image.getWidth(); x++) for(int y = 0; y < image.getHeight(); y++) colorArray[x][y] = getColor(new Color(image.getRGB(x, y), true));

        return colorArray;
    }

    private String[] toLines(String[][] Colors) {

        String[] lines = new String[Colors[0].length];

        String transparency_color = DEFAULT_TRANSPARENCY_COLOR.toString();
        String empty_placeholder = " [|] ";
        String fill_placeholder = "[X]";

        for(int y = 0; y < Colors[0].length; y++) {

            StringBuilder line = new StringBuilder();

            String p = org.bukkit.ChatColor.RESET.toString();

            for(String[] color : Colors) {

                String cC = color[y];

                if(cC == null) {

                    if(!p.equals(transparency_color)) {

                        line.append(transparency_color);
                        p = transparency_color;

                    }

                    line.append(empty_placeholder);
                } else {

                    if(!p.equals(cC)) {

                        line.append(cC);
                        p = cC;
                    }

                    line.append(fill_placeholder);
                }
            }

            lines[y] = line.toString();
        }

        return lines;
    }

    private BufferedImage resizeImage(BufferedImage Image, int Width, int Height) {

        AffineTransform affineTransform = new AffineTransform();

        affineTransform.scale(Width / (double) Image.getWidth(), Height / (double) Image.getHeight());

        AffineTransformOp operation = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

        return operation.filter(Image, null);
    }

    private double getDistance(Color Color1, Color Color2) {

        double rmean = (Color1.getRed() + Color2.getRed()) / 2.0;
        double r = Color1.getRed() - Color2.getRed();
        double g = Color1.getGreen() - Color2.getGreen();
        int b = Color1.getBlue() - Color2.getBlue();

        return (2 + rmean / 256.0) * r * r + 4.0 * g * g + (2 + (255 - rmean) / 256.0) * b * b;
    }

    private boolean checkIdentical(Color Color1, Color Color2) { return Math.abs(Color1.getRed() - Color2.getRed()) <= 5 && Math.abs(Color1.getGreen() - Color2.getGreen()) <= 5 && Math.abs(Color1.getBlue() - Color2.getBlue()) <= 5; }

    private String getColor(Color Color) {

        if(Color.getAlpha() < 128) return null;

        if(HEX_COLORS) return net.md_5.bungee.api.ChatColor.of(Color).toString();

        int index = 0;
        double best = -1;

        for(int i = 0; i < colors.length; i++) if(checkIdentical(colors[i], Color)) return org.bukkit.ChatColor.values()[i].toString();

        for(int i = 0; i < colors.length; i++) {

            double distance = getDistance(Color, colors[i]);

            if(distance < best || best == -1) {
                best = distance;
                index = i;
            }

        }

        return org.bukkit.ChatColor.values()[index].toString();
    }

}