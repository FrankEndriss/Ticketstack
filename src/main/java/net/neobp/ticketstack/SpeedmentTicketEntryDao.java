package net.neobp.ticketstack;

import java.util.List;

/** This implements the persistence layer using the Speedment framework
 */
public class SpeedmentTicketEntryDao implements TicketEntryDao {

	@Override
	public List<TicketEntry> getAllTicketEntries() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateTicketText(TicketEntry ticketEntry, String updText) {
		// TODO Auto-generated method stub

	}

	@Override
	public void insertTicket(TicketEntry ticketEntry) {
		// TODO Auto-generated method stub

	}

	@Override
	public TicketEntry getTicketEntry(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean moveTicketUp(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean moveTicketDown(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int removeTicketEntry(String id) {
		// TODO Auto-generated method stub
		return 0;
	}

}
