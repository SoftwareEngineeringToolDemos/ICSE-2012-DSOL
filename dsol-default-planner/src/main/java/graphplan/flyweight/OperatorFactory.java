/*
 * ---------------------------------------------------------------------------
 * Copyright (C) 2010  Felipe Meneguzzi
 * JavaGP is distributed under LGPL. See file LGPL.txt in this directory.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * To contact the author:
 * http://www.meneguzzi.eu/felipe/contact.html
 * ---------------------------------------------------------------------------
 */
package graphplan.flyweight;

import graphplan.domain.Operator;
import graphplan.domain.Proposition;
import graphplan.domain.jason.OperatorImpl;
import graphplan.domain.jason.PropositionImpl;
import graphplan.domain.jason.SeamOperatorImpl;
import graphplan.graph.PropositionLevel;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.LiteralImpl;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A factory class to be used in the creation and maintenance of
 * operators for our planning system using the flyweight pattern.
 * XXX It may have to be modified if multiple instances of the planner are to be used 
 * @author Felipe Meneguzzi
 *
 */
public class OperatorFactory {
	public static final String NOOP_FUNCTOR = "noop";
	public static boolean multiThread = true;
	//private static OperatorFactory operatorFactory = null;
	
	private static ThreadLocal<OperatorFactory> operatorFactory = null;
	private static OperatorFactory operatorFactorySingleThread = null;
	
	//ADDED_CODE
	public static OperatorFactory getInstance(boolean forceNew) {
		if(multiThread){
			if(operatorFactory == null){
				operatorFactory = new ThreadLocal<OperatorFactory>();	
			}
			if(forceNew || operatorFactory.get() == null) {
				operatorFactory.set(new OperatorFactory());
			}
			return operatorFactory.get();			
		}
		else{
			if(forceNew || operatorFactorySingleThread == null){
				operatorFactorySingleThread = new OperatorFactory();
			}
			return operatorFactorySingleThread;
		}
	}

	/**
	 * Returns the singleton <code>OperatorFactory</code> instance.
	 * @return
	 */
	//ADDED_CODE
	public static OperatorFactory getInstance() {
		return getInstance(false);
	}
	
	protected Map<String, OperatorImpl> operatorInstances;
	protected Map<String, OperatorImpl> operatorTemplates;
	
	public OperatorFactory() {
		if(multiThread){
			this.operatorInstances = Collections.synchronizedMap(new LinkedHashMap<String, OperatorImpl>());
			this.operatorTemplates = Collections.synchronizedMap(new LinkedHashMap<String, OperatorImpl>());			
		}
		else{
			this.operatorInstances = new LinkedHashMap<String, OperatorImpl>();
			this.operatorTemplates = new LinkedHashMap<String, OperatorImpl>();						
		}
	}
	
	public Operator createOperatorTemplate(String signature, String[] preconds, String[] effects) {
		Structure oper = Structure.parse(signature);
		ArrayList<Proposition> precondProps = new ArrayList<Proposition>(preconds.length);
		ArrayList<Proposition> effectProps = new ArrayList<Proposition>(effects.length);
		
		for (int i = 0; i < preconds.length; i++) {
			PropositionImpl precond = new PropositionImpl(preconds[i]);
			precondProps.add(precond);
		}
		
		for (int i = 0; i < effects.length; i++) {
			PropositionImpl effect = new PropositionImpl(effects[i]);
			effectProps.add(effect);
		}
		
		OperatorImpl operator = new OperatorImpl(oper, precondProps, effectProps);
		
		return operator;
	}
	
	public void addOperatorTemplate(Operator operator) throws OperatorFactoryException {
		//We check if the operator has no arguments, or if it is not ground
		//Operators with arguments should not be ground
		//ADDED_CODE:
		List terms = operator.getTerms();
		if((terms == null || terms.isEmpty()) || !operator.isGround()) {
			//TODO this cast to OperatorImpl has to be reviewed, since it violates the Bridge pattern 
			this.operatorTemplates.put(operator.getOperatorIndicator(), (OperatorImpl)operator);
		} else {
			throw new OperatorFactoryException("Operator "+operator+" is not ground");
		}
	}
	
	public static void reset() {
		operatorFactory.get().resetOperatorFactory();
	}

	public void resetInvalidOperations() {
		if(invalidOperations != null){
			invalidOperations.clear();			
		}
	}
	
	/**
	 * Resets the operator factory so that it is detached from all flyweight
	 * operators created so far. Invoke this method with caution.
	 *
	 */
	protected void resetOperatorFactory() {
		this.operatorInstances.clear();
		this.operatorTemplates.clear();
		this.invalidOperations.clear();
	}

	
	/**
	 * Resets the operator templates from this operator factory,
	 * this method is relevant when operating with multiple
	 * domains being used at the same time.
	 *
	 */
	public void resetOperatorTemplates() {
		this.operatorTemplates.clear();
	}

	
	/**
	 * Creates a noop action to propagate the supplied proposition between two proposition
	 * levels.
	 * @param proposition
	 */
	public Operator getNoop(Proposition proposition) {
		String noopSignature = NOOP_FUNCTOR+"_";
		if(proposition.negated()) {
			noopSignature+="not_"+(proposition.toString().substring(1));
		} else {
			noopSignature+=proposition.toString();
		}
		if(operatorInstances.containsKey(noopSignature)) {
			return operatorInstances.get(noopSignature);
		} else {
			List<Proposition> precondsEffects = new ArrayList<Proposition>(1);
			precondsEffects.add(proposition);
			Structure signature = Structure.parse(noopSignature);
			OperatorImpl operator = new OperatorImpl(signature, precondsEffects, precondsEffects);
			this.operatorInstances.put(noopSignature, operator);
			return operator;
		}
	}

	/**
	 * Returns a fully instantiated operator.
	 * 
	 * @param operatorSignature
	 * @return
	 * @throws OperatorFactoryException
	 */
	public Operator getOperator(String operatorSignature) throws OperatorFactoryException {
		//If this operator has been used before, retrieve it from the flyweights
		if(operatorInstances.containsKey(operatorSignature)) {
			return operatorInstances.get(operatorSignature);
		} else {
			//Otherwise, we have to create a new one from a template
			Structure signature = Structure.parse(operatorSignature);
			//If a template exists matching the supplied indicator
			if(this.operatorTemplates.containsKey(signature.getPredicateIndicator().toString())) {
				//We get the template
				OperatorImpl template = (OperatorImpl) this.operatorTemplates.get(signature.getPredicateIndicator().toString());
				//And start copying it
				Structure templateSignature = new Structure(template);
				Unifier un = new Unifier();
				//The signature should unify with the template
				if(!un.unifies(signature, templateSignature)) {
					//This should not happen in a properly described domain
					throw new OperatorFactoryException("Failed to unify "+templateSignature+" with operator "+templateSignature+" instantiating "+operatorSignature);
				}
				templateSignature.apply(un);
				PropositionFactory propositionFactory = PropositionFactory.getInstance();
				
				//Then get the preconditions in the template
				List<Proposition> templatePreconds = template.getPreconds();
				List<Proposition> concretePreconds = new ArrayList<Proposition>(templatePreconds.size());
				//And apply the unifier to them
				for (Iterator iter = templatePreconds.iterator(); iter
						.hasNext();) {
					PropositionImpl precond = (PropositionImpl) iter.next();
					Literal literal = new LiteralImpl(precond);
					literal.apply(un);
					PropositionImpl concretePrecond = (PropositionImpl)propositionFactory.getProposition(literal.toString());
					// XXX Uncomment the code to debug operator instantiation, it is removed to avoid wasting time here
//					if(!concretePrecond.isGround()) {
//						//throw new OperatorFactoryException("We have a non-concrete precond in operator "+signature+": "+concretePrecond);
//						System.err.println("We have a non-concrete precond in operator "+signature+": "+concretePrecond);
//					}
					concretePreconds.add(concretePrecond);
				}
				
				List<Proposition> templateEffects = template.getEffects();
				List<Proposition> concreteEffects = new ArrayList<Proposition>(templateEffects.size());
				
				for (Iterator iter = templateEffects.iterator(); iter
						.hasNext();) {
					PropositionImpl effect = (PropositionImpl) iter.next();
					Literal literal = new LiteralImpl(effect);
					literal.apply(un);
					PropositionImpl concreteEffect = (PropositionImpl)propositionFactory.getProposition(literal.toString());
					// XXX Uncomment the code to debug operator instantiation, it is removed to avoid wasting time here
//					if(!concreteEffect.isGround()) {
//						//throw new OperatorFactoryException("We have a non-concrete effect in operator "+signature+": "+concreteEffect);
//						System.err.println("We have a non-concrete effect in operator "+signature+": "+concreteEffect);
//					}
					concreteEffects.add(concreteEffect);
				}
				
				OperatorImpl operatorImpl;
				if (!template.isSeam()) {
					operatorImpl = new OperatorImpl(templateSignature,
							concretePreconds, concreteEffects);
				} else {
					operatorImpl = new SeamOperatorImpl(templateSignature,
							concretePreconds, concreteEffects);
				}
				this.operatorInstances.put(operatorImpl.getSignature(), operatorImpl);
				return operatorImpl;
			} else {
				throw new OperatorFactoryException("No operator template for "+operatorSignature);
			}
		}
	}

	public List<Operator> getRequiringOperatorTemplates(PropositionLevel propositionLevel) {
		List<Operator> templates = new ArrayList<Operator>();
		
		OperatorImpl oper = null;
		boolean unifiesAllPreconditions = true;
		boolean unifiesPreCondition = false;
		List<Proposition> preconds = null;
		Proposition operatorPrecondition;
		//Scan every operator template
		for(Iterator<OperatorImpl> e = operatorTemplates.values().iterator(); e.hasNext(); ) {
			oper = e.next();
			//if the parameter is null or a true proposition
			//return any empty preconditions operator
			if(propositionLevel == null) {
				if(oper.getPreconds().isEmpty()) {
					templates.add(oper);
				}
				continue;
			}			
			//or trying to unify the supplied precondition with any of the preconditions
			unifiesAllPreconditions = true;
			unifiesPreCondition = false;
			preconds = oper.getPreconds();
			for (int i = 0; i < preconds.size(); i++) {
				operatorPrecondition = preconds.get(i);
				unifiesPreCondition = false;
				for(Proposition precond:propositionLevel){
					unifiesPreCondition = unifiesPreCondition || precond.unifies(operatorPrecondition);
				}
				unifiesAllPreconditions = unifiesAllPreconditions && unifiesPreCondition;					
			}
			if(unifiesAllPreconditions && !oper.getPreconds().isEmpty()) {
				templates.add(oper);
			}
		}
		return templates;
	}
	
	public List<Operator> getRequiringOperatorTemplates(Proposition precond) {
		List<Operator> templates = new ArrayList<Operator>();
		//Scan every operator template
		for(Iterator<OperatorImpl> e = operatorTemplates.values().iterator(); e.hasNext(); ) {
			OperatorImpl oper = e.next();
			//if the parameter is null or a true proposition
			//return any empty preconditions operator
			if(precond == null) {
				if(oper.getPreconds().isEmpty()) {
					templates.add(oper);
				}
				continue;
			}			
			//or trying to unify the supplied precondition with any of the preconditions
			for (Proposition oPrecond : oper.getPreconds()) {
				if(precond.unifies(oPrecond)) {
					templates.add(oper);
				}
			}
		}
		return templates;
	}
	
	public List<Operator> getCausingOperatorsTemplates(Proposition effect) {
		List<Operator> templates = new ArrayList<Operator>();
		//Scan every operator template
		for(Iterator<OperatorImpl> e = operatorTemplates.values().iterator(); e.hasNext(); ) {
			OperatorImpl oper = e.next();
			//and trying to unify the supplied effect with any of the effects
			for (Proposition oEffect : oper.getEffects()) {
				if(effect.unifies(oEffect)) {
					templates.add(oper);
				}
			}
		}
		return templates;
	}
	
	/**
	 * Returns all possible instantiations of the supplied operators, given
	 * the supplied preconditions.
	 * 
	 * @param operators
	 * @param propositions
	 * @return
	 * @throws OperatorFactoryException 
	 */
	public Set<Operator> getAllPossibleInstantiations(	List<Operator> operators, 
														final Set<Proposition> preconds) throws OperatorFactoryException {
		
		final Set<Operator> instances = new LinkedHashSet<Operator>();
		List operatorTerms = null;
		Set<Term> terms = null;
		Term termInstances[] = null;
		TermInstanceIterator2 it = null;
		Operator instance = null;
		OperatorImpl copy;
		Structure struct;
		Unifier unifier = new Unifier();
		
		//For each operator template 
		//We need to instantiate it in every possible way
		//allowed by the propositions
		for(Operator operator : operators) {
			//If this operator has no parameters, there is only one way
			//to instantiate it
			operatorTerms = operator.getTerms();
			if((operatorTerms == null || operatorTerms.isEmpty())) {
				if(!isInvalidOperatorIntantiation(operator)){
					instances.add(getOperator(operator.getSignature()));					
				}
			} else {
				terms = getAllPossibleTerms(preconds,operator);
				if(operatorTerms.size() > terms.size()){
					//IMPOSSIBLE UNIFICATION
					continue;
				}
				it = new TermInstanceIterator2(terms, operatorTerms.size());
				
				while(it.hasNext()){
					termInstances = it.next();
					copy = (OperatorImpl) operator.clone();					
					struct = new Structure(copy.getFunctor());
					struct.addTerms(termInstances);
					
					unifier.clear();
					if(unifier.unifies(copy, struct)) {
						copy.apply(unifier);
					} 

					if(preconds.containsAll(copy.getPreconds())) {
						instance = getOperator(copy.getSignature());
						//ADDED_CODE: this if was added
						if(!isInvalidOperatorIntantiation(instance)){
							instances.add(instance);	
						}
					}
				}
			}
		}
		return instances;
	}

	
	protected Set<Term> getAllPossibleTerms(Set<Proposition> propositions, Operator operator) {
		Set<Term> possibleTerms = new LinkedHashSet<Term>();
		Set<String> operatorPreconds = new HashSet<String>();
		String functor = null;
		for(Proposition precond : operator.getPreconds()){
			if(precond.getTerms() != null){
				functor = precond.getFunctor();
				if(precond.negated()){
					functor = "~"+functor;
				}
				operatorPreconds.add(functor);
			}
		}
		
		for(Proposition proposition : propositions) {
			functor = proposition.getFunctor();
			if(proposition.negated()){
				functor = "~"+functor;
			}
			
			List<Term> terms = proposition.getTerms();
			if(terms != null && !terms.isEmpty() && operatorPreconds.contains(functor)) {
				possibleTerms.addAll(terms);
			}
		}		
		return possibleTerms;//.toArray(emptyArray);
	}
	

	
	
	/**
	 * A utility function that returns an empty operator iterator
	 * @return
	 */
	public static Iterator<Operator> getEmptyIterator() {
		Iterator<Operator> iterator = new Iterator<Operator>() {

			public boolean hasNext() {return false;}

			public Operator next() {return null;}

			public void remove() {}
			
		};
		
		return iterator;
	}
	
	
	
	/////////////////////// ADDED_CODE: ///////////////////////////////////////////
	
//    private ThreadLocal<HashMap<String, Operator>> invaliOperations = new ThreadLocal<HashMap<String, Operator>>() {
//        protected synchronized HashMap<String, Operator> initialValue() {
//            return new HashMap<String, Operator>();
//        }
//    };
    
	Set<String> invalidOperations = new HashSet<String>();
	
    private boolean isInvalidOperatorIntantiation(Operator operator){
    	return invalidOperations.contains(Structure.parse(operator.toString()).toString());
    }
	
	public void addInvalidOperatorInstantiation(String operator){
		invalidOperations.add(Structure.parse(operator).toString());
	}	
	
    //////////////////////////////////////////////////////////////////////////////
}
