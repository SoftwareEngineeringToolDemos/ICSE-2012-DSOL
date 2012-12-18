package graphplan.flyweight;

import jason.asSyntax.Term;

import java.util.Iterator;
import java.util.Set;

/**
 * An iterator to generate all possible combinations of
 * terms for a given vector size
 * @author Felipe Meneguzzi
 *
 */
public class TermInstanceIterator implements Iterator<Term[]> {
	protected final Iterator<Term>[] iterators;
	protected final Term[] currentTerms;
	protected final Set<Term> terms;
	
	public TermInstanceIterator(Set<Term> terms, int size) {
		iterators = new Iterator[size];
		currentTerms = new Term[size];
		this.terms = terms;
		for(int i=0; i<iterators.length; i++) {
			iterators[i]=terms.iterator();
			//Initialize all but the first term
			//to comply with the next method
			if(i>0) {
				currentTerms[i] = iterators[i].next();
			}
		}
	}

	public boolean hasNext() {
		for(Iterator<Term> iterator : iterators) {
			if(iterator.hasNext()) {
				return true;
			}
		}
		return false;
	}

	public Term[] next() {
		boolean advanceNext = true;
		int i=0;
		while(advanceNext) {
			if(iterators[i].hasNext()) {
				advanceNext = false;
				currentTerms[i] = iterators[i].next();
			} else {
				iterators[i] = terms.iterator();
				currentTerms[i] = iterators[i].next();
				i++;
			}
		}
		return currentTerms;
	}

	public void remove() {}
	
}