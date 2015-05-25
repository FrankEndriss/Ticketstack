package net.neobp.ticketstack;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/** The REST adapter to the TicketDB
 * 
 * For combining JAX-RS and EJB see
 * http://stackoverflow.com/questions/3027834/inject-an-ejb-into-jax-rs-restful-service
 */
@Path("TicketEntryResource")
@Stateless
public class TicketEntryResource {

	//@Resource(name="jdbc/TicketstackDB")
	//private DataSource ticketDB_dataSource;
	
	@EJB
	public JdbcTicketDB jdbcTicketDB;
	
	/**@return List of all TicketEntries */
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public List<TicketEntry> getAllTicketEntries() {
		System.out.println("getAllTicketEntries() called");
		return jdbcTicketDB.getAllTicketEntries();
	}
	
	@POST
	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public void insertTicket(final TicketEntry ticketEntry) {
		System.out.println("insertTicket called, "+ticketEntry.getTicket());
		jdbcTicketDB.insertTicket(ticketEntry);
	}
	
	/** @param id of TicketEntry
	 *  @return Ticket with ID id
	 */
	@GET
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	@Path("{id}")
	public TicketEntry getTicketEntry(@PathParam("id") final String id) {
		return jdbcTicketDB.getTicketEntry(id);
	}

	@POST
	@Path("{id}/up")
	public void moveTicketUp(@PathParam("id") final String id) {
		jdbcTicketDB.moveTicketUp(id);
	}
	
	@POST
	@Path("{id}/down")
	public void moveTicketDown(@PathParam("id") final String id) {
		jdbcTicketDB.moveTicketDown(id);
	}
	
	@POST
	@Path("{id}/delete")
	public void deleteTicket(@PathParam("id") final String id) {
		System.out.println("deleteTicket called, "+id);
		jdbcTicketDB.removeTicketEntry(id);
	}
	
}
