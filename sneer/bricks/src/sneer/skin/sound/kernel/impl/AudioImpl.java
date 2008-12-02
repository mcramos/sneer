package sneer.skin.sound.kernel.impl;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

import sneer.kernel.container.Inject;
import sneer.pulp.blinkinglights.BlinkingLights;
import sneer.pulp.blinkinglights.Light;
import sneer.pulp.blinkinglights.LightType;
import sneer.skin.sound.kernel.Audio;

class AudioImpl implements Audio {

	static private final int SAMPLE_RATE = 8000;
	static private final int SAMPLE_SIZE_IN_BITS = 16;
	static private final int CHANNELS = 2; //Stereo for linux/alsa compatibility - do not use mono.
	static private final boolean SIGNED = true;
	static private final boolean BIG_ENDIAN = false;

	static private final AudioFormat DEFAULT_AUDIO_FORMAT = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, CHANNELS, SIGNED, BIG_ENDIAN);

	@Inject static private BlinkingLights _lights;
	
	private Light _light;

	@Override
	public SourceDataLine tryToOpenSourceDataLine() {
		return tryToOpenSourceDataLine(defaultAudioFormat());
	}
	
	@Override
	public SourceDataLine tryToOpenSourceDataLine(AudioFormat audioFormat) {
		_light = _lights.prepare(LightType.ERROR);
		
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
		SourceDataLine dataLine;
		try {
			dataLine = (SourceDataLine) AudioSystem.getLine(info);
			dataLine.open();
		} catch (LineUnavailableException e) {
			_lights.turnOnIfNecessary(_light, "Problem with Audio Playback", e);
			return null;
		}
		
		_lights.turnOffIfNecessary(_light);
		dataLine.start();
		return dataLine;
	}

	
	@Override
	public TargetDataLine openTargetDataLine() throws LineUnavailableException {
		TargetDataLine dataLine = AudioSystem	.getTargetDataLine(defaultAudioFormat());
		dataLine.open();
		dataLine.start();
		return dataLine;
	}

	@Override
	public AudioFormat defaultAudioFormat() {
		return DEFAULT_AUDIO_FORMAT;
	}

}
