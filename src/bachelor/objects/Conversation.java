package bachelor.objects;


public class Conversation {

	private User sender;
	private User receiver;
	private String message;
	private String sent;
	

	public Conversation(User sender, User receiver, String message, String sent) {
		this.sender = sender;
		this.receiver = receiver;
		this.message = message;
		this.sent = sent;

	}

	public User getSender() {
		return sender;
	}
	
	public void setSender(User sender) {
		this.sender = sender;
	}

	public User getReceiver() {
		return receiver;
	}

	public void setReceiver(User receiver) {
		this.receiver = receiver;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSent() {
		return sent;
	}

	public void setSent(String sent) {
		this.sent = sent;
	}
}
