package dk.betex

import org.junit._
import Assert._
import java.util.Date
import dk.betex.api._
import IBet.BetTypeEnum._
import IBet.BetStatusEnum._

class MarketPlaceBetTest {

	/** 
	 *  Tests for placeBet and matching.
	 * 
	 * */

	@Test(expected=classOf[IllegalArgumentException]) 
	def testPlaceBetBetSizeLessThanMin {
		val market = new Market(1,"Match Odds","Man Utd vs Arsenal",1,new Date(2000),List(new Market.Runner(11,"Man Utd"),new Market.Runner(12,"Arsenal")))	
		market.placeBet(100,123,0,1.01,BACK,11,1000)
	}


	@Test(expected=classOf[IllegalArgumentException]) 
	def testPlaceBetMarketRunnerNotFound {
		val market = new Market(1,"Match Odds","Man Utd vs Arsenal",1,new Date(2000),List(new Market.Runner(11,"Man Utd"),new Market.Runner(12,"Arsenal")))
		market.placeBet(100,123,2,1.01,BACK,13,1000)
	}

	@Test(expected=classOf[IllegalArgumentException])
	def testPlaceBetDuplicateBetId {
		val market = new Market(1,"Match Odds","Man Utd vs Arsenal",1,new Date(2000),List(new Market.Runner(11,"Man Utd"),new Market.Runner(12,"Arsenal")))
		market.placeBet(100,123,2,1.01,BACK,11,1000)
		market.placeBet(100,123,2,1.01,BACK,11,1001)
	}

	@Test def testPlaceBackBet {
		val market = new Market(1,"Match Odds","Man Utd vs Arsenal",1,new Date(2000),List(new Market.Runner(11,"Man Utd"),new Market.Runner(12,"Arsenal")))
		val placedBet = market.placeBet(100,123,2,1.5,BACK,11,1000)

		val bets = market.getBets(123)
		assertEquals(1, bets.size)
		assertEquals(100,bets(0).betId)
		assertEquals(123,bets(0).userId)
		assertEquals(2,bets(0).betSize,0)
		assertEquals(1.5,bets(0).betPrice,0)
		assertEquals(BACK,bets(0).betType)
		assertEquals(U,bets(0).betStatus)
		assertEquals(1,bets(0).marketId)
		assertEquals(11,bets(0).runnerId)
		assertEquals(1000,bets(0).placedDate)
		assertEquals(None,bets(0).matchedDate)
		
		assertEquals(100,placedBet.betId)
		assertEquals(123,placedBet.userId)
		assertEquals(2,placedBet.betSize,0)
		assertEquals(1.5,placedBet.betPrice,0)
		assertEquals(BACK,placedBet.betType)
		assertEquals(U,placedBet.betStatus)
		assertEquals(1,placedBet.marketId)
		assertEquals(11,placedBet.runnerId)
		assertEquals(1000,placedBet.placedDate)
		assertEquals(None,placedBet.matchedDate)
	}

	@Test def testPlaceLayBet {
		val market = new Market(1,"Match Odds","Man Utd vs Arsenal",1,new Date(2000),List(new Market.Runner(11,"Man Utd"),new Market.Runner(12,"Arsenal")))

		market.placeBet(100,123,2,1.5,LAY,11,1000)

		val bets = market.getBets(123)
		assertEquals(1, bets.size)
		assertEquals(100,bets(0).betId)
		assertEquals(123,bets(0).userId)
		assertEquals(2,bets(0).betSize,0)
		assertEquals(1.5,bets(0).betPrice,0)
		assertEquals(LAY,bets(0).betType)	
		assertEquals(U,bets(0).betStatus)
		assertEquals(1,bets(0).marketId)
		assertEquals(11,bets(0).runnerId)
		assertEquals(1000,bets(0).placedDate)
		assertEquals(None,bets(0).matchedDate)
	}

	@Test def testPlaceAFewBets {
		val market = new Market(1,"Match Odds","Man Utd vs Arsenal",1,new Date(2000),List(new Market.Runner(11,"Man Utd"),new Market.Runner(12,"Arsenal")))

		market.placeBet(100,122,13,2.1,LAY,11,1000)
		market.placeBet(101,121,3,2.2,LAY,11,1001)
		market.placeBet(102,122,5,2.2,LAY,11,1002)
		market.placeBet(103,121,8,2.4,BACK,11,1003)
		market.placeBet(104,122,25,2.5,BACK,11,1004)

		val bets = market.getBets(121)
		assertEquals(2, bets.size)

			assertEquals(103,bets(0).betId)
		assertEquals(121,bets(0).userId)
		assertEquals(8,bets(0).betSize,0)
		assertEquals(2.4,bets(0).betPrice,0)
		assertEquals(BACK,bets(0).betType)
		assertEquals(U,bets(0).betStatus)
		assertEquals(1,bets(0).marketId)
		assertEquals(11,bets(0).runnerId)
		
		assertEquals(101,bets(1).betId)
		assertEquals(121,bets(1).userId)
		assertEquals(3,bets(1).betSize,0)
		assertEquals(2.2,bets(1).betPrice,0)
		assertEquals(LAY,bets(1).betType)
		assertEquals(U,bets(1).betStatus)
		assertEquals(1,bets(1).marketId)
		assertEquals(11,bets(1).runnerId)

		val bets122 = market.getBets(122)
		assertEquals(3, bets122.size)

		assertEquals(104,bets122(0).betId)
		assertEquals(122,bets122(0).userId)
		assertEquals(25,bets122(0).betSize,0)
		assertEquals(2.5,bets122(0).betPrice,0)
		assertEquals(BACK,bets122(0).betType)
		assertEquals(U,bets122(0).betStatus)
		assertEquals(1,bets122(0).marketId)
		assertEquals(11,bets122(0).runnerId)
		
		assertEquals(100,bets122(1).betId)
		assertEquals(122,bets122(1).userId)
		assertEquals(13,bets122(1).betSize,0)
		assertEquals(2.1,bets122(1).betPrice,0)
		assertEquals(LAY,bets122(1).betType)
		assertEquals(U,bets122(1).betStatus)
		assertEquals(1,bets122(1).marketId)
		assertEquals(11,bets122(1).runnerId)

		assertEquals(102,bets122(2).betId)
		assertEquals(122,bets122(2).userId)
		assertEquals(5,bets122(2).betSize,0)
		assertEquals(2.2,bets122(2).betPrice,0)
		assertEquals(LAY,bets122(2).betType)
		assertEquals(U,bets122(2).betStatus)
		assertEquals(1,bets122(2).marketId)
		assertEquals(11,bets122(2).runnerId)

	}

	@Test def testPlaceLayThenPlaceBackOnDifferentRunner {
		val market = new Market(1,"Match Odds","Man Utd vs Arsenal",1,new Date(2000),List(new Market.Runner(11,"Man Utd"),new Market.Runner(12,"Arsenal")))

		market.placeBet(100,122,2,5,LAY,11,1000)
		market.placeBet(101,122,2,4,BACK,12,1001)

		val bets122 = market.getBets(122)
		assertEquals(2, bets122.size)

		assertEquals(101,bets122(0).betId)
		assertEquals(122,bets122(0).userId)
		assertEquals(2,bets122(0).betSize,0)
		assertEquals(4,bets122(0).betPrice,0)
		assertEquals(BACK,bets122(0).betType)
		assertEquals(U,bets122(0).betStatus)
		assertEquals(1,bets122(0).marketId)
		assertEquals(12,bets122(0).runnerId)
		
		assertEquals(100,bets122(1).betId)
		assertEquals(122,bets122(1).userId)
		assertEquals(2,bets122(1).betSize,0)
		assertEquals(5,bets122(1).betPrice,0)
		assertEquals(LAY,bets122(1).betType)
		assertEquals(U,bets122(1).betStatus)
		assertEquals(1,bets122(1).marketId)
		assertEquals(11,bets122(1).runnerId)

		
	}

	@Test def testPlaceBackThenPlaceLayOnDifferentRunner {
		val market = new Market(1,"Match Odds","Man Utd vs Arsenal",1,new Date(2000),List(new Market.Runner(11,"Man Utd"),new Market.Runner(12,"Arsenal")))

		market.placeBet(100,122,2,5,BACK,11,1000)
		market.placeBet(101,122,2,6,LAY,12,1001)

		val bets122 = market.getBets(122)
		assertEquals(2, bets122.size)

		assertEquals(100,bets122(0).betId)
		assertEquals(122,bets122(0).userId)
		assertEquals(2,bets122(0).betSize,0)
		assertEquals(5,bets122(0).betPrice,0)
		assertEquals(BACK,bets122(0).betType)
		assertEquals(U,bets122(0).betStatus)
		assertEquals(1,bets122(0).marketId)
		assertEquals(11,bets122(0).runnerId)

		assertEquals(101,bets122(1).betId)
		assertEquals(122,bets122(1).userId)
		assertEquals(2,bets122(1).betSize,0)
		assertEquals(6,bets122(1).betPrice,0)
		assertEquals(LAY,bets122(1).betType)
		assertEquals(U,bets122(1).betStatus)
		assertEquals(1,bets122(1).marketId)
		assertEquals(12,bets122(1).runnerId)
	}

	@Test def testMatchLayBetWithBackBet {
		val market = new Market(1,"Match Odds","Man Utd vs Arsenal",1,new Date(2000),List(new Market.Runner(11,"Man Utd"),new Market.Runner(12,"Arsenal")))

		market.placeBet(100,122,2,5,BACK,11,1000)
		market.placeBet(101,122,2,4,BACK,11,1001)
		val placedLayBet = market.placeBet(102,123,2,7,LAY,11,1002)

		/**Check bets for user 122.*/
		val bets122 = market.getBets(122)
		assertEquals(2, bets122.size)

		assertEquals(100,bets122(0).betId)
		assertEquals(122,bets122(0).userId)
		assertEquals(2,bets122(0).betSize,0)
		assertEquals(5,bets122(0).betPrice,0)
		assertEquals(BACK,bets122(0).betType)
		assertEquals(U,bets122(0).betStatus)
		assertEquals(1,bets122(0).marketId)
		assertEquals(11,bets122(0).runnerId)
		assertEquals(1000,bets122(0).placedDate)
		assertEquals(None,bets122(0).matchedDate)

		assertEquals(101,bets122(1).betId)
		assertEquals(122,bets122(1).userId)
		assertEquals(2,bets122(1).betSize,0)
		assertEquals(4,bets122(1).betPrice,0)
		assertEquals(BACK,bets122(1).betType)
		assertEquals(M,bets122(1).betStatus)
		assertEquals(1,bets122(1).marketId)
		assertEquals(11,bets122(1).runnerId)
		assertEquals(1001,bets122(1).placedDate)
		assertEquals(Option(1002),bets122(1).matchedDate)

		/**Check bets for user 123.*/
		val bets123 = market.getBets(123)
		assertEquals(1, bets123.size)

		assertEquals(102,bets123(0).betId)
		assertEquals(123,bets123(0).userId)
		assertEquals(2,bets123(0).betSize,0)
		assertEquals(4,bets123(0).betPrice,0)
		assertEquals(LAY,bets123(0).betType)
		assertEquals(M,bets123(0).betStatus)
		assertEquals(1,bets123(0).marketId)
		assertEquals(11,bets123(0).runnerId)
		assertEquals(1002,bets123(0).placedDate)
		assertEquals(Option(1002),bets123(0).matchedDate)
		
		assertEquals(102,placedLayBet.betId)
		assertEquals(123,placedLayBet.userId)
		assertEquals(2,placedLayBet.betSize,0)
		assertEquals(7,placedLayBet.betPrice,0)
		assertEquals(LAY,placedLayBet.betType)
		assertEquals(U,placedLayBet.betStatus)
		assertEquals(1,placedLayBet.marketId)
		assertEquals(11,placedLayBet.runnerId)
		assertEquals(1002,placedLayBet.placedDate)
		assertEquals(None,placedLayBet.matchedDate)
	}

	@Test def testMatchBackBetWithLayBet {
		val market = new Market(1,"Match Odds","Man Utd vs Arsenal",1,new Date(2000),List(new Market.Runner(11,"Man Utd"),new Market.Runner(12,"Arsenal")))

		market.placeBet(100,122,2,4,LAY,11,1000)
		market.placeBet(101,122,2,5,LAY,11,1000)
		market.placeBet(102,123,2,3,BACK,11,1000)

		/**Check bets for user 122.*/
		val bets122 = market.getBets(122)
		assertEquals(2, bets122.size)

		assertEquals(100,bets122(0).betId)
		assertEquals(122,bets122(0).userId)
		assertEquals(2,bets122(0).betSize,0)
		assertEquals(4,bets122(0).betPrice,0)
		assertEquals(LAY,bets122(0).betType)
		assertEquals(U,bets122(0).betStatus)
		assertEquals(1,bets122(0).marketId)
		assertEquals(11,bets122(0).runnerId)

		assertEquals(101,bets122(1).betId)
		assertEquals(122,bets122(1).userId)
		assertEquals(2,bets122(1).betSize,0)
		assertEquals(5,bets122(1).betPrice,0)
		assertEquals(LAY,bets122(1).betType)
		assertEquals(M,bets122(1).betStatus)
		assertEquals(1,bets122(1).marketId)
		assertEquals(11,bets122(1).runnerId)

		/**Check bets for user 123.*/
		val bets123 = market.getBets(123)
		assertEquals(1, bets123.size)

		assertEquals(102,bets123(0).betId)
		assertEquals(123,bets123(0).userId)
		assertEquals(2,bets123(0).betSize,0)
		assertEquals(5,bets123(0).betPrice,0)
		assertEquals(BACK,bets123(0).betType)
		assertEquals(M,bets123(0).betStatus)
		assertEquals(1,bets123(0).marketId)
		assertEquals(11,bets123(0).runnerId)
	}

	@Test def testMatchLayBetWithTwoBackBets {
		val market = new Market(1,"Match Odds","Man Utd vs Arsenal",1,new Date(2000),List(new Market.Runner(11,"Man Utd"),new Market.Runner(12,"Arsenal")))

		market.placeBet(100,122,2,5,BACK,11,1000)
		market.placeBet(101,122,2,4,BACK,11,1000)
		market.placeBet(102,123,4,7,LAY,11,1000)

		/**Check bets for user 122.*/
		val bets122 = market.getBets(122)
		assertEquals(2, bets122.size)

		assertEquals(101,bets122(0).betId)
		assertEquals(122,bets122(0).userId)
		assertEquals(2,bets122(0).betSize,0)
		assertEquals(4,bets122(0).betPrice,0)
		assertEquals(BACK,bets122(0).betType)
		assertEquals(M,bets122(0).betStatus)
		assertEquals(1,bets122(0).marketId)
		assertEquals(11,bets122(0).runnerId)

		assertEquals(100,bets122(1).betId)
		assertEquals(122,bets122(1).userId)
		assertEquals(2,bets122(1).betSize,0)
		assertEquals(5,bets122(1).betPrice,0)
		assertEquals(BACK,bets122(1).betType)
		assertEquals(M,bets122(1).betStatus)
		assertEquals(1,bets122(1).marketId)
		assertEquals(11,bets122(1).runnerId)

		/**Check bets for user 123.*/
		val bets123 = market.getBets(123)
		assertEquals(2, bets123.size)

		assertEquals(102,bets123(0).betId)
		assertEquals(123,bets123(0).userId)
		assertEquals(2,bets123(0).betSize,0)
		assertEquals(4,bets123(0).betPrice,0)
		assertEquals(LAY,bets123(0).betType)
		assertEquals(M,bets123(0).betStatus)
		assertEquals(1,bets123(0).marketId)
		assertEquals(11,bets123(0).runnerId)

		assertEquals(102,bets123(1).betId)
		assertEquals(123,bets123(1).userId)
		assertEquals(2,bets123(1).betSize,0)
		assertEquals(5,bets123(1).betPrice,0)
		assertEquals(LAY,bets123(1).betType)
		assertEquals(M,bets123(1).betStatus)
		assertEquals(1,bets123(1).marketId)
		assertEquals(11,bets123(1).runnerId)
	}

	@Test def testMatchBackBetWithTwoLaysBets {
		val market = new Market(1,"Match Odds","Man Utd vs Arsenal",1,new Date(2000),List(new Market.Runner(11,"Man Utd"),new Market.Runner(12,"Arsenal")))

		market.placeBet(100,122,2,4,LAY,11,1000)
		market.placeBet(101,122,2,5,LAY,11,1000)
		market.placeBet(102,123,4,3,BACK,11,1000)

		/**Check bets for user 122.*/
		val bets122 = market.getBets(122)
		assertEquals(2, bets122.size)

		assertEquals(101,bets122(0).betId)
		assertEquals(122,bets122(0).userId)
		assertEquals(2,bets122(0).betSize,0)
		assertEquals(5,bets122(0).betPrice,0)
		assertEquals(LAY,bets122(0).betType)
		assertEquals(M,bets122(0).betStatus)
		assertEquals(1,bets122(0).marketId)
		assertEquals(11,bets122(0).runnerId)

		assertEquals(100,bets122(1).betId)
		assertEquals(122,bets122(1).userId)
		assertEquals(2,bets122(1).betSize,0)
		assertEquals(4,bets122(1).betPrice,0)
		assertEquals(LAY,bets122(1).betType)
		assertEquals(M,bets122(1).betStatus)
		assertEquals(1,bets122(1).marketId)
		assertEquals(11,bets122(1).runnerId)

		/**Check bets for user 123.*/
		val bets123 = market.getBets(123)
		assertEquals(2, bets123.size)

		assertEquals(102,bets123(0).betId)
		assertEquals(123,bets123(0).userId)
		assertEquals(2,bets123(0).betSize,0)
		assertEquals(5,bets123(0).betPrice,0)
		assertEquals(BACK,bets123(0).betType)
		assertEquals(M,bets123(0).betStatus)
		assertEquals(1,bets123(0).marketId)
		assertEquals(11,bets123(0).runnerId)

		assertEquals(102,bets123(1).betId)
		assertEquals(123,bets123(1).userId)
		assertEquals(2,bets123(1).betSize,0)
		assertEquals(4,bets123(1).betPrice,0)
		assertEquals(BACK,bets123(1).betType)
		assertEquals(M,bets123(1).betStatus)
		assertEquals(1,bets123(1).marketId)
		assertEquals(11,bets123(1).runnerId)
	}

	@Test def testMatchBackBetPartiallyMatchedWithLayBet {
		val market = new Market(1,"Match Odds","Man Utd vs Arsenal",1,new Date(2000),List(new Market.Runner(11,"Man Utd"),new Market.Runner(12,"Arsenal")))

		market.placeBet(100,122,2,5,LAY,11,1000)
		market.placeBet(101,123,6,3,BACK,11,1000)

		/**Check bets for user 122.*/
		val bets122 = market.getBets(122)
		assertEquals(1, bets122.size)

		assertEquals(100,bets122(0).betId)
		assertEquals(122,bets122(0).userId)
		assertEquals(2,bets122(0).betSize,0)
		assertEquals(5,bets122(0).betPrice,0)
		assertEquals(LAY,bets122(0).betType)
		assertEquals(M,bets122(0).betStatus)
		assertEquals(1,bets122(0).marketId)
		assertEquals(11,bets122(0).runnerId)

		/**Check bets for user 123.*/
		val bets123 = market.getBets(123)
		assertEquals(2, bets123.size)

		assertEquals(101,bets123(0).betId)
		assertEquals(123,bets123(0).userId)
		assertEquals(4,bets123(0).betSize,0)
		assertEquals(3,bets123(0).betPrice,0)
		assertEquals(BACK,bets123(0).betType)
		assertEquals(U,bets123(0).betStatus)
		assertEquals(1,bets123(0).marketId)
		assertEquals(11,bets123(0).runnerId)

		assertEquals(101,bets123(1).betId)
		assertEquals(123,bets123(1).userId)
		assertEquals(2,bets123(1).betSize,0)
		assertEquals(5,bets123(1).betPrice,0)
		assertEquals(BACK,bets123(1).betType)
		assertEquals(M,bets123(1).betStatus)
		assertEquals(1,bets123(1).marketId)
		assertEquals(11,bets123(1).runnerId)
	}
	@Test def testMatchBackBetPartiallyMatchedWithTwoLayBets {
		val market = new Market(1,"Match Odds","Man Utd vs Arsenal",1,new Date(2000),List(new Market.Runner(11,"Man Utd"),new Market.Runner(12,"Arsenal")))

		market.placeBet(100,122,7,2,LAY,11,1000)
		market.placeBet(101,122,2,4,LAY,11,1000)
		market.placeBet(102,122,3,5,LAY,11,1000)
		market.placeBet(103,123,4,3,BACK,11,1000)

		/**Check bets for user 122.*/
		val bets122 = market.getBets(122)
		assertEquals(4, bets122.size)

		assertEquals(100,bets122(0).betId)
		assertEquals(122,bets122(0).userId)
		assertEquals(7,bets122(0).betSize,0)
		assertEquals(2,bets122(0).betPrice,0)
		assertEquals(LAY,bets122(0).betType)
		assertEquals(U,bets122(0).betStatus)
		assertEquals(1,bets122(0).marketId)
		assertEquals(11,bets122(0).runnerId)

		assertEquals(101,bets122(1).betId)
		assertEquals(122,bets122(1).userId)
		assertEquals(1,bets122(1).betSize,0)
		assertEquals(4,bets122(1).betPrice,0)
		assertEquals(LAY,bets122(1).betType)
		assertEquals(U,bets122(1).betStatus)
		assertEquals(1,bets122(1).marketId)
		assertEquals(11,bets122(1).runnerId)

		assertEquals(102,bets122(2).betId)
		assertEquals(122,bets122(2).userId)
		assertEquals(3,bets122(2).betSize,0)
		assertEquals(5,bets122(2).betPrice,0)
		assertEquals(LAY,bets122(2).betType)
		assertEquals(M,bets122(2).betStatus)
		assertEquals(1,bets122(2).marketId)
		assertEquals(11,bets122(2).runnerId)

		assertEquals(101,bets122(3).betId)
		assertEquals(122,bets122(3).userId)
		assertEquals(1,bets122(3).betSize,0)
		assertEquals(4,bets122(3).betPrice,0)
		assertEquals(LAY,bets122(3).betType)
		assertEquals(M,bets122(3).betStatus)
		assertEquals(1,bets122(3).marketId)
		assertEquals(11,bets122(3).runnerId)



		/**Check bets for user 123.*/
		val bets123 = market.getBets(123)
		assertEquals(2, bets123.size)

		assertEquals(103,bets123(0).betId)
		assertEquals(123,bets123(0).userId)
		assertEquals(3,bets123(0).betSize,0)
		assertEquals(5,bets123(0).betPrice,0)
		assertEquals(BACK,bets123(0).betType)
		assertEquals(M,bets123(0).betStatus)
		assertEquals(1,bets123(0).marketId)
		assertEquals(11,bets123(0).runnerId)

		assertEquals(103,bets123(1).betId)
		assertEquals(123,bets123(1).userId)
		assertEquals(1,bets123(1).betSize,0)
		assertEquals(4,bets123(1).betPrice,0)
		assertEquals(BACK,bets123(1).betType)
		assertEquals(M,bets123(1).betStatus)
		assertEquals(1,bets123(1).marketId)
		assertEquals(11,bets123(1).runnerId)

	}
	@Test def testMatchBackBetPartiallyMatchedWithTwoLayBetsNoBetsRemainsAtMatchingPrice {
		val market = new Market(1,"Match Odds","Man Utd vs Arsenal",1,new Date(2000),List(new Market.Runner(11,"Man Utd"),new Market.Runner(12,"Arsenal")))

		market.placeBet(100,122,7,2,LAY,11,1000)
		market.placeBet(101,122,2,4,LAY,11,1000)
		market.placeBet(102,122,3,5,LAY,11,1000)
		market.placeBet(103,123,6,3,BACK,11,1000)

		/**Check bets for user 122.*/
		val bets122 = market.getBets(122)
		assertEquals(3, bets122.size)

		assertEquals(100,bets122(0).betId)
		assertEquals(122,bets122(0).userId)
		assertEquals(7,bets122(0).betSize,0)
		assertEquals(2,bets122(0).betPrice,0)
		assertEquals(LAY,bets122(0).betType)
		assertEquals(U,bets122(0).betStatus)
		assertEquals(1,bets122(0).marketId)
		assertEquals(11,bets122(0).runnerId)

		assertEquals(102,bets122(1).betId)
		assertEquals(122,bets122(1).userId)
		assertEquals(3,bets122(1).betSize,0)
		assertEquals(5,bets122(1).betPrice,0)
		assertEquals(LAY,bets122(1).betType)
		assertEquals(M,bets122(1).betStatus)
		assertEquals(1,bets122(1).marketId)
		assertEquals(11,bets122(1).runnerId)

		assertEquals(101,bets122(2).betId)
		assertEquals(122,bets122(2).userId)
		assertEquals(2,bets122(2).betSize,0)
		assertEquals(4,bets122(2).betPrice,0)
		assertEquals(LAY,bets122(2).betType)
		assertEquals(M,bets122(2).betStatus)
		assertEquals(1,bets122(2).marketId)
		assertEquals(11,bets122(2).runnerId)

		/**Check bets for user 123.*/
		val bets123 = market.getBets(123)
		assertEquals(3, bets123.size)

		assertEquals(103,bets123(0).betId)
		assertEquals(123,bets123(0).userId)
		assertEquals(1,bets123(0).betSize,0)
		assertEquals(3,bets123(0).betPrice,0)
		assertEquals(BACK,bets123(0).betType)
		assertEquals(U,bets123(0).betStatus)
		assertEquals(1,bets123(0).marketId)
		assertEquals(11,bets123(0).runnerId)

		assertEquals(103,bets123(1).betId)
		assertEquals(123,bets123(1).userId)
		assertEquals(3,bets123(1).betSize,0)
		assertEquals(5,bets123(1).betPrice,0)
		assertEquals(BACK,bets123(1).betType)
		assertEquals(M,bets123(1).betStatus)
		assertEquals(1,bets123(1).marketId)
		assertEquals(11,bets123(1).runnerId)

		assertEquals(103,bets123(2).betId)
		assertEquals(123,bets123(2).userId)
		assertEquals(2,bets123(2).betSize,0)
		assertEquals(4,bets123(2).betPrice,0)
		assertEquals(BACK,bets123(2).betType)
		assertEquals(M,bets123(2).betStatus)
		assertEquals(1,bets123(2).marketId)
		assertEquals(11,bets123(2).runnerId)


	}

	@Test def testMatchBackBetFullyMatchedWithBiggerLayBet {
		val market = new Market(1,"Match Odds","Man Utd vs Arsenal",1,new Date(2000),List(new Market.Runner(11,"Man Utd"),new Market.Runner(12,"Arsenal")))

		market.placeBet(100,122,6,5,LAY,11,1000)
		market.placeBet(101,123,2,3,BACK,11,1000)

		/**Check bets for user 122.*/
		val bets122 = market.getBets(122)
		assertEquals(2, bets122.size)

		assertEquals(100,bets122(0).betId)
		assertEquals(122,bets122(0).userId)
		assertEquals(4,bets122(0).betSize,0)
		assertEquals(5,bets122(0).betPrice,0)
		assertEquals(LAY,bets122(0).betType)
		assertEquals(U,bets122(0).betStatus)
		assertEquals(1,bets122(0).marketId)
		assertEquals(11,bets122(0).runnerId)

		assertEquals(100,bets122(1).betId)
		assertEquals(122,bets122(1).userId)
		assertEquals(2,bets122(1).betSize,0)
		assertEquals(5,bets122(1).betPrice,0)
		assertEquals(LAY,bets122(1).betType)
		assertEquals(M,bets122(1).betStatus)
		assertEquals(1,bets122(1).marketId)
		assertEquals(11,bets122(1).runnerId)

		/**Check bets for user 123.*/
		val bets123 = market.getBets(123)
		assertEquals(1, bets123.size)

		assertEquals(101,bets123(0).betId)
		assertEquals(123,bets123(0).userId)
		assertEquals(2,bets123(0).betSize,0)
		assertEquals(5,bets123(0).betPrice,0)
		assertEquals(BACK,bets123(0).betType)
		assertEquals(M,bets123(0).betStatus)
		assertEquals(1,bets123(0).marketId)
		assertEquals(11,bets123(0).runnerId)

	}

	@Test def testMatchLayBetPartiallyMatchedWithBackBet {
		val market = new Market(1,"Match Odds","Man Utd vs Arsenal",1,new Date(2000),List(new Market.Runner(11,"Man Utd"),new Market.Runner(12,"Arsenal")))

		market.placeBet(100,122,2,8,BACK,11,1000)
		market.placeBet(101,123,6,11,LAY,11,1000)

		/**Check bets for user 122.*/
		val bets122 = market.getBets(122)
		assertEquals(1, bets122.size)

		assertEquals(100,bets122(0).betId)
		assertEquals(122,bets122(0).userId)
		assertEquals(2,bets122(0).betSize,0)
		assertEquals(8,bets122(0).betPrice,0)
		assertEquals(BACK,bets122(0).betType)
		assertEquals(M,bets122(0).betStatus)
		assertEquals(1,bets122(0).marketId)
		assertEquals(11,bets122(0).runnerId)

		/**Check bets for user 123.*/
		val bets123 = market.getBets(123)
		assertEquals(2, bets123.size)

		assertEquals(101,bets123(0).betId)
		assertEquals(123,bets123(0).userId)
		assertEquals(4,bets123(0).betSize,0)
		assertEquals(11,bets123(0).betPrice,0)
		assertEquals(LAY,bets123(0).betType)
		assertEquals(U,bets123(0).betStatus)
		assertEquals(1,bets123(0).marketId)
		assertEquals(11,bets123(0).runnerId)

		assertEquals(101,bets123(1).betId)
		assertEquals(123,bets123(1).userId)
		assertEquals(2,bets123(1).betSize,0)
		assertEquals(8,bets123(1).betPrice,0)
		assertEquals(LAY,bets123(1).betType)
		assertEquals(M,bets123(1).betStatus)
		assertEquals(1,bets123(1).marketId)
		assertEquals(11,bets123(1).runnerId)

	}

	@Test def testMatchLayBetPartiallyMatchedWithTwoBackBets {
		val market = new Market(1,"Match Odds","Man Utd vs Arsenal",1,new Date(2000),List(new Market.Runner(11,"Man Utd"),new Market.Runner(12,"Arsenal")))

		market.placeBet(100,122,7,9,BACK,11,1000)
		market.placeBet(101,122,2,6,BACK,11,1000)
		market.placeBet(102,122,3,5,BACK,11,1000)
		market.placeBet(103,123,4,7,LAY,11,1000)

		/**Check bets for user 122.*/
		val bets122 = market.getBets(122)
		assertEquals(4, bets122.size)

		assertEquals(100,bets122(0).betId)
		assertEquals(122,bets122(0).userId)
		assertEquals(7,bets122(0).betSize,0)
		assertEquals(9,bets122(0).betPrice,0)
		assertEquals(BACK,bets122(0).betType)
		assertEquals(U,bets122(0).betStatus)
		assertEquals(1,bets122(0).marketId)
		assertEquals(11,bets122(0).runnerId)

		assertEquals(101,bets122(1).betId)
		assertEquals(122,bets122(1).userId)
		assertEquals(1,bets122(1).betSize,0)
		assertEquals(6,bets122(1).betPrice,0)
		assertEquals(BACK,bets122(1).betType)
		assertEquals(U,bets122(1).betStatus)
		assertEquals(1,bets122(1).marketId)
		assertEquals(11,bets122(1).runnerId)

		assertEquals(102,bets122(2).betId)
		assertEquals(122,bets122(2).userId)
		assertEquals(3,bets122(2).betSize,0)
		assertEquals(5,bets122(2).betPrice,0)
		assertEquals(BACK,bets122(2).betType)
		assertEquals(M,bets122(2).betStatus)
		assertEquals(1,bets122(2).marketId)
		assertEquals(11,bets122(2).runnerId)

		assertEquals(101,bets122(3).betId)
		assertEquals(122,bets122(3).userId)
		assertEquals(1,bets122(3).betSize,0)
		assertEquals(6,bets122(3).betPrice,0)
		assertEquals(BACK,bets122(3).betType)
		assertEquals(M,bets122(3).betStatus)
		assertEquals(1,bets122(3).marketId)
		assertEquals(11,bets122(3).runnerId)

		/**Check bets for user 123.*/
		val bets123 = market.getBets(123)
		assertEquals(2, bets123.size)

		assertEquals(103,bets123(0).betId)
		assertEquals(123,bets123(0).userId)
		assertEquals(3,bets123(0).betSize,0)
		assertEquals(5,bets123(0).betPrice,0)
		assertEquals(LAY,bets123(0).betType)
		assertEquals(M,bets123(0).betStatus)
		assertEquals(1,bets123(0).marketId)
		assertEquals(11,bets123(0).runnerId)

		assertEquals(103,bets123(1).betId)
		assertEquals(123,bets123(1).userId)
		assertEquals(1,bets123(1).betSize,0)
		assertEquals(6,bets123(1).betPrice,0)
		assertEquals(LAY,bets123(1).betType)
		assertEquals(M,bets123(1).betStatus)
		assertEquals(1,bets123(1).marketId)
		assertEquals(11,bets123(1).runnerId)
	}

	@Test def testMatchLayBetPartiallyMatchedWithTwoBackBetsNoBetsRemainsAtMatchingPrice {
		val market = new Market(1,"Match Odds","Man Utd vs Arsenal",1,new Date(2000),List(new Market.Runner(11,"Man Utd"),new Market.Runner(12,"Arsenal")))

		market.placeBet(100,122,7,9,BACK,11,1000)
		market.placeBet(101,122,2,6,BACK,11,1000)
		market.placeBet(102,122,3,5,BACK,11,1000)
		market.placeBet(103,123,6,7,LAY,11,1000)

		/**Check bets for user 122.*/
		val bets122 = market.getBets(122)
		assertEquals(3, bets122.size)

		assertEquals(100,bets122(0).betId)
		assertEquals(122,bets122(0).userId)
		assertEquals(7,bets122(0).betSize,0)
		assertEquals(9,bets122(0).betPrice,0)
		assertEquals(BACK,bets122(0).betType)
		assertEquals(U,bets122(0).betStatus)
		assertEquals(1,bets122(0).marketId)
		assertEquals(11,bets122(0).runnerId)

		assertEquals(102,bets122(1).betId)
		assertEquals(122,bets122(1).userId)
		assertEquals(3,bets122(1).betSize,0)
		assertEquals(5,bets122(1).betPrice,0)
		assertEquals(BACK,bets122(1).betType)
		assertEquals(M,bets122(1).betStatus)
		assertEquals(1,bets122(1).marketId)
		assertEquals(11,bets122(1).runnerId)

		assertEquals(101,bets122(2).betId)
		assertEquals(122,bets122(2).userId)
		assertEquals(2,bets122(2).betSize,0)
		assertEquals(6,bets122(2).betPrice,0)
		assertEquals(BACK,bets122(2).betType)
		assertEquals(M,bets122(2).betStatus)
		assertEquals(1,bets122(2).marketId)
		assertEquals(11,bets122(2).runnerId)

		/**Check bets for user 123.*/
		val bets123 = market.getBets(123)
		assertEquals(3, bets123.size)

		assertEquals(103,bets123(0).betId)
		assertEquals(123,bets123(0).userId)
		assertEquals(1,bets123(0).betSize,0)
		assertEquals(7,bets123(0).betPrice,0)
		assertEquals(LAY,bets123(0).betType)
		assertEquals(U,bets123(0).betStatus)
		assertEquals(1,bets123(0).marketId)
		assertEquals(11,bets123(0).runnerId)

		assertEquals(103,bets123(1).betId)
		assertEquals(123,bets123(1).userId)
		assertEquals(3,bets123(1).betSize,0)
		assertEquals(5,bets123(1).betPrice,0)
		assertEquals(LAY,bets123(1).betType)
		assertEquals(M,bets123(1).betStatus)
		assertEquals(1,bets123(1).marketId)
		assertEquals(11,bets123(1).runnerId)

		assertEquals(103,bets123(2).betId)
		assertEquals(123,bets123(2).userId)
		assertEquals(2,bets123(2).betSize,0)
		assertEquals(6,bets123(2).betPrice,0)
		assertEquals(LAY,bets123(2).betType)
		assertEquals(M,bets123(2).betStatus)
		assertEquals(1,bets123(2).marketId)
		assertEquals(11,bets123(2).runnerId)

	}

	@Test def testMatchLayBetFullyMatchedWithBiggerBackBet {
		val market = new Market(1,"Match Odds","Man Utd vs Arsenal",1,new Date(2000),List(new Market.Runner(11,"Man Utd"),new Market.Runner(12,"Arsenal")))

		market.placeBet(100,122,6,8,BACK,11,1000)
		market.placeBet(101,123,2,11,LAY,11,1000)

		/**Check bets for user 122.*/
		val bets122 = market.getBets(122)
		assertEquals(2, bets122.size)

		assertEquals(100,bets122(0).betId)
		assertEquals(122,bets122(0).userId)
		assertEquals(4,bets122(0).betSize,0)
		assertEquals(8,bets122(0).betPrice,0)
		assertEquals(BACK,bets122(0).betType)
		assertEquals(U,bets122(0).betStatus)
		assertEquals(1,bets122(0).marketId)
		assertEquals(11,bets122(0).runnerId)

		assertEquals(100,bets122(1).betId)
		assertEquals(122,bets122(1).userId)
		assertEquals(2,bets122(1).betSize,0)
		assertEquals(8,bets122(1).betPrice,0)
		assertEquals(BACK,bets122(1).betType)
		assertEquals(M,bets122(1).betStatus)
		assertEquals(1,bets122(1).marketId)
		assertEquals(11,bets122(1).runnerId)

		/**Check bets for user 123.*/
		val bets123 = market.getBets(123)
		assertEquals(1, bets123.size)

		assertEquals(101,bets123(0).betId)
		assertEquals(123,bets123(0).userId)
		assertEquals(2,bets123(0).betSize,0)
		assertEquals(8,bets123(0).betPrice,0)
		assertEquals(LAY,bets123(0).betType)
		assertEquals(M,bets123(0).betStatus)
		assertEquals(1,bets123(0).marketId)
		assertEquals(11,bets123(0).runnerId)

	}

	/**Test for match ordering, many bets on the same price.*/
	@Test def testMatchBackBetsWithLayBetTwoLayBetsOnTheSamePrice {
		val market = new Market(1,"Match Odds","Man Utd vs Arsenal",1,new Date(2000),List(new Market.Runner(11,"Man Utd"),new Market.Runner(12,"Arsenal")))

		market.placeBet(101,122,2,1.9,LAY,11,1000)
		market.placeBet(102,122,5,1.9,LAY,11,1000)
		market.placeBet(103,123,1,1.9,BACK,11,1000)
		market.placeBet(104,123,1,1.9,BACK,11,1000)

		/**Check bets for user 122.*/
		val bets122 = market.getBets(122)
		assertEquals(3, bets122.size)

		assertEquals(102,bets122(0).betId)
		assertEquals(122,bets122(0).userId)
		assertEquals(5,bets122(0).betSize,0)
		assertEquals(1.9,bets122(0).betPrice,0)
		assertEquals(LAY,bets122(0).betType)
		assertEquals(U,bets122(0).betStatus)
		assertEquals(1,bets122(0).marketId)
		assertEquals(11,bets122(0).runnerId)

		assertEquals(101,bets122(1).betId)
		assertEquals(122,bets122(1).userId)
		assertEquals(1,bets122(1).betSize,0)
		assertEquals(1.9,bets122(1).betPrice,0)
		assertEquals(LAY,bets122(1).betType)
		assertEquals(M,bets122(1).betStatus)
		assertEquals(1,bets122(1).marketId)
		assertEquals(11,bets122(1).runnerId)

		assertEquals(101,bets122(2).betId)
		assertEquals(122,bets122(2).userId)
		assertEquals(1,bets122(2).betSize,0)
		assertEquals(1.9,bets122(2).betPrice,0)
		assertEquals(LAY,bets122(2).betType)
		assertEquals(M,bets122(2).betStatus)
		assertEquals(1,bets122(2).marketId)
		assertEquals(11,bets122(2).runnerId)

		/**Check bets for user 123.*/
		val bets123 = market.getBets(123)
		assertEquals(2, bets123.size)

		assertEquals(103,bets123(0).betId)
		assertEquals(123,bets123(0).userId)
		assertEquals(1,bets123(0).betSize,0)
		assertEquals(1.9,bets123(0).betPrice,0)
		assertEquals(BACK,bets123(0).betType)
		assertEquals(M,bets123(0).betStatus)
		assertEquals(1,bets123(0).marketId)
		assertEquals(11,bets123(0).runnerId)
		
		assertEquals(104,bets123(1).betId)
		assertEquals(123,bets123(1).userId)
		assertEquals(1,bets123(1).betSize,0)
		assertEquals(1.9,bets123(1).betPrice,0)
		assertEquals(BACK,bets123(1).betType)
		assertEquals(M,bets123(1).betStatus)
		assertEquals(1,bets123(1).marketId)
		assertEquals(11,bets123(1).runnerId)

	}

	/**Test scenarios for rounding issues.*/
	@Test def testMatchRoundingLayBetWithBackBet {
		val market = new Market(1,"Match Odds","Man Utd vs Arsenal",1,new Date(2000),List(new Market.Runner(11,"Man Utd"),new Market.Runner(12,"Arsenal")))

		market.placeBet(100,122,2.26,5,BACK,11,1000)
		market.placeBet(101,123,2.26,6,LAY,11,1000)

		/**Check bets for user 122.*/
		val bets122 = market.getBets(122)
		assertEquals(1, bets122.size)


		assertEquals(100,bets122(0).betId)
		assertEquals(122,bets122(0).userId)
		assertEquals(2.26,bets122(0).betSize,0)
		assertEquals(5.0,bets122(0).betPrice,0)
		assertEquals(BACK,bets122(0).betType)
		assertEquals(M,bets122(0).betStatus)
		assertEquals(1,bets122(0).marketId)
		assertEquals(11,bets122(0).runnerId)

		/**Check bets for user 123.*/
		val bets123 = market.getBets(123)
		assertEquals(1, bets123.size)

		assertEquals(101,bets123(0).betId)
		assertEquals(123,bets123(0).userId)
		assertEquals(2.26,bets123(0).betSize,0)
		assertEquals(5.0,bets123(0).betPrice,0)
		assertEquals(LAY,bets123(0).betType)
		assertEquals(M,bets123(0).betStatus)
		assertEquals(1,bets123(0).marketId)
		assertEquals(11,bets123(0).runnerId)
	}

}