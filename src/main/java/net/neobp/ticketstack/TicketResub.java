package net.neobp.ticketstack;

import java.sql.Timestamp;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TicketResub {
	
	public TicketResub() {
		// jaxb needs this
	}

	private String ticket;
	private Timestamp resubts;
	private String text;
	
	public String getTicket() {
		return ticket;
	}

	public void setTicket(final String ticket) {
		this.ticket = ticket;
	}

	public String getText() {
		return text;
	}

	public void setText(final String text) {
		this.text = text;
	}

	public Timestamp getResubts() {
		return resubts;
	}

	public void setResubts(final Timestamp resubts) {
		this.resubts = resubts;
	}

}
