package snapps.listentome.speex.impl;

import org.xiph.speex.SpeexEncoder;

import snapps.listentome.speex.Encoder;
import sneer.kernel.container.Inject;
import sneer.skin.sound.kernel.Audio;

class EncoderImpl implements Encoder {

	private final SpeexEncoder _encoder = new SpeexEncoder();
	
	@Inject
	private static Audio _audio;
	
	EncoderImpl() {
		_encoder.init(SpeexConstants.NARROWBAND_ENCODING, SpeexConstants.SOUND_QUALITY, (int) _audio.defaultAudioFormat().getFrameRate(), _audio.defaultAudioFormat().getChannels());
	}

	@Override
	public byte[] getProcessedData() {
		byte[] speexBuffer = new byte[_encoder.getProcessedDataByteSize()];
		_encoder.getProcessedData(speexBuffer, 0);
		return speexBuffer;
	}

	@Override
	public boolean processData(byte[] pcmBuffer) {
		return _encoder.processData(pcmBuffer, 0, pcmBuffer.length);
	}

}
