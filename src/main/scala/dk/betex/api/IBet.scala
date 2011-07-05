package dk.betex.api

import IBet._
/** This trait represents a bet on a betting exchange.
 * 
 * @author korzekwad
 *
 */
object IBet {
	
	object BetTypeEnum extends Enumeration{
		type BetTypeEnum = Value
		val BACK = Value("BACK")
		val LAY = Value("LAY")
		
		 override def toString() = BetTypeEnum.values.mkString("BetTypeEnum [",", ","]")
	}

	object BetStatusEnum extends Enumeration{
		type BetStatusEnum = Value
		val M = Value("M") //matched
		val U = Value("U") //unmatched
		
		 override def toString() = BetStatusEnum.values.mkString("BetStatusEnum [",", ","]")
	}
	
}
trait IBet {

	val betId:Long
	val userId: Long
	val betSize:Double
	val betPrice:Double
	val betType:BetTypeEnum.BetTypeEnum
	val betStatus:BetStatusEnum.BetStatusEnum
	val marketId:Long
	val runnerId:Long
	val placedDate:Long
	/**None if bet is not matched.*/
	val matchedDate:Option[Long]
	
	
	/**Match two bets. Bet that the matchedBet method is executed on is always matched at the best available price. 
	 * Examples: backBetWithPrice2.matchBet(layBetWithPrice3) = matched price 3,layBetWithPrice3.matchBet(backBetWithPrice2) = matched price 2. 
	 * 
	 * @param bet Bet to be matched with.
	 * @return Result of bets matching. In the general form it consists of 4 elements. 
	 * 1 - matched portion of first bet, 2 - unmatched portion of first bet, 3 - matched portion of second bet, 4 - unmatched portion of second bet.
	 *   
	 * */
	def matchBet(bet:IBet):List[IBet] 
}