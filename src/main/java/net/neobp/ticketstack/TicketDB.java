package net.neobp.ticketstack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Simple database for TicketEntry objects.
 * TODO: implement writing access
 */
public class TicketDB {
	
	private final static Map<String, TicketEntry> tickets=new HashMap<String, TicketEntry>();
	
	static { // test data
		TicketEntry t1=new TicketEntry();
		t1.setTicket("SENVION_W-73");
		t1.setText("text zu senvion-w73");
		t1.setPrio(2);
		tickets.put(t1.getTicket(), t1);

		TicketEntry t2=new TicketEntry();
		t2.setTicket("SENVION_W-65");
		t2.setText("text zu senvion-w65");
		t2.setPrio(1);
		tickets.put(t2.getTicket(), t2);

		TicketEntry t3=new TicketEntry();
		t3.setTicket("SENVION_N-25");
		t3.setText("text zu senvion-n25.... langer text");
		t3.setPrio(3);
		tickets.put(t3.getTicket(), t3);
	}
	
	public static TicketEntry getTicketEntry(final String id) {
		return tickets.get(id);
	}
	
	public static List<TicketEntry> getAllTicketEntries() {
		return new ArrayList<TicketEntry>(tickets.values());
	}
	
	public static void removeTicketEntry(final String ticket) {
		tickets.remove(ticket);
	}
	
	/** Update/Insert of ticket. ticket.getTicket() must not be null, primaryKey.
	 * @param ticket the updated ticket
	 */
	public static void upsertTicketEntry(final TicketEntry ticket) {
		tickets.put(ticket.getTicket(), ticket);
	}
	
	/** Swap priority with the ticket in the list just before this ticket.
	 * Synchronized since two tickets are updated atomically
	 * @param id of the ticket to move up
	 */
	public static synchronized void moveTicketUp(final String id) {
		final TicketEntry ticketEntry=getTicketEntry(id);
		if(ticketEntry==null)
			throw new IllegalArgumentException("notfound: "+id);
		
		List<TicketEntry> entries=new ArrayList<TicketEntry>(tickets.values());
		final Comparator<TicketEntry> comparator=new Comparator<TicketEntry>() {
			@Override
			public int compare(TicketEntry o1, TicketEntry o2) {
				return o1.getPrio()-o2.getPrio();
			}
		};
		Collections.sort(entries, comparator);
		
		int idx=Collections.binarySearch(entries, ticketEntry, comparator);
		if(idx>0) { // found and not first
			int prevPrio=entries.get(idx-1).getPrio();
			entries.get(idx-1).setPrio(ticketEntry.getPrio());
			ticketEntry.setPrio(prevPrio);
		}
	}
}
