
package service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getTranslatedPollResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getTranslatedPollResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="translatedPoll" type="{http://service/}poll" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getTranslatedPollResponse", propOrder = {
    "translatedPoll"
})
public class GetTranslatedPollResponse {

    @XmlElement(namespace = "")
    protected Poll translatedPoll;

    /**
     * Gets the value of the translatedPoll property.
     * 
     * @return
     *     possible object is
     *     {@link Poll }
     *     
     */
    public Poll getTranslatedPoll() {
        return translatedPoll;
    }

    /**
     * Sets the value of the translatedPoll property.
     * 
     * @param value
     *     allowed object is
     *     {@link Poll }
     *     
     */
    public void setTranslatedPoll(Poll value) {
        this.translatedPoll = value;
    }

}
