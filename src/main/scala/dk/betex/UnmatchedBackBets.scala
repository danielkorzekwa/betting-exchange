package dk.betex

import api._
import IUnmatchedBets._
import scala.collection._
import mutable.ListBuffer
import IBet.BetStatusEnum._
import IBet.BetTypeEnum._
import Market._
import IMarket._

/**This class represents unmatched back bets. 
 * It also acts as a bet matching engine taking a bet and matching it against unmatched bets in a model.
 * 
 * This is a stateful component.
 * 
 * @author korzekwad
 *
 */
class UnmatchedBackBets extends IUnmatchedBets {

  /**Returns prices used for matching. Different prices in different order are used for matching for back and lay bets.*/
  protected def getPricesToBeMatched(runnerId: Long, price: Double): Iterator[Double] = getRunnerBets(runnerId).keys.filter(p => p <= price).toList.sortWith((a, b) => a < b).iterator

  /**Returns best unmatched price
   * 
   * @return Double.NaN is returned if price is not available.
   * */
  def getBestPrice(runnerId: Long): IRunnerPrice = {
    val runnerBackBetsMap = getRunnerBets(runnerId)
    val pricesToLay = runnerBackBetsMap.keys

    val bestPriceToLay = if (!pricesToLay.isEmpty) {
      val price = pricesToLay.min
      val totalStake = BetUtil.totalStake(runnerBackBetsMap(price).toList)
      new RunnerPrice(price, 0d, totalStake)
    } else new RunnerPrice(Double.NaN, 0d, 0d)

    bestPriceToLay
  }
}