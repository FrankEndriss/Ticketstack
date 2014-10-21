package net.neobp.ticketstack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Simple database for TicketEntry objects.
 * TODO: implement persistence
 */
public class TicketDB {
	
	/** TODO use a List sorted by prio.
	 * This is a map TicketEntry.getTicket() -> TicketEntry
	 **/
	private final static Map<String, TicketEntry> tickets=new HashMap<String, TicketEntry>();
	
	static { // test data
		wipe();
	}
	
	public static TicketEntry getTicketEntry(final String id) {
		return tickets.get(id);
	}
	
	public static List<TicketEntry> getAllTicketEntries() {
		List<TicketEntry> retList=new ArrayList<TicketEntry>(tickets.values());
		Collections.sort(retList, prioComparator);
		return retList;
	}
	
	/** Removes a TicketEntry
	 * @param ticket
	 * @return true if found and removed, false if notfound
	 */
	public static void removeTicketEntry(final String id) {
		if(tickets.remove(id)==null)
			throw new IllegalArgumentException("notfound: "+id);
	}
	
	/** Inserts the ticket ticketEntry as first ticket.
	 * Prios of all other Tickets are adjusted
	 * @param ticketEntry
	 */
	public static void insertTicket(final TicketEntry ticketEntry) {
		if(tickets.get(ticketEntry.getTicket())!=null)
			throw new IllegalArgumentException("ticket exists");
			
		final List<TicketEntry> tickets=getAllTicketEntries();
		// Prio of new ticket
		final int firstPrio=tickets.size()>0?
				tickets.get(0).getPrio():
				1;
		ticketEntry.setPrio(firstPrio);
		// adjust prio of all other tickets
		for(TicketEntry te : tickets) {
			te.setPrio(te.getPrio()+1);
		}
		
	}
	
	/** Update/Insert of ticket. ticket.getTicket() must not be null, primaryKey.
	 * @param ticket the updated ticket
	 */
	public static void upsertTicketEntry(final TicketEntry ticket) {
		tickets.put(ticket.getTicket(), ticket);
	}
	

	private final static Comparator<TicketEntry> prioComparator=new Comparator<TicketEntry>() {
			@Override
			public int compare(TicketEntry o1, TicketEntry o2) {
				return o1.getPrio()-o2.getPrio();
			}
		};

	/** Swap priority with the ticket in the list just after this ticket.
	 * Synchronized since two tickets are updated atomically
	 * @param id of the ticket to move down
	 */
	public static synchronized void moveTicketDown(final String id) {
		final TicketEntry ticketEntry=getTicketEntry(id);
		if(ticketEntry==null)
			throw new IllegalArgumentException("notfound: "+id);
		
		List<TicketEntry> entries=new ArrayList<TicketEntry>(tickets.values());
		Collections.sort(entries, prioComparator);
		
		int idx=Collections.binarySearch(entries, ticketEntry, prioComparator);
		System.out.println("found at: "+idx);
		if(idx>=0 && idx<entries.size()-1) { // found and not last, swap prio with next ticket
			int nextPrio=entries.get(idx+1).getPrio();
			entries.get(idx+1).setPrio(ticketEntry.getPrio());
			ticketEntry.setPrio(nextPrio);
		}
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
		Collections.sort(entries, prioComparator);
		
		int idx=Collections.binarySearch(entries, ticketEntry, prioComparator);
		System.out.println("found at: "+idx);
		if(idx>0) { // found and not first, swap prio with previous ticket
			int prevPrio=entries.get(idx-1).getPrio();
			entries.get(idx-1).setPrio(ticketEntry.getPrio());
			ticketEntry.setPrio(prevPrio);
		}
	}

	/** Wipes out the complete database.
	 * TODO: Wenn Mandantenfähig, dann wipe(mandant)
	 */
	public static void wipe() {
		tickets.clear();

		// Testdata
		TicketEntry t1=new TicketEntry();
		t1.setTicket("SENVION_W-73");
		t1.setText("text zu senvion-w73");
		t1.setPrio(4);
		tickets.put(t1.getTicket(), t1);

		TicketEntry t2=new TicketEntry();
		t2.setTicket("SENVION_W-65");
		t2.setText("text zu senvion-w65");
		t2.setPrio(3);
		tickets.put(t2.getTicket(), t2);

		TicketEntry t3=new TicketEntry();
		t3.setTicket("SENVION_N-25");
		t3.setText("text zu senvion-n25.... langer text");
		t3.setPrio(1);
		tickets.put(t3.getTicket(), t3);
	}
}
