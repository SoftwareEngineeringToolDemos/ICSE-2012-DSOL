package org.dsol.planner.qos;

public enum QoS {
	
	RELIABILITY {
		@Override
		public String value() {
			return "reliability";
		}
	}, RESPONSE_TIME {
		@Override
		public String value() {
			return "response_time";
		}
	};

	public abstract String value();

}
