package net.neobp.ticketstack;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import net.neobp.speedment.SpeedmentApplication;
import net.neobp.speedment.SpeedmentApplicationBuilder;
import net.neobp.speedment.ticketstack.public_.tickets.Tickets;
import net.neobp.speedment.ticketstack.public_.tickets.TicketsImpl;
import net.neobp.speedment.ticketstack.public_.tickets.TicketsManager;

/** This implements the persistence layer using the Speedment framework.
 * As of 2016-12-28 it is not possible to create new ticket since
 * that fails with an exception in the sql layer of speedment.
 * It seems that insert does work only for tables with a primary key
 * of type long, at least used with postgreSQL.
 */
@Component
@DaoImplementation(orm=DaoImplementation.ORM.SPEEDMENT)
public class SpeedmentTicketEntryDao implements TicketEntryDao {

	private final SpeedmentApplication app=new SpeedmentApplicationBuilder().build();
	private final TicketsManager ticketsManager=app.getOrThrow(TicketsManager.class);

	/**
	 * Mapper to convert from speedment ticket entities to javaEE ticket entities.
	 */
	private final Function<Tickets, TicketEntry> ticketMapper=new Function<Tickets, TicketEntry>() {
		@Override
		public TicketEntry apply(final Tickets t) {
			TicketEntry ticketEntry=new TicketEntry();
			ticketEntry.setTicket(t.getTicket());
			ticketEntry.setPrio(t.getPrio().orElse(-1));
			ticketEntry.setText(t.getText().orElse(""));
			return ticketEntry;
		}
	};

	@Override
	public List<TicketEntry> getAllTicketEntries() {
		return ticketsManager.stream()
				.sorted(Tickets.PRIO.comparator())
				.map(ticketMapper)
				.collect(Collectors.toList());
	}

	@Override
	public void updateTicketText(TicketEntry ticketEntry, String updText) {
		ticketsManager.stream()
			.filter(Tickets.TICKET.equal(ticketEntry.getTicket()))
			.map(Tickets.TEXT.setTo(updText))
			.forEach(ticketsManager.updater());
	}

	@Override
	public void insertTicket(TicketEntry ticketEntry) {
		// should be possible to call a builder method instead of constructor 
		TicketsImpl newTicket=new TicketsImpl();
		newTicket.setText(ticketEntry.getText());
		newTicket.setPrio(ticketEntry.getPrio());
		newTicket.setTicket(ticketEntry.getTicket());
		newTicket.setTstate(TicketState.REGULAR.toString());
		ticketsManager.persist(newTicket);
	}

	@Override
	public TicketEntry getTicketEntry(final String id) {
		return ticketsManager.stream()
			.filter(Tickets.TICKET.equal(id))
			.findFirst()
			.map(ticketMapper)
			.orElse(null);
	}

	@Override
	public boolean moveTicketUp(final String id) {
		// prio of the ticket with id
		final Tickets thisOne= ticketsManager.stream()
			.filter(Tickets.TICKET.equal(id))
			.findFirst().orElse(null);
		if(thisOne==null)
			return false;
		final int thisOnePrio=thisOne.getPrio().orElse(-1);
		
		// the ticket just before the ticket with id
		final Tickets theOneBefore=ticketsManager.stream()
			.filter(Tickets.PRIO.lessThan(thisOnePrio))
			.sorted(Tickets.PRIO.comparator().reversed())
			.findFirst().orElse(null);
		if(theOneBefore==null)
			return false;
		
		// swap the priorities
		thisOne.setPrio(theOneBefore.getPrio().orElse(-1));
		theOneBefore.setPrio(thisOnePrio);
		
		// and update the database
		ticketsManager.update(thisOne);
		ticketsManager.update(theOneBefore);
		
		return true;
	}

	@Override
	public boolean moveTicketDown(String id) {
		// prio of the ticket with id
		final Tickets thisOne= ticketsManager.stream()
			.filter(Tickets.TICKET.equal(id))
			.findFirst().orElse(null);
		if(thisOne==null)
			return false;
		final int thisOnePrio=thisOne.getPrio().orElse(-1);

		// the ticket just after the ticket with id
		final Tickets theOneAfter=ticketsManager.stream()
			.filter(Tickets.PRIO.greaterThan(thisOnePrio))
			.sorted(Tickets.PRIO.comparator())
			.findFirst().orElse(null);
		if(theOneAfter==null)
			return false;
		
		// swap the priorities
		thisOne.setPrio(theOneAfter.getPrio().orElse(-1));
		theOneAfter.setPrio(thisOnePrio);
		
		// and update the database
		ticketsManager.update(thisOne);
		ticketsManager.update(theOneAfter);
		
		return true;
	}

	@Override
	public int removeTicketEntry(String id) {
		final AtomicInteger i=new AtomicInteger(0);
		ticketsManager.stream()
			.filter(Tickets.TICKET.equal(id))
			.limit(1)
			.forEach(t -> { 
					i.incrementAndGet(); 
					ticketsManager.remover();
				});
		return i.get();
	}

}
