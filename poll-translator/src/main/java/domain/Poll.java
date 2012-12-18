package domain;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="poll", namespace="http://doodle.com/xsd1")
@XmlType(propOrder = { "type","hidden","levels","title","description","initiator","options" })
@XmlAccessorType(XmlAccessType.FIELD)
public class Poll {

	@XmlElement(namespace="http://doodle.com/xsd1")
	private String type;
	@XmlElement(namespace="http://doodle.com/xsd1")
	private boolean hidden;
	@XmlElement(namespace="http://doodle.com/xsd1")
	private int levels;
	@XmlElement(namespace="http://doodle.com/xsd1")
	private String title;
	@XmlElement(namespace="http://doodle.com/xsd1")
	private String description;
	@XmlElement(namespace="http://doodle.com/xsd1")
	private Initiator initiator;
	@XmlElement(namespace="http://doodle.com/xsd1")
	private Options options;

	public Poll() {}
	
	public Poll(Poll poll) {
		this(poll.type,poll.hidden,poll.levels,poll.title,poll.description,poll.initiator,poll.options);
	}
	
	public Poll(String type, boolean hidden, int levels, String title,
			String description, Initiator initiator, Options options) {
		super();
		this.type = type;
		this.hidden = hidden;
		this.levels = levels;
		this.title = title;
		this.description = description;
		this.initiator = initiator;
		this.options = options;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
	
	public Options getOptions() {
		return options;
	}

	public void setOptions(Options options) {
		this.options = options;
	}

	public Initiator getInitiator() {
		return initiator;
	}

	public void setInitiator(Initiator initiator) {
		this.initiator = initiator;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public int getLevels() {
		return levels;
	}

	public void setLevels(int levels) {
		this.levels = levels;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	
	
//	<poll xmlns="http://doodle.com/xsd1">
//	<type>TEXT</type>
//	<hidden>false</hidden>
//	<levels>2</levels>
//	<title>PPP</title>
//	<description>yum!</description>
//	<initiator>
//	<name>Paul</name>
//	</initiator>
//	<options>
//	<option>Pasta</option>
//	<option>Pizza</option>
//	<option>Polenta</option>
//	</options>
//	</poll>
	
	
	
}
