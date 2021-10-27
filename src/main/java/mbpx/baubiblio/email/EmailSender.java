package mbpx.baubiblio.email;

public interface EmailSender {
	void send(String to, String subject, String content);

}
