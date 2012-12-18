package graphplan.domain.jason;

import graphplan.domain.Operator;
import graphplan.domain.Proposition;
import jason.asSyntax.Structure;

import java.util.List;

@SuppressWarnings("unchecked")
public class SeamOperatorImpl extends OperatorImpl{

	public SeamOperatorImpl(String declaration) {
		super(declaration);
	}
	
	public SeamOperatorImpl(Structure functor, List<Proposition> preconds, List<Proposition> effects) {
		super(functor,preconds,effects);
	}
	
	public SeamOperatorImpl(Operator operator) {
		super(operator);
	}
	
	@Override
	public boolean isSeam() {
		return true;
	}

	
	
}
