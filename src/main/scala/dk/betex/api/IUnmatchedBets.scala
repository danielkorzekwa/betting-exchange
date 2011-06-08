package dk.betex.api

import IUnmatchedBets._
import scala.collection._
import mutable.ListBuffer
import IMarket._
import IBet.BetStatusEnum._
import IBet.BetTypeEnum._
import dk.betex._
import scala.annotation.tailrec

/**This trait represents unmatched back or lay bets. 
 * It also acts as a bet matching engine taking a bet and matching it against unmatched bets in a model.
 * 
 * This is a stateful component.
 * 
 * @author korzekwad
 *
 */
object IUnmatchedBets {

  /**This class represents a result of matching a bet against unmatched bets, e.g. matching back bet against lay bets.
   * 
   * @param unmatchedBet Unmatched portion of a bet that was matched against unmatched bets. If a bet is fully matched then unmatchedBet is None.
   * @param matchedBets List of all back and lay matched bets. Examples: 
   * If a bet is fully matched against another bet then matchedBets list contains two matched bets. 
   * If a bet is matched against 2 unmatched bets then matchedBets contains 3 matched bets.
   * If a bet is not matched then matchedBets list is empty.
   * 	  
   */
  class BetMatchingResult(val unmatchedBet: Option[IBet], val matchedBets: List[IBet]) {
    override def toString = "BetMatchingResult [unmatchedBet=%s, matchedBets=%s".format(unmatchedBet, matchedBets)
  }
}

trait IUnmatchedBets {
  /**key - runnerId, value - runnerBackBetsPerPrice*/
  private val unmatchedBets = scala.collection.mutable.Map[Long, scala.collection.mutable.Map[Double, ListBuffer[IBet]]]()

  /**Returns Map[price,bets] for runnerId*/
  protected def getRunnerBets(runnerId: Long): mutable.Map[Double, ListBuffer[IBet]] = {
    unmatchedBets.getOrElseUpdate(runnerId, scala.collection.mutable.Map[Double, ListBuffer[IBet]]())
  }

  /**Add unmatched bet to a model. Only bet of a type, of which this instance of UnmatchedBets is parameterised by, can be added to a model, otherwise exception should be thrown.*/
  def addBet(bet: IBet) = getRunnerBets(bet.runnerId).getOrElseUpdate(bet.betPrice, ListBuffer()) += bet

  /**Match a bet against unmatched bets in a model.
   * 
   * @param bet The bet to be matched against unmatched bets.
   * @return Result of bet matching @see BetMatchingResult
   */
  def matchBet(bet: IBet): BetMatchingResult = {
    val pricesToBeMatched = getPricesToBeMatched(bet.runnerId, bet.betPrice)

    val betsToBeMatched = getRunnerBets(bet.runnerId)

    /**Result of bet matching.*/
    val matchedBets = ListBuffer[IBet]()
    var unmatchedBet: Option[IBet] = None

    @tailrec
    def matchBetRec(newBet: IBet, priceToMatch: Double): Unit = {

      val priceBets = betsToBeMatched.getOrElse(priceToMatch, ListBuffer())
      if (!priceBets.isEmpty) {

        /**Get bet to be matched and remove it from the main list of bets - it will be added later as a result of matching.*/
        val betToBeMatched = priceBets.head
        priceBets.remove(0)

        /**Do the bets matching.*/
        val matchingResult = newBet.matchBet(betToBeMatched)
        matchingResult.filter(b => b.betStatus == U && b.betId != bet.betId).foreach(b => priceBets.insert(0, b))
        matchingResult.filter(b => b.betStatus == M).foreach(b => matchedBets += b)

        /**Make sure that priceBetsMap doesn't contain entries with empty bets list.*/
        if (priceBets.isEmpty) betsToBeMatched.remove(priceToMatch)

        /**Find unmatched portion for a bet being placed.*/
        val unmatchedPortion = matchingResult.find(b => b.betId == bet.betId && b.betStatus == U)
        if (!unmatchedPortion.isEmpty) {
          matchBetRec(unmatchedPortion.get, priceToMatch)
        }
      } else {
        if (pricesToBeMatched.hasNext) {
          matchBetRec(newBet, pricesToBeMatched.next)
        } else {
          unmatchedBet = Some(newBet)
        }
      }
    }

    if (pricesToBeMatched.hasNext) matchBetRec(bet, pricesToBeMatched.next)
    else unmatchedBet = Some(bet)

    new BetMatchingResult(unmatchedBet, matchedBets.toList)
  }

  /**Returns all unmatched bets user id.
   *
   *@param userId
   */
  def getBets(userId: Int): List[IBet] = {
    val unmatchedBetsList = for {
      runnerBackBetsMap <- unmatchedBets.values
      val runnerBets = runnerBackBetsMap.values.foldLeft(List[IBet]())((a, b) => a.toList ::: b.toList).filter(b => b.userId == userId)
    } yield runnerBets

    List.flatten(unmatchedBetsList.toList)
  }
  
   /**Returns all unmatched bets for a runner id*/
  def getBets(runnerId:Long):List[IBet] = getRunnerBets(runnerId).values.foldLeft(List[IBet]())((a, b) => a.toList ::: b.toList)

   /**Returns bet for a number o criteria.
	 * 
	 * @param betPrice
	 * @param runnerId
	 */
	def getBets(betPrice: Double, runnerId:Long):List[IBet] = getRunnerBets(runnerId).getOrElse(betPrice,new ListBuffer()).toList
	
  /** Cancels bets on a betting exchange market.
   * 
   * @param userId 
   * @param betsSize Total size of bets to be cancelled.
   * @param betPrice The price that bets are cancelled on.
   * @param runnerId 
   * 
   * @return Amount cancelled. Zero is returned if nothing is available to cancel.
   */
  def cancelBets(userId: Long, betsSize: Double, betPrice: Double, runnerId: Long): Double = {

    val runnerBets = getRunnerBets(runnerId)
    val priceBets = runnerBets.getOrElse(betPrice, new ListBuffer[IBet]())

    /**Cancel bets with lowest priority first (worst position in a bets queue)*/
    val betsToBeCancelled = priceBets.filter(b => b.userId == userId).reverseIterator

    @tailrec
    def cancelRec(amountToCancel: Double, amountCancelled: Double): Double = {
      val betToCancel = betsToBeCancelled.next
      val betCanceledAmount = if (amountToCancel >= betToCancel.betSize) {
        priceBets -= betToCancel
        
        betToCancel.betSize
      } else {
        val updatedBet = Bet(betToCancel.betId, betToCancel.userId, betToCancel.betSize - amountToCancel, betToCancel.betPrice, betToCancel.betType, betToCancel.marketId, betToCancel.runnerId,betToCancel.placedDate)
        priceBets.update(priceBets.indexOf(betToCancel, 0), updatedBet)
        amountToCancel
      }
      val newAmountToCancel = amountToCancel - betCanceledAmount
      val newAmountCancelled = amountCancelled + betCanceledAmount
      if (betsToBeCancelled.hasNext && newAmountToCancel > 0) cancelRec(newAmountToCancel, newAmountCancelled)
      else newAmountCancelled
    }

    val totalCancelled = if (betsToBeCancelled.hasNext) cancelRec(betsSize, 0) else 0

    /**Make sure that there are no empty entries in priceBetsMap*/
    if (totalCancelled > 0 && priceBets.isEmpty) runnerBets.remove(betPrice)

    totalCancelled
  }

  /** Cancels a bet on a betting exchange market.
   *
   * @param betId Unique id of a bet to be cancelled.
   * 
   * @return amount cancelled
   * @throws NoSuchElementException is thrown if no unmatched bet for betId/userId found.
   */
  def cancelBet(betId: Long): Double = {

    /**Finds a bet for a betId in a map of pricesBets
     * @throws RuntimeException is thrown if more than one bet is found
     */
    def findBet(pricesBets: scala.collection.mutable.Map[Double, ListBuffer[IBet]]): Option[IBet] = {

      val foundBets = for {
        priceBets <- pricesBets.values;
        val foundBet = priceBets.find(b => b.betId == betId);
        if (foundBet.isDefined)
      } yield foundBet

      foundBets match {
        case Nil => None
        case x :: Nil => x
        case x :: xs => throw new IllegalStateException("Duplicate bets found=" + foundBets)
      }
    }

    /**Find a bet for a betId across all runners.*/
    val foundBets = for {
      pricesBets <- unmatchedBets.values
      val foundBet = findBet(pricesBets)
      if (foundBet.isDefined)
    } yield foundBet.get

    /**Canncel a bet.*/
    val sizeCancelled: Double = foundBets match {
      case Nil => 0
      case bet :: Nil => {
    	  val priceBets = unmatchedBets(bet.runnerId)(bet.betPrice)
         priceBets -= bet
        /**Make sure that there are no empty entries in priceBetsMap*/
         if(priceBets.isEmpty) unmatchedBets(bet.runnerId).remove(bet.betPrice)
        bet.betSize
      }
      case x :: xs => throw new IllegalStateException("Duplicate bets found=" + foundBets)
    }

    sizeCancelled
  }

  /**Returns best unmatched price.
   * 
   * @return Double.NaN is returned if price is not available.
   * */
  def getBestPrice(runnerId: Long): IRunnerPrice
  
  /**Returns prices used for matching. Different prices in different order are used for matching for back and lay bets.*/
  protected def getPricesToBeMatched(runnerId: Long, price: Double): Iterator[Double]
  
}