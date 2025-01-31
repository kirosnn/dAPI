package fr.kirosnn.dAPI.utils.text.simpletext;

import java.util.regex.Pattern;

/**
 * The type Patterns.
 */
public class Patterns {
    /**
     * The constant HEX_PATTERN.
     */
    public static final Pattern HEX_PATTERN = Pattern.compile("<#([A-Fa-f0-9]{6})>");
    /**
     * The constant TAG_PATTERN.
     */
    public static final Pattern TAG_PATTERN = Pattern.compile("<([a-z_]+)>");
    /**
     * The constant CLOSE_TAG_PATTERN.
     */
    public static final Pattern CLOSE_TAG_PATTERN = Pattern.compile("</([a-z_]+)>");
    /**
     * The constant RAINBOW_PATTERN.
     */
    public static final Pattern RAINBOW_PATTERN = Pattern.compile("<rainbow>(.*?)</rainbow>", Pattern.DOTALL);
    /**
     * The constant GRADIENT_PATTERN.
     */
    public static final Pattern GRADIENT_PATTERN = Pattern.compile("<gradient:#([A-Fa-f0-9]{6})\\s*:\\s*#([A-Fa-f0-9]{6})>(.*?)</gradient>");
    /**
     * The constant MULTI_GRADIENT_PATTERN.
     */
    public static final Pattern MULTI_GRADIENT_PATTERN = Pattern.compile("<gradient:((?:#?[A-Fa-f0-9]{6}:?)+)>(.*?)</gradient>");
}