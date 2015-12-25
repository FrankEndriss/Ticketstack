package net.neobp.ticketstack;

import java.util.List;

/** Interface for TicketEntry persistence access
 */
public interface TicketEntryDao {

	/**
	 * @return all existing TicketEntry
	 */
	List<TicketEntry> getAllTicketEntries();

	/**
	 * @param ticketEntry a new ticket entry
	 */
	void insertTicket(TicketEntry ticketEntry);

	/**
	 * @param id
	 * @return the ticket with the id id
	 */
	TicketEntry getTicketEntry(String id);

	/** Swaps the priorities of the ticket with id id, and the ticket just before that ticket 
	 * (in a imaginary list where the tickets are sortet by prio)
	 * @param id
	 * @return 
	 */
	boolean moveTicketUp(String id);

	/** Swaps the priorities of the ticket with id id, and the ticket just after that ticket 
	 * (in a imaginary list where the tickets are sortet by prio)
	 * @param id
	 */
	void moveTicketDown(String id);

	/** Removes a TicketEntry
	 * @param id
	 * @return 
	 */
	int removeTicketEntry(String id);

}