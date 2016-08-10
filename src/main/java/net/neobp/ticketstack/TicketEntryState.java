package net.neobp.ticketstack;

/**
 * Possible states of a ticket entry.
 * @author Frank Endriss (fj.endriss@gmail.com)
 * @version $Id$
 * @since 1.0
 */
public enum TicketEntryState {
	/**
	 * One should work on this ticket if he finds the time. (In Arbeit).
	 */
	WORK,
	/**
	 * One has to wait for someting before he can work on this ticket (Unterbrochen).
	 */
	WAIT,
	/**
	 * One has to wait for a date/time before he can/must work on this ticket (Wiedervorlage).
	 */
	RESUBMISSION,
	/**
	 * This ticket is done, former it was deleted instead of using this state.
	 */
	DONE
}
