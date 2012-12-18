package domain;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "options", namespace = "http://doodle.com/xsd1")
@XmlAccessorType(XmlAccessType.FIELD)
public class Options {

	public Options() {
	}

	public Options(Collection<String> option) {
		super();
		this.option = option;
	}

	@XmlElement(namespace = "http://doodle.com/xsd1")
	private Collection<String> option;

	public Collection<String> getOption() {
		return option;
	}

	public void setOption(Collection<String> option) {
		this.option = option;
	}

}
