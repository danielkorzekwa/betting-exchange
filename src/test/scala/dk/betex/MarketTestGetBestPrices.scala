package dk.betex

import org.junit._
import Assert._
import java.util.Date
import dk.betex.api._
import IBet.BetTypeEnum._
import IBet.BetStatusEnum._

class MarketTestGetBestPrices {
	
	/** 
	 *  Tests for getBestPrices for runner.
	 * 
	 * */
	@Test(expected=classOf[IllegalArgumentException]) def testGetBestPricesForNotExistingRunner  {
		val market = new Market(1,"Match Odds","Man Utd vs Arsenal",1,new Date(2000),List(new Market.Runner(11,"Man Utd"),new Market.Runner(12,"Arsenal")))

		market.getBestPrices(13)
	}

	@Test def testGetBestPricesBothToBackAndToLayAreAvailable {
		val market = new Market(1,"Match Odds","Man Utd vs Arsenal",1,new Date(2000),List(new Market.Runner(11,"Man Utd"),new Market.Runner(12,"Arsenal")))

		market.placeBet(100,122,13,2.1,LAY,11,1000)
		market.placeBet(101,121,3,2.2,LAY,11,1000)
		market.placeBet(102,122,5,2.2,LAY,11,1000)
		market.placeBet(103,121,9,2.4,BACK,11,1000)
		market.placeBet(104,122,25,2.5,BACK,11,1000)

		val bestPrices = market.getBestPrices(11)
		assertEquals(2.2,bestPrices._1.price,0)
		assertEquals(8.0,bestPrices._1.totalToBack,0)
		assertEquals(0,bestPrices._1.totalToLay,0)
		
		assertEquals(2.4,bestPrices._2.price,0)
		assertEquals(0,bestPrices._2.totalToBack,0)
		assertEquals(9,bestPrices._2.totalToLay,0)
	}

	@Test def testGetBestPricesBothToBackAndToLayAreAvailablePlusBetOnOtherRunner {
		val market = new Market(1,"Match Odds","Man Utd vs Arsenal",1,new Date(2000),List(new Market.Runner(11,"Man Utd"),new Market.Runner(12,"Arsenal")))

		market.placeBet(100,122,13,2.1,LAY,11,1000)
		market.placeBet(101,121,3,2.2,LAY,11,1000)
		market.placeBet(102,122,5,2.2,LAY,11,1000)
		market.placeBet(103,121,8,2.4,BACK,11,1000)
		market.placeBet(104,122,25,2.5,BACK,11,1000)

		market.placeBet(105,122,13,2.3,LAY,12,1001)
		market.placeBet(106,122,25,2.3,BACK,12,1001)

		val bestPrices = market.getBestPrices(11)
		assertEquals(2.2,bestPrices._1.price,0)
		assertEquals(8,bestPrices._1.totalToBack,0)
		assertEquals(0,bestPrices._1.totalToLay,0)
		
		assertEquals(2.4,bestPrices._2.price,0)
		assertEquals(0,bestPrices._2.totalToBack,0)
		assertEquals(8,bestPrices._2.totalToLay,0)
	}

	@Test def testGetBestPricesBothToBackAndToLayAreAvailablePlusSettledBets {
		val market = new Market(1,"Match Odds","Man Utd vs Arsenal",1,new Date(2000),List(new Market.Runner(11,"Man Utd"),new Market.Runner(12,"Arsenal")))

		market.placeBet(100,122,13,2.1,LAY,11,1000)
		market.placeBet(101,121,3,2.2,LAY,11,1000)
		market.placeBet(102,122,5,2.2,LAY,11,1000)
		market.placeBet(103,121,8,2.4,BACK,11,1000)
		market.placeBet(104,122,25,2.5,BACK,11,1000)

		/**Matching bets*/
		market.placeBet(105,121,8,2.4,LAY,11,1000)
		market.placeBet(106,122,8,2.2,BACK,11,1000)

		val bestPrices = market.getBestPrices(11)
		assertEquals(2.1,bestPrices._1.price,0)
		assertEquals(13,bestPrices._1.totalToBack,0)
		assertEquals(0,bestPrices._1.totalToLay,0)
		
		assertEquals(2.5,bestPrices._2.price,0)
		assertEquals(0,bestPrices._2.totalToBack,0)
		assertEquals(25,bestPrices._2.totalToLay,0)
	}

	@Test def testGetBestPricesToBackPriceIsNotAvailable {
		val market = new Market(1,"Match Odds","Man Utd vs Arsenal",1,new Date(2000),List(new Market.Runner(11,"Man Utd"),new Market.Runner(12,"Arsenal")))

		market.placeBet(103,121,8,2.4,BACK,11,1000)
		market.placeBet(104,122,25,2.5,BACK,11,1000)

		val bestPrices = market.getBestPrices(11)
		assertEquals(Double.NaN,bestPrices._1.price,0)
		assertEquals(0,bestPrices._1.totalToBack,0)
		assertEquals(0,bestPrices._1.totalToLay,0)
		
		assertEquals(2.4,bestPrices._2.price,0)
		assertEquals(0,bestPrices._2.totalToBack,0)
		assertEquals(8,bestPrices._2.totalToLay,0)
	}
	
	@Test def testGetBestPricesPlaceAndCancelBets {
		val market = new Market(1,"Match Odds","Man Utd vs Arsenal",1,new Date(2000),List(new Market.Runner(11,"Man Utd"),new Market.Runner(12,"Arsenal")))

		
		market.placeBet(101,122,25,2.7,BACK,11,1000)
		market.placeBet(102,122,8,2.6,LAY,11,1000)
		
		market.cancelBets(122,25,2.7,BACK,11)
		market.cancelBets(122,8,2.6,LAY,11)

		val bestPrices = market.getBestPrices(11)
		assertEquals(Double.NaN,bestPrices._1.price,0)
		assertEquals(0,bestPrices._1.totalToBack,0)
		assertEquals(0,bestPrices._1.totalToLay,0)
		
		assertEquals(Double.NaN,bestPrices._2.price,0)
		assertEquals(0,bestPrices._2.totalToBack,0)
		assertEquals(0,bestPrices._2.totalToLay,0)
	}
	
	@Test def testGetBestPricesPlaceAndCancelBet {
		val market = new Market(1,"Match Odds","Man Utd vs Arsenal",1,new Date(2000),List(new Market.Runner(11,"Man Utd"),new Market.Runner(12,"Arsenal")))

		
		market.placeBet(101,122,25,2.7,BACK,11,1000)
		market.placeBet(102,122,8,2.6,LAY,11,1000)
		
		market.cancelBet(101)
		market.cancelBet(102)

		val bestPrices = market.getBestPrices(11)
		assertEquals(Double.NaN,bestPrices._1.price,0)
		assertEquals(0,bestPrices._1.totalToBack,0)
		assertEquals(0,bestPrices._1.totalToLay,0)
		
		assertEquals(Double.NaN,bestPrices._2.price,0)
		assertEquals(0,bestPrices._2.totalToBack,0)
		assertEquals(0,bestPrices._2.totalToLay,0)
	}

	@Test def testGetBestPricesToLayPriceIsNotAvailable {
		val market = new Market(1,"Match Odds","Man Utd vs Arsenal",1,new Date(2000),List(new Market.Runner(11,"Man Utd"),new Market.Runner(12,"Arsenal")))

		market.placeBet(100,122,13,2.1,LAY,11,1000)
		market.placeBet(101,121,3,2.2,LAY,11,1000)
		market.placeBet(102,122,5,2.2,LAY,11,1000)

		val bestPrices = market.getBestPrices(11)
		assertEquals(2.2,bestPrices._1.price,0)
		assertEquals(8,bestPrices._1.totalToBack,0)
		assertEquals(0,bestPrices._1.totalToLay,0)
		
		assertEquals(Double.NaN,bestPrices._2.price,0)
		assertEquals(0,bestPrices._2.totalToBack,0)
		assertEquals(0,bestPrices._2.totalToLay,0)
	}

	@Test def testGetBestPricesNoBestPricesAvailable {
		val market = new Market(1,"Match Odds","Man Utd vs Arsenal",1,new Date(2000),List(new Market.Runner(11,"Man Utd"),new Market.Runner(12,"Arsenal")))

		val bestPrices = market.getBestPrices(11)
		assertEquals(Double.NaN,bestPrices._1.price,0)
		assertEquals(Double.NaN,bestPrices._2.price,0)
	}
	
	/** 
	 *  Tests for getBestPrices for market.
	 * 
	 * */
@Test def testGetBestPricesBothToBackAndToLayAreAvailableOnTwoRunners {
		val market = new Market(1,"Match Odds","Man Utd vs Arsenal",1,new Date(2000),List(new Market.Runner(11,"Man Utd"),new Market.Runner(12,"Arsenal")))

		market.placeBet(100,122,13,2.1,LAY,11,1000)
		market.placeBet(101,121,3,2.2,LAY,11,1000)
		market.placeBet(102,122,5,2.2,LAY,11,1000)
		market.placeBet(103,121,8,2.4,BACK,11,1000)
		market.placeBet(104,122,25,2.5,BACK,11,1000)

		market.placeBet(105,122,13,2.6,LAY,12,1001)
		market.placeBet(106,122,25,2.8,BACK,12,1001)

		val bestPrices = market.getBestPrices()
		assertEquals(2,bestPrices.size)
		assertEquals(2.2,bestPrices(11)._1.price,0)
		assertEquals(8,bestPrices(11)._1.totalToBack,0)
		assertEquals(0,bestPrices(11)._1.totalToLay,0)
		
		assertEquals(2.4,bestPrices(11)._2.price,0)
		assertEquals(0,bestPrices(11)._2.totalToBack,0)
		assertEquals(8,bestPrices(11)._2.totalToLay,0)
		
		assertEquals(2.6,bestPrices(12)._1.price,0)
		assertEquals(13,bestPrices(12)._1.totalToBack,0)
		assertEquals(0,bestPrices(12)._1.totalToLay,0)
		
		assertEquals(2.8,bestPrices(12)._2.price,0)
		assertEquals(0,bestPrices(12)._2.totalToBack,0)
		assertEquals(25,bestPrices(12)._2.totalToLay,0)
	}
}