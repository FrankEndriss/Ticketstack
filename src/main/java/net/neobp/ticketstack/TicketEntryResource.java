package net.neobp.ticketstack;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/** The REST adapter to the TicketDB
 */
//@Path("/TicketEntryResource")
@RestController
public class TicketEntryResource {

	private TicketEntryDao teDao;

	public TicketEntryResource(final TicketEntryDao teDao) {
		this.teDao=teDao;
		throw new RuntimeException("hello world");
	}
	
	/**@return List of all TicketEntries */
//	@GET
//	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@RequestMapping(value="/", method=RequestMethod.GET)
	public List<TicketEntry> getAllTicketEntries() {
		System.out.println("getAllTicketEntries() called");
		return teDao.getAllTicketEntries();
	}
	
//	@POST
//	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@RequestMapping(value="/", method=RequestMethod.POST)
	public void insertTicket(final TicketEntry ticketEntry) {
		System.out.println("insertTicket called, "+ticketEntry.getTicket());
		teDao.insertTicket(ticketEntry);
	}
	
	/** @param id of TicketEntry
	 *  @return Ticket with ID id
	 */
//	@GET
//	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	public TicketEntry getTicketEntry( @PathVariable("id") final String id) {
		return teDao.getTicketEntry(id);
	}

//	@POST
//	@Path("{id}/up")
	@RequestMapping(value="/{id}/up", method=RequestMethod.POST)
	public void moveTicketUp( @PathVariable("id") final String id) {
		teDao.moveTicketUp(id);
	}
	
//	@POST
//	@Path("{id}/down")
	@RequestMapping(value="/{id}/down", method=RequestMethod.POST)
	public void moveTicketDown(@PathVariable("id") final String id) {
		teDao.moveTicketDown(id);
	}
	
//	@POST
//	@Path("{id}/delete")
	@RequestMapping(value="/{id}/delete", method=RequestMethod.POST)
	public void deleteTicket(@PathVariable("id") final String id) {
		System.out.println("deleteTicket called, "+id);
		teDao.removeTicketEntry(id);
	}
	
}
