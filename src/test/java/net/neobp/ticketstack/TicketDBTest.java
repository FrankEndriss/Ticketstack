package net.neobp.ticketstack;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TicketDBTest {

	private TicketDB ticketDB;
	
	private File tmpDir;

	@Before
	public void setUp() throws IOException {
		tmpDir=File.createTempFile("ticketstack", "ts");
		tmpDir.delete();
		tmpDir.mkdir();
		ticketDB=new TicketDB();
		ticketDB.init(tmpDir.getAbsolutePath());
		tmpDir.deleteOnExit();

		insert3tickets();
	}
	
	@After
	public void tearDown() {
		removeAllTickets();
		deleteRecursive(tmpDir);
	}
	
	private void deleteRecursive(final File dir) {
		for(File f : dir.listFiles()) {
			if(f.isDirectory())
				deleteRecursive(f);
			else
				f.delete();
		}
		dir.delete();
	}

	private void removeAllTickets() {
		for(TicketEntry ent : ticketDB.getAllTicketEntries())
			ticketDB.removeTicketEntry(ent.getTicket());
		
	}

	private void insert3tickets() {
		// Testdata
		TicketEntry t1=new TicketEntry();
		t1.setTicket("SENVION_W-73");
		t1.setText("text zu senvion-w73");
		ticketDB.insertTicket(t1);

		TicketEntry t2=new TicketEntry();
		t2.setTicket("SENVION_W-65");
		t2.setText("text zu senvion-w65");
		ticketDB.insertTicket(t2);

		TicketEntry t3=new TicketEntry();
		t3.setTicket("SENVION_N-25");
		t3.setText("text zu senvion-n25.... langer text");
		ticketDB.insertTicket(t3);
	}

	@Test
	public void testGetTicketEntry() {
		final TicketEntry ticket=ticketDB.getTicketEntry("SENVION_W-65");
		assertNotNull("ticket not found", ticket);
		assertEquals("ticket id", "SENVION_W-65", ticket.getTicket());
	}

	@Test
	public void testGetAllTicketEntries() {
		final List<TicketEntry> tickets=ticketDB.getAllTicketEntries();
		assertEquals("must be 3 tickets", 3, tickets.size());
		
		// TODO test if sorted by prio
	}

	@Test
	public void testRemoveTicketEntry() {
		ticketDB.removeTicketEntry("SENVION_W-65");
		final List<TicketEntry> tickets=ticketDB.getAllTicketEntries();
		assertEquals("must be 2 tickets left", 2, tickets.size());
	}

	@Test
	public void testUpdateTicketEntry() {
		TicketEntry ticket=ticketDB.getTicketEntry("SENVION_W-65");
		final String testtext="changed text";
		ticket.setText(testtext);
		ticketDB.updateTicketEntry(ticket);

		// query again
		ticket=ticketDB.getTicketEntry("SENVION_W-65");
		assertEquals("tickettext", testtext, ticket.getText());

		// check list size
		final List<TicketEntry> tickets=ticketDB.getAllTicketEntries();
		assertEquals("must still be 3 tickets", 3, tickets.size());
	}

	@Test
	public void testMoveTicketUp() {
		List<TicketEntry> tickets=ticketDB.getAllTicketEntries();
		// should be sorted by prio
		// search index of testticket
		int oldIdx=-1;
		for(int i=0; i<tickets.size(); i++) {
			if("SENVION_W-65".equals(tickets.get(i).getTicket())) {
				oldIdx=i;
				break;
			}
		}

		ticketDB.moveTicketUp("SENVION_W-65");

		tickets=ticketDB.getAllTicketEntries();
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
