package dk.betex

import org.junit._
import Assert._
import dk.betex.api.IBet.BetTypeEnum._
import java.util.Date

class BetUtilTest {

	/**Tests for avgPrice */
	@Test def testAvgPriceNoBets {
		val bets = Nil
		assertTrue(BetUtil.avgPrice(bets).isNaN)
	}

	@Test def testAvgPrice {
		val bets = Bet(100,123,2,2,BACK,1,11,100) ::Bet(101,123,3,3,BACK,1,12,100) :: Nil
		assertEquals(2.6,BetUtil.avgPrice(bets),0)
	}

	/**Tests for totalStake.*/
	@Test def testTotalStakeNoBets {
		val bets = Nil
		assertEquals(0,BetUtil.totalStake(bets),0)
	}

	@Test def testTotalStake {
		val bets = Bet(100,123,2,2,BACK,1,11,100) :: Bet(101,123,3,3,BACK,1,12,100) :: Nil
		assertEquals(5,BetUtil.totalStake(bets),0)
	}

	/** 
	 *  Tests for getRunnerPrices.
	 * 
	 * */

	@Test def testGetPricesNoBets {
		val bets = Nil
		assertEquals(0,BetUtil.mapToPrices(bets).size)
	}

	@Test  def testGetRunnerPrices {
		val bets = Bet(100,122,13,2.1,LAY,1,11,100) ::Bet(100,121,3,2.2,LAY,1,11,100) :: 
			Bet(102,122,5,2.2,LAY,1,11,100):: Bet(103,121,8,2.4,BACK,1,11,100) :: Bet(104,122,25,2.5,BACK,1,11,100) :: Nil

			val runnerPrices = BetUtil.mapToPrices(bets)
			assertEquals(4,runnerPrices.size)

			assertEquals(13, runnerPrices(2.1)._1,0)
			assertEquals(0, runnerPrices(2.1)._2,0)

			assertEquals(8, runnerPrices(2.2)._1,0)
			assertEquals(0, runnerPrices(2.2)._2,0)

			assertEquals(0, runnerPrices(2.4)._1,0)
			assertEquals(8, runnerPrices(2.4)._2,0)

			assertEquals(0, runnerPrices(2.5)._1,0)
			assertEquals(25, runnerPrices(2.5)._2,0)
	}

	@Test  def testGetRunnerLayAndBackBetsonTheSamePrice {
		val bets = Bet(100,122,5,2.4,BACK,1,11,100) ::Bet(101,122,8,2.4,LAY,1,11,100) :: Nil

		val runnerPrices = BetUtil.mapToPrices(bets)

		assertEquals(1,runnerPrices.size)
		assertEquals(8, runnerPrices(2.4)._1,0)
		assertEquals(5, runnerPrices(2.4)._2,0)
	}
}