package dfcsantos.wusic.notification.playingtrack.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;

import org.jmock.Expectations;
import org.junit.Ignore;
import org.junit.Test;

import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.expression.tuples.testsupport.pump.TuplePump;
import sneer.bricks.expression.tuples.testsupport.pump.TuplePumps;
import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.identity.seals.OwnSeal;
import sneer.bricks.identity.seals.Seal;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.social.Contact;
import sneer.bricks.network.social.Contacts;
import sneer.bricks.network.social.attributes.Attributes;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.brickness.testsupport.Bind;
import sneer.foundation.environments.Environment;
import sneer.foundation.environments.Environments;
import sneer.foundation.lang.Closure;
import sneer.foundation.lang.ClosureX;
import sneer.foundation.lang.exceptions.Refusal;
import dfcsantos.tracks.Track;
import dfcsantos.tracks.Tracks;
import dfcsantos.wusic.Wusic;
import dfcsantos.wusic.notification.playingtrack.PlayingTrack;
import dfcsantos.wusic.notification.playingtrack.server.PlayingTrackPublisher;

public class PlayingTrackTest extends BrickTest {

	@Bind private final Wusic _wusic = mock(Wusic.class);
	private final Register<Track> _playingTrack = my(Signals.class).newRegister(null);

	private Environment _local;
	private Contact _localContact;
	private Attributes _remoteAttributes;

	private TuplePump _tuplePump;

	@Ignore
	@Test
	public void playingTrackBroadcast() throws Exception {
		checking(new Expectations() {{
			oneOf(_wusic).playingTrack(); will(returnValue(_playingTrack.output()));
		}});

		my(PlayingTrackPublisher.class);

		_local = my(Environment.class);
		Environment remote = newTestEnvironment(my(Clock.class));
		configureStorageFolder(remote, "remote/data");

		_tuplePump = my(TuplePumps.class).startPumpingWith(remote);

		final Seal localSeal = my(OwnSeal.class).get().currentValue();
		Environments.runWith(remote, new ClosureX<Refusal>() { @Override public void run() throws Refusal {
			_localContact = my(Contacts.class).addContact("local");
			my(ContactSeals.class).put("local", localSeal);
			_remoteAttributes = my(Attributes.class);

			testPlayingTrack("track1");
			testPlayingTrack("track2");
			testPlayingTrack("track2");
			testPlayingTrack("track3");
			testPlayingTrack("");
			testPlayingTrack("track4");

			testNullPlayingTrack();
		}});

		crash(remote);
	}

	private void testPlayingTrack(String trackName) {
		setLocalPlayingTrack(trackName.isEmpty() ? "" : trackName + ".mp3");
		_tuplePump.waitForAllDispatchingToFinish();
		assertEquals(trackName, playingTrackReceivedFromLocal());
	}

	private void testNullPlayingTrack() {
		my(Clock.class).advanceTime(1);
		_playingTrack.setter().consume(null);
		my(TupleSpace.class).waitForAllDispatchingToFinish();
		assertEquals("", playingTrackReceivedFromLocal());
	}

	private String playingTrackReceivedFromLocal() {
		return _remoteAttributes.attributeValueFor(_localContact, PlayingTrack.class, String.class).currentValue();
	}

	private void setLocalPlayingTrack(final String trackName) {
		Environments.runWith(_local, new Closure() { @Override public void run() {
			_playingTrack.setter().consume(my(Tracks.class).newTrack(new File(trackName)));
		}});
	}

}
