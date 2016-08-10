package net.neobp.ticketstack;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TicketEntry {
	
	public TicketEntry() {
		// jaxb needs this
	}

	private String ticket;
	private String text;
	private int prio;
	private TicketEntryState state;
	
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

	public TicketEntryState getState() {
		return state;
	}

	public void setState(final TicketEntryState state) {
		this.state = state;
	}

}
