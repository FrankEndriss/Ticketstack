package net.neobp.ticketstack;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.sql.DataSource;
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

	@EJB(name="JdbcTicketDB")
	private JdbcTicketDB jdbcTicketDB;
	
	/**@return List of all TicketEntries */
	@GET
	@Produces({MediaType.APPLICATION_JSON})
//	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public List<TicketEntry> getAllTicketEntries() {
		System.out.println("getAllTicketEntries() called, jdbcTicketDB="+jdbcTicketDB);
		List<TicketEntry> list=jdbcTicketDB.getAllTicketEntries();
		// debug
		if(list.isEmpty()) {
			list=new ArrayList<TicketEntry>();
			TicketEntry fakeEntry=new TicketEntry();
			fakeEntry.setPrio(1);
			fakeEntry.setText("fake entry text");
			fakeEntry.setTicket("myTicketID1");
			list.add(fakeEntry);

			TicketEntry fakeEntry2=new TicketEntry();
			fakeEntry2.setPrio(2);
			fakeEntry2.setText("fake entry2 text");
			fakeEntry2.setTicket("myTicketID2");
			list.add(fakeEntry2);
		}
		// end debug
		return list;
	}
	
	@POST
	@Consumes({MediaType.APPLICATION_JSON})
//	@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
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
