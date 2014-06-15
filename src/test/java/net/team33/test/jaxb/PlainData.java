package net.team33.test.jaxb;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;
import java.util.Objects;

import static net.team33.test.jaxb.MappedData.Property.COUNTRY;
import static net.team33.test.jaxb.MappedData.Property.FIRST_NAME;
import static net.team33.test.jaxb.MappedData.Property.NAME;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
public class PlainData {

    private String name;
    private String firstName;
    private Country country;

    public PlainData() {
        this(NAME.getInitial().toString(), (String) FIRST_NAME.getInitial(), (Country) COUNTRY.getInitial());
    }

    public PlainData(final String name, final String firstName, final Country country) {
        this.name = name;
        this.firstName = firstName;
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(final Country country) {
        this.country = country;
    }

    @Override
    public boolean equals(final Object o) {
        return this == o || ((o instanceof PlainData) && equals_((PlainData) o));
    }

    private boolean equals_(final PlainData other) {
        return Objects.equals(name, other.name)
                && Objects.equals(firstName, other.firstName)
                && Objects.equals(country, other.country);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(name);
        result = (31 * result) + Objects.hashCode(firstName);
        result = (31 * result) + Objects.hashCode(country);
        return result;
    }

    @Override
    public String toString() {
        return String.format("PlainData(name(%s), firstName(%s), country=(%s)}", name, firstName, country);
    }
}
