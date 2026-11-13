package utils;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads the size the Downloads screen shows for a video, such as "46.53 MB". Both decimal marks are understood: with
 * a dot and a comma in one number the later one is the decimal mark ("1,234.5" and "1.234,5" are both 1234.5), a lone
 * comma followed by three digits is a thousands separator ("1,234" is 1234) and one followed by one or two digits is
 * a decimal comma ("46,53" is 46.53).
 */
public final class FileSizeUtils {

    private static final Pattern SIZE = Pattern.compile("([\\d.,]+)\\s*(KB|MB|GB)");

    private static final int KILOBYTES_PER_MEGABYTE = 1024;

    private FileSizeUtils() {}

    /** Returns the size in megabytes, or 0 when the line holds no size. */
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
        int lastDot = number.lastIndexOf('.');
        int lastComma = number.lastIndexOf(',');
        if (lastDot >= 0 && lastComma >= 0) {
            return lastComma > lastDot
                    ? number.replace(".", "").replace(',', '.')
                    : number.replace(",", "");
        }
        if (lastComma < 0) {
            return number;
        }
        int digitsAfterComma = number.length() - lastComma - 1;
        return digitsAfterComma == 3 ? number.replace(",", "") : number.replace(',', '.');
    }
}
