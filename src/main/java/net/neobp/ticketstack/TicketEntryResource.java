package net.neobp.ticketstack;

import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/** The REST adapter to the TicketDB
 */
@Controller
//@RestController
@RequestMapping(value = "/api")
public class TicketEntryResource {
    private final Logger log = Logger.getLogger(TicketEntryResource.class);

	private TicketEntryDao teDao;

	@Inject
	public TicketEntryResource(final TicketEntryDao teDao) {
		this.teDao=teDao;
		log.info("TicketEntryResource created");
	}
	
	/** @return List of all TicketEntries */
//	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@RequestMapping(value="/list.*", method=RequestMethod.GET)
	public ResponseEntity<List<TicketEntry>> getAllTicketEntries() {
		log.info("getAllTicketEntries() called");
		return new ResponseEntity<List<TicketEntry>>(teDao.getAllTicketEntries(), HttpStatus.OK);
	}
	
//	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@RequestMapping(value="/", method=RequestMethod.POST)
	public void insertTicket(final TicketEntry ticketEntry) {
		log.info("insertTicket() called, "+ticketEntry.getTicket());
		teDao.insertTicket(ticketEntry);
	}
	
	/** @param id of TicketEntry
	 *  @return Ticket with ID id
	 */
//	@GET
//	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	public TicketEntry getTicketEntry( @PathVariable("id") final String id) {
		log.info("getTicketEntry() called: "+id);
		return teDao.getTicketEntry(id);
	}

//	@POST
//	@Path("{id}/up")
	@RequestMapping(value="/{id}/up", method=RequestMethod.POST)
	public void moveTicketUp( @PathVariable("id") final String id) {
		log.info("moveTicketUp() called: "+id);
		teDao.moveTicketUp(id);
	}
	
//	@POST
//	@Path("{id}/down")
	@RequestMapping(value="/{id}/down", method=RequestMethod.POST)
	public void moveTicketDown(@PathVariable("id") final String id) {
		log.info("moveTicketDown() called: "+id);
		teDao.moveTicketDown(id);
	}
	
//	@POST
//	@Path("{id}/delete")
	@RequestMapping(value="/{id}/delete", method=RequestMethod.POST)
	public void deleteTicket(@PathVariable("id") final String id) {
		log.info("deleteTicket called, "+id);
		teDao.removeTicketEntry(id);
	}
	
}
