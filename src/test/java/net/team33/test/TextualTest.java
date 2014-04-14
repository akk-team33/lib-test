package net.team33.test;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

/**
 * Test class for {@link Textual}
 * Created by andi.kluge on 14.04.14.
 */
public class TextualTest {

    private static final String A_STRING = "A string with \"quotes\" and \\backslashes\\";
    private static final int INT_278 = 278;
    private static final double DOUBLE_PI = 3.141592654;
    private static final char CHAR_C = 'c';
    private static final char CHAR_Q = '\'';
    private static final char CHAR_BS = '\\';
    private static final Date A_DATE = new Date(0);

    private static final String EXPECTED_01 = "" +
            "[\n" +
            "    \"A string with \\\"quotes\\\" and \\\\backslashes\\\\\",\n" +
            "    278,\n" +
            "    3.141592654,\n" +
            "    null,\n" +
            "    true,\n" +
            "    'c',\n" +
            "    '\\'',\n" +
            "    '\\\\',\n" +
            "    [\n" +
            "        true,\n" +
            "        [],\n" +
            "        [\n" +
            "            3.141592654\n" +
            "        ],\n" +
            "        [\n" +
            "            1,\n" +
            "            2,\n" +
            "            3\n" +
            "        ]\n" +
            "    ],\n" +
            "    <\"Thu Jan 01 01:00:00 CET 1970\">\n" +
            "]";

    @Test
    public final void testFromIterable() throws Exception {
        Assert.assertEquals(
                EXPECTED_01,
                textual().valueOf(asList(A_STRING, INT_278, DOUBLE_PI, null, true, CHAR_C, CHAR_Q, CHAR_BS,
                        asList(true, emptyList(), singletonList(DOUBLE_PI), asList(1, 2, 3)), A_DATE))
        );
    }

    private static Textual textual() {
        return new Textual();
    }
}
