//Copyright (C) 2004 Klaus Wuestefeld
//This is free software. It is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the license distributed along with this file for more details.

package views;

public interface StringView {

	void addObserver(Observer observer);

	public interface Observer {
		void observeChange(String newValue);
	}

	String currentValue();

}
