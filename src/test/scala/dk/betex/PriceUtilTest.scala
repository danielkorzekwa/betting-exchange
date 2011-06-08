package dk.betex

import org.junit._
import Assert._
import PriceUtil.PriceRoundEnum._

class PriceUtilTest {

	@Test def testValidate {

		val validate = PriceUtil.validate(PriceUtil.getPriceRanges) _

		assertEquals(1.64,validate(1.6317991631799162, ROUND_UP), 0);
		assertEquals(1.63, validate(1.6317991631799162, ROUND_DOWN), 0);

		assertEquals(1.88, validate(1.88, ROUND_UP), 0);
		assertEquals(1.88, validate(1.88, ROUND_DOWN), 0);

		assertEquals(4.9,validate(4.8 + 0.01,ROUND_UP),0)
		assertEquals(1.01, validate(0.5, ROUND_UP), 0);
		assertEquals(1.01, validate(0.5, ROUND_DOWN), 0);

		assertEquals(1000, validate(2000, ROUND_UP), 0);
		assertEquals(1000, validate(2000, ROUND_DOWN), 0);

		assertEquals(1.01, validate(1.01, ROUND_UP), 0);
		assertEquals(1.01, validate(1.01, ROUND_DOWN), 0);

		assertEquals(1000, validate(1000, ROUND_UP), 0);
		assertEquals(1000, validate(1000, ROUND_DOWN), 0);

		assertEquals(2.0, validate(2.0, ROUND_UP), 0);
		assertEquals(2.0, validate(2.0, ROUND_DOWN), 0);

		assertEquals(2.02, validate(2.01, ROUND_UP), 0);
		assertEquals(2.0, validate(2.01, ROUND_DOWN), 0);

		assertEquals(44, validate(43, ROUND_UP), 0);
		assertEquals(44, validate(44, ROUND_UP), 0);
		assertEquals(46, validate(45, ROUND_UP), 0);

		assertEquals(860, validate(856, ROUND_UP), 0);

		assertEquals(1000, validate(999.99, ROUND_UP), 0);
		assertEquals(990, validate(999.99, ROUND_DOWN), 0);
	}

	@Test def testPriceUp {
		assertEquals(1.91,PriceUtil.priceUp(PriceUtil.getPriceRanges,1.9),0)
		assertEquals(5.1,PriceUtil.priceUp(PriceUtil.getPriceRanges,5.01),0)
		assertEquals(130,PriceUtil.priceUp(PriceUtil.getPriceRanges,120),0)
	}

	@Test def testPriceDown {
		assertEquals(1.89,PriceUtil.priceDown(PriceUtil.getPriceRanges,1.9),0)
		assertEquals(5.0,PriceUtil.priceDown(PriceUtil.getPriceRanges,5.01),0)
		assertEquals(110,PriceUtil.priceDown(PriceUtil.getPriceRanges,120),0)
	}
	
	@Test def testMovePriceUpNumOfSteps {
		assertEquals(2.96,PriceUtil.move(2.96,0),0)
		assertEquals(2.98,PriceUtil.move(2.96,1),0)
		assertEquals(3.00,PriceUtil.move(2.96,2),0)
		assertEquals(3.05,PriceUtil.move(2.96,3),0)
		assertEquals(3.0,PriceUtil.move(3.05,-1),0)
		assertEquals(2.98,PriceUtil.move(3.05,-2),0)
		assertEquals(2.96,PriceUtil.move(3.05,-3),0)
		
		assertEquals(2.28,PriceUtil.move(2.22,3),0)
		assertEquals(2.3,PriceUtil.move(2.22,4),0)
	}
	

	
	@Test def testAvgPrice {
		assertEquals(2, PriceUtil.avgPrice(1.5->3.0),0)
	}

}