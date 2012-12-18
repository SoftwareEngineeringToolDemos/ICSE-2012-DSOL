package graphplan.flyweight;

import jason.asSyntax.Term;

import java.util.Iterator;
import java.util.Set;

public class TermInstanceIterator2 implements Iterator<Term[]> {
	protected Term[] availableTerms = null;
	private Term[] next;
	
	Permutation permutation;
	CombinationGenerator combinationGenerator;
	int[] currentCombination;
	private boolean moveToNextCombination = true;
	private int size;
	
	private static Term[] emptyArray = new Term[0];
	public TermInstanceIterator2(Set<Term> availableTerms, int size) {
		this.availableTerms = availableTerms.toArray(emptyArray);
		next = new Term[size];
		combinationGenerator = new CombinationGenerator(this.availableTerms.length, size);
		permutation = new Permutation(size);
		permutation.last();
		moveToNextCombination = true;
		this.size = size;
	}

	public boolean hasNext() {
		if(!permutation.hasNext()){
			moveToNextCombination = true;
		}
		else{
			return true;
		}
		return combinationGenerator.hasMore();
	}

	public Term[] next() {
		if(moveToNextCombination){
			currentCombination = combinationGenerator.getNext();
			moveToNextCombination = false;
		}
		permutation.next();
		for (int i = 0; i < size; i++) {
			this.next[i] = availableTerms[currentCombination[permutation.get(i)]];
		}
		return this.next;
	}

	public void remove() {}
	
}