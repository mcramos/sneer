package spikes.sandro.summit.register.impl;

import basis.lang.Consumer;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import spikes.sandro.summit.register.SimpleRegister;
import static basis.environments.Environments.my;

class SimpleRegisterImpl implements SimpleRegister{

	private final Register<String> _register = my(Signals.class).newRegister("Olá Mundo!");
	
	@Override
	public Signal<String> output(){
		return _register.output();
	}

	@Override
	public Consumer<String> setter() {
		return _register.setter();
	}
}
