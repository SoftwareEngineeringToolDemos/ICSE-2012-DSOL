import graphplan.domain.LiveProposition;
import graphplan.domain.Proposition;
import graphplan.domain.jason.PropositionImpl;
import junit.framework.Assert;

import org.junit.Test;


public class LivePropositionTest {
	
	@Test
	public void gt(){
		Proposition proposition = new PropositionImpl("gt(1,2)");
		Assert.assertFalse(LiveProposition.gt.exec(proposition));
	}

	@Test
	public void gt2(){
		Proposition proposition = new PropositionImpl("gt(2,2)");
		Assert.assertFalse(LiveProposition.gt.exec(proposition));
	}

	@Test
	public void gt3(){
		Proposition proposition = new PropositionImpl("gt(2,1)");
		Assert.assertTrue(LiveProposition.gt.exec(proposition));
	}
	
	@Test(expected=RuntimeException.class)
	public void gt4(){
		Proposition proposition = new PropositionImpl("gt(1)");
		LiveProposition.gt.exec(proposition);

	}
	
	@Test
	public void ge(){
		Proposition proposition = new PropositionImpl("ge(1,2)");
		Assert.assertFalse(LiveProposition.ge.exec(proposition));
	}

	@Test
	public void ge2(){
		Proposition proposition = new PropositionImpl("ge(2,2)");
		Assert.assertTrue(LiveProposition.ge.exec(proposition));
	}

	@Test
	public void ge3(){
		Proposition proposition = new PropositionImpl("ge(2,1)");
		Assert.assertTrue(LiveProposition.ge.exec(proposition));
	}
	
	@Test(expected=RuntimeException.class)
	public void ge4(){
		Proposition proposition = new PropositionImpl("ge(1)");
		LiveProposition.ge.exec(proposition);

	}
	
	@Test
	public void lt(){
		Proposition proposition = new PropositionImpl("lt(1,2)");
		Assert.assertTrue(LiveProposition.lt.exec(proposition));
	}

	@Test
	public void lt2(){
		Proposition proposition = new PropositionImpl("lt(2,2)");
		Assert.assertFalse(LiveProposition.lt.exec(proposition));
	}

	@Test
	public void lt3(){
		Proposition proposition = new PropositionImpl("lt(2,1)");
		Assert.assertFalse(LiveProposition.lt.exec(proposition));
	}
	
	@Test(expected=RuntimeException.class)
	public void lt4(){
		Proposition proposition = new PropositionImpl("lt(1)");
		LiveProposition.lt.exec(proposition);

	}
	
	@Test
	public void le(){
		Proposition proposition = new PropositionImpl("le(1,2)");
		Assert.assertTrue(LiveProposition.le.exec(proposition));
	}

	@Test
	public void le2(){
		Proposition proposition = new PropositionImpl("le(2,2)");
		Assert.assertTrue(LiveProposition.le.exec(proposition));
	}

	@Test
	public void le3(){
		Proposition proposition = new PropositionImpl("le(2,1)");
		Assert.assertFalse(LiveProposition.le.exec(proposition));
	}
	
	@Test(expected=RuntimeException.class)
	public void le4(){
		Proposition proposition = new PropositionImpl("le(1)");
		LiveProposition.le.exec(proposition);

	}
	
	@Test
	public void eq(){
		Proposition proposition = new PropositionImpl("eq(1,2)");
		Assert.assertFalse(LiveProposition.eq.exec(proposition));
	}

	@Test
	public void eq2(){
		Proposition proposition = new PropositionImpl("eq(2,2)");
		Assert.assertTrue(LiveProposition.eq.exec(proposition));
	}

	@Test
	public void eq3(){
		Proposition proposition = new PropositionImpl("eq(2,1)");
		Assert.assertFalse(LiveProposition.eq.exec(proposition));
	}
	
	@Test(expected=RuntimeException.class)
	public void eq4(){
		Proposition proposition = new PropositionImpl("eq(1)");
		LiveProposition.eq.exec(proposition);

	}
	
	@Test
	public void fromValue(){
		Assert.assertEquals(LiveProposition.gt, LiveProposition.valueOf("gt"));
		Assert.assertEquals(LiveProposition.lt, LiveProposition.valueOf("lt"));
		Assert.assertEquals(LiveProposition.ge, LiveProposition.valueOf("ge"));
		Assert.assertEquals(LiveProposition.le, LiveProposition.valueOf("le"));
		Assert.assertEquals(LiveProposition.eq, LiveProposition.valueOf("eq"));
	}
}
