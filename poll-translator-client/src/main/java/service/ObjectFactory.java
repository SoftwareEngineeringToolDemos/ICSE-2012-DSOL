
package service;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the service package. 
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

    private final static QName _GetTranslatedPoll_QNAME = new QName("http://service/", "getTranslatedPoll");
    private final static QName _GetTranslatedPollResponse_QNAME = new QName("http://service/", "getTranslatedPollResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: service
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Initiator }
     * 
     */
    public Initiator createInitiator() {
        return new Initiator();
    }

    /**
     * Create an instance of {@link GetTranslatedPollResponse }
     * 
     */
    public GetTranslatedPollResponse createGetTranslatedPollResponse() {
        return new GetTranslatedPollResponse();
    }

    /**
     * Create an instance of {@link Poll }
     * 
     */
    public Poll createPoll() {
        return new Poll();
    }

    /**
     * Create an instance of {@link Options }
     * 
     */
    public Options createOptions() {
        return new Options();
    }

    /**
     * Create an instance of {@link GetTranslatedPoll }
     * 
     */
    public GetTranslatedPoll createGetTranslatedPoll() {
        return new GetTranslatedPoll();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetTranslatedPoll }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service/", name = "getTranslatedPoll")
    public JAXBElement<GetTranslatedPoll> createGetTranslatedPoll(GetTranslatedPoll value) {
        return new JAXBElement<GetTranslatedPoll>(_GetTranslatedPoll_QNAME, GetTranslatedPoll.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetTranslatedPollResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://service/", name = "getTranslatedPollResponse")
    public JAXBElement<GetTranslatedPollResponse> createGetTranslatedPollResponse(GetTranslatedPollResponse value) {
        return new JAXBElement<GetTranslatedPollResponse>(_GetTranslatedPollResponse_QNAME, GetTranslatedPollResponse.class, null, value);
    }

}
