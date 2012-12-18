
package com.doodle.xsd1;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import service.Initiator;
import service.Options;
import service.Poll;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.doodle.xsd1 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Type_QNAME = new QName("http://doodle.com/xsd1", "type");
    private final static QName _Options_QNAME = new QName("http://doodle.com/xsd1", "options");
    private final static QName _Option_QNAME = new QName("http://doodle.com/xsd1", "option");
    private final static QName _EMailAddress_QNAME = new QName("http://doodle.com/xsd1", "eMailAddress");
    private final static QName _Poll_QNAME = new QName("http://doodle.com/xsd1", "poll");
    private final static QName _Title_QNAME = new QName("http://doodle.com/xsd1", "title");
    private final static QName _Levels_QNAME = new QName("http://doodle.com/xsd1", "levels");
    private final static QName _Initiator_QNAME = new QName("http://doodle.com/xsd1", "initiator");
    private final static QName _Name_QNAME = new QName("http://doodle.com/xsd1", "name");
    private final static QName _Hidden_QNAME = new QName("http://doodle.com/xsd1", "hidden");
    private final static QName _Description_QNAME = new QName("http://doodle.com/xsd1", "description");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.doodle.xsd1
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://doodle.com/xsd1", name = "type")
    public JAXBElement<String> createType(String value) {
        return new JAXBElement<String>(_Type_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Options }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://doodle.com/xsd1", name = "options")
    public JAXBElement<Options> createOptions(Options value) {
        return new JAXBElement<Options>(_Options_QNAME, Options.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://doodle.com/xsd1", name = "option")
    public JAXBElement<String> createOption(String value) {
        return new JAXBElement<String>(_Option_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://doodle.com/xsd1", name = "eMailAddress")
    public JAXBElement<String> createEMailAddress(String value) {
        return new JAXBElement<String>(_EMailAddress_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Poll }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://doodle.com/xsd1", name = "poll")
    public JAXBElement<Poll> createPoll(Poll value) {
        return new JAXBElement<Poll>(_Poll_QNAME, Poll.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://doodle.com/xsd1", name = "title")
    public JAXBElement<String> createTitle(String value) {
        return new JAXBElement<String>(_Title_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Integer }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://doodle.com/xsd1", name = "levels")
    public JAXBElement<Integer> createLevels(Integer value) {
        return new JAXBElement<Integer>(_Levels_QNAME, Integer.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Initiator }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://doodle.com/xsd1", name = "initiator")
    public JAXBElement<Initiator> createInitiator(Initiator value) {
        return new JAXBElement<Initiator>(_Initiator_QNAME, Initiator.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://doodle.com/xsd1", name = "name")
    public JAXBElement<String> createName(String value) {
        return new JAXBElement<String>(_Name_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://doodle.com/xsd1", name = "hidden")
    public JAXBElement<Boolean> createHidden(Boolean value) {
        return new JAXBElement<Boolean>(_Hidden_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://doodle.com/xsd1", name = "description")
    public JAXBElement<String> createDescription(String value) {
        return new JAXBElement<String>(_Description_QNAME, String.class, null, value);
    }

}
