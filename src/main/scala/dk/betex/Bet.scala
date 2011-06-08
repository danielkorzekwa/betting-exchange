package dk.betex

import dk.betex.api._
import dk.betex.api.IBet._
import dk.betex.api.IBet.BetTypeEnum._
import dk.betex.api.IBet.BetStatusEnum._
/** This class represents a bet on a betting exchange.
 * 
 * @author korzekwad
 *
 */
object Bet {

  /**Creates unsettled bet.
   * 
   * @param betId
   * @param userId
   * @param betSize
   * @param betPrice
   * @param betType
   * @param marketId
   * @param runnerId
   * @return
   */
  def apply(betId: Long, userId: Long, betSize: Double, betPrice: Double, betType: BetTypeEnum, marketId: Long, runnerId: Long, placedDate:Long): IBet =
    new Bet(betId, userId, betSize, betPrice, betType, U, marketId, runnerId,placedDate,None)
}

case class Bet(val betId: Long, val userId: Long, val betSize: Double, val betPrice: Double, val betType: BetTypeEnum, val betStatus: BetStatusEnum, val marketId: Long, val runnerId: Long,val placedDate:Long,val matchedDate:Option[Long]) extends IBet {
  require(betPrice >= 1.01 && betPrice <= 1000, "Bet price must be between 1.01 and 1000, betPrice=" + betPrice)

  /**Match two bets. Bet that the matchedBet method is executed on is always matched at the best available price. 
   * Examples: backBetWithPrice2.matchBet(layBetWithPrice3) = matched price 3,layBetWithPrice3.matchBet(backBetWithPrice2) = matched price 2. 
   * 
   * @param bet The bet to be matched with.
   * @return Result of bets matching. In the general form it consists of 4 elements. 
   * 1 - matched portion of first bet, 2 - unmatched portion of first bet, 3 - matched portion of second bet, 4 - unmatched portion of second bet.
   *   
   * */
  def matchBet(bet: IBet): List[IBet] = {

    /**Do not match scenarios.*/
    if (betType == bet.betType || marketId != bet.marketId || runnerId != bet.runnerId || betStatus == IBet.BetStatusEnum.M || bet.betStatus == IBet.BetStatusEnum.M || (betType == IBet.BetTypeEnum.BACK && betPrice > bet.betPrice) || (betType == IBet.BetTypeEnum.LAY && betPrice < bet.betPrice)) {
      val firstBetUnmatchedPortion = new Bet(betId, userId, betSize, betPrice, betType, betStatus, marketId, runnerId,placedDate,None)
      val secondBetUnmatchedPortion = new Bet(bet.betId, bet.userId, bet.betSize, bet.betPrice, bet.betType, bet.betStatus, bet.marketId, bet.runnerId,bet.placedDate,None)

      List(firstBetUnmatchedPortion, secondBetUnmatchedPortion)
    } else {
      /**Match on the best available price.*/
      val matchedPrice = bet.betPrice
      val matchedSize = betSize.min(bet.betSize)

      val firstBetMatchedPortion = new Bet(betId, userId, matchedSize, matchedPrice, betType, BetStatusEnum.M, marketId, runnerId,placedDate,Option(placedDate))
      val firstBetUnmatchedSize = betSize - matchedSize
      val firstBetUnmatchedPortion = new Bet(betId, userId, firstBetUnmatchedSize, betPrice, betType, BetStatusEnum.U, marketId, runnerId,placedDate,None)

      val secondBetMatchedPortion = new Bet(bet.betId, bet.userId, matchedSize, matchedPrice, bet.betType, BetStatusEnum.M, bet.marketId, bet.runnerId,bet.placedDate,Option(placedDate))
      val secondBetUnmatchedSize = bet.betSize - matchedSize
      val secondBetUnmatchedPortion = new Bet(bet.betId, bet.userId, secondBetUnmatchedSize, bet.betPrice, bet.betType, BetStatusEnum.U, bet.marketId, bet.runnerId,bet.placedDate,None)

      List(firstBetMatchedPortion, firstBetUnmatchedPortion, secondBetMatchedPortion, secondBetUnmatchedPortion).filter(b => b.betSize > 0)
    }

  }

  override def toString = "Bet [betId=%s, userId=%s, betSize=%s, betPrice=%s, betType=%s, betStatus=%s, marketId=%s, runnerId=%s, placedDate=%s,matchedDate=%s]".format(betId, userId, betSize, betPrice, betType, betStatus, marketId, runnerId,placedDate,matchedDate)
}

