package dk.betex.api

import java.util.Date

/** This trait represents a betting exchange. It allows to create market, place bet, cancel bet, etc.
 * @author korzekwad
 *
 */
trait IBetex {

  /**Creates market on a betting exchange.
   * 
   * @param market
   * 
   * @return Created market
   */
  def createMarket(marketId: Long, marketName: String, eventName: String, numOfWinners: Int, marketTime: Date, runners: List[IMarket.IRunner]): IMarket

  /**Finds market for market id.
   * 
   * @param marketId
   * 
   * @return Found market is returned or exception is thrown if market not exists.
   */
  def findMarket(marketId: Long): IMarket

  /**Removes market from betting exchange.
   * 
   * @param marketId
   * @return Removed market or None if market didn't exist.
   */
  def removeMarket(marketId: Long): Option[IMarket]

  /**Returns all markets on a betting exchange.*/
  def getMarkets(): List[IMarket]

  /**Removes all markets and bets.*/
  def clear(): Unit

}