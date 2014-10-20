package net.neobp.ticketstack;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TicketDBTest {

	@Before
	public void setUp() {
		TicketDB.wipe();
		// TODO: Move testdata from TicketDB to this class
	}

	@Test
	public void testGetTicketEntry() {
		final TicketEntry ticket=TicketDB.getTicketEntry("SENVION_W-65");
		assertNotNull("ticket not found", ticket);
		assertEquals("ticket prio", 3, ticket.getPrio());
		assertEquals("ticket id", "SENVION_W-65", ticket.getTicket());
	}

	@Test
	public void testGetAllTicketEntries() {
		final List<TicketEntry> tickets=TicketDB.getAllTicketEntries();
		assertEquals("must be 3 tickets", 3, tickets.size());
		
		// TODO test if sorted by prio
	}

	@Test
	public void testRemoveTicketEntry() {
		TicketDB.removeTicketEntry("SENVION_W-65");
		final List<TicketEntry> tickets=TicketDB.getAllTicketEntries();
		assertEquals("must be 2 tickets left", 2, tickets.size());
	}

	@Test
	public void testUpsertTicketEntry_update() {
		TicketEntry ticket=TicketDB.getTicketEntry("SENVION_W-65");
		final String testtext="my text asdhaoshga";
		ticket.setText(testtext);
		TicketDB.upsertTicketEntry(ticket);

		// query again
		ticket=TicketDB.getTicketEntry("SENVION_W-65");
		assertEquals("tickettext", testtext, ticket.getText());

		// check list size
		final List<TicketEntry> tickets=TicketDB.getAllTicketEntries();
		assertEquals("must still be 3 tickets", 3, tickets.size());
	}

	@Test
	public void testUpsertTicketEntry_insert() {
		final String newTicketId="newTicket42";
		TicketEntry ticket=new TicketEntry();
		ticket.setTicket(newTicketId);
		TicketDB.upsertTicketEntry(ticket);

		// check list size
		final List<TicketEntry> tickets=TicketDB.getAllTicketEntries();
		assertEquals("must be 4 tickets now", 4, tickets.size());
		
		// TODO: check prio
	}

	@Test
	public void testMoveTicketUp() {
		List<TicketEntry> tickets=TicketDB.getAllTicketEntries();
		// should be sorted by prio
		// search index of testticket
		int oldIdx=-1;
		for(int i=0; i<tickets.size(); i++) {
			if("SENVION_W-65".equals(tickets.get(i).getTicket())) {
				oldIdx=i;
				break;
			}
		}

		TicketDB.moveTicketUp("SENVION_W-65");

		tickets=TicketDB.getAllTicketEntries();
		// should be sorted by prio
		// search index of testticket
		int newIdx=-1;
		for(int i=0; i<tickets.size(); i++) {
			if("SENVION_W-65".equals(tickets.get(i).getTicket())) {
				newIdx=i;
				break;
			}
		}
		
		assertEquals("new index must be one less than old", newIdx+1, oldIdx);

	}

}
