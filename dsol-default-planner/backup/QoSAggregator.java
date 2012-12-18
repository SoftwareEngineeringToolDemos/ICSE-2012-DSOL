package graphplan.domain.qos;

import graphplan.PlanResult;

public interface QoSAggregator{
	
	public String getMetricName();
	public Double getPlanQoSAggregatedValue(PlanResult plan);

}
