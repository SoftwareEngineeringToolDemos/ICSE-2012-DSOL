package domain;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder = { "option"})
public class Option {
	
	public Option() {}
	
	public Option(String option) {
		super();
		this.option = option;
	}

	private String option;

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	
}
