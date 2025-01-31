package fr.kirosnn.dAPI.utils.text.simpletext;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static fr.kirosnn.dAPI.utils.text.simpletext.ColorUtils.convertHexToBukkit;

/**
 * The type Effects.
 */
public class Effects {

    /**
     * Apply rainbow effect string.
     *
     * @param text the text
     * @return the string
     */
    public static @NotNull String applyRainbowEffect(@NotNull String text) {
        Color[] rainbowColors = {
                new Color(255, 0, 0), new Color(255, 165, 0),
                new Color(255, 255, 0), new Color(0, 255, 0),
                new Color(0, 0, 255), new Color(75, 0, 130),
                new Color(148, 0, 211)
        };

        StringBuilder result = new StringBuilder();
        int cycleLength = rainbowColors.length;

        for (int i = 0; i < text.length(); i++) {
            Color color = rainbowColors[i % cycleLength];
            String hex = String.format("%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
            result.append(convertHexToBukkit(hex)).append(text.charAt(i));
        }

        return result.toString();
    }

    /**
     * Apply gradient effect string.
     *
     * @param startHex the start hex
     * @param endHex   the end hex
     * @param text     the text
     * @return the string
     */
    public static @NotNull String applyGradientEffect(@NotNull String startHex, @NotNull String endHex, @NotNull String text) {
        Color startColor = Color.decode("#" + startHex);
        Color endColor = Color.decode("#" + endHex);
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            float ratio = (float) i / Math.max(text.length() - 1, 1);
            int r = (int) (startColor.getRed() + ratio * (endColor.getRed() - startColor.getRed()));
            int g = (int) (startColor.getGreen() + ratio * (endColor.getGreen() - startColor.getGreen()));
            int b = (int) (startColor.getBlue() + ratio * (endColor.getBlue() - startColor.getBlue()));

            String hex = String.format("%02X%02X%02X", r, g, b);
            result.append(convertHexToBukkit(hex)).append(text.charAt(i));
        }

        return result + "§r";
    }

    /**
     * Apply multi gradient effect string.
     *
     * @param colors the colors
     * @param text   the text
     * @return the string
     */
    public static @NotNull String applyMultiGradientEffect(@NotNull String colors, @NotNull String text) {
        String[] colorArray = colors.split(":");
        int colorCount = colorArray.length;
        int textLength = text.length();

        if (colorCount < 2 || textLength == 0) return text;

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < textLength; i++) {
            float ratio = (float) i / (textLength - 1);
            int colorIndex = (int) Math.floor(ratio * (colorCount - 1));
            float localRatio = (ratio * (colorCount - 1)) - colorIndex;

            String startHex = colorArray[colorIndex].startsWith("#") ? colorArray[colorIndex] : "#" + colorArray[colorIndex];
            String endHex = colorArray[Math.min(colorIndex + 1, colorCount - 1)];
            endHex = endHex.startsWith("#") ? endHex : "#" + endHex;

            if (!startHex.matches("#[A-Fa-f0-9]{6}") || !endHex.matches("#[A-Fa-f0-9]{6}")) {
                return text;
            }

            Color startColor = Color.decode(startHex);
            Color endColor = Color.decode(endHex);

            int r = (int) (startColor.getRed() + localRatio * (endColor.getRed() - startColor.getRed()));
            int g = (int) (startColor.getGreen() + localRatio * (endColor.getGreen() - startColor.getGreen()));
            int b = (int) (startColor.getBlue() + localRatio * (endColor.getBlue() - startColor.getBlue()));

            String hex = String.format("%02X%02X%02X", r, g, b);
            result.append(convertHexToBukkit(hex)).append(text.charAt(i));
        }

        return result + "§r";
    }
}