package dk.betex

import org.junit._
import Assert._
import dk.betex.api._
import IBet.BetTypeEnum._
import IBet.BetStatusEnum._

class BetTest {

	/**
	 * Create bet scenarios.
	 **/
	
	@Test def testCreateBet{
		new Bet(10,123,2,1.01,BACK,U,1,11,1000,None)
		new Bet(10,123,2,1.5,BACK,M,1,11,1000,None)
		new Bet(10,123,2,1000,BACK,M,1,11,1000,None)
		new Bet(10,123,100,3,LAY,U,1,11,1000,None)
	}
	
	@Test(expected=classOf[IllegalArgumentException]) def testCreateBetPriceLessThanMin{
		new Bet(10,123,2,1,BACK,U,1,11,1000,None)
	}
	
	@Test(expected=classOf[IllegalArgumentException]) def testCreateBetPriceMoreThanMax{
		new Bet(10,123,2,1001,BACK,U,1,11,1000,None)
	}
	
	/**
	 * Bets matched scenarios.
	 **/
	
	@Test def testMatchBetBackWithLay {
		val firstBet = new Bet(10,122,10,2,BACK,U,1,11,1000,None)
		val secondBet = new Bet(11,123,10,2,LAY,U,1,11,1001,None)
		
		val matchResult = firstBet.matchBet(secondBet)
		
		assertEquals(2,matchResult.size)
		
		assertEquals(10,matchResult(0).betId)
		assertEquals(122,matchResult(0).userId)
		assertEquals(10,matchResult(0).betSize,0)
		assertEquals(2,matchResult(0).betPrice,0)
		assertEquals(BACK,matchResult(0).betType)
		assertEquals(M,matchResult(0).betStatus)
		assertEquals(1,matchResult(0).marketId)
		assertEquals(11,matchResult(0).runnerId)
		
		assertEquals(11,matchResult(1).betId)
		assertEquals(123,matchResult(1).userId)
		assertEquals(10,matchResult(1).betSize,0)
		assertEquals(2,matchResult(1).betPrice,0)
		assertEquals(LAY,matchResult(1).betType)
		assertEquals(M,matchResult(1).betStatus)
		assertEquals(1,matchResult(1).marketId)
		assertEquals(11,matchResult(1).runnerId)
		
	}
	
	@Test def testMatchBetBackWithLayDifferentPrice {
		val firstBet = new Bet(10,122,10,1.5,BACK,U,1,11,1000,None)
		val secondBet = new Bet(11,123,10,2,LAY,U,1,11,1001,None)
		
		val matchResult = firstBet.matchBet(secondBet)
		
		assertEquals(2,matchResult.size)
		
		assertEquals(10,matchResult(0).betId)
		assertEquals(122,matchResult(0).userId)
		assertEquals(10,matchResult(0).betSize,0)
		assertEquals(2,matchResult(0).betPrice,0)
		assertEquals(BACK,matchResult(0).betType)
		assertEquals(M,matchResult(0).betStatus)
		assertEquals(1,matchResult(0).marketId)
		assertEquals(11,matchResult(0).runnerId)
		
		assertEquals(11,matchResult(1).betId)
		assertEquals(123,matchResult(1).userId)
		assertEquals(10,matchResult(1).betSize,0)
		assertEquals(2,matchResult(1).betPrice,0)
		assertEquals(LAY,matchResult(1).betType)
		assertEquals(M,matchResult(1).betStatus)
		assertEquals(1,matchResult(1).marketId)
		assertEquals(11,matchResult(1).runnerId)
		
	}
	
	@Test def testMatchBetBigBackWithLay {
		val firstBet = new Bet(10,122,12,2,BACK,U,1,11,1000,None)
		val secondBet = new Bet(11,123,10,2,LAY,U,1,11,1001,None)
		
		val matchResult = firstBet.matchBet(secondBet)
		
		assertEquals(3,matchResult.size)
		
		assertEquals(10,matchResult(0).betId)
		assertEquals(122,matchResult(0).userId)
		assertEquals(10,matchResult(0).betSize,0)
		assertEquals(2,matchResult(0).betPrice,0)
		assertEquals(BACK,matchResult(0).betType)
		assertEquals(M,matchResult(0).betStatus)
		assertEquals(1,matchResult(0).marketId)
		assertEquals(11,matchResult(0).runnerId)
		
		assertEquals(10,matchResult(1).betId)
		assertEquals(122,matchResult(1).userId)
		assertEquals(2,matchResult(1).betSize,0)
		assertEquals(2,matchResult(1).betPrice,0)
		assertEquals(BACK,matchResult(1).betType)
		assertEquals(U,matchResult(1).betStatus)
		assertEquals(1,matchResult(1).marketId)
		assertEquals(11,matchResult(1).runnerId)
		
		assertEquals(11,matchResult(2).betId)
		assertEquals(123,matchResult(2).userId)
		assertEquals(10,matchResult(2).betSize,0)
		assertEquals(2,matchResult(2).betPrice,0)
		assertEquals(LAY,matchResult(2).betType)
		assertEquals(M,matchResult(2).betStatus)
		assertEquals(1,matchResult(2).marketId)
		assertEquals(11,matchResult(2).runnerId)
		
	}
	
	@Test def testMatchBetBigBackWithLayDifferentPrice {
		val firstBet = new Bet(10,122,12,2,BACK,U,1,11,1000,None)
		val secondBet = new Bet(11,123,10,3,LAY,U,1,11,1001,None)
		
		val matchResult = firstBet.matchBet(secondBet)
		
		assertEquals(3,matchResult.size)
		
		assertEquals(10,matchResult(0).betId)
		assertEquals(122,matchResult(0).userId)
		assertEquals(10,matchResult(0).betSize,0)
		assertEquals(3,matchResult(0).betPrice,0)
		assertEquals(BACK,matchResult(0).betType)
		assertEquals(M,matchResult(0).betStatus)
		assertEquals(1,matchResult(0).marketId)
		assertEquals(11,matchResult(0).runnerId)
		
		assertEquals(10,matchResult(1).betId)
		assertEquals(122,matchResult(1).userId)
		assertEquals(2,matchResult(1).betSize,0)
		assertEquals(2,matchResult(1).betPrice,0)
		assertEquals(BACK,matchResult(1).betType)
		assertEquals(U,matchResult(1).betStatus)
		assertEquals(1,matchResult(1).marketId)
		assertEquals(11,matchResult(1).runnerId)
		
		assertEquals(11,matchResult(2).betId)
		assertEquals(123,matchResult(2).userId)
		assertEquals(10,matchResult(2).betSize,0)
		assertEquals(3,matchResult(2).betPrice,0)
		assertEquals(LAY,matchResult(2).betType)
		assertEquals(M,matchResult(2).betStatus)
		assertEquals(1,matchResult(2).marketId)
		assertEquals(11,matchResult(2).runnerId)
		
	}
	
	@Test def testMatchBetBackWithBigLay {
		val firstBet = new Bet(10,122,10,2,BACK,U,1,11,1000,None)
		val secondBet = new Bet(11,123,15,2,LAY,U,1,11,1001,None)
		
		val matchResult = firstBet.matchBet(secondBet)
		
		assertEquals(3,matchResult.size)
		
		assertEquals(10,matchResult(0).betId)
		assertEquals(122,matchResult(0).userId)
		assertEquals(10,matchResult(0).betSize,0)
		assertEquals(2,matchResult(0).betPrice,0)
		assertEquals(BACK,matchResult(0).betType)
		assertEquals(M,matchResult(0).betStatus)
		assertEquals(1,matchResult(0).marketId)
		assertEquals(11,matchResult(0).runnerId)
		
			assertEquals(11,matchResult(1).betId)
		assertEquals(123,matchResult(1).userId)
		assertEquals(10,matchResult(1).betSize,0)
		assertEquals(2,matchResult(1).betPrice,0)
		assertEquals(LAY,matchResult(1).betType)
		assertEquals(M,matchResult(1).betStatus)
		assertEquals(1,matchResult(1).marketId)
		assertEquals(11,matchResult(1).runnerId)
		
		assertEquals(11,matchResult(2).betId)
		assertEquals(123,matchResult(2).userId)
		assertEquals(5,matchResult(2).betSize,0)
		assertEquals(2,matchResult(2).betPrice,0)
		assertEquals(LAY,matchResult(2).betType)
		assertEquals(U,matchResult(2).betStatus)
		assertEquals(1,matchResult(2).marketId)
		assertEquals(11,matchResult(2).runnerId)
		
	}
	
	@Test def testMatchBetBackWithBigLayDifferentPrice {
		val firstBet = new Bet(10,122,10,2,BACK,U,1,11,1000,None)
		val secondBet = new Bet(11,123,15,3,LAY,U,1,11,1001,None)
		
		val matchResult = firstBet.matchBet(secondBet)
		
		assertEquals(3,matchResult.size)
		
		assertEquals(10,matchResult(0).betId)
		assertEquals(122,matchResult(0).userId)
		assertEquals(10,matchResult(0).betSize,0)
		assertEquals(3,matchResult(0).betPrice,0)
		assertEquals(BACK,matchResult(0).betType)
		assertEquals(M,matchResult(0).betStatus)
		assertEquals(1,matchResult(0).marketId)
		assertEquals(11,matchResult(0).runnerId)
		
			assertEquals(11,matchResult(1).betId)
		assertEquals(123,matchResult(1).userId)
		assertEquals(10,matchResult(1).betSize,0)
		assertEquals(3,matchResult(1).betPrice,0)
		assertEquals(LAY,matchResult(1).betType)
		assertEquals(M,matchResult(1).betStatus)
		assertEquals(1,matchResult(1).marketId)
		assertEquals(11,matchResult(1).runnerId)
		
		assertEquals(11,matchResult(2).betId)
		assertEquals(123,matchResult(2).userId)
		assertEquals(5,matchResult(2).betSize,0)
		assertEquals(3,matchResult(2).betPrice,0)
		assertEquals(LAY,matchResult(2).betType)
		assertEquals(U,matchResult(2).betStatus)
		assertEquals(1,matchResult(2).marketId)
		assertEquals(11,matchResult(2).runnerId)
		
	}
	
	@Test def testMatchBetLayWithBack {
		val firstBet = new Bet(10,122,10,2,LAY,U,1,11,1000,None)
		val secondBet = new Bet(11,123,10,2,BACK,U,1,11,1001,None)
		
		val matchResult = firstBet.matchBet(secondBet)
		
		assertEquals(2,matchResult.size)
		
		assertEquals(10,matchResult(0).betId)
		assertEquals(122,matchResult(0).userId)
		assertEquals(10,matchResult(0).betSize,0)
		assertEquals(2,matchResult(0).betPrice,0)
		assertEquals(LAY,matchResult(0).betType)
		assertEquals(M,matchResult(0).betStatus)
		assertEquals(1,matchResult(0).marketId)
		assertEquals(11,matchResult(0).runnerId)
		
		assertEquals(11,matchResult(1).betId)
		assertEquals(123,matchResult(1).userId)
		assertEquals(10,matchResult(1).betSize,0)
		assertEquals(2,matchResult(1).betPrice,0)
		assertEquals(BACK,matchResult(1).betType)
		assertEquals(M,matchResult(1).betStatus)
		assertEquals(1,matchResult(1).marketId)
		assertEquals(11,matchResult(1).runnerId)
		
	}
	
		@Test def testMatchBetLayWithBackDifferentPrice {
		val firstBet = new Bet(10,122,10,2,LAY,U,1,11,1000,None)
		val secondBet = new Bet(11,123,10,1.5,BACK,U,1,11,1001,None)
		
		val matchResult = firstBet.matchBet(secondBet)
		
		assertEquals(2,matchResult.size)
		
		assertEquals(10,matchResult(0).betId)
		assertEquals(122,matchResult(0).userId)
		assertEquals(10,matchResult(0).betSize,0)
		assertEquals(1.5,matchResult(0).betPrice,0)
		assertEquals(LAY,matchResult(0).betType)
		assertEquals(M,matchResult(0).betStatus)
		assertEquals(1,matchResult(0).marketId)
		assertEquals(11,matchResult(0).runnerId)
		
		assertEquals(11,matchResult(1).betId)
		assertEquals(123,matchResult(1).userId)
		assertEquals(10,matchResult(1).betSize,0)
		assertEquals(1.5,matchResult(1).betPrice,0)
		assertEquals(BACK,matchResult(1).betType)
		assertEquals(M,matchResult(1).betStatus)
		assertEquals(1,matchResult(1).marketId)
		assertEquals(11,matchResult(1).runnerId)
		
	}
	
	@Test def testMatchBetBigLayWithBack {
		val firstBet = new Bet(10,122,14,2,LAY,U,1,11,1000,None)
		val secondBet = new Bet(11,123,10,2,BACK,U,1,11,1001,None)
		
		val matchResult = firstBet.matchBet(secondBet)
		
		assertEquals(3,matchResult.size)
		
		assertEquals(10,matchResult(0).betId)
		assertEquals(122,matchResult(0).userId)
		assertEquals(10,matchResult(0).betSize,0)
		assertEquals(2,matchResult(0).betPrice,0)
		assertEquals(LAY,matchResult(0).betType)
		assertEquals(M,matchResult(0).betStatus)
		assertEquals(1,matchResult(0).marketId)
		assertEquals(11,matchResult(0).runnerId)
		
		assertEquals(10,matchResult(1).betId)
		assertEquals(122,matchResult(1).userId)
		assertEquals(4,matchResult(1).betSize,0)
		assertEquals(2,matchResult(1).betPrice,0)
		assertEquals(LAY,matchResult(1).betType)
		assertEquals(U,matchResult(1).betStatus)
		assertEquals(1,matchResult(1).marketId)
		assertEquals(11,matchResult(1).runnerId)
		
		assertEquals(11,matchResult(2).betId)
		assertEquals(123,matchResult(2).userId)
		assertEquals(10,matchResult(2).betSize,0)
		assertEquals(2,matchResult(2).betPrice,0)
		assertEquals(BACK,matchResult(2).betType)
		assertEquals(M,matchResult(2).betStatus)
		assertEquals(1,matchResult(2).marketId)
		assertEquals(11,matchResult(2).runnerId)
		
	}
	
	@Test def testMatchBetBigLayWithBackDifferentPrice {
		val firstBet = new Bet(10,122,14,2,LAY,U,1,11,1000,None)
		val secondBet = new Bet(11,123,10,1.5,BACK,U,1,11,1001,None)
		
		val matchResult = firstBet.matchBet(secondBet)
		
		assertEquals(3,matchResult.size)
		
		assertEquals(10,matchResult(0).betId)
		assertEquals(122,matchResult(0).userId)
		assertEquals(10,matchResult(0).betSize,0)
		assertEquals(1.5,matchResult(0).betPrice,0)
		assertEquals(LAY,matchResult(0).betType)
		assertEquals(M,matchResult(0).betStatus)
		assertEquals(1,matchResult(0).marketId)
		assertEquals(11,matchResult(0).runnerId)
		
		assertEquals(10,matchResult(1).betId)
		assertEquals(122,matchResult(1).userId)
		assertEquals(4,matchResult(1).betSize,0)
		assertEquals(2,matchResult(1).betPrice,0)
		assertEquals(LAY,matchResult(1).betType)
		assertEquals(U,matchResult(1).betStatus)
		assertEquals(1,matchResult(1).marketId)
		assertEquals(11,matchResult(1).runnerId)
		
		assertEquals(11,matchResult(2).betId)
		assertEquals(123,matchResult(2).userId)
		assertEquals(10,matchResult(2).betSize,0)
		assertEquals(1.5,matchResult(2).betPrice,0)
		assertEquals(BACK,matchResult(2).betType)
		assertEquals(M,matchResult(2).betStatus)
		assertEquals(1,matchResult(2).marketId)
		assertEquals(11,matchResult(2).runnerId)
		
	}
	
	@Test def testMatchBetLayWithBigBack {
		val firstBet = new Bet(10,122,10,2,LAY,U,1,11,1000,None)
		val secondBet = new Bet(11,123,15,2,BACK,U,1,11,1001,None)
		
		val matchResult = firstBet.matchBet(secondBet)
		
		assertEquals(3,matchResult.size)
		
		assertEquals(10,matchResult(0).betId)
		assertEquals(122,matchResult(0).userId)
		assertEquals(10,matchResult(0).betSize,0)
		assertEquals(2,matchResult(0).betPrice,0)
		assertEquals(LAY,matchResult(0).betType)
		assertEquals(M,matchResult(0).betStatus)
		assertEquals(1,matchResult(0).marketId)
		assertEquals(11,matchResult(0).runnerId)
		
		assertEquals(11,matchResult(1).betId)
		assertEquals(123,matchResult(1).userId)
		assertEquals(10,matchResult(1).betSize,0)
		assertEquals(2,matchResult(1).betPrice,0)
		assertEquals(BACK,matchResult(1).betType)
		assertEquals(M,matchResult(1).betStatus)
		assertEquals(1,matchResult(1).marketId)
		assertEquals(11,matchResult(1).runnerId)
		
		assertEquals(11,matchResult(2).betId)
		assertEquals(123,matchResult(2).userId)
		assertEquals(5,matchResult(2).betSize,0)
		assertEquals(2,matchResult(2).betPrice,0)
		assertEquals(BACK,matchResult(2).betType)
		assertEquals(U,matchResult(2).betStatus)
		assertEquals(1,matchResult(2).marketId)
		assertEquals(11,matchResult(2).runnerId)
		
	}
	
	@Test def testMatchBetLayWithBigBackDifferentPrice {
		val firstBet = new Bet(10,122,10,2,LAY,U,1,11,1001,None)
		val secondBet = new Bet(11,123,15,1.5,BACK,U,1,11,1000,None)
		
		val matchResult = firstBet.matchBet(secondBet)
		
		assertEquals(3,matchResult.size)
		
		assertEquals(10,matchResult(0).betId)
		assertEquals(122,matchResult(0).userId)
		assertEquals(10,matchResult(0).betSize,0)
		assertEquals(1.5,matchResult(0).betPrice,0)
		assertEquals(LAY,matchResult(0).betType)
		assertEquals(M,matchResult(0).betStatus)
		assertEquals(1,matchResult(0).marketId)
		assertEquals(11,matchResult(0).runnerId)
		
		assertEquals(11,matchResult(1).betId)
		assertEquals(123,matchResult(1).userId)
		assertEquals(10,matchResult(1).betSize,0)
		assertEquals(1.5,matchResult(1).betPrice,0)
		assertEquals(BACK,matchResult(1).betType)
		assertEquals(M,matchResult(1).betStatus)
		assertEquals(1,matchResult(1).marketId)
		assertEquals(11,matchResult(1).runnerId)
		
		assertEquals(11,matchResult(2).betId)
		assertEquals(123,matchResult(2).userId)
		assertEquals(5,matchResult(2).betSize,0)
		assertEquals(1.5,matchResult(2).betPrice,0)
		assertEquals(BACK,matchResult(2).betType)
		assertEquals(U,matchResult(2).betStatus)
		assertEquals(1,matchResult(2).marketId)
		assertEquals(11,matchResult(2).runnerId)
		
	}
	
	
	/**
	 * Bets not matched scenarios.
	 **/
	
	@Test def testMatchBetTwoBackBets {
		val firstBet = new Bet(10,122,10,2,BACK,U,1,11,1001,None)
		val secondBet = new Bet(11,123,10,2,BACK,U,1,11,1000,None)
		
		val matchResult = firstBet.matchBet(secondBet)
		
		assertEquals(2,matchResult.size)
		
		assertEquals(10,matchResult(0).betId)
		assertEquals(122,matchResult(0).userId)
		assertEquals(10,matchResult(0).betSize,0)
		assertEquals(2,matchResult(0).betPrice,0)
		assertEquals(BACK,matchResult(0).betType)
		assertEquals(U,matchResult(0).betStatus)
		assertEquals(1,matchResult(0).marketId)
		assertEquals(11,matchResult(0).runnerId)
		
		assertEquals(11,matchResult(1).betId)
		assertEquals(123,matchResult(1).userId)
		assertEquals(10,matchResult(1).betSize,0)
		assertEquals(2,matchResult(1).betPrice,0)
		assertEquals(BACK,matchResult(1).betType)
		assertEquals(U,matchResult(1).betStatus)
		assertEquals(1,matchResult(1).marketId)
		assertEquals(11,matchResult(1).runnerId)
		
	}
	
	@Test def testMatchBetTwoLayBets {
		val firstBet = new Bet(10,122,10,2,LAY,U,1,11,1001,None)
		val secondBet = new Bet(11,123,10,2,LAY,U,1,11,1000,None)
		
		val matchResult = firstBet.matchBet(secondBet)
		
		assertEquals(2,matchResult.size)
		
		assertEquals(10,matchResult(0).betId)
		assertEquals(122,matchResult(0).userId)
		assertEquals(10,matchResult(0).betSize,0)
		assertEquals(2,matchResult(0).betPrice,0)
		assertEquals(LAY,matchResult(0).betType)
		assertEquals(U,matchResult(0).betStatus)
		assertEquals(1,matchResult(0).marketId)
		assertEquals(11,matchResult(0).runnerId)
		
		assertEquals(11,matchResult(1).betId)
		assertEquals(123,matchResult(1).userId)
		assertEquals(10,matchResult(1).betSize,0)
		assertEquals(2,matchResult(1).betPrice,0)
		assertEquals(LAY,matchResult(1).betType)
		assertEquals(U,matchResult(1).betStatus)
		assertEquals(1,matchResult(1).marketId)
		assertEquals(11,matchResult(1).runnerId)
		
	}
	
	@Test def testMatchBetDifferentMarketId {
		val firstBet = new Bet(10,122,10,2,LAY,U,1,11,1001,None)
		val secondBet = new Bet(11,123,10,2,BACK,U,2,11,1000,None)
		
		val matchResult = firstBet.matchBet(secondBet)
		
		assertEquals(2,matchResult.size)
		
		assertEquals(10,matchResult(0).betId)
		assertEquals(122,matchResult(0).userId)
		assertEquals(10,matchResult(0).betSize,0)
		assertEquals(2,matchResult(0).betPrice,0)
		assertEquals(LAY,matchResult(0).betType)
		assertEquals(U,matchResult(0).betStatus)
		assertEquals(1,matchResult(0).marketId)
		assertEquals(11,matchResult(0).runnerId)
		
		assertEquals(11,matchResult(1).betId)
		assertEquals(123,matchResult(1).userId)
		assertEquals(10,matchResult(1).betSize,0)
		assertEquals(2,matchResult(1).betPrice,0)
		assertEquals(BACK,matchResult(1).betType)
		assertEquals(U,matchResult(1).betStatus)
		assertEquals(2,matchResult(1).marketId)
		assertEquals(11,matchResult(1).runnerId)
		
	}
	
	@Test def testMatchBetDifferentRunnerId {
		val firstBet = new Bet(10,122,10,2,LAY,U,1,11,1001,None)
		val secondBet = new Bet(11,123,10,2,BACK,U,1,12,1000,None)
		
		val matchResult = firstBet.matchBet(secondBet)
		
		assertEquals(2,matchResult.size)
		
		assertEquals(10,matchResult(0).betId)
		assertEquals(122,matchResult(0).userId)
		assertEquals(10,matchResult(0).betSize,0)
		assertEquals(2,matchResult(0).betPrice,0)
		assertEquals(LAY,matchResult(0).betType)
		assertEquals(U,matchResult(0).betStatus)
		assertEquals(1,matchResult(0).marketId)
		assertEquals(11,matchResult(0).runnerId)
		
		assertEquals(11,matchResult(1).betId)
		assertEquals(123,matchResult(1).userId)
		assertEquals(10,matchResult(1).betSize,0)
		assertEquals(2,matchResult(1).betPrice,0)
		assertEquals(BACK,matchResult(1).betType)
		assertEquals(U,matchResult(1).betStatus)
		assertEquals(1,matchResult(1).marketId)
		assertEquals(12,matchResult(1).runnerId)
		
	}
	@Test def testMatchBetFirstBackSecondLayPriceNotMatching {
		val firstBet = new Bet(10,122,10,3,BACK,U,1,11,1001,None)
		val secondBet = new Bet(11,123,10,2,LAY,U,1,11,1000,None)
		
		val matchResult = firstBet.matchBet(secondBet)
		
		assertEquals(2,matchResult.size)
		
		assertEquals(10,matchResult(0).betId)
		assertEquals(122,matchResult(0).userId)
		assertEquals(10,matchResult(0).betSize,0)
		assertEquals(3,matchResult(0).betPrice,0)
		assertEquals(BACK,matchResult(0).betType)
		assertEquals(U,matchResult(0).betStatus)
		assertEquals(1,matchResult(0).marketId)
		assertEquals(11,matchResult(0).runnerId)
		
		assertEquals(11,matchResult(1).betId)
		assertEquals(123,matchResult(1).userId)
		assertEquals(10,matchResult(1).betSize,0)
		assertEquals(2,matchResult(1).betPrice,0)
		assertEquals(LAY,matchResult(1).betType)
		assertEquals(U,matchResult(1).betStatus)
		assertEquals(1,matchResult(1).marketId)
		assertEquals(11,matchResult(1).runnerId)
		
	}
	
	@Test def testMatchBetFirstLaySecondBackPriceNotMatching {
		val firstBet = new Bet(10,122,10,2,LAY,U,1,11,1000,None)
		val secondBet = new Bet(11,123,10,3,BACK,U,1,11,1001,None)
		
		val matchResult = firstBet.matchBet(secondBet)
		
		assertEquals(2,matchResult.size)
		
		assertEquals(10,matchResult(0).betId)
		assertEquals(122,matchResult(0).userId)
		assertEquals(10,matchResult(0).betSize,0)
		assertEquals(2,matchResult(0).betPrice,0)
		assertEquals(LAY,matchResult(0).betType)
		assertEquals(U,matchResult(0).betStatus)
		assertEquals(1,matchResult(0).marketId)
		assertEquals(11,matchResult(0).runnerId)
		
		assertEquals(11,matchResult(1).betId)
		assertEquals(123,matchResult(1).userId)
		assertEquals(10,matchResult(1).betSize,0)
		assertEquals(3,matchResult(1).betPrice,0)
		assertEquals(BACK,matchResult(1).betType)
		assertEquals(U,matchResult(1).betStatus)
		assertEquals(1,matchResult(1).marketId)
		assertEquals(11,matchResult(1).runnerId)
		
	}
	
	@Test def testMatchBetFirstBetAlreadyMatched{
		val firstBet = new Bet(10,122,10,2,LAY,M,1,11,1000,None)
		val secondBet = new Bet(11,123,10,2,BACK,U,1,11,1001,None)
		
		val matchResult = firstBet.matchBet(secondBet)
		
		assertEquals(2,matchResult.size)
		
		assertEquals(10,matchResult(0).betId)
		assertEquals(122,matchResult(0).userId)
		assertEquals(10,matchResult(0).betSize,0)
		assertEquals(2,matchResult(0).betPrice,0)
		assertEquals(LAY,matchResult(0).betType)
		assertEquals(M,matchResult(0).betStatus)
		assertEquals(1,matchResult(0).marketId)
		assertEquals(11,matchResult(0).runnerId)
		
		assertEquals(11,matchResult(1).betId)
		assertEquals(123,matchResult(1).userId)
		assertEquals(10,matchResult(1).betSize,0)
		assertEquals(2,matchResult(1).betPrice,0)
		assertEquals(BACK,matchResult(1).betType)
		assertEquals(U,matchResult(1).betStatus)
		assertEquals(1,matchResult(1).marketId)
		assertEquals(11,matchResult(1).runnerId)
		
	}
	
	@Test def testMatchBetSecondBetAlreadyMatched{
		val firstBet = new Bet(10,122,10,2,LAY,U,1,11,1001,None)
		val secondBet = new Bet(11,123,10,2,BACK,M,1,11,1000,None)
		
		val matchResult = firstBet.matchBet(secondBet)
		
		assertEquals(2,matchResult.size)
		
		assertEquals(10,matchResult(0).betId)
		assertEquals(122,matchResult(0).userId)
		assertEquals(10,matchResult(0).betSize,0)
		assertEquals(2,matchResult(0).betPrice,0)
		assertEquals(LAY,matchResult(0).betType)
		assertEquals(U,matchResult(0).betStatus)
		assertEquals(1,matchResult(0).marketId)
		assertEquals(11,matchResult(0).runnerId)
		
		assertEquals(11,matchResult(1).betId)
		assertEquals(123,matchResult(1).userId)
		assertEquals(10,matchResult(1).betSize,0)
		assertEquals(2,matchResult(1).betPrice,0)
		assertEquals(BACK,matchResult(1).betType)
		assertEquals(M,matchResult(1).betStatus)
		assertEquals(1,matchResult(1).marketId)
		assertEquals(11,matchResult(1).runnerId)
		
	}
	
	@Test def testMatchBetBothBetsAlreadyMatched{
		val firstBet = new Bet(10,122,10,2,LAY,M,1,11,1001,None)
		val secondBet = new Bet(11,123,10,2,BACK,M,1,11,1000,None)
		
		val matchResult = firstBet.matchBet(secondBet)
		
		assertEquals(2,matchResult.size)
		
		assertEquals(10,matchResult(0).betId)
		assertEquals(122,matchResult(0).userId)
		assertEquals(10,matchResult(0).betSize,0)
		assertEquals(2,matchResult(0).betPrice,0)
		assertEquals(LAY,matchResult(0).betType)
		assertEquals(M,matchResult(0).betStatus)
		assertEquals(1,matchResult(0).marketId)
		assertEquals(11,matchResult(0).runnerId)
		
		assertEquals(11,matchResult(1).betId)
		assertEquals(123,matchResult(1).userId)
		assertEquals(10,matchResult(1).betSize,0)
		assertEquals(2,matchResult(1).betPrice,0)
		assertEquals(BACK,matchResult(1).betType)
		assertEquals(M,matchResult(1).betStatus)
		assertEquals(1,matchResult(1).marketId)
		assertEquals(11,matchResult(1).runnerId)
		
	}
}