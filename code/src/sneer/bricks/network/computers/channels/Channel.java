package sneer.bricks.network.computers.channels;

import java.nio.ByteBuffer;

import basis.lang.Consumer;
import basis.lang.Producer;


public interface Channel {
	
	long id();
	
	void open(Producer<ByteBuffer> sender, Consumer<ByteBuffer> receiver);
	
	int maxPacketSize();

}