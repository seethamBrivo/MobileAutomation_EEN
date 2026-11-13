package utils;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class FileSizeUtils {

    private static final Pattern SIZE = Pattern.compile("([\\d.,]+)\\s*(KB|MB|GB)");

    private static final int KILOBYTES_PER_MEGABYTE = 1024;

    private FileSizeUtils() {}

    /**
     * Reads the size from a line such as "46.53 MB • Expires 2026-11-26" and returns it in megabytes, or 0 when the
     * line holds no size. A comma followed by one or two digits is a decimal comma ("46,53"), a comma followed by
     * three digits is a thousands separator ("1,234").
     */
    public static double toMegabytes(String sizeLine) {
        Matcher matcher = SIZE.matcher(sizeLine);
        if (!matcher.find()) {
            return 0;
        }
        double amount = Double.parseDouble(normalise(matcher.group(1)));
        return switch (matcher.group(2).toUpperCase(Locale.ROOT)) {
            case "KB" -> amount / KILOBYTES_PER_MEGABYTE;
            case "GB" -> amount * KILOBYTES_PER_MEGABYTE;
            default -> amount;
        };
    }

    private static String normalise(String number) {
        if (number.contains(".") || !number.contains(",")) {
            return number.replace(",", "");
        }
        int digitsAfterComma = number.length() - number.lastIndexOf(',') - 1;
        return digitsAfterComma == 3 ? number.replace(",", "") : number.replace(',', '.');
    }
}
