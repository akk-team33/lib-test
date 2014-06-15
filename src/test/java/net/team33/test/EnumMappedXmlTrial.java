package net.team33.test;

import net.team33.test.jaxb.Country;
import net.team33.test.jaxb.MappedData;
import net.team33.test.jaxb.PlainData;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

public class EnumMappedXmlTrial {

    private static final String EXPECTED_FIRST = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<root>\n" +
            "    <address>\n" +
            "        <country>ZZZ</country>\n" +
            "        <firstName>unknown</firstName>\n" +
            "        <name>unknown</name>\n" +
            "    </address>\n" +
            "</root>\n";

    private static <T> T unMarshal(final String xml, Class<T> dataClass) throws IOException {
        try (final Reader reader = new StringReader(xml)) {
            return JAXB.unmarshal(reader, dataClass);
        }
    }

    @Test
    public void testUnMarshalFirst() throws Exception {
        Assert.assertEquals(
                new RootPlain(),
                unMarshal(EXPECTED_FIRST, RootPlain.class)
        );
    }

    @Test
    public void testUnMarshalSecond() throws Exception {
        Assert.assertEquals(
                new RootMapped(),
                unMarshal(EXPECTED_FIRST, RootMapped.class)
        );
    }

    @Test
    public void testUnMarshalThird() throws Exception {
        final RootMapped expected = new RootMapped();
        expected.address = MappedData.builder()
                .setName("another name")
                .setFirstName("another first name")
                .setCountry(Country.AUT)
                .build();
        Assert.assertEquals(
                expected,
                unMarshal(marshal(expected), RootMapped.class)
        );
    }

    @Test
    public void testMarshalFirst() throws Exception {
        Assert.assertEquals(
                EXPECTED_FIRST,
                marshal(new RootPlain())
        );
    }

    @Test
    public void testMarshalSecond() throws Exception {
        Assert.assertEquals(
                marshal(new RootPlain()),
                marshal(new RootMapped())
        );
    }

    private String marshal(final Object origin) throws IOException {
        try (final StringWriter out = new StringWriter()) {
            JAXB.marshal(origin, out);
            return out.toString();
        }
    }

    public static abstract class Root {

        abstract Object member();

        @Override
        public final boolean equals(final Object o) {
            return (this == o) || ((o instanceof Root) && member().equals(((Root) o).member()));
        }

        @Override
        public String toString() {
            return member().toString();
        }

        @Override
        public final int hashCode() {
            return member().hashCode();
        }
    }

    @XmlRootElement(name = "root")
    public static class RootPlain extends Root {
        public PlainData address = new PlainData();

        @Override
        Object member() {
            return address;
        }
    }

    @XmlRootElement(name = "root")
    public static class RootMapped extends Root {
        public MappedData address = MappedData.builder().build();

        @Override
        Object member() {
            return address;
        }
    }
}
