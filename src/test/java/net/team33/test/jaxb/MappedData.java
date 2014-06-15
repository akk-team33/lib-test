package net.team33.test.jaxb;

import net.team33.test.EnumMapped;
import net.team33.test.Mapped;

import javax.xml.bind.annotation.XmlAccessOrder;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorOrder;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@SuppressWarnings("UnusedDeclaration")
@XmlJavaTypeAdapter(MappedData.XmlAdapter.class)
public class MappedData extends EnumMapped<MappedData.Property> {

    private MappedData(final Mapper<Property, ?> mapper) {
        super(mapper);
    }

    public static Builder builder() {
        return new Builder();
    }

    public final String getName() {
        return get(Property.NAME);
    }

    public final String getFirstName() {
        return get(Property.FIRST_NAME);
    }

    public final Country getCountry() {
        return get(Property.COUNTRY);
    }

    static enum Property implements Mapped.Key {

        NAME(false) {
            @Override
            public final Class<String> getValueClass() {
                return String.class;
            }

            @Override
            public final String getInitial() {
                return "unknown";
            }
        },
        FIRST_NAME(false) {
            @Override
            public final Class<String> getValueClass() {
                return String.class;
            }

            @Override
            public final String getInitial() {
                return "unknown";
            }
        },
        COUNTRY(false) {
            @Override
            public final Class<Country> getValueClass() {
                return Country.class;
            }

            @Override
            public final Country getInitial() {
                return Country.ZZZ;
            }
        };

        private final boolean nullable;

        Property(final boolean nullable) {
            this.nullable = nullable;
        }

        @Override
        public boolean isNullable() {
            return nullable;
        }
    }

    @XmlAccessorType(XmlAccessType.PROPERTY)
    @XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
    public static class Builder extends Mapper<Property, Builder> {

        private Builder() {
            super(Property.class);
        }

        @Override
        protected final Builder finallyThis() {
            return this;
        }

        public final MappedData build() {
            return new MappedData(this);
        }

        @XmlElement
        public final String getName() {
            return get(Property.NAME);
        }

        public final Builder setName(final String name) {
            return set(Property.NAME, name);
        }

        @XmlElement
        public final String getFirstName() {
            return get(Property.FIRST_NAME);
        }

        public final Builder setFirstName(final String firstName) {
            return set(Property.FIRST_NAME, firstName);
        }

        @XmlElement
        public Country getCountry() {
            return get(Property.COUNTRY);
        }

        public final Builder setCountry(final Country country) {
            return set(Property.COUNTRY, country);
        }
    }

    static class XmlAdapter extends javax.xml.bind.annotation.adapters.XmlAdapter<Builder, MappedData> {
        @Override
        public final MappedData unmarshal(final Builder v) throws Exception {
            return v.build();
        }

        @Override
        public final Builder marshal(final MappedData v) throws Exception {
            return builder().set(v.asMap());
        }
    }
}
