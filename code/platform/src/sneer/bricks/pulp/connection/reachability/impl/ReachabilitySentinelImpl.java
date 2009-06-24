package sneer.bricks.pulp.connection.reachability.impl;

import static sneer.foundation.environments.Environments.my;
import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.hardware.cpu.threads.Stepper;
import sneer.bricks.network.computers.sockets.accepter.SocketAccepter;
import sneer.bricks.pulp.blinkinglights.BlinkingLights;
import sneer.bricks.pulp.blinkinglights.Light;
import sneer.bricks.pulp.blinkinglights.LightType;
import sneer.bricks.pulp.connection.reachability.ReachabilitySentinel;
import sneer.bricks.pulp.reactive.Signals;
import sneer.foundation.lang.Consumer;

class ReachabilitySentinelImpl implements ReachabilitySentinel {

	private static final int THIRTY_SECONDS = 30*1000;
	private static Light _unreachable;

	private final SocketAccepter _socketAccepter = my(SocketAccepter.class);
	private final BlinkingLights _lights = my(BlinkingLights.class);
	private final Clock _clock = my(Clock.class);	

	private long _lastIncomingSocketTime = _clock.time();

	@SuppressWarnings("unused") private final Object _referenceToAvoidGc;

	{
		_referenceToAvoidGc = my(Signals.class).receive(_socketAccepter.lastAcceptedSocket(), new Consumer<Object>() {@Override public void consume(Object value) {
			updateIncomingSocketTime();
		}});

		_clock.wakeUpEvery(THIRTY_SECONDS, new Stepper() { @Override public boolean step() {
			if (_clock.time() - _lastIncomingSocketTime >= THIRTY_SECONDS)
				_lights.turnOnIfNecessary(unreachableLight(), "Unreachable", "You have not received any incoming socket connections recently. Either none of your contacts are online or your machine is unreachable from the internet.");
			return true;
		}});		
	}

	private void updateIncomingSocketTime() {
		_lastIncomingSocketTime = _clock.time();
		_lights.turnOffIfNecessary(unreachableLight());
	}

	private synchronized Light unreachableLight() {
		if (null == _unreachable)
			_unreachable = _lights.prepare(LightType.WARN);
		return _unreachable;
	}
}