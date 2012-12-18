package org.dsol.planner.qos;

public class QosBound {

	private QoSMetric qoSMetric;
	private boolean lowerBound;

	private QosBound(QoSMetric qoSMetric, boolean lowerBound) {
		this.qoSMetric = qoSMetric;
		this.lowerBound = lowerBound;
	}

	public boolean isLowerBound() {
		return lowerBound;
	}

	public Double getValue() {
		return qoSMetric.getValue();
	}

	public String getMetricName() {
		return qoSMetric.getName();
	}

	public static QosBound createUpperBound(QoSMetric qoSMetric) {
		return new QosBound(qoSMetric, false);
	}

	public static QosBound createLowerBound(QoSMetric qoSMetric) {
		return new QosBound(qoSMetric, true);
	}

}
