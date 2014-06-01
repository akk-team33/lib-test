package net.team33.test;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReportTest {

    private static final Map<Object, Object> A_MAP = new LinkedHashMap<>(0);

    static {
        A_MAP.put("This", "is a Map entry");
        A_MAP.put("This is", new String[]{"another", "Map", "entry"});
        A_MAP.put(Arrays.asList("A", "List", "as"), "key");
    }

    @Test
    public void testToStringNone() throws Exception {
        Assert.assertEquals(
                "[]",
                Report.EMPTY.toString()
        );
    }

    @Test
    public void testToStringEmpty() throws Exception {
        Assert.assertEquals(
                Report.EMPTY.toString(),
                new Report(Collections.emptyList()).toString()
        );
    }

    @Test
    public void testToString() throws Exception {
        Assert.assertEquals(
                String.format("" +
                        "[%n" +
                        "   This is a String,%n" +
                        "   [%n" +
                        "      This,%n" +
                        "      is,%n" +
                        "      an,%n" +
                        "      array%n" +
                        "   ],%n" +
                        "   [%n" +
                        "      This,%n" +
                        "      is,%n" +
                        "      a,%n" +
                        "      List%n" +
                        "   ],%n" +
                        "   [%n" +
                        "      This -> is a Map entry,%n" +
                        "      This is -> [%n" +
                        "         another,%n" +
                        "         Map,%n" +
                        "         entry%n" +
                        "      ],%n" +
                        "      [%n" +
                        "         A,%n" +
                        "         List,%n" +
                        "         as%n" +
                        "      ] -> key%n" +
                        "   ],%n" +
                        "   java.lang.Exception: An Exception%n" +
                        "]"),
                new Report(Arrays.asList(
                        "This is a String",
                        new String[]{"This", "is", "an", "array"},
                        Arrays.asList("This", "is", "a", "List"),
                        A_MAP,
                        new Exception("An Exception"))
                ).toString()
        );
    }
}
