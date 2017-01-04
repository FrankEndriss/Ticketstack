package net.neobp.ticketstack;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/*
CREATE TABLE tickets
(
  ticket character varying(256) NOT NULL,
  text character varying(2048),
  prio integer,
  CONSTRAINT tickets_pkey PRIMARY KEY (ticket)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE tickets
  OWNER TO postgres;
*/
@Component
public class TicketEntryDaoImpl implements TicketEntryDao {
    private final static Logger log = Logger.getLogger(TicketEntryDaoImpl.class);
    
	private final DataSource dataSource;

	private final JdbcTemplate jdbcTemplate;
	private final TicketEntryRowMapper teRowMapper=new TicketEntryRowMapper();
	
	@Inject
	public TicketEntryDaoImpl(final DataSource dataSource) {
		this.dataSource=dataSource;
		this.jdbcTemplate=new JdbcTemplate(this.dataSource);
		log.info("TicketEntryDaoImpl created");
	}
	
	@Override
	public List<TicketEntry> getAllTicketEntries() {
		return jdbcTemplate.query(
				"select * from tickets order by prio",
				teRowMapper);
	}

	@Override
	@Transactional
	public void updateTicketText(final TicketEntry ticketEntry, final String updText) {
		jdbcTemplate.update(
			"update tickets set text = ? where ticket = ?",
			updText,
			ticketEntry.getTicket());
	}

	@Override
	@Transactional
	public void insertTicket(final TicketEntry ticketEntry) {
//				"insert into tickets (prio, text, ticket) values(?, ?, ?)",
		jdbcTemplate.update(
// TODO: "min(prio)-1" returns null if the table is empty, need to fix
// Fix by implementing MinPrioService??? It is fairly unspecified how to synchronize in sql.
				"insert into tickets (prio, text, ticket) select min(prio)-1, ?, ? from tickets",
				ticketEntry.getText(),
				ticketEntry.getTicket());
	}

	@Override
	public TicketEntry getTicketEntry(final String ticket) {
		Collection<TicketEntry> list=jdbcTemplate.query(
			"select prio, text, ticket from tickets where ticket = ?",
			new Object[]{ ticket },
			teRowMapper);
		if(list.size()>0)
			return list.iterator().next();

		return null;
	}

	private class TicketEntryRowMapper implements RowMapper<TicketEntry> {
		@Override
		public TicketEntry mapRow(final ResultSet rs, final int rowNum) throws SQLException {
			final TicketEntry te=new TicketEntry();
			te.setPrio(rs.getInt("prio"));
			te.setText(rs.getString("text"));
			te.setTicket(rs.getString("ticket"));
			return te;
		}
	}

	@Override
	@Transactional
	public boolean moveTicketDown(final String ticket) {
		final TicketEntry thisTicket=getTicketEntry(ticket);
		if(thisTicket==null)
			return false;
		// select the ticket just after this ticket
		final Collection<TicketEntry> ticketAfter=jdbcTemplate.query(
				"select * from tickets where prio > ? order by prio limit 1",
				new Object[]{ thisTicket.getPrio() },
				teRowMapper);
		if(ticketAfter.size()==0)
			return false;

		swapPrios(thisTicket, ticketAfter.iterator().next());
		return true;
	}

	@Override
	@Transactional
	public boolean moveTicketUp(final String ticket) {
		final TicketEntry thisTicket=getTicketEntry(ticket);
		if(thisTicket==null)
			return false;

		// select the ticket just before this ticket
		final Collection<TicketEntry> ticketsBefore=jdbcTemplate.query(
				"select * from tickets where prio < ? order by prio desc limit 1",
				new Object[]{ thisTicket.getPrio() },
				teRowMapper);
		if(ticketsBefore.size()==0)
			return false;
		
		swapPrios(thisTicket, ticketsBefore.iterator().next());
		return true;
	}
	
	private void swapPrios(final TicketEntry t1, final TicketEntry t2) {
		// use three corner swap in case column prio is distinct indexed
		final int tmpPrio=jdbcTemplate.queryForObject("select max(prio)+1000 from tickets", Integer.class);
		updatePrio(t1.getTicket(), tmpPrio);
		updatePrio(t2.getTicket(), t1.getPrio());
		updatePrio(t1.getTicket(), t2.getPrio());
	}

	private void updatePrio(final String ticket, final int prio)  {
		jdbcTemplate.update(
				"update tickets set prio=? where ticket= ?",
				prio, ticket);
	}

	@Override
	public int removeTicketEntry(final String ticket) {
		return jdbcTemplate.update(
				"delete from tickets where ticket=?",
				ticket);
	}

}
