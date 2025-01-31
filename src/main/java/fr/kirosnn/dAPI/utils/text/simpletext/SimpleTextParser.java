package fr.kirosnn.dAPI.utils.text.simpletext;

import org.jetbrains.annotations.NotNull;

public class SimpleTextParser {

    public static @NotNull String parse(@NotNull String input) {
        if (input.isEmpty()) return "";

        String parsedText = Patterns.TAG_PATTERN.matcher(input).replaceAll(match ->
                ColorUtils.COLOR_MAP.getOrDefault(match.group(1).toLowerCase(), "")
        );

        parsedText = Patterns.GRADIENT_PATTERN.matcher(parsedText).replaceAll(match ->
                Effects.applyGradientEffect(match.group(1), match.group(2), match.group(3))
        );

        parsedText = Patterns.RAINBOW_PATTERN.matcher(parsedText).replaceAll(match ->
                Effects.applyRainbowEffect(match.group(1))
        );

        parsedText = Patterns.HEX_PATTERN.matcher(parsedText).replaceAll(match ->
                ColorUtils.convertHexToBukkit(match.group(1))
        );

        parsedText = Patterns.MULTI_GRADIENT_PATTERN.matcher(parsedText).replaceAll(match ->
                Effects.applyMultiGradientEffect(match.group(1), match.group(2))
        );

        parsedText = Patterns.CLOSE_TAG_PATTERN.matcher(parsedText).replaceAll("§r");

        return parsedText.replace("&", "§");
    }
}