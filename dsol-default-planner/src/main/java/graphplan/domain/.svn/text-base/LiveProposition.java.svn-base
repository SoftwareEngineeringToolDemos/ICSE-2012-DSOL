package graphplan.domain;

import jason.asSyntax.Term;

import java.util.List;

public enum LiveProposition {

	gt{
		@Override
		public boolean exec(Proposition proposition) {
			List<Term> terms = proposition.getTerms();
			if(terms.size() != 2){
				throw new RuntimeException("\"gt\" predicate must be executed with two parameters");
			}
			
			int firstValue = Integer.parseInt(terms.get(0).toString());
			int secondValue = Integer.parseInt(terms.get(1).toString());
			
			return firstValue > secondValue;
		}
	},
	
	ge{
		@Override
		public boolean exec(Proposition proposition) {
			List<Term> terms = proposition.getTerms();
			if(terms.size() != 2){
				throw new RuntimeException("\"ge\" predicate must be executed with two parameters");
			}
			
			int firstValue = Integer.parseInt(terms.get(0).toString());
			int secondValue = Integer.parseInt(terms.get(1).toString());
			
			return firstValue >= secondValue;
		}
	},

	lt{
		@Override
		public boolean exec(Proposition proposition) {
			List<Term> terms = proposition.getTerms();
			if(terms.size() != 2){
				throw new RuntimeException("\"lt\" predicate must be executed with two parameters");
			}
			
			int firstValue = Integer.parseInt(terms.get(0).toString());
			int secondValue = Integer.parseInt(terms.get(1).toString());
			
			return firstValue < secondValue;
		}
	},	
	
	le{
		@Override
		public boolean exec(Proposition proposition) {
			List<Term> terms = proposition.getTerms();
			if(terms.size() != 2){
				throw new RuntimeException("\"le\" predicate must be executed with two parameters");
			}
			
			int firstValue = Integer.parseInt(terms.get(0).toString());
			int secondValue = Integer.parseInt(terms.get(1).toString());
			
			return firstValue <= secondValue;
		}
	},
	eq{
		@Override
		public boolean exec(Proposition proposition) {
			List<Term> terms = proposition.getTerms();
			if(terms.size() != 2){
				throw new RuntimeException("\"eq\" predicate must be executed with two parameters");
			}
			
			String firstValue = terms.get(0).toString();
			String secondValue = terms.get(1).toString();
			
			return firstValue.equals(secondValue);
		}
	},
	not_equal{
		@Override
		public boolean exec(Proposition proposition) {
			List<Term> terms = proposition.getTerms();
			if(terms.size() != 2){
				throw new RuntimeException("\"not_equal\" predicate must be executed with two parameters");
			}
			
			String firstValue = terms.get(0).toString();
			String secondValue = terms.get(1).toString();
			
			return !firstValue.equals(secondValue);
		}
	};;
	
	public abstract boolean exec(Proposition proposition);

	public static boolean isValid(String name){
		return 	name.equals("gt") ||
				name.equals("ge") ||
				name.equals("lt") ||
				name.equals("le") ||
				name.equals("eq") ||
				name.equals("not_equal");
		//return name.matches(values);	
	}
}
