package net.neobp.ticketstack;

import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/** The REST adapter to the TicketEntry database
 */
@RestController
@RequestMapping(value = "/api")
public class TicketEntryResource {
    private final Logger log = Logger.getLogger(TicketEntryResource.class);

	private final TicketEntryDao teDao;

	@Inject
	public TicketEntryResource(final TicketEntryDao teDao) {
		this.teDao=teDao;
		log.info("TicketEntryResource created");
	}
	
	/** @return List of all TicketEntries */
	@RequestMapping(value="/", method=RequestMethod.GET)
	public List<TicketEntry> getAllTicketEntries() {
		log.info("getAllTicketEntries() called");
		final List<TicketEntry> ticketEntries=teDao.getAllTicketEntries();
		if(log.isInfoEnabled()) {
			for(TicketEntry te : ticketEntries)
				log.info("ticketEntry: "+te.getTicket()+" "+te.getPrio()+" "+te.getText());
			log.info("end of List<TicketEntry>");
		}
		return ticketEntries;
	}
	
	@RequestMapping(value="/", method=RequestMethod.POST)
	public ResponseEntity<TicketEntry> insertTicket(@RequestBody TicketEntry ticketEntry) {
		log.info("insertTicket() called, ticketEntry="+ticketEntry);
		if(ticketEntry!=null) {
			log.info("insertTicket() called, ticketEntry.ticket="+ticketEntry.getTicket());
			log.info("insertTicket() called, ticketEntry.text="+ticketEntry.getText());
		}
		try {
			teDao.insertTicket(ticketEntry);
			return new ResponseEntity<TicketEntry>(teDao.getTicketEntry(ticketEntry.getTicket()), HttpStatus.OK);
		}catch(Exception e) {
			return new ResponseEntity<TicketEntry>(HttpStatus.CONFLICT);
		}
	}
	
	/** @param id of TicketEntry
	 *  @return Ticket with ID id
	 */
//	@GET
//	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	public ResponseEntity<TicketEntry> getTicketEntry( @PathVariable("id") final String id) {
		log.info("getTicketEntry() called: "+id);
		TicketEntry ret=teDao.getTicketEntry(id);
		if(ret!=null)
			return new ResponseEntity<TicketEntry>(ret, HttpStatus.OK);
		else 
			return new ResponseEntity<TicketEntry>(HttpStatus.NOT_FOUND);
	}

//	@POST
//	@Path("{id}/up")
	@RequestMapping(value="/{id}/up", method=RequestMethod.POST)
	public ResponseEntity<Void> moveTicketUp( @PathVariable("id") final String id) {
		log.info("moveTicketUp() called: "+id);
		if(teDao.moveTicketUp(id)) {
			return new ResponseEntity<Void>(HttpStatus.OK);
		} else if(teDao.getTicketEntry(id)==null)
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		else
			return new ResponseEntity<Void>(HttpStatus.NOT_ACCEPTABLE);
			
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
