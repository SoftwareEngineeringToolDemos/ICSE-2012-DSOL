package org.dsol.planner.api;

import jason.asSyntax.Structure;
import jason.asSyntax.Term;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dsol.planner.qos.QoS;
import org.dsol.planner.qos.QoSMetric;
import org.dsol.planner.qos.QosBound;

public class Goal extends ArrayList<Fact> {

	protected Map<String, QosBound> desiredQoSValues = new HashMap<String, QosBound>();
	protected List<String> maxGuarantees = new ArrayList<String>();
	protected List<String> minGuarantees = new ArrayList<String>();

	@Override
	public boolean add(Fact goalPart) {
		if (goalPart == null) {
			throw new IllegalArgumentException("GoalPart cannot be null!");
		}

		String goalName = getGoalName(goalPart);
		if (isQoSGoal(goalName)) {
			// check whether this is a QoS goal of not
			if (isADesiredGoal(goalName)) {
				setDesiredQoS(goalPart);
			} else if (isAMaxGoal(goalName)) {
				setMaxQoS(goalPart);
			} else if (isAMinGoal(goalName)) {
				setMinQoS(goalPart);
			}
			return true;
		}
		return super.add(goalPart);
	}

	private boolean isADesiredGoal(String goalName) {
		return Planner.DESIRED_QOS.equals(goalName);
	}
	
	private boolean isAMaxGoal(String goalName) {
		return Planner.MAX_QOS.equals(goalName);
	}
	
	private boolean isAMinGoal(String goalName) {
		return Planner.MIN_QOS.equals(goalName);
	}

	private void setDesiredQoS(Fact desiredGoal) {
		List<Term> terms = Structure.parseLiteral(desiredGoal.get()).getTerms();
		if (terms.size() < 2) {
			throw new RuntimeException(
					"Invalid DESIRED goal. expected [desired(Metric, Value, UpperBound or LowerBound)]");
		}
		String qosMetricName = terms.get(0).toString();
		String qosBoundValue = terms.get(1).toString();
		QoSMetric qosMetric = new QoSMetric(qosMetricName, new Double(
				qosBoundValue));
		QosBound qosBound = null;

		if (qosMetricName.equals(QoS.RELIABILITY.value())) {
			qosBound = QosBound.createLowerBound(qosMetric);

		} else if (qosMetricName.equals(QoS.RESPONSE_TIME.value())) {
			qosBound = QosBound.createUpperBound(qosMetric);
		} else {
			boolean upperBound = false; // by default is an lower bound
			if (terms.size() >= 3) {
				String boundLimit = terms.get(2).toString();// UpperBound or
															// LowerBound
				upperBound = Planner.UPPER_BOUND.equalsIgnoreCase(boundLimit);
			}

			if (upperBound) {
				qosBound = QosBound.createUpperBound(qosMetric);
			} else {
				qosBound = QosBound.createLowerBound(qosMetric);
			}
		}

		desiredQoSValues.put(qosBound.getMetricName(), qosBound);
	}

	private void setMaxQoS(Fact maxGoal) {
		List<Term> terms = Structure.parseLiteral(maxGoal.get()).getTerms();
		if (terms.isEmpty()) {
			throw new RuntimeException(
					"Invalid MAX goal. expected [max(Metric)]");
		}
		String qosMetricName = terms.get(0).toString();
		maxGuarantees.add(qosMetricName);
	}

	private void setMinQoS(Fact minGoal) {
		List<Term> terms = Structure.parseLiteral(minGoal.get()).getTerms();
		if (terms.isEmpty()) {
			throw new RuntimeException(
					"Invalid MIN goal. expected [max(Metric)]");
		}
		String qosMetricName = terms.get(0).toString();
		minGuarantees.add(qosMetricName);
	}

	private String getGoalName(Fact goal) {
		return Structure.parseLiteral(goal.get()).getFunctor();
	}

	private boolean isQoSGoal(String goalName) {
		// check whether this is a QoS goal of not
		return Planner.DESIRED_QOS.equals(goalName)
				|| Planner.MAX_QOS.equals(goalName)
				|| Planner.MIN_QOS.equals(goalName);
	}

	public boolean isQoSEnabled() {
		return false;//!desiredQoSValues.isEmpty() || !maxGuarantees.isEmpty() || !minGuarantees.isEmpty();
	}
	
	public QosBound getDesiredQoSValue(String metricName) {
		return desiredQoSValues.get(metricName);
	}
	
	public boolean isDesiredValueDefinedForQoSMetric(String qosMetric) {
		return desiredQoSValues.containsKey(qosMetric);
	}
	
	@Override
	public String toString() {
		if (isEmpty()) {
			return "error: goal with no facts";
		}

		StringBuilder asString = new StringBuilder("goal(");
		for (Fact fact : this) {
			asString.append(fact.get());
			asString.append(",");
		}
		asString.deleteCharAt(asString.length() - 1);
		asString.append(")");
		return asString.toString();
	}
}
