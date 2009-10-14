package dfcsantos.tracks.client.tests;

import static sneer.foundation.environments.Environments.my;

import java.io.File;
import java.io.IOException;

import org.jmock.Expectations;
import org.junit.Test;

import sneer.bricks.hardwaresharing.files.client.FileClient;
import sneer.bricks.network.social.ContactManager;
import sneer.bricks.pulp.crypto.Crypto;
import sneer.bricks.pulp.crypto.Sneer1024;
import sneer.bricks.pulp.keymanager.Seals;
import sneer.bricks.pulp.tuples.TupleSpace;
import sneer.bricks.software.folderconfig.FolderConfig;
import sneer.bricks.software.folderconfig.tests.BrickTest;
import sneer.foundation.brickness.testsupport.Bind;
import dfcsantos.tracks.client.TrackClient;
import dfcsantos.tracks.endorsements.TrackEndorsement;

public class TrackClientTest extends BrickTest {

	@Bind private final FileClient _fileClient = mock(FileClient.class);
	
	@Test(timeout=4000)
	public void trackDownload() throws IOException {
		final Sneer1024 hash1 = my(Crypto.class).digest(new byte[]{1});
		
		checking(new Expectations(){{
			exactly(1).of(_fileClient).fetch(new File(candidatesFolder(), "foo.mp3"), 42, hash1);
		}});
		
		my(TrackClient.class);
		
		publishEndorsementTuple(hash1, 42, "songs/subfolder/foo.mp3");
	}

	private File candidatesFolder() {
		return new File(my(FolderConfig.class).tmpFolderFor(TrackClient.class), "candidates");
	}

	private void publishEndorsementTuple(final Sneer1024 hash1, int lastModified, String track) {
		TrackEndorsement trackEndorsement = new TrackEndorsement(track, lastModified, hash1);
		stamp(trackEndorsement);
		my(TupleSpace.class).acquire(trackEndorsement);
		my(TupleSpace.class).waitForAllDispatchingToFinish();
	}

	private void stamp(TrackEndorsement trackEndorsement) {
		trackEndorsement.stamp(my(Seals.class).sealGiven(my(ContactManager.class).produceContact("Someone Else")), 1234);
	}

}
