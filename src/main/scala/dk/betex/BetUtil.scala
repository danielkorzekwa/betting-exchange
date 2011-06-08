package dk.betex

import dk.betex.api._
import IMarket._
import IBet.BetTypeEnum._
import dk.betex.Market._

/**Provides some bet utilities.
 * 
 * @author korzekwad
 *
 */
object BetUtil {

	/**Calculate avg weighted price.
	 * 
	 * @param bets
	 * @return
	 */
	def avgPrice(bets:List[IBet]):Double = bets.foldLeft(0d)((sum,bet)=> sum + bet.betPrice*bet.betSize) /totalStake(bets)
	
	/** Returns total volume to back and to lay at all prices based on a given list of bets. 
	 *  Prices with zero volume are not returned by this method.
	 * 
	 * @param bets
	 * @return key - price, value (totalToBack,totalToLay)
	 */
	def mapToPrices(bets:List[IBet]):Map[Double,Tuple2[Double,Double]] = {
			val betsByPriceMap = bets.groupBy(b => b.betPrice) 
			betsByPriceMap.mapValues(bets => (totalStake(bets.filter(_.betType==LAY)),totalStake(bets.filter(_.betType==BACK))))
	}
	
	/**Returns total stake for all bets.*/
	def totalStake(bets:List[IBet]) = bets.foldLeft(0d)(_ + _.betSize) 
}