package fr.kirosnn.dAPI.utils.text.simpletext;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;

/**
 * The type Simple text parser.
 */
public class SimpleTextParser {

    /**
     * Parse string.
     *
     * @param input the input
     * @return the string
     */
    public static @NotNull String parse(@NotNull String input) {
        if (input.isEmpty()) return "";

        StringBuffer buffer = new StringBuffer();

        Matcher matcher = Patterns.TAG_PATTERN.matcher(input);
        while (matcher.find()) {
            matcher.appendReplacement(buffer, ColorUtils.COLOR_MAP.getOrDefault(matcher.group(1).toLowerCase(), ""));
        }
        matcher.appendTail(buffer);
        String parsedText = buffer.toString();

        buffer.setLength(0);
        matcher = Patterns.GRADIENT_PATTERN.matcher(parsedText);
        while (matcher.find()) {
            matcher.appendReplacement(buffer, Effects.applyGradientEffect(matcher.group(1), matcher.group(2), matcher.group(3)));
        }
        matcher.appendTail(buffer);
        parsedText = buffer.toString();

        buffer.setLength(0);
        matcher = Patterns.RAINBOW_PATTERN.matcher(parsedText);
        while (matcher.find()) {
            matcher.appendReplacement(buffer, Effects.applyRainbowEffect(matcher.group(1)));
        }
        matcher.appendTail(buffer);
        parsedText = buffer.toString();

        buffer.setLength(0);
        matcher = Patterns.HEX_PATTERN.matcher(parsedText);
        while (matcher.find()) {
            matcher.appendReplacement(buffer, ColorUtils.convertHexToBukkit(matcher.group(1)));
        }
        matcher.appendTail(buffer);
        parsedText = buffer.toString();

        buffer.setLength(0);
        matcher = Patterns.MULTI_GRADIENT_PATTERN.matcher(parsedText);
        while (matcher.find()) {
            matcher.appendReplacement(buffer, Effects.applyMultiGradientEffect(matcher.group(1), matcher.group(2)));
        }
        matcher.appendTail(buffer);
        parsedText = buffer.toString();

        buffer.setLength(0);
        matcher = Patterns.BOLD_PATTERN.matcher(parsedText);
        while (matcher.find()) {
            matcher.appendReplacement(buffer, "§l" + matcher.group(1));
        }
        matcher.appendTail(buffer);
        parsedText = buffer.toString();

        return parsedText.replace("&", "§").replaceAll(Patterns.CLOSE_TAG_PATTERN.pattern(), "§r");
    }
}