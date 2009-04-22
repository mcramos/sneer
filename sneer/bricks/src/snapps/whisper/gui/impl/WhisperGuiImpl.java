package snapps.whisper.gui.impl;

import static sneer.commons.environments.Environments.my;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTextField;
import javax.swing.JToggleButton;

import snapps.whisper.gui.WhisperGui;
import sneer.commons.environments.Environments;
import sneer.hardware.cpu.lang.Consumer;
import sneer.pulp.reactive.Signal;
import sneer.pulp.reactive.Signals;
import sneer.pulp.reactive.gates.logic.LogicGates;
import sneer.skin.colors.Colors;
import sneer.skin.main.dashboard.InstrumentPanel;
import sneer.skin.main.instrumentregistry.InstrumentRegistry;
import sneer.skin.main.synth.Synth;
import sneer.skin.rooms.ActiveRoomKeeper;
import sneer.skin.sound.loopback.LoopbackTester;
import sneer.skin.sound.mic.Mic;
import sneer.skin.sound.speaker.Speaker;
import sneer.skin.widgets.reactive.ReactiveWidgetFactory;
import sneer.skin.widgets.reactive.TextWidget;

class WhisperGuiImpl implements WhisperGui {

	private static final Synth _synth = my(Synth.class);
	private final LoopbackTester _loopback = my(LoopbackTester.class);
	private final InstrumentRegistry _instrumentManager = my(InstrumentRegistry.class);
	private final Speaker _speaker = my(Speaker.class);
	private final Mic _mic = my(Mic.class);

	private final JToggleButton _whisperButton = new JToggleButton();
	private final JToggleButton _loopBackButton = new JToggleButton();

	private TextWidget<JTextField> _roomField;

	WhisperGuiImpl(){
		_synth.load(this);
		initSynth();
		_instrumentManager.registerInstrument(this);
	}

	private void initSynth() {
		_whisperButton.setName("WhisperButton");
		_synth.attach(_whisperButton);
		_loopBackButton.setName("LoopbackButton");
		_synth.attach(_loopBackButton);
	}
	
	private Signal<Boolean> isRunning() {
		return my(LogicGates.class).and(_mic.isRunning(), _speaker.isRunning());
	}

	@Override
	public void init(InstrumentPanel window) {
		Container container = window.contentPane();
		container.setBackground(my(Colors.class).solid());
		container.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 5));
		
		ActiveRoomKeeper room = Environments.my(ActiveRoomKeeper.class);

		_roomField = Environments.my(ReactiveWidgetFactory.class).newTextField(room.room(), room.setter());
		room.setter().consume("<Room>");

		container.add(_roomField.getMainWidget());
		_roomField.getMainWidget().setPreferredSize(new Dimension(100,36));
		
		container.add(_whisperButton);
		container.add(_loopBackButton);
		
		createWhisperButtonListener();
		createLoopBackButtonListener();
		
		my(Signals.class).receive(this, new Consumer<Boolean>() { @Override public void consume(Boolean isRunning) {
			_whisperButton.setSelected(isRunning);
			_roomField.getMainWidget().setEnabled(isRunning);
		}}, isRunning());

	}
	
	@Override
	public int defaultHeight() {
		return 50;
	}

	private void createWhisperButtonListener() {
		_whisperButton.addMouseListener(new MouseAdapter() {	@Override public void mouseReleased(MouseEvent e) {
			if (_whisperButton.isSelected()) whisperOn();
			else whisperOff();
			_whisperButton.setSelected(false);
		}});
	}

	private void createLoopBackButtonListener() {
		_loopBackButton.addMouseListener(new MouseAdapter() {	@Override public void mouseReleased(MouseEvent e) {
			if(_loopBackButton.isSelected()){
				_loopBackButton.setSelected(_loopback.start());
				return;
			}
			_loopback.stop();
		}});
	}

	protected void whisperOff() {
		_mic.close();
		_speaker.close();
	}

	protected void whisperOn() {
		_mic.open();
		_speaker.open();
	}

	@Override
	public String title() {
		return "Whisper";
	}
}