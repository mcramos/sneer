package spikes.sneer.kernel.container.bytecode.dependencies;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import basis.brickness.Brick;


@Brick
public interface DependencyFinder {

	List<String> findDependencies(InputStream classInputStream) throws IOException;

}

