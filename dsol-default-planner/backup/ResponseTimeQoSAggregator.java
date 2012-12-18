package graphplan.domain.qos;

import java.util.List;

import graphplan.PlanResult;
import graphplan.domain.Operator;

public class ResponseTimeQoSAggregator implements QoSAggregator {

	@Override
	public String getMetricName() {
		return QoS.RESPONSE_TIME.value();
	}

	@Override
	public Double getPlanQoSAggregatedValue(PlanResult plan) {
		return getPlanResponseExecutionTime(plan);
	}
	
	
	protected Double getPlanResponseExecutionTime(PlanResult plan) {
		Double planExecutionTime = 0d;
		for (List<Operator> level : plan.getSteps()) {
			planExecutionTime = planExecutionTime + getMaxExecutionTime(level);
		}
		
		return planExecutionTime;
	}

	protected Double getMaxExecutionTime(List<Operator> level) {
		Double max = 0d;
		for(Operator operator:level){
			Double responseTime = getResponseTime(operator);
			if(responseTime > max){
				max = responseTime;
			}
		}
		return max;
	}
	
	protected Double getResponseTime(Operator operator){
		Double responseTime = operator.getQoSValue(QoS.RESPONSE_TIME.value());
		if(responseTime == null){
			return 0d;
		}
		return responseTime;
	}
}
