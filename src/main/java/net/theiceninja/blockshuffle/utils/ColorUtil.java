package net.theiceninja.blockshuffle.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

public class ColorUtil {

    public static Component color(@NotNull String str) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(str);
    }

}
