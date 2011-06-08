package dk.betex

import org.junit._
import Assert._
import RunnerTradedVolume._

class RunnerTradedVolumeTest {

	/**
	 * Tests scenarios for delta operation.
	 * 
	 * */
	
	@Test def testDeltaBothTradedVolumesAreEmpty {
		val tradedVolumeDelta = new RunnerTradedVolume(Nil) - new RunnerTradedVolume(Nil)
		assertEquals(0, tradedVolumeDelta.pricesTradedVolume.size)
	}
	
	@Test def testDeltaBothRunnerTradedVolumesAreTheSame {
		val newTradedVolume = new PriceTradedVolume(1.84,100) :: new PriceTradedVolume(1.86,200) :: Nil
		val previousTradedVolume = new PriceTradedVolume(1.84,100) :: new PriceTradedVolume(1.86,200) :: Nil

		val tradedVolumeDelta = new RunnerTradedVolume(newTradedVolume) - new RunnerTradedVolume(previousTradedVolume)
		assertEquals(0, tradedVolumeDelta.pricesTradedVolume.size)
	}
	
	@Test def testDeltaNewTradedVolumeIsAvailable {
		val newTradedVolume = new PriceTradedVolume(1.84,100) :: new PriceTradedVolume(1.85,150) :: new PriceTradedVolume(1.86,200) :: Nil
		val previousTradedVolume = new PriceTradedVolume(1.84,100) :: new PriceTradedVolume(1.86,200) :: Nil

		val tradedVolumeDelta = new RunnerTradedVolume(newTradedVolume) - new RunnerTradedVolume(previousTradedVolume)
		assertEquals(1, tradedVolumeDelta.pricesTradedVolume.size)

		assertEquals(1.85,tradedVolumeDelta.pricesTradedVolume(0).price,0)
		assertEquals(150,tradedVolumeDelta.pricesTradedVolume(0).totalMatchedAmount,0)
	}
	
	@Test def testDeltaRunnerTradedVolumesAreUpdated {
		val newTradedVolume = new PriceTradedVolume(1.84,120) :: new PriceTradedVolume(1.86,170) :: Nil
		val previousTradedVolume = new PriceTradedVolume(1.84,100) :: new PriceTradedVolume(1.86,200) :: Nil

		val tradedVolumeDelta = new RunnerTradedVolume(newTradedVolume) - new RunnerTradedVolume(previousTradedVolume)
		assertEquals(2, tradedVolumeDelta.pricesTradedVolume.size)

		assertEquals(1.84,tradedVolumeDelta.pricesTradedVolume(0).price,0)
		assertEquals(20,tradedVolumeDelta.pricesTradedVolume(0).totalMatchedAmount,0)

		assertEquals(1.86,tradedVolumeDelta.pricesTradedVolume(1).price,0)
		assertEquals(-30,tradedVolumeDelta.pricesTradedVolume(1).totalMatchedAmount,0)
	}
	
	@Test def deltaRunnerTradedVolumeIsNotAvailableAnymore {
		val newTradedVolume = new PriceTradedVolume(1.86,200) :: Nil
		val previousTradedVolume = new PriceTradedVolume(1.84,100) :: new PriceTradedVolume(1.86,200) :: Nil

		val tradedVolumeDelta = new RunnerTradedVolume(newTradedVolume) - new RunnerTradedVolume(previousTradedVolume)
		assertEquals(1, tradedVolumeDelta.pricesTradedVolume.size)

		assertEquals(1.84,tradedVolumeDelta.pricesTradedVolume(0).price,0)
		assertEquals(-100,tradedVolumeDelta.pricesTradedVolume(0).totalMatchedAmount,0)
	}
	
	/**
	 * Test scenarios for total traded volume.*
	 */
	@Test def totalTradedVolumeIsZero {
			val tradedVolume = new RunnerTradedVolume(Nil)
			assertEquals(0,tradedVolume.totalTradedVolume,0)
	}
	
	@Test def totalTradedVolume {
			val tradedVolume = new RunnerTradedVolume(new PriceTradedVolume(1.84,100) :: new PriceTradedVolume(1.86,200) :: Nil)
			assertEquals(300,tradedVolume.totalTradedVolume,0)
	}
	
	/**
	 * Test scenarios for avg price.*
	 */
	@Test def testAvgPriceNoTradedVolume {
		val tradedVolume = new RunnerTradedVolume(Nil)
			assertEquals(Double.NaN,tradedVolume.avgPrice,0)
	}
	
	@Test def testAvgPriceInTheMiddle {
		val tradedVolume = new RunnerTradedVolume(new PriceTradedVolume(1.5,100) :: new PriceTradedVolume(2,100) :: Nil)
			assertEquals(1.75,tradedVolume.avgPrice,0)
	}
	
	@Test def testAvgPrice{
		val tradedVolume = new RunnerTradedVolume(new PriceTradedVolume(1.5,100) :: new PriceTradedVolume(2,200) :: Nil)
			assertEquals(1.833,tradedVolume.avgPrice,0.001)
	}
}