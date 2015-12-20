package net.neobp.ticketstack;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TicketEntry {
	
	public TicketEntry() {
		// jaxb needs this
	}

	private long id;
	private String ticket;
	private String text;
	private int prio;
	
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

	public int getPrio() {
		return prio;
	}

	public void setPrio(final int prio) {
		this.prio = prio;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
