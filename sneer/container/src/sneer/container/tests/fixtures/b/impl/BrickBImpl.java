package sneer.container.tests.fixtures.b.impl;

import static sneer.commons.environments.Environments.my;
import sneer.container.tests.fixtures.a.BrickA;
import sneer.container.tests.fixtures.b.BrickB;

public class BrickBImpl implements BrickB {{
	my(BrickA.class).setProperty("BrickB was here!");
}}