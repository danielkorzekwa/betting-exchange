package dk.betex

import org.junit._
import Assert._
import java.util.Date

class BetexTest {

	private val betex = new Betex()

	/** 
	 *  Tests for createMarket.
	 * 
	 * */

	@Test def testCreateMarket {
		val newMarket = betex.createMarket(10,"Match Odds","Man Utd vs Arsenal",1,new Date(2000),List(new Market.Runner(11,"Man Utd"),new Market.Runner(12,"Arsenal")))
		assertEquals(1,betex.getMarkets.size)

		val marketFromBetex = betex.getMarkets()(0)

		assertEquals(newMarket,marketFromBetex)
		assertEquals(10,marketFromBetex.marketId)
		assertEquals("Match Odds",marketFromBetex.marketName)
		assertEquals("Man Utd vs Arsenal",marketFromBetex.eventName)
		assertEquals(1,marketFromBetex.numOfWinners)
		assertEquals(new Date(2000),marketFromBetex.marketTime)

		assertEquals(11,marketFromBetex.runners(0).runnerId)
		assertEquals("Man Utd",marketFromBetex.runners(0).runnerName)
		assertEquals(12,marketFromBetex.runners(1).runnerId)
		assertEquals("Arsenal",marketFromBetex.runners(1).runnerName)
	}

	@Test def testCreateTwoMarkets {
		betex.createMarket(10,"Match Odds","Man Utd vs Arsenal",1,new Date(2000),List(new Market.Runner(11,"Man Utd"),new Market.Runner(12,"Arsenal")))
		betex.createMarket(20,"Match Odds","Fulham vs Wigan",1,new Date(2000),List(new Market.Runner(31,"Fulham"),new Market.Runner(42,"Wigan")))
		assertEquals(2,betex.getMarkets.size)

		val marketFromBetex1 = betex.getMarkets()(0)
		assertEquals(20,marketFromBetex1.marketId)
		assertEquals("Match Odds",marketFromBetex1.marketName)
		assertEquals("Fulham vs Wigan",marketFromBetex1.eventName)
		assertEquals(1,marketFromBetex1.numOfWinners)
		assertEquals(new Date(2000),marketFromBetex1.marketTime)

		assertEquals(31,marketFromBetex1.runners(0).runnerId)
		assertEquals("Fulham",marketFromBetex1.runners(0).runnerName)
		assertEquals(42,marketFromBetex1.runners(1).runnerId)
		assertEquals("Wigan",marketFromBetex1.runners(1).runnerName)

		val marketFromBetex2 = betex.getMarkets()(1)
		assertEquals(10,marketFromBetex2.marketId)
		assertEquals("Match Odds",marketFromBetex2.marketName)
		assertEquals("Man Utd vs Arsenal",marketFromBetex2.eventName)
		assertEquals(1,marketFromBetex2.numOfWinners)
		assertEquals(new Date(2000),marketFromBetex2.marketTime)

		assertEquals(11,marketFromBetex2.runners(0).runnerId)
		assertEquals("Man Utd",marketFromBetex2.runners(0).runnerName)
		assertEquals(12,marketFromBetex2.runners(1).runnerId)
		assertEquals("Arsenal",marketFromBetex2.runners(1).runnerName)
	}

	@Test(expected=classOf[IllegalArgumentException]) 
	def testCreateMarketAlreadyExist {
		betex.createMarket(10,"Match Odds","Man Utd vs Arsenal",1,new Date(2000),List(new Market.Runner(11,"Man Utd"),new Market.Runner(12,"Arsenal")))
		betex.createMarket(10,"Match Odds","Man Utd vs Arsenal",1,new Date(2000),List(new Market.Runner(11,"Man Utd"),new Market.Runner(12,"Arsenal")))
	}

	/**Tests for findMarket*/
	@Test(expected=classOf[NoSuchElementException])
	def testFindMarketNotExist() {
		betex.findMarket(123)
	}

	@Test def testFindMarket() {

		betex.createMarket(10,"Match Odds","Man Utd vs Arsenal",1,new Date(2000),List(new Market.Runner(11,"Man Utd"),new Market.Runner(12,"Arsenal")))
		betex.createMarket(20,"Match Odds","Fulham vs Wigan",1,new Date(2000),List(new Market.Runner(31,"Fulham"),new Market.Runner(42,"Wigan")))

		assertEquals(10,betex.findMarket(10).marketId)
		assertEquals("Man Utd vs Arsenal",betex.findMarket(10).eventName)

		assertEquals(20,betex.findMarket(20).marketId)
		assertEquals("Fulham vs Wigan",betex.findMarket(20).eventName)

	}

	
	/**Tests for removeMarket*/
	@Test def removeMarket {
		betex.createMarket(10,"Match Odds","Man Utd vs Arsenal",1,new Date(2000),List(new Market.Runner(11,"Man Utd"),new Market.Runner(12,"Arsenal")))
		betex.createMarket(20,"Match Odds","Fulham vs Wigan",1,new Date(2000),List(new Market.Runner(31,"Fulham"),new Market.Runner(42,"Wigan")))

		assertEquals(2,betex.getMarkets.size)
		assertEquals(10,betex.removeMarket(10).get.marketId)
		assertEquals(1,betex.getMarkets.size)
		
		assertEquals(20,betex.removeMarket(20).get.marketId)
		assertEquals(0,betex.getMarkets.size)
	}
	
	@Test() 
	def removeNotExistingMarket {
		betex.createMarket(10,"Match Odds","Man Utd vs Arsenal",1,new Date(2000),List(new Market.Runner(11,"Man Utd"),new Market.Runner(12,"Arsenal")))
		betex.createMarket(20,"Match Odds","Fulham vs Wigan",1,new Date(2000),List(new Market.Runner(31,"Fulham"),new Market.Runner(42,"Wigan")))

		betex.removeMarket(11).isEmpty
	}
	/**
	 * Tests for clear 
	 * */
	
	@Test def clear {
		betex.createMarket(10,"Match Odds","Man Utd vs Arsenal",1,new Date(2000),List(new Market.Runner(11,"Man Utd"),new Market.Runner(12,"Arsenal")))
		betex.createMarket(20,"Match Odds","Fulham vs Wigan",1,new Date(2000),List(new Market.Runner(31,"Fulham"),new Market.Runner(42,"Wigan")))

		assertEquals(2,betex.getMarkets.size)
		betex.clear()
		assertEquals(0,betex.getMarkets.size)

	}
}
