package net.theiceninja.blockshuffle.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtil {

    public String formatTimer(int timer) {
        return "&#FDE686" + timer / 60 + "&8:&#FDE686" + (timer % 60 >= 10 ? timer % 60 : "0" + timer % 60);
    }

}
