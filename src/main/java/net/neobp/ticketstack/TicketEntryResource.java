package net.neobp.ticketstack;

import java.util.List;

import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.annotation.Resource;

/** The REST adapter to the TicketDB
 */
@Path("/TicketEntryResource")
public class TicketEntryResource {

	@Resource(name="jdbc/TicketstackDB")
	private DataSource ticketDB_dataSource;
	
	private TicketDBIf ticketDB;
	
	/**@return List of all TicketEntries */
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public List<TicketEntry> getAllTicketEntries() {
		System.out.println("getAllTicketEntries() called");
		return ticketDB.getAllTicketEntries();
	}
	
	@POST
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public void insertTicket(final TicketEntry ticketEntry) {
		System.out.println("insertTicket called, "+ticketEntry.getTicket());
		ticketDB.insertTicket(ticketEntry);
	}
	
	/** @param id of TicketEntry
	 *  @return Ticket with ID id
	 */
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Path("{id}")
	public TicketEntry getTicketEntry(@PathParam("id") final String id) {
		return ticketDB.getTicketEntry(id);
	}

	@POST
	@Path("{id}/up")
	public void moveTicketUp(@PathParam("id") final String id) {
		ticketDB.moveTicketUp(id);
	}
	
	@POST
	@Path("{id}/down")
	public void moveTicketDown(@PathParam("id") final String id) {
		ticketDB.moveTicketDown(id);
	}
	
	@POST
	@Path("{id}/delete")
	public void deleteTicket(@PathParam("id") final String id) {
		System.out.println("deleteTicket called, "+id);
		ticketDB.removeTicketEntry(id);
	}
	
}
