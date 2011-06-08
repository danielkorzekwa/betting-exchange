package dk.betex.api

import IRunnerTradedVolume._

/**This trait represents total traded volume for all prices for a given runner in a market.
 * 
 * @author korzekwad
 *
 */
object IRunnerTradedVolume {

	/**This trait represents total amount matched for the given odds.*/
	trait IPriceTradedVolume {
		val price: Double
		val totalMatchedAmount:Double
	}
}

trait  IRunnerTradedVolume {
	val pricesTradedVolume:List[IPriceTradedVolume]

  /**Returns delta between this and that runner traded volume objects.*/
  def -(that:IRunnerTradedVolume): IRunnerTradedVolume

  /**Returns total traded volume for all prices.*/
  def totalTradedVolume:Double

  /**Returns volume weighed average price for traded volume on all prices.*/
  def avgPrice:Double
}