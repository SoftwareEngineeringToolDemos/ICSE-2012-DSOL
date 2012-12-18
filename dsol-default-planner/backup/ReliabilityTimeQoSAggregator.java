package graphplan.domain.qos;


public class ReliabilityTimeQoSAggregator extends ProductQoSAggregator {

	@Override
	public String getMetricName() {
		return QoS.RELIABILITY.value();
	}

}
