package spikes.legobricks;

import spikes.lego.Container;
import spikes.lego.ContainerUtils;
import wheel.lang.Threads;

public class SampleApplication  {

	public static void main(String[] args) throws Exception {
		Container container = ContainerUtils.getContainer();
		container.produce(NameGui.class);
		Threads.sleepWithoutInterruptions(5000);
	}
}
