package bachelor.objects;

public class Inbox {
	private User sender;
	private String message;
	private String sent;
	
	
	public Inbox(User sender, String message, String sent) {
		this.sender = sender;
		this.message = message;
		this.sent = sent;
	}
	
	public User getSender() {
		return sender;
	}
	public void setSender(User sender) {
		this.sender = sender;
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
