package sneer.pulp.reactive.tests;

import static sneer.commons.environments.Environments.my;

import org.junit.Test;

import sneer.brickness.testsupport.BrickTest;
import sneer.commons.lang.Functor;
import sneer.hardware.cpu.lang.Consumer;
import sneer.pulp.reactive.Reception;
import sneer.pulp.reactive.Register;
import sneer.pulp.reactive.Signal;
import sneer.pulp.reactive.Signals;

public class SignalsTest extends BrickTest {

	private final Signals _subject = my(Signals.class);

	@Test
	public void adapt() {
		Register<Integer> register = my(Signals.class).newRegister(1);

		Signal<String> output = _subject.adapt(register.output(), new Functor<Integer, String>() { @Override public String evaluate(Integer value) {
			return value == 1 ? "one" : "something else";
		}});

		assertEquals("one", output.currentValue());
		register.setter().consume(42);
		assertEquals("something else", output.currentValue());
	}

	@Test
	public void adaptSignal() {
		Register<Integer> chooser = my(Signals.class).newRegister(1);
		final Register<String> register1 = my(Signals.class).newRegister("1 foo");
		final Register<String> register2 = my(Signals.class).newRegister("2 foo");
		
		Signal<String> output = _subject.adaptSignal(chooser.output(), new Functor<Integer, Signal<String>>() { @Override public Signal<String> evaluate(Integer value) {
			return value == 1 ? register1.output() : register2.output();
		}});
		
		assertEquals("1 foo", output.currentValue());
		chooser.setter().consume(2);
		assertEquals("2 foo", output.currentValue());
		register2.setter().consume("2 bar");
		assertEquals("2 bar", output.currentValue());
	}

	@Test
	public void receive() {
		final StringBuilder received = new StringBuilder();

		Register<String> register1 = _subject.newRegister(null);
		Register<String> register2 = _subject.newRegister("hey");

		@SuppressWarnings("unused")
		Object reception = _subject.receive(new Consumer<String>() { @Override public void consume(String value) {
			received.append(value);
		}}, register1.output(), register2.output());
		assertEquals("nullhey", received.toString());

		register1.setter().consume("foo");
		register2.setter().consume("bar");
		assertEquals("nullheyfoobar", received.toString());

		register1.setter().consume("baz1");
		register2.setter().consume("baz2");
		assertEquals("nullheyfoobarbaz1baz2", received.toString());
	}

	@Test
	public void receptionDisposal() {
		final StringBuilder received = new StringBuilder();

		Register<String> register = _subject.newRegister("hey");

		Reception reception = _subject.receive(new Consumer<String>() { @Override public void consume(String value) {
			received.append(value);
		}}, register.output());

		register.setter().consume("foo");
		assertEquals("heyfoo", received.toString());

		reception.dispose();

		register.setter().consume("banana");
		assertFalse(received.toString().contains("banana"));
	}

	@Test (timeout = 6000)
	public void receptionGc() {
		final StringBuilder received = new StringBuilder();

		Register<String> register = _subject.newRegister("hey");

		@SuppressWarnings("unused")
		Object reception = _subject.receive(new Consumer<String>() { @Override public void consume(String value) {
			received.append(value);
		}}, register.output());

		register.setter().consume("foo");
		assertEquals("heyfoo", received.toString());

		reception = null;

		do {
			System.gc();

			received.delete(0, received.length());
			register.setter().consume("something else");
			register.setter().consume("banana");
		} while (received.toString().contains("banana"));
	}
}
