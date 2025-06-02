package io.github.tootertutor.eventhorizons.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

/**
 * Utility class for parsing gradient syntax and generating color gradients.
 * Supports gradient syntax like "#ee9a00-#00edd5" with optional direction arrows:
 * ">", "<", "<>", "><"
 */
public class ColorGradientUtil {
    // Updated regex to allow optional whitespace before arrow, and optional arrow without space or with space
    private static final Pattern GRADIENT_PATTERN = Pattern.compile("^(#([\\da-fA-F]{6}))-(#([\\da-fA-F]{6}))(\\s*)(>|<|<>|><)?$");

    /**
     * Parses the gradient string and returns the start color, end color, and direction.
     * Supports optional whitespace between gradient and arrow.
     * @param gradientString The gradient string, e.g. "#ee9a00-#00edd5 <>" or "#ee9a00-#00edd5<>"
     * @return GradientInfo object if valid, null otherwise.
     */
    public static GradientInfo parseGradientString(String gradientString) {
        if (gradientString == null) return null;
        Matcher matcher = GRADIENT_PATTERN.matcher(gradientString.trim());
        if (!matcher.matches()) return null;

        String startHex = matcher.group(1);
        String endHex = matcher.group(3);
        String arrow = matcher.group(6); // Corrected group index for arrow

        if (arrow != null) {
            arrow = arrow.trim(); // Trim whitespace if any
        }

        // Debug print for parsing
        System.out.println("parseGradientString: startHex=" + startHex + ", endHex=" + endHex + ", arrow=" + arrow);

        GradientDirection direction = GradientDirection.LEFT_TO_RIGHT;
        if (arrow != null) {
            switch (arrow) {
                case "<":
                    direction = GradientDirection.RIGHT_TO_LEFT;
                    break;
                case "<>":
                    direction = GradientDirection.CENTER_TO_ENDS;
                    break;
                case "><":
                    direction = GradientDirection.ENDS_TO_CENTER;
                    break;
                case ">":
                default:
                    direction = GradientDirection.LEFT_TO_RIGHT;
                    break;
            }
        }

        return new GradientInfo(TextColor.fromHexString(startHex), TextColor.fromHexString(endHex), direction);
    }

    public enum GradientDirection {
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT,
        CENTER_TO_ENDS,
        ENDS_TO_CENTER
    }

    /**
     * Generates a list of TextColors forming a gradient between start and end colors.
     * @param startColor The start color.
     * @param endColor The end color.
     * @param length The number of colors to generate.
     * @param direction The gradient direction.
     * @return List of TextColors representing the gradient.
     */
    public static List<TextColor> generateGradient(TextColor startColor, TextColor endColor, int length, GradientDirection direction) {
        List<TextColor> colors = new ArrayList<>();
        if (length <= 0) return colors;

        int[] startRGB = new int[] { startColor.red(), startColor.green(), startColor.blue() };
        int[] endRGB = new int[] { endColor.red(), endColor.green(), endColor.blue() };

        switch (direction) {
            case LEFT_TO_RIGHT:
                for (int i = 0; i < length; i++) {
                    double ratio = (double) i / (length - 1);
                    colors.add(interpolateColor(startRGB, endRGB, ratio));
                }
                break;
            case RIGHT_TO_LEFT:
                for (int i = 0; i < length; i++) {
                    double ratio = (double) (length - 1 - i) / (length - 1);
                    colors.add(interpolateColor(startRGB, endRGB, ratio));
                }
                break;
            case CENTER_TO_ENDS:
                // Gradient from center to ends: center is startColor, ends are endColor
                int center = length / 2;
                for (int i = 0; i < length; i++) {
                    double ratio;
                    if (i <= center) {
                        ratio = center == 0 ? 0 : (double) i / center;
                    } else {
                        ratio = center == 0 ? 0 : (double) (length - 1 - i) / (length - 1 - center);
                    }
                    colors.add(interpolateColor(startRGB, endRGB, ratio));
                }
                break;
            case ENDS_TO_CENTER:
                // Gradient from ends to center: ends are startColor, center is endColor
                center = length / 2;
                for (int i = 0; i < length; i++) {
                    double ratio;
                    if (i <= center) {
                        ratio = center == 0 ? 1 : 1 - ((double) i / center);
                    } else {
                        ratio = center == 0 ? 1 : 1 - ((double) (length - 1 - i) / (length - 1 - center));
                    }
                    colors.add(interpolateColor(startRGB, endRGB, ratio));
                }
                break;
        }

        return colors;
    }

    private static TextColor interpolateColor(int[] startRGB, int[] endRGB, double ratio) {
        int r = (int) (startRGB[0] + (endRGB[0] - startRGB[0]) * ratio);
        int g = (int) (startRGB[1] + (endRGB[1] - startRGB[1]) * ratio);
        int b = (int) (startRGB[2] + (endRGB[2] - startRGB[2]) * ratio);
        return TextColor.color(r, g, b);
    }

    /**
     * Applies a gradient to the input text and returns a list of Components with gradient colors.
     * @param text The input text.
     * @param gradientInfo The gradient info.
     * @return List of Components with gradient colors applied character-wise.
     */
    public static List<Component> applyGradient(String text, GradientInfo gradientInfo) {
        List<Component> components = new ArrayList<>();
        if (text == null || text.isEmpty() || gradientInfo == null) {
            components.add(Component.text(text));
            return components;
        }

        List<TextColor> colors = generateGradient(gradientInfo.startColor, gradientInfo.endColor, text.length(), gradientInfo.direction);

        for (int i = 0; i < text.length(); i++) {
            components.add(Component.text(String.valueOf(text.charAt(i))).color(colors.get(i)));
        }

        return components;
    }

    public static class GradientInfo {
        public final TextColor startColor;
        public final TextColor endColor;
        public final GradientDirection direction;

        public GradientInfo(TextColor startColor, TextColor endColor, GradientDirection direction) {
            this.startColor = startColor;
            this.endColor = endColor;
            this.direction = direction;
        }
    }
}
