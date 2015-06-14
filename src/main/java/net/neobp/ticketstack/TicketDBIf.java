package net.neobp.ticketstack;

import java.util.List;

import javax.ejb.Local;

@Local
public interface TicketDBIf {
	public TicketEntry getTicketEntry(final String id);
	public List<TicketEntry> getAllTicketEntries(); 
	
	/** Removes a TicketEntry
	 * @param id the ticket to remove
	 */
	public void removeTicketEntry(final String id);
	
	/** Inserts the ticket ticketEntry as first ticket.
	 * Prios of all other Tickets are adjusted
	 * @param ticketEntry
	 */
	public void insertTicket(final TicketEntry ticketEntry);
	
	/** Swap priority with the ticket in the list just after this ticket.
	 * @param id of the ticket to move down
	 */
	public void moveTicketDown(final String id);
	
	/** Swap priority with the ticket in the list just before this ticket.
	 * @param id of the ticket to move up
	 */
	public void moveTicketUp(final String id);
	
}