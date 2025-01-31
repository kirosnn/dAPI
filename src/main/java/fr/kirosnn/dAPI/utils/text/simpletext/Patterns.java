package fr.kirosnn.dAPI.utils.text.simpletext;

import java.util.regex.Pattern;

public class Patterns {
    public static final Pattern HEX_PATTERN = Pattern.compile("<#([A-Fa-f0-9]{6})>");
    public static final Pattern TAG_PATTERN = Pattern.compile("<([a-z_]+)>");
    public static final Pattern CLOSE_TAG_PATTERN = Pattern.compile("</([a-z_]+)>");
    public static final Pattern RAINBOW_PATTERN = Pattern.compile("<rainbow>(.*?)</rainbow>", Pattern.DOTALL);
    public static final Pattern GRADIENT_PATTERN = Pattern.compile("<gradient:#([A-Fa-f0-9]{6})\\s*:\\s*#([A-Fa-f0-9]{6})>(.*?)</gradient>");
    public static final Pattern MULTI_GRADIENT_PATTERN = Pattern.compile("<gradient:((?:#?[A-Fa-f0-9]{6}:?)+)>(.*?)</gradient>");
}