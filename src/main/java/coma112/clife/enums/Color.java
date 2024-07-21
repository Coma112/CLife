package coma112.clife.enums;

import coma112.clife.CLife;
import coma112.clife.enums.keys.ConfigKeys;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public enum Color {
    DARK_GREEN,
    LIME,
    YELLOW,
    ORANGE,
    RED,
    VIOLET;

    private static final Map<Color, List<Color>> attackRules = new HashMap<>();
    private static final Map<Color, List<Color>> immunityRules = new HashMap<>();

    static {
        loadAttackRules();
    }

    public static Color fromString(@NotNull String color) {
        for (Color playerColor : values()) {
            if (playerColor.name().equalsIgnoreCase(color)) return playerColor;
        }
        return null;
    }

    public static Color getColorForTime(int time) {
        Color highestColor = DARK_GREEN;
        int highestUpperLimit = Integer.MIN_VALUE;

        for (Color color : values()) {
            String range = CLife.getInstance().getConfiguration().getString("color-per-life." + color.name().toLowerCase().replace("_", "-"));

            if (range != null) {
                String[] limits = range.split("-");
                int lower = Integer.parseInt(limits[1]);
                int upper = Integer.parseInt(limits[0]);

                if (time >= lower && time < upper) return color;

                if (upper > highestUpperLimit) {
                    highestUpperLimit = upper;
                    highestColor = color;
                }
            }
        }

        if (time >= highestUpperLimit) return highestColor;
        return VIOLET;
    }

    public static int getUpperLimit(@NotNull Color color) {
        String range = CLife.getInstance().getConfiguration().getString("color-per-life." + color.name().toLowerCase().replace("_", "-"));

        if (range != null) {
            String[] limits = range.split("-");
            return Integer.parseInt(limits[0]);
        }

        return 0;
    }


    public String getColorCode() {
        return switch (this) {
            case DARK_GREEN -> "&2";
            case LIME -> "&a";
            case YELLOW -> "&e";
            case ORANGE -> "&6";
            case RED -> "&c";
            case VIOLET -> "&d";
        };
    }

    public String getName() {
        return switch (this) {
            case DARK_GREEN -> ConfigKeys.COLOR_DARK_GREEN.getString();
            case LIME -> ConfigKeys.COLOR_LIME.getString();
            case YELLOW -> ConfigKeys.COLOR_YELLOW.getString();
            case ORANGE -> ConfigKeys.COLOR_ORANGE.getString();
            case RED -> ConfigKeys.COLOR_RED.getString();
            case VIOLET -> ConfigKeys.COLOR_VIOLET.getString();
        };
    }

    public boolean canAttack(@NotNull Color victimColor) {
        List<Color> allowedVictims = attackRules.getOrDefault(this, Collections.emptyList());
        List<Color> immuneVictims = immunityRules.getOrDefault(this, Collections.emptyList());

        if (!allowedVictims.isEmpty()) return allowedVictims.contains(victimColor);
        else if (!immuneVictims.isEmpty()) return !immuneVictims.contains(victimColor);

        return false;
    }

    private static void loadAttackRules() {
        String attackConfig = CLife.getInstance().getConfiguration().getString("attack_rule");

        if (attackConfig != null) {
            String[] rules = attackConfig.split(";");

            for (String rule : rules) {
                if (rule.contains("->")) {
                    String[] parts = rule.split("->");

                    if (parts.length == 2) {
                        Color attacker = Color.fromString(parts[0].trim());
                        List<Color> victims = Arrays
                                .stream(parts[1].split(","))
                                .map(String::trim)
                                .map(Color::fromString)
                                .collect(Collectors.toList());

                        if (attacker != null) attackRules.put(attacker, victims);
                    }
                } else if (rule.contains("!->")) {
                    String[] parts = rule.split("!->");

                    if (parts.length == 2) {
                        Color attacker = Color.fromString(parts[0].trim());
                        List<Color> immunities = Arrays.stream(parts[1].split(","))
                                .map(String::trim)
                                .map(Color::fromString)
                                .collect(Collectors.toList());

                        if (attacker != null) immunityRules.put(attacker, immunities);
                    }
                }
            }
        }
    }
}
