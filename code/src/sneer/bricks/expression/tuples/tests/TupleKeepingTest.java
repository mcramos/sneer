package sneer.bricks.expression.tuples.tests;

import static sneer.foundation.environments.Environments.my;

import org.junit.Test;

import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.expression.tuples.dispatcher.TupleDispatcher;
import sneer.bricks.expression.tuples.floodcache.FloodedTupleCache;
import sneer.bricks.expression.tuples.kept.KeptTuples;
import sneer.bricks.hardware.cpu.lang.contracts.WeakContract;
import sneer.bricks.software.folderconfig.testsupport.BrickTestBase;
import sneer.foundation.lang.Consumer;

public class TupleKeepingTest extends BrickTestBase {

	private int _notificationCounter;
	
	
	@Test //(timeout = 5000)
	public void tuplesLimitAmount() {

		@SuppressWarnings("unused")
		WeakContract contract = subject().addSubscription(KeptTuple.class, new Consumer<KeptTuple>() { @Override public void consume(KeptTuple ignored) {
			_notificationCounter++;
		}});
		
		subject().keep(KeptTuple.class);
		KeptTuple tuple = new KeptTuple(1);
		subject().add(tuple);
		flushCache();
		subject().add(tuple);
		
		my(TupleDispatcher.class).waitForAllDispatchingToFinish();

		assertEquals(1, my(KeptTuples.class).all().length);
		assertEquals(1, _notificationCounter);
	}


	private TupleSpace subject() {
		return my(TupleSpace.class);
	}


	private void flushCache() {
		int cacheSize = my(FloodedTupleCache.class).maxSize();
		publishTestTuples(cacheSize);
	}


	private void publishTestTuples(int amount) {
		for (int i = 0; i < amount; i++)
			subject().add(new TestTuple(i));
	}
	
}


