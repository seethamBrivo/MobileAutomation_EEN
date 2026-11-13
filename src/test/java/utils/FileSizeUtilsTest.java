package utils;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class FileSizeUtilsTest {

    private static final double DELTA = 0.001;

    @Test
    public void readsMegabytesFromTheSizeLine() {
        assertEquals(FileSizeUtils.toMegabytes("46.53 MB • Expires 2026-11-26 06:03"), 46.53, DELTA);
    }

    @Test
    public void convertsKilobytesAndGigabytes() {
        assertEquals(FileSizeUtils.toMegabytes("735.85 KB • Expires 2026-11-26"), 0.7186, DELTA);
        assertEquals(FileSizeUtils.toMegabytes("1.5 GB"), 1536, DELTA);
    }

    @Test
    public void readsADecimalComma() {
        assertEquals(FileSizeUtils.toMegabytes("46,53 MB"), 46.53, DELTA);
    }

    @Test
    public void readsAThousandsSeparator() {
        assertEquals(FileSizeUtils.toMegabytes("1,234 MB"), 1234, DELTA);
        assertEquals(FileSizeUtils.toMegabytes("1,234.5 MB"), 1234.5, DELTA);
        assertEquals(FileSizeUtils.toMegabytes("1.234,5 MB"), 1234.5, DELTA);
    }

    @Test
    public void aLineWithoutASizeIsZero() {
        assertEquals(FileSizeUtils.toMegabytes("Expires 2026-11-26"), 0, DELTA);
    }
}
