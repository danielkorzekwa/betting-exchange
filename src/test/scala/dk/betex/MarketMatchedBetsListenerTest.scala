package dk.betex

import org.junit._
import Assert._
import java.util.Date
import dk.betex.api._
import IBet.BetTypeEnum._
import IBet.BetStatusEnum._
import scala.collection.mutable.ListBuffer

class MarketMatchedBetsListenerTest {

  @Test
  def testListenerRegisteredAfterBetIsMatched {
    val matchedBets = ListBuffer[IBet]()
    val market = new Market(1, "Match Odds", "Man Utd vs Arsenal", 1, new Date(2000), List(new Market.Runner(11, "Man Utd"), new Market.Runner(12, "Arsenal")))

    market.placeBet(100, 123, 10, 3, BACK, 11,100)
    market.placeBet(101, 124, 7, 3, LAY, 11,100)
    market.addMatchedBetsListener(bet => bet.userId == 124, bet => { matchedBets += bet })
    assertEquals(0, matchedBets.size)

  }

  @Test
  def testOneBetIsMatched {

    val matchedBets = ListBuffer[IBet]()
    val market = new Market(1, "Match Odds", "Man Utd vs Arsenal", 1, new Date(2000), List(new Market.Runner(11, "Man Utd"), new Market.Runner(12, "Arsenal")))
    market.addMatchedBetsListener(bet => bet.userId == 124, bet => { matchedBets += bet })
    market.placeBet(100, 123, 10, 3, BACK, 11,100)
    market.placeBet(101, 124, 7, 3, LAY, 11,101)

    assertEquals(1, matchedBets.size)
    assertEquals(Bet(101, 124, 7, 3, LAY, M, 1, 11,101,Some(101)), matchedBets(0))
  }

  @Test
  def twoBetsAreMatched {
    val matchedBets = ListBuffer[IBet]()
    val market = new Market(1, "Match Odds", "Man Utd vs Arsenal", 1, new Date(2000), List(new Market.Runner(11, "Man Utd"), new Market.Runner(12, "Arsenal")))
    market.addMatchedBetsListener(bet => bet.runnerId == 11, bet => { matchedBets += bet })
    market.placeBet(100, 123, 10, 3, BACK, 11,100)
    market.placeBet(101, 124, 7, 3, LAY, 11,101)

    assertEquals(2, matchedBets.size)
    assertEquals(Bet(101, 124, 7, 3, LAY, M, 1, 11,101,Some(101)), matchedBets(0))
    assertEquals(Bet(100, 123, 7, 3, BACK, M, 1, 11,100,Some(101)), matchedBets(1))
  }

}