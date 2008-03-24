//Copyright (C) 2004 Klaus Wuestefeld
//This is free software. It is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the license distributed along with this file for more details.
//Contributions: Kalecser Kurtz.

package sneer.old.life;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import wheel.graphics.JpgImage;
import wheel.reactive.Signal;
import wheel.reactive.Source;
import wheel.reactive.impl.SourceImpl;
import wheel.reactive.sets.SetSignal;
import wheel.reactive.sets.SetSource;


public class LifeImpl implements Life, Serializable {

	private final Source<String> _name = new SourceImpl<String>("");
	private final Source<String> _thoughtOfTheDay = new SourceImpl<String>("");
	private final Source<JpgImage> _picture = new SourceImpl<JpgImage>(null);

	private String _profile;
    private String _contactInfo;

	private SetSource<String> _nicknames = new SetSource<String>();
	private final Map<String, LifeView> _contactsByNickname = new HashMap<String, LifeView>();
	private final Map<String, Object> _thingsByName = new HashMap<String, Object>();


	public LifeImpl(String name) {
		name(name);
	}
	
	public void name(String newName) {
		_name.setter().consume(newName);
	}

	public void thoughtOfTheDay(String thought) {
		_thoughtOfTheDay.setter().consume(thought);
	}
	
	public SetSignal<String> nicknames() {
		return _nicknames;
	}

	public void giveSomebodyANickname(LifeView somebody, String nickname) throws IllegalArgumentException {
		if (somebody == null || nickname == null || nickname.equals("")) throw new IllegalArgumentException();
		if (_contactsByNickname.containsKey(nickname)) throw new IllegalArgumentException();
		
		_contactsByNickname.put(nickname, somebody);
		_nicknames.add(nickname);
	}

	public void changeNickname(String oldNickname, String newNickname) throws IllegalArgumentException {
		LifeView lifeView = contact(oldNickname);
		giveSomebodyANickname(lifeView, newNickname);
		forgetNickname(oldNickname);
	}

	public void forgetNickname(String oldNickname) {
		_contactsByNickname.remove(oldNickname);
		_nicknames.remove(oldNickname);
	}

	public Signal<String> name() {
		return _name.output();
	}

	public Signal<String> thoughtOfTheDay() {
		return _thoughtOfTheDay.output();
	}

	public LifeView contact(String nickname) {
		return _contactsByNickname.get(nickname);
	}


    public void profile(String profile) {
        _profile = profile;
    }

    public String profile() {
        return _profile;
    }

    public void contactInfo(String contactInfo) {
        _contactInfo = contactInfo;
    }

    public String contactInfo() {
        return _contactInfo;
    }
  
	public Date lastSightingDate() {
		return new Date(); //A local LifeView is always up-to-date.
	}

	public Signal<JpgImage> picture() {
		return _picture.output();
	}

	public void picture(JpgImage picture) {
		_picture.setter().consume(picture);
	}

	public Object thing(String name) {
		return _thingsByName.get(name);
	}

	public void thing(String name, Object thing) {
		_thingsByName.put(name, thing);
	}
	
	public Map<String, Object> things() {
		return _thingsByName;
	}

	private static final long serialVersionUID = 1L;
}
