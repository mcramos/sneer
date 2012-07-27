package sneer.bricks.snapps.chat.impl;

import static basis.environments.Environments.my;
import sneer.bricks.expression.tuples.TupleSpace;
import sneer.bricks.hardware.clock.Clock;
import sneer.bricks.identity.seals.contacts.ContactSeals;
import sneer.bricks.network.social.Contact;
import sneer.bricks.snapps.chat.ChatMessage;
import sneer.bricks.snapps.chat.PrivateChat;
import sneer.bricks.snapps.chat.gui.panels.Message;
import sneer.bricks.snapps.contacts.actions.ContactAction;
import sneer.bricks.snapps.contacts.actions.ContactActionManager;
import sneer.bricks.snapps.contacts.gui.ContactsGui;
import basis.lang.CacheMap;
import basis.lang.Consumer;
import basis.lang.Producer;

class PrivateChatImpl implements PrivateChat {

	private static final int TEN_MINUTES = 1000 * 60 * 10;
	
	protected CacheMap<Contact, ChatFrame> framesByContact = CacheMap.newInstance();

	@SuppressWarnings("unused") private Object refToAvoidGc;

	{
		my(ContactActionManager.class).addContactAction(new ContactAction() { @Override public void run() {
			final Contact contact = my(ContactsGui.class).selectedContact().currentValue();
			frameFor(contact).show();
		}

		@Override public String caption() { return "Chat"; }
		@Override public boolean isVisible() { return true; }
		@Override public boolean isEnabled() { return true; }
		@Override public int positionInMenu() { return 300; }
		});
		
		handleReceivedMessages();
	}

	@Override
	public Message convert(ChatMessage message) {
		return ChatFrame.convert(message);
	}

	private void handleReceivedMessages() {
		refToAvoidGc = my(TupleSpace.class).addSubscription(ChatMessage.class, new Consumer<ChatMessage>() { @Override public void consume(ChatMessage message) {
			if (isPublic(message)) return;
			if (isOld(message)) return;
			Contact contact = my(ContactSeals.class).contactGiven(message.publisher);
			if (contact == null) return;
			frameFor(contact).showMessage(convert(message));
		}});
	}
	
	private boolean isPublic(ChatMessage message) {		
		return message.addressee == null;
	}

	
	private boolean isOld(ChatMessage message) {
		return now() - message.publicationTime > TEN_MINUTES;
	}

	
	private long now() {
		return my(Clock.class).time().currentValue();
	}
	
	private ChatFrame frameFor(final Contact contact) {
		return framesByContact.get(contact, new Producer<ChatFrame>() { @Override public ChatFrame produce() {
			return new ChatFrame(contact);
		}});
	}
}
