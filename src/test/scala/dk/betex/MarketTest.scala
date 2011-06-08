package dk.betex

import org.junit._
import Assert._
import java.util.Date
import dk.betex.api._
import IBet.BetTypeEnum._
import IBet.BetStatusEnum._

class MarketTest {

  /**
   * Tests for market creation. 
   */

  @Test
  def testCreateMarket {
    new Market(10, "Match Odds", "Man Utd vs Arsenal", 2, new Date(2000), List(new Market.Runner(11, "Man Utd"), new Market.Runner(12, "Arsenal")))
  }

  @Test(expected = classOf[IllegalArgumentException])
  def testCreateMarketWrongNumOfWinners {
    new Market(10, "Match Odds", "Man Utd vs Arsenal", 0, new Date(2000), List(new Market.Runner(11, "Man Utd"), new Market.Runner(12, "Arsenal")))
  }

  @Test(expected = classOf[IllegalArgumentException])
  def testCreateMarketWrongNumOfRunners {
    new Market(10, "Match Odds", "Man Utd vs Arsenal", 1, new Date(2000), List(new Market.Runner(11, "Man Utd")))
  }

  /**
   *Tests for cancel bet.
   */
  @Test(expected = classOf[NoSuchElementException])
  def testCancelBetForNotExistingBetId {
    val market = new Market(1, "Match Odds", "Man Utd vs Arsenal", 1, new Date(2000), List(new Market.Runner(11, "Man Utd"), new Market.Runner(12, "Arsenal")))
    market.placeBet(100, 123, 2, 3, BACK, 11,1000)

    market.cancelBet(101)
  }

  @Test(expected = classOf[NoSuchElementException])
  def testCancelFullyMatchedBet {
    val market = new Market(1, "Match Odds", "Man Utd vs Arsenal", 1, new Date(2000), List(new Market.Runner(11, "Man Utd"), new Market.Runner(12, "Arsenal")))
    market.placeBet(100, 123, 2, 3, BACK, 11,1000)
    market.placeBet(101, 123, 2, 3, LAY, 11,1000)

    assertEquals(2, market.getBets(123).size)
    market.cancelBet(100)
  }

  @Test
  def testCancelUnsettledBackBet {
    val market = new Market(1, "Match Odds", "Man Utd vs Arsenal", 1, new Date(2000), List(new Market.Runner(11, "Man Utd"), new Market.Runner(12, "Arsenal")))
    market.placeBet(100, 123, 2, 3, BACK, 11,1000)

    assertEquals(1, market.getBets(123).size)
    assertEquals(2, market.cancelBet(100), 0)
    assertEquals(0, market.getBets(123).size)
  }

  @Test
  def testCancelUnsettledLayBet {
    val market = new Market(1, "Match Odds", "Man Utd vs Arsenal", 1, new Date(2000), List(new Market.Runner(11, "Man Utd"), new Market.Runner(12, "Arsenal")))
    market.placeBet(100, 123, 2, 3, LAY, 11,1000)

    assertEquals(1, market.getBets(123).size)
    assertEquals(2, market.cancelBet(100), 0)
    assertEquals(0, market.getBets(123).size)
  }

  @Test
  def testCancelPartiallyMatchedBackBet {
    val market = new Market(1, "Match Odds", "Man Utd vs Arsenal", 1, new Date(2000), List(new Market.Runner(11, "Man Utd"), new Market.Runner(12, "Arsenal")))
    market.placeBet(100, 123, 3, 3, BACK, 11,1000)
    market.placeBet(101, 123, 2, 3, LAY, 11,1000)

    assertEquals(3, market.getBets(123).size)
    assertEquals(1, market.cancelBet(100), 0)
    assertEquals(2, market.getBets(123).size)
  }

  @Test
  def testCancelPartiallyMatchedLayBet {
    val market = new Market(1, "Match Odds", "Man Utd vs Arsenal", 1, new Date(2000), List(new Market.Runner(11, "Man Utd"), new Market.Runner(12, "Arsenal")))
    market.placeBet(100, 123, 2, 3, BACK, 11,1000)
    market.placeBet(101, 123, 3, 3, LAY, 11,1000)

    assertEquals(3, market.getBets(123).size)
    assertEquals(1, market.cancelBet(101), 0)
    assertEquals(2, market.getBets(123).size)
  }

  /** 
   *  Tests for getBets.
   * 
   * */

  @Test
  def testGetBetsForNotExistingUser {
    val market = new Market(1, "Match Odds", "Man Utd vs Arsenal", 1, new Date(2000), List(new Market.Runner(11, "Man Utd"), new Market.Runner(12, "Arsenal")))

    market.placeBet(100, 123, 2, 1.5, BACK, 11,1000)
    assertEquals(0, market.getBets(1234).size)
  }

  @Test
  def testGetBetsNoBets {
    val market = new Market(1, "Match Odds", "Man Utd vs Arsenal", 1, new Date(2000), List(new Market.Runner(11, "Man Utd"), new Market.Runner(12, "Arsenal")))

    assertEquals(0, market.getBets(123).size)
  }

  @Test
  def getBetsMatchedOnly {
    val market = new Market(1, "Match Odds", "Man Utd vs Arsenal", 1, new Date(2000), List(new Market.Runner(11, "Man Utd"), new Market.Runner(12, "Arsenal")))

    market.placeBet(100, 123, 2, 1.5, BACK, 11,1000)
    market.placeBet(101, 123, 3, 1.4, BACK, 11,1000)
    market.placeBet(102, 123, 2, 1.6, LAY, 11,1000)

    val matchedBets = market.getBets(123, true)
    assertEquals(2, matchedBets.size)

    val allBets = market.getBets(123, false)
    assertEquals(4, allBets.size)
  }

  /** 
   *  Tests for getRunnerPrices.
   * 
   * */

  @Test
  def testGetRunnerPricesNoBets {
    val market = new Market(1, "Match Odds", "Man Utd vs Arsenal", 1, new Date(2000), List(new Market.Runner(11, "Man Utd"), new Market.Runner(12, "Arsenal")))

    assertEquals(0, market.getRunnerPrices(11).size)
  }

  @Test(expected = classOf[IllegalArgumentException])
  def testGetRunnerPricesForNotExistingRunner {
    val market = new Market(1, "Match Odds", "Man Utd vs Arsenal", 1, new Date(2000), List(new Market.Runner(11, "Man Utd"), new Market.Runner(12, "Arsenal")))

    assertEquals(0, market.getRunnerPrices(1234).size)
  }

  @Test
  def testGetRunnerPricesForUnmatchedBetsOnly {
    val market = new Market(1, "Match Odds", "Man Utd vs Arsenal", 1, new Date(2000), List(new Market.Runner(11, "Man Utd"), new Market.Runner(12, "Arsenal")))

    market.placeBet(100, 122, 13, 2.1, LAY, 11,1000)
    market.placeBet(101, 121, 3, 2.2, LAY, 11,1000)
    market.placeBet(102, 122, 5, 2.2, LAY, 11,1000)
    market.placeBet(103, 121, 8, 2.4, BACK, 11,1000)
    market.placeBet(104, 122, 25, 2.5, BACK, 11,1000)

    val runnerPrices = market.getRunnerPrices(11)

    assertEquals(4, runnerPrices.size)

    assertEquals(2.1, runnerPrices(0).price, 0)
    assertEquals(13, runnerPrices(0).totalToBack, 0)
    assertEquals(0, runnerPrices(0).totalToLay, 0)

    assertEquals(2.2, runnerPrices(1).price, 0)
    assertEquals(8, runnerPrices(1).totalToBack, 0)
    assertEquals(0, runnerPrices(1).totalToLay, 0)

    assertEquals(2.4, runnerPrices(2).price, 0)
    assertEquals(0, runnerPrices(2).totalToBack, 0)
    assertEquals(8, runnerPrices(2).totalToLay, 0)

    assertEquals(2.5, runnerPrices(3).price, 0)
    assertEquals(0, runnerPrices(3).totalToBack, 0)
    assertEquals(25, runnerPrices(3).totalToLay, 0)
  }

  @Test
  def testGetRunnerPricesForUnmatchedBetsOnlyPlacedInDifferentOrder {
    val market = new Market(1, "Match Odds", "Man Utd vs Arsenal", 1, new Date(2000), List(new Market.Runner(11, "Man Utd"), new Market.Runner(12, "Arsenal")))

    market.placeBet(101, 121, 3, 2.2, LAY, 11,1000)
    market.placeBet(102, 122, 5, 2.2, LAY, 11,1000)
    market.placeBet(100, 122, 13, 2.1, LAY, 11,1000)
    market.placeBet(104, 122, 25, 2.5, BACK, 11,1000)
    market.placeBet(103, 121, 8, 2.4, BACK, 11,1000)

    val runnerPrices = market.getRunnerPrices(11)

    assertEquals(4, runnerPrices.size)

    assertEquals(2.1, runnerPrices(0).price, 0)
    assertEquals(13, runnerPrices(0).totalToBack, 0)
    assertEquals(0, runnerPrices(0).totalToLay, 0)

    assertEquals(2.2, runnerPrices(1).price, 0)
    assertEquals(8, runnerPrices(1).totalToBack, 0)
    assertEquals(0, runnerPrices(1).totalToLay, 0)

    assertEquals(2.4, runnerPrices(2).price, 0)
    assertEquals(0, runnerPrices(2).totalToBack, 0)
    assertEquals(8, runnerPrices(2).totalToLay, 0)

    assertEquals(2.5, runnerPrices(3).price, 0)
    assertEquals(0, runnerPrices(3).totalToBack, 0)
    assertEquals(25, runnerPrices(3).totalToLay, 0)
  }

  @Test
  def testGetRunnerPricesForUnmatchedBetsOnMoreThanOneRunner {
    val market = new Market(1, "Match Odds", "Man Utd vs Arsenal", 1, new Date(2000), List(new Market.Runner(11, "Man Utd"), new Market.Runner(12, "Arsenal")))

    /**Unmatched bets on runner 11.*/
    market.placeBet(100, 122, 13, 2.1, LAY, 11,1000)
    market.placeBet(101, 121, 3, 2.2, LAY, 11,1000)
    market.placeBet(102, 122, 5, 2.2, LAY, 11,1000)
    market.placeBet(103, 121, 8, 2.4, BACK, 11,1000)
    market.placeBet(104, 122, 25, 2.5, BACK, 11,1000)

    /**Unmatched bets on runner 12.*/
    market.placeBet(105, 122, 13, 2.1, LAY, 12,1001)
    market.placeBet(106, 122, 15, 2.4, BACK, 12,1001)

    val runnerPrices = market.getRunnerPrices(11)

    assertEquals(4, runnerPrices.size)

    assertEquals(2.1, runnerPrices(0).price, 0)
    assertEquals(13, runnerPrices(0).totalToBack, 0)
    assertEquals(0, runnerPrices(0).totalToLay, 0)

    assertEquals(2.2, runnerPrices(1).price, 0)
    assertEquals(8, runnerPrices(1).totalToBack, 0)
    assertEquals(0, runnerPrices(1).totalToLay, 0)

    assertEquals(2.4, runnerPrices(2).price, 0)
    assertEquals(0, runnerPrices(2).totalToBack, 0)
    assertEquals(8, runnerPrices(2).totalToLay, 0)

    assertEquals(2.5, runnerPrices(3).price, 0)
    assertEquals(0, runnerPrices(3).totalToBack, 0)
    assertEquals(25, runnerPrices(3).totalToLay, 0)
  }

  @Test
  def testGetRunnerPricesForUnmatchedAndMatchedBets {
    val market = new Market(1, "Match Odds", "Man Utd vs Arsenal", 1, new Date(2000), List(new Market.Runner(11, "Man Utd"), new Market.Runner(12, "Arsenal")))

    /**Unmatched bets.*/
    market.placeBet(100, 122, 13, 2.1, LAY, 11,1000)
    market.placeBet(104, 122, 25, 2.5, BACK, 11,1000)

    /**Matching bets.*/
    market.placeBet(105, 122, 10, 2.5, LAY, 11,1000)

    val runnerPrices = market.getRunnerPrices(11)

    assertEquals(2, runnerPrices.size)

    assertEquals(2.1, runnerPrices(0).price, 0)
    assertEquals(13, runnerPrices(0).totalToBack, 0)
    assertEquals(0, runnerPrices(0).totalToLay, 0)

    assertEquals(2.5, runnerPrices(1).price, 0)
    assertEquals(0, runnerPrices(1).totalToBack, 0)
    assertEquals(15, runnerPrices(1).totalToLay, 0)
  }

  @Test
  def testGetRunnerLayAndBackBetsonTheSamePrice {
    val market = new Market(1, "Match Odds", "Man Utd vs Arsenal", 1, new Date(2000), List(new Market.Runner(11, "Man Utd"), new Market.Runner(12, "Arsenal")))

    market.placeBet(100, 122, 5, 2.4, BACK, 11,1000)
    market.placeBet(101, 122, 8, 2.4, LAY, 11,1000)

    val runnerPrices = market.getRunnerPrices(11)

    assertEquals(1, runnerPrices.size)

    assertEquals(2.4, runnerPrices(0).price, 0)
    assertEquals(3, runnerPrices(0).totalToBack, 0)
    assertEquals(0, runnerPrices(0).totalToLay, 0)
  }

  /** 
   *  Tests for getRunnerTradedVolume.
   * 
   * */
  @Test
  def testRunnerTradedVolumeNoBets {
    val market = new Market(1, "Match Odds", "Man Utd vs Arsenal", 1, new Date(2000), List(new Market.Runner(11, "Man Utd"), new Market.Runner(12, "Arsenal")))

    assertEquals(0, market.getRunnerTradedVolume(11).pricesTradedVolume.size)
  }
  @Test
  def testRunnerTradedVolumeNoMatchedBets {
    val market = new Market(1, "Match Odds", "Man Utd vs Arsenal", 1, new Date(2000), List(new Market.Runner(11, "Man Utd"), new Market.Runner(12, "Arsenal")))
    market.placeBet(100, 122, 5, 2.4, BACK, 11,1000)
    market.placeBet(101, 122, 8, 2.3, LAY, 11,1000)

    assertEquals(0, market.getRunnerTradedVolume(11).pricesTradedVolume.size)
  }

  @Test(expected = classOf[IllegalArgumentException])
  def testRunnerTradedVolumeForNotExistingRunner {
    val market = new Market(1, "Match Odds", "Man Utd vs Arsenal", 1, new Date(2000), List(new Market.Runner(11, "Man Utd"), new Market.Runner(12, "Arsenal")))

    market.getRunnerTradedVolume(1234)
  }

  @Test
  def testRunnerTradedVolumeOneMatchedBet {
    val market = new Market(1, "Match Odds", "Man Utd vs Arsenal", 1, new Date(2000), List(new Market.Runner(11, "Man Utd"), new Market.Runner(12, "Arsenal")))
    market.placeBet(100, 122, 5, 2.4, BACK, 11,1000)
    market.placeBet(101, 122, 8, 2.6, LAY, 11,1000)

    val tradedVolume = market.getRunnerTradedVolume(11)
    assertEquals(1, tradedVolume.pricesTradedVolume.size)
    assertEquals(2.4, tradedVolume.pricesTradedVolume(0).price, 0)
    assertEquals(5, tradedVolume.pricesTradedVolume(0).totalMatchedAmount, 0)
  }

  @Test
  def testRunnerTradedVolumeThreeMatchedBet {
    val market = new Market(1, "Match Odds", "Man Utd vs Arsenal", 1, new Date(2000), List(new Market.Runner(11, "Man Utd"), new Market.Runner(12, "Arsenal")))
    market.placeBet(100, 122, 5, 2.4, BACK, 11,1000)
    market.placeBet(101, 122, 8, 2.6, LAY, 11,1000)
    market.placeBet(102, 122, 4, 2.5, BACK, 11,1000)
    market.placeBet(103, 122, 2, 2.8, LAY, 11,1000)

    val tradedVolume = market.getRunnerTradedVolume(11)
    assertEquals(3, tradedVolume.pricesTradedVolume.size)
    assertEquals(2.4, tradedVolume.pricesTradedVolume(0).price, 0)
    assertEquals(5, tradedVolume.pricesTradedVolume(0).totalMatchedAmount, 0)

    assertEquals(2.5, tradedVolume.pricesTradedVolume(1).price, 0)
    assertEquals(1, tradedVolume.pricesTradedVolume(1).totalMatchedAmount, 0)

    assertEquals(2.6, market.getRunnerTradedVolume(11).pricesTradedVolume(2).price, 0)
    assertEquals(3, tradedVolume.pricesTradedVolume(2).totalMatchedAmount, 0)
  }

  @Test
  def testRunnerTradedVolumeMatchedBetsOnTwoRunners {
    val market = new Market(1, "Match Odds", "Man Utd vs Arsenal", 1, new Date(2000), List(new Market.Runner(11, "Man Utd"), new Market.Runner(12, "Arsenal")))
    market.placeBet(100, 122, 5, 2.4, BACK, 11,1000)
    market.placeBet(101, 122, 8, 2.6, LAY, 11,1000)
    market.placeBet(102, 122, 4, 2.5, BACK, 12,1001)
    market.placeBet(103, 122, 2, 2.8, LAY, 12,1001)

    val tradedVolume11 = market.getRunnerTradedVolume(11)
    assertEquals(1, tradedVolume11.pricesTradedVolume.size)
    assertEquals(2.4, tradedVolume11.pricesTradedVolume(0).price, 0)
    assertEquals(5, tradedVolume11.pricesTradedVolume(0).totalMatchedAmount, 0)

    val tradedVolume12 = market.getRunnerTradedVolume(12)
    assertEquals(1, tradedVolume12.pricesTradedVolume.size)
    assertEquals(2.5, tradedVolume12.pricesTradedVolume(0).price, 0)
    assertEquals(2, tradedVolume12.pricesTradedVolume(0).totalMatchedAmount, 0)

  }

  /** 
   *  Tests for getTotalTradedVolume.
   * 
   * */
  @Test
  def testTotalTradedVolumeNoBets {
    val market = new Market(1, "Match Odds", "Man Utd vs Arsenal", 1, new Date(2000), List(new Market.Runner(11, "Man Utd"), new Market.Runner(12, "Arsenal")))

    assertEquals(0, market.getTotalTradedVolume(11), 0)
  }
  @Test
  def testTotalTradedVolumeNoMatchedBets {
    val market = new Market(1, "Match Odds", "Man Utd vs Arsenal", 1, new Date(2000), List(new Market.Runner(11, "Man Utd"), new Market.Runner(12, "Arsenal")))
    market.placeBet(100, 122, 5, 2.4, BACK, 11,1000)
    market.placeBet(101, 122, 8, 2.3, LAY, 11,1000)

    assertEquals(0, market.getTotalTradedVolume(11), 0)
  }

  @Test(expected = classOf[IllegalArgumentException])
  def testTotalTradedVolumeForNotExistingRunner {
    val market = new Market(1, "Match Odds", "Man Utd vs Arsenal", 1, new Date(2000), List(new Market.Runner(11, "Man Utd"), new Market.Runner(12, "Arsenal")))

    market.getTotalTradedVolume(1234)
  }

  @Test
  def testTotalTradedVolumeOneMatchedBet {
    val market = new Market(1, "Match Odds", "Man Utd vs Arsenal", 1, new Date(2000), List(new Market.Runner(11, "Man Utd"), new Market.Runner(12, "Arsenal")))
    market.placeBet(100, 122, 5, 2.4, BACK, 11,1000)
    market.placeBet(101, 122, 8, 2.6, LAY, 11,1000)

    assertEquals(5, market.getTotalTradedVolume(11), 0)
  }

  @Test
  def testTotalTradedVolumeThreeMatchedBet {
    val market = new Market(1, "Match Odds", "Man Utd vs Arsenal", 1, new Date(2000), List(new Market.Runner(11, "Man Utd"), new Market.Runner(12, "Arsenal")))
    market.placeBet(100, 122, 5, 2.4, BACK, 11,1000)
    market.placeBet(101, 122, 8, 2.6, LAY, 11,1000)
    market.placeBet(102, 122, 4, 2.5, BACK, 11,1000)
    market.placeBet(103, 122, 2, 2.8, LAY, 11,1000)

    assertEquals(9, market.getTotalTradedVolume(11), 0)
  }

  @Test
  def testTotalTradedVolumeMatchedBetsOnTwoRunners {
    val market = new Market(1, "Match Odds", "Man Utd vs Arsenal", 1, new Date(2000), List(new Market.Runner(11, "Man Utd"), new Market.Runner(12, "Arsenal")))
    market.placeBet(100, 122, 5, 2.4, BACK, 11,1000)
    market.placeBet(101, 122, 8, 2.6, LAY, 11,1000)
    market.placeBet(102, 122, 4, 2.5, BACK, 12,1001)
    market.placeBet(103, 122, 2, 2.8, LAY, 12,1001)

    assertEquals(5, market.getTotalTradedVolume(11), 0)
    assertEquals(2, market.getTotalTradedVolume(12), 0)

  }

}