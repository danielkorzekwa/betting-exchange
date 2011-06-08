package dk.betex

import java.util.Date
import dk.betex.api._
import dk.betex.api.IBet._
import scala.collection.mutable.ListBuffer
import IBet.BetStatusEnum._
import IBet.BetTypeEnum._
import Market._
import IMarket._
import IRunnerTradedVolume._
import scala.collection._
import IUnmatchedBets._

/**
 * This class represents a market on a betting exchange.
 * @author korzekwad
 *
 */
object Market {
  case class Runner(val runnerId: Long, val runnerName: String) extends IMarket.IRunner {
    override def toString = "Runner [runnerId=%s, runnerName=%s]".format(runnerId, runnerName)
  }

  case class RunnerPrice(val price: Double, val totalToBack: Double, val totalToLay: Double) extends IMarket.IRunnerPrice {
    override def toString = "RunnerPrice [price=%s, totalToBack=%s, totalToLay=%s]".format(price, totalToBack, totalToLay)
  }

}

class Market(val marketId: Long, val marketName: String, val eventName: String, val numOfWinners: Int, val marketTime: Date, val runners: List[IMarket.IRunner]) extends IMarket {

  /**key - runnerId, value - runnerBackBetsPerPrice*/
  private val backBets = new UnmatchedBackBets()
  /**key - runnerId, value - runnerLayBetsPerPrice*/
  private val layBets = new UnmatchedLayBets()

  private val matchedBackBets = ListBuffer[IBet]()
  private val matchedLayBets = ListBuffer[IBet]()

  /**[runnerId, total traded volume]*/
  private val totalTradedVolume: mutable.Map[Long, Double] = mutable.Map()

  private val betsIds = scala.collection.mutable.Set[Long]()

  /**List of registered listeners (functions) to be triggered every time when a new bet is matched.*/
  private val matchedBetsListeners = new ListBuffer[(IBet) => Unit]()

  /**List of registered listeners (functions) to be triggered every time when a bet is placed and not matched immediately.*/
  private val unmatchedBetsListeners = new ListBuffer[(IBet) => Unit]()

  /**List of registered listeners (functions) to be triggered every time when a bet is cancelled.*/
  private val cancelBetsListeners = new ListBuffer[(IBet) => Unit]()

  require(numOfWinners > 0, "numOfWinners should be bigger than 0, numOfWinners=" + numOfWinners)
  require(runners.size > 1, "Number of market runners should be bigger than 1, numOfRunners=" + runners.size)
  /**Returns Map[price,bets] for runnerId*/
  private def getRunnerBets(runnerId: Long, bets: mutable.Map[Long, mutable.Map[Double, ListBuffer[IBet]]]): mutable.Map[Double, ListBuffer[IBet]] = {
    bets.getOrElseUpdate(runnerId, scala.collection.mutable.Map[Double, ListBuffer[IBet]]())
  }

  /**
   * Places a bet on a betting exchange market.
   *
   * @param betId
   * @param userId
   * @param betSize
   * @param betPrice
   * @param betType
   * @param runnerId
   *
   * @return The bet that was placed.
   */
  def placeBet(betId: Long, userId: Long, betSize: Double, betPrice: Double, betType: BetTypeEnum, runnerId: Long, placedDate: Long): IBet = {

    require(betSize > 0, "Bet size must be >0, betSize=" + betSize)
    require(runners.exists(s => s.runnerId == runnerId), "Can't place bet on a market. Market runner not found for marketId/runnerId=" + marketId + "/" + runnerId)
    require(!betsIds.contains(betId), "Bet for betId=%s already exists".format(betId))

    betsIds += betId

    val newBet = new Bet(betId, userId, betSize, betPrice, betType, U, marketId, runnerId, placedDate, None)

    /**Match bet.*/
    val betMatchingResult: BetMatchingResult = betType match {
      case BACK => {
        val betMatchingResults = layBets.matchBet(newBet)
        betMatchingResults.unmatchedBet.foreach(b => backBets.addBet(b))

        betMatchingResults
      }
      case LAY => {
        val betMatchingResults = backBets.matchBet(newBet)
        betMatchingResults.unmatchedBet.foreach(b => layBets.addBet(b))
        betMatchingResults
      }
    }

    /**Trigger unmatched bets listeners.*/
    if (betMatchingResult.unmatchedBet.isDefined) {
      unmatchedBetsListeners.foreach(l => l(betMatchingResult.unmatchedBet.get))
    }

    /**Add matched bets.*/
    for (matchedBet <- betMatchingResult.matchedBets) {
      matchedBet.betType match {
        case BACK => {
          matchedBackBets += matchedBet
          /**Add to matched bets totals only back bets to not double count.*/
          totalTradedVolume(matchedBet.runnerId) = totalTradedVolume.getOrElse(matchedBet.runnerId, 0d) + matchedBet.betSize
        }
        case LAY => matchedLayBets += matchedBet
      }

      /**Trigger matched bets listeners.*/
      matchedBetsListeners.foreach(l => l(matchedBet))
    }

    newBet
  }

  /**
   * Cancels a bet on a betting exchange market.
   *
   * @param betId Unique id of a bet to be cancelled.
   *
   * @return amount cancelled
   * @throws NoSuchElementException is thrown if no unmatched bet for betId/userId found.
   */
  def cancelBet(betId: Long): Double = {

    val backCancelledAmount = backBets.cancelBet(betId)
    if (backCancelledAmount > 0) backCancelledAmount
    else {
      val layCancelledAmount = layBets.cancelBet(betId)
      if (layCancelledAmount > 0) layCancelledAmount else throw new NoSuchElementException("Bet not found for bet id=" + betId)
    }
  }

  /**
   * Cancels bets on a betting exchange market.
   *
   * @param userId
   * @param betsSize Total size of bets to be cancelled.
   * @param betPrice The price that bets are cancelled on.
   * @param betType
   * @param runnerId
   *
   * @return Amount cancelled. Zero is returned if nothing is available to cancel.
   */
  def cancelBets(userId: Long, betsSize: Double, betPrice: Double, betType: BetTypeEnum, runnerId: Long): Double = {

    val amountCancelled = betType match {
      case BACK => backBets.cancelBets(userId, betsSize, betPrice, runnerId)
      case LAY => layBets.cancelBets(userId, betsSize, betPrice, runnerId)
    }

    cancelBetsListeners.foreach(l => l(new Bet(-1l, userId, amountCancelled, betPrice, betType, U, marketId, runnerId, -1l, None)))
    amountCancelled
  }

  /**
   * Returns total unmatched volume to back and to lay at all prices for all runners in a market on a betting exchange.
   *  Prices with zero volume are not returned by this method.
   *
   * @param runnerId Unique runner id that runner prices are returned for.
   * @return
   */
  def getRunnerPrices(runnerId: Long): List[IMarket.IRunnerPrice] = {
    require(runners.exists(s => s.runnerId == runnerId), "Market runner not found for marketId/runnerId=" + marketId + "/" + runnerId)

    val allBackBetsList = backBets.getBets(runnerId)
    val allLayBetsList = layBets.getBets(runnerId)

    val betsByPriceMap = (allBackBetsList.toList ::: allLayBetsList.toList).toList.groupBy(b => b.betPrice)

    def totalStake(bets: List[IBet], betType: BetTypeEnum) = bets.filter(b => b.betType == betType).foldLeft(0d)(_ + _.betSize)
    betsByPriceMap.map(entry => new RunnerPrice(entry._1, totalStake(entry._2, LAY), totalStake(entry._2, BACK))).toList.sortWith(_.price < _.price)
  }

  /**
   * Returns best toBack/toLay prices for market runner.
   * Element 1 - best price to back, element 2 - best price to lay
   * Double.NaN is returned if price is not available.
   * @return
   */
  def getBestPrices(runnerId: Long): Tuple2[IRunnerPrice, IRunnerPrice] = {

    require(runners.exists(s => s.runnerId == runnerId), "Market runner not found for marketId/runnerId=" + marketId + "/" + runnerId)

    val bestPriceToBack = layBets.getBestPrice(runnerId)
    val bestPriceToLay = backBets.getBestPrice(runnerId)

    new Tuple2(bestPriceToBack, bestPriceToLay)

  }

  /**
   * Returns best toBack/toLay prices for market.
   *
   * @return Key - runnerId, Value - market prices (element 1 - priceToBack, element 2 - priceToLay)
   */
  def getBestPrices(): Map[Long, Tuple2[IRunnerPrice, IRunnerPrice]] = {
    Map(runners.map(r => r.runnerId -> getBestPrices(r.runnerId)): _*)
  }

  /**Returns total traded volume for all prices on all runners in a market.*/
  def getRunnerTradedVolume(runnerId: Long): IRunnerTradedVolume = {
    require(runners.exists(s => s.runnerId == runnerId), "Market runner not found for marketId/runnerId=" + marketId + "/" + runnerId)

    /**Take only BACK bets to not double count traded volume (each matched back bet has corresponding matched lay bet.*/
    val betsByPrice = matchedBackBets.toList.filter(b => b.runnerId == runnerId).groupBy(b => b.betPrice)

    /**Map betsByPrice to list of PriceTradedVolume.*/
    val pricesTradedVolume = betsByPrice.map(entry => new RunnerTradedVolume.PriceTradedVolume(entry._1, entry._2.foldLeft(0d)(_ + _.betSize))).toList.sortWith(_.price < _.price)
    new RunnerTradedVolume(pricesTradedVolume)
  }

  /**Returns total traded volume for a given runner.*/
  def getTotalTradedVolume(runnerId: Long): Double = {
    require(runners.exists(s => s.runnerId == runnerId), "Market runner not found for marketId/runnerId=" + marketId + "/" + runnerId)
    totalTradedVolume.getOrElse(runnerId, 0)
  }

  /**
   * Returns all bets placed by user on that market.
   *
   * @param userId
   */
  def getBets(userId: Int): List[IBet] = backBets.getBets(userId) ::: layBets.getBets(userId) ::: matchedBackBets.filter(b => b.userId == userId).toList ::: matchedLayBets.filter(b => b.userId == userId).toList

  /**
   * Returns all bets placed by user on that market.
   *
   * @param userId
   * @param matchedBetsOnly If true then matched bets are returned only,
   * otherwise all unmatched and matched bets for user are returned.
   */
  def getBets(userId: Int, matchedBetsOnly: Boolean): List[IBet] = {
    val bets = matchedBetsOnly match {
      case true => matchedBackBets.filter(b => b.userId == userId).toList ::: matchedLayBets.filter(b => b.userId == userId).toList
      case false => getBets(userId)
    }
    bets
  }

  /**
   * Returns bet for a number o criteria.
   *
   * @param userId
   * @param betStatus
   * @param betType
   * @param betPrice
   * @param runnerId
   */
  def getBets(userId: Int, betStatus: BetStatusEnum, betType: BetTypeEnum, betPrice: Double, runnerId: Long): List[IBet] = {

    val bets = betStatus match {
      case U =>
        betType match {
          case BACK => backBets.getBets(betPrice, runnerId)
          case LAY => layBets.getBets(betPrice, runnerId)
        }
      case M => matchedBackBets.filter(b => b.betPrice == betPrice && b.runnerId == runnerId).toList ::: matchedLayBets.filter(b => b.betPrice == betPrice && b.runnerId == runnerId).toList
    }

    bets.filter(b => b.userId == userId)
  }

  /**
   * Register listener on those matched bets, which match filter criteria
   *
   * @param filter If true then listener is triggered for this bet.
   * @param listener
   */
  def addMatchedBetsListener(filter: (IBet) => Boolean, listener: (IBet) => Unit) = {
    matchedBetsListeners += { (bet: IBet) => if (filter(bet)) listener(bet) }
  }

  /**
   * Register listener on those unmatched bets, which match filter criteria
   *
   * @param filter If true then listener is triggered for this bet.
   * @param listener
   */
  def addUnmatchedBetsListener(filter: (IBet) => Boolean, listener: (IBet) => Unit) {
    unmatchedBetsListeners += { (bet: IBet) => if (filter(bet)) listener(bet) }
  }

  /**
   * Register listener on cancelled bets.
   *
   * @param listener
   */
  def addCancelledBetsListener(listener: (IBet) => Unit) {
    cancelBetsListeners += listener
  }

  override def toString = "Market [marketId=%s, marketName=%s, eventName=%s, numOfWinners=%s, marketTime=%s, runners=%s]".format(marketId, marketName, eventName, numOfWinners, marketTime, runners)
}