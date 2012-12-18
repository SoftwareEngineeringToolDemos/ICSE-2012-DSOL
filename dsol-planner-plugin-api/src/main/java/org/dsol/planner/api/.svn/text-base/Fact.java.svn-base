package org.dsol.planner.api;

public class Fact {
	private String value;

	public Fact(String value) {
		this.value = value.trim();
	}
	
	/**
	 * Return the string representation of the fact
	 * @return
	 */
	public String get(){
		return value;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Fact other = (Fact) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return value;
	}	
}
