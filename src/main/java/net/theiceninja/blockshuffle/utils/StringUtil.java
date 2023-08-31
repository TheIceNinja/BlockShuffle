package net.theiceninja.blockshuffle.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;

@UtilityClass
public class StringUtil {

    public String formatTimer(int timer) {
        return "&#FDE686" + timer / 60 + "&8:&#FDE686" + (timer % 60 >= 10 ? timer % 60 : "0" + timer % 60);
    }

    public String formatMaterialName(Material material) {
        return (
                Character.toString(material.name().charAt(0)).toUpperCase() +
                        material.name().toLowerCase().substring(1)
        ).replace("_", " ");
    }

}
