package dk.betex

import dk.betex.api._
import scala.collection._
import scala.collection.mutable.Map
import java.util.Date

/** This class represents a betting exchange. It allows to create market, place bet, cancel bet, etc.
 * 
 * @author korzekwad
 *
 */
class Betex extends IBetex {

  private val markets = new mutable.HashMap[Long, IMarket] with mutable.SynchronizedMap[Long, IMarket]

  /**Creates market on a betting exchange.
   * 
   * @param market
   * 
   * @return Created market
   */
  def createMarket(marketId: Long, marketName: String, eventName: String, numOfWinners: Int, marketTime: Date, runners: List[IMarket.IRunner]): IMarket = {
    require(!markets.contains(marketId), "Market already exist for marketId=" + marketId)

    val newMarket = new Market(marketId, marketName, eventName, numOfWinners, marketTime, runners)
    markets += marketId -> newMarket
    newMarket
  }

  /**Removes market from betting exchange.
   * 
   * @param marketId
   * @return Removed market or None if market didn't exist.
   */
  def findMarket(marketId: Long): IMarket = markets(marketId)

  /**Removes market from betting exchange.
   * @return Removed market or None if market didn't exist.
   */
  def removeMarket(marketId: Long): Option[IMarket] = markets.remove(marketId)

  /**Returns all markets.*/
  def getMarkets(): List[IMarket] = markets.values.toList

  /**Removes all markets and bets.*/
  def clear(): Unit = markets.clear()

}