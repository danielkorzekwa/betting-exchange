package dk.betex

import api._
import IRunnerTradedVolume._
import RunnerTradedVolume._

/**This class represents total traded volume for all prices for a given runner in a market.
 * 
 * @author korzekwad
 *
 */
object  RunnerTradedVolume {
	
	class PriceTradedVolume(val price:Double, val totalMatchedAmount:Double) extends IPriceTradedVolume {
		override def toString = "PriceTradedVolume [price=%s, totalMatchedAmount=%s]".format(price,totalMatchedAmount)
	}
}
class RunnerTradedVolume(val pricesTradedVolume:List[IPriceTradedVolume]) extends IRunnerTradedVolume {

	/**Returns delta between this and that runner traded volume objects.*/
	def -(that:IRunnerTradedVolume): IRunnerTradedVolume = {
		val allPrices = (pricesTradedVolume.map(_.price) :::that.pricesTradedVolume.map(_.price)).distinct

		/**Get delta between this and that and previous prices traded volume.*/
		val deltaForUpdatedAndNewTradedVolume = for {
			price <- allPrices
			val newTradedVolume = pricesTradedVolume.find(_.price==price).getOrElse(new PriceTradedVolume(price,0))
			val previousTradedVolume = that.pricesTradedVolume.find(_.price==price).getOrElse(new PriceTradedVolume(price,0))
			val tradedVolumeDelta = new PriceTradedVolume(newTradedVolume.price,(newTradedVolume.totalMatchedAmount-previousTradedVolume.totalMatchedAmount))

			if(tradedVolumeDelta.totalMatchedAmount != 0)
		} yield tradedVolumeDelta

		new RunnerTradedVolume(deltaForUpdatedAndNewTradedVolume)
	}

	/**Returns total traded volume for all prices.*/
	def totalTradedVolume:Double = pricesTradedVolume.foldLeft(0d)((sum,tv)=>sum + tv.totalMatchedAmount)

	/**Returns volume weighed average price for traded volume on all prices.*/
	def avgPrice:Double = pricesTradedVolume.foldLeft(0d)((a,b)=> a+b.price*b.totalMatchedAmount) / totalTradedVolume

	override def toString = "RunnerTradedVolume [pricesTradedVolume=%s]".format(pricesTradedVolume)
}
