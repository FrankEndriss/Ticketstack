package net.neobp.ticketstack;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/** The REST adapter to the TicketDB
 */
@Path("/TicketEntryResource")
public class TicketEntryResource {

	/**@return List of all TicketEntries */
	@GET
//	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Produces(MediaType.APPLICATION_XML)
	public List<TicketEntry> getAllTicketEntries() {
		System.out.println("getAllTicketEntries() called");
		return TicketDB.getAllTicketEntries();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	public void insertTicket(final TicketEntry ticketEntry) {
		System.out.println("insertTicket called, "+ticketEntry.getTicket());
		TicketDB.insertTicket(ticketEntry);
	}
	
	/** @param id of TicketEntry
	 *  @return Ticket with ID id
	 */
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Path("{id}")
	public TicketEntry getTicketEntry(@PathParam("id") final String id) {
		return TicketDB.getTicketEntry(id);
	}

	@POST
	@Path("{id}/up")
	public void moveTicketUp(@PathParam("id") final String id) {
		TicketDB.moveTicketUp(id);
	}
	
	@POST
	@Path("{id}/down")
	public void moveTicketDown(@PathParam("id") final String id) {
		TicketDB.moveTicketDown(id);
	}
	
	@DELETE
	@Path("{id}/delete")
	public void deleteTicket(@PathParam("id") final String id) {
		TicketDB.removeTicketEntry(id);
	}
	
}
