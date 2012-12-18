package domain;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="initiator", namespace="http://doodle.com/xsd1")
@XmlType(propOrder = { "name","eMailAddress" })
@XmlAccessorType(XmlAccessType.FIELD)
public class Initiator {
	
	@XmlElement(namespace="http://doodle.com/xsd1")
	private String name;
	@XmlElement(namespace="http://doodle.com/xsd1")
	private String eMailAddress;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String geteMailAddress() {
		return eMailAddress;
	}
	public void seteMailAddress(String eMailAddress) {
		this.eMailAddress = eMailAddress;
	}
	
	

}
