package sneer.bricks.pulp.log.workers.sysout.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.pulp.log.LogWorker;
import sneer.bricks.pulp.log.Logger;
import sneer.bricks.pulp.log.formatter.LogFormatter;
import sneer.bricks.pulp.log.workers.sysout.SysoutLogWorker;

public class SysoutLogWorkerImpl implements SysoutLogWorker {

	SysoutLogWorkerImpl(){
		my(Logger.class).setDelegate(new SysoutLogWorker());
	}
	
	class SysoutLogWorker implements LogWorker{
		@Override
		public void log(Throwable throwable, String message, Object... messageInsets){
			logMessage(message, messageInsets);
			logThrowable(throwable);
			System.out.println();
		}

		private void logThrowable(Throwable throwable) {
			if (throwable==null) return;
			throwable.printStackTrace(System.out);
		}

		private void logMessage(String message, Object... messageInsets) {
			if (message==null) return;
			System.out.print(my(LogFormatter.class).format(message, messageInsets));
		}
		
		@Override public void log(String message, Object... messageInsets) {  log(null, message, messageInsets); }
		@Override public void log(Throwable throwable) { log(throwable, null); }
		@Override public void logShort(Throwable throwable, String message, Object... messageInsets) { log(null, throwable.getMessage(), messageInsets);}
	}
}