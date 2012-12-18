
package service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for poll complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="poll">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://doodle.com/xsd1}type" minOccurs="0"/>
 *         &lt;element ref="{http://doodle.com/xsd1}hidden"/>
 *         &lt;element ref="{http://doodle.com/xsd1}levels"/>
 *         &lt;element ref="{http://doodle.com/xsd1}title" minOccurs="0"/>
 *         &lt;element ref="{http://doodle.com/xsd1}description" minOccurs="0"/>
 *         &lt;element ref="{http://doodle.com/xsd1}initiator" minOccurs="0"/>
 *         &lt;element ref="{http://doodle.com/xsd1}options" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "poll", propOrder = {
    "type",
    "hidden",
    "levels",
    "title",
    "description",
    "initiator",
    "options"
})
public class Poll {

    @XmlElement(namespace = "http://doodle.com/xsd1")
    protected String type;
    @XmlElement(namespace = "http://doodle.com/xsd1")
    protected boolean hidden;
    @XmlElement(namespace = "http://doodle.com/xsd1")
    protected int levels;
    @XmlElement(namespace = "http://doodle.com/xsd1")
    protected String title;
    @XmlElement(namespace = "http://doodle.com/xsd1")
    protected String description;
    @XmlElement(namespace = "http://doodle.com/xsd1")
    protected Initiator initiator;
    @XmlElement(namespace = "http://doodle.com/xsd1")
    protected Options options;

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the hidden property.
     * 
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * Sets the value of the hidden property.
     * 
     */
    public void setHidden(boolean value) {
        this.hidden = value;
    }

    /**
     * Gets the value of the levels property.
     * 
     */
    public int getLevels() {
        return levels;
    }

    /**
     * Sets the value of the levels property.
     * 
     */
    public void setLevels(int value) {
        this.levels = value;
    }

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the initiator property.
     * 
     * @return
     *     possible object is
     *     {@link Initiator }
     *     
     */
    public Initiator getInitiator() {
        return initiator;
    }

    /**
     * Sets the value of the initiator property.
     * 
     * @param value
     *     allowed object is
     *     {@link Initiator }
     *     
     */
    public void setInitiator(Initiator value) {
        this.initiator = value;
    }

    /**
     * Gets the value of the options property.
     * 
     * @return
     *     possible object is
     *     {@link Options }
     *     
     */
    public Options getOptions() {
        return options;
    }

    /**
     * Sets the value of the options property.
     * 
     * @param value
     *     allowed object is
     *     {@link Options }
     *     
     */
    public void setOptions(Options value) {
        this.options = value;
    }

}
