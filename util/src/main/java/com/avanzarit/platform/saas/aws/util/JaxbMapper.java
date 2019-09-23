package com.avanzarit.platform.saas.aws.util;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Mapper class that can marshal objects to XML and unmarshal them from XML.
 */
public class JaxbMapper {
    /**
     * Marshals the given object to its pretty-printed XML representation using JAXB annotations.
     *
     * @return The XML result as a byte array.
     */
    public byte[] marshal(Object object) throws IOException, JAXBException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass());
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            marshaller.marshal(object, outputStream);

            return outputStream.toByteArray();
        }
    }

    /**
     * Unmarshals the given input stream to a Java object instance of the requested class.
     *
     * @return An instance of the class provided in the cls parameter.
     */
    public <T> T unmarshal(InputStream inputStream, Class<T> cls) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(cls);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        return cls.cast(unmarshaller.unmarshal(inputStream));
    }
}
