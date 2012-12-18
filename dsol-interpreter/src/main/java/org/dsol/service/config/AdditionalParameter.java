package org.dsol.service.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "additional-parameter", propOrder = {"index","name","value"})
@XmlAccessorType(XmlAccessType.FIELD)
public class AdditionalParameter {
	
	@XmlAttribute(name="at-index")
	private Integer index;
	@XmlAttribute
	private String name;
	@XmlAttribute
	private String value;
	
	public AdditionalParameter() {}
	
	public AdditionalParameter(Integer index, String name, String value){
		this.index = index;
		this.name = name;
		this.value = value;
	}
	
	public Integer getIndex() {
		return index;
	}
	
	public String getName() {
		return name;
	}
	
	public String getValue() {
		return value;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	

}
