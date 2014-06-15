package net.team33.test.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class AddressAdapter extends XmlAdapter<PlainData, MappedData> {
    @Override
    public MappedData unmarshal(final PlainData v) throws Exception {
        throw new Error("Not yet implemented");
    }

    @Override
    public PlainData marshal(final MappedData v) throws Exception {
        return new PlainData(v.getName(), v.getFirstName(), v.getCountry());
    }
}
