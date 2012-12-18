package org.dsol.planner.qos;

public class QoSMetric {
	
	private String name;
	private Double value;
	
	public QoSMetric(String metricName, Double value) {
		super();
		this.name = metricName;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	
	public Double getValue() {
		return value;
	}
}
