package sneer.skin.widgets.reactive;

import javax.swing.JComponent;

public interface ComponentWidget<MAINCOMPONENT extends JComponent> extends Widget<MAINCOMPONENT>{

	public abstract JComponent getComponent();

}