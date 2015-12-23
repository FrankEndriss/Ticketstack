package net.neobp.ticketstack;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
	
	public List<TicketEntry> getAllTicketEntries() {
		return jdbcTemplate.query(
				"select * from tickets order by prio",
				teRowMapper);
	}

	@Transactional
	public void insertTicket(TicketEntry ticketEntry) {
		jdbcTemplate.update(
				"insert into tickets (prio, text, ticket) values(?, ?, ?)",
				ticketEntry.getPrio(),
				ticketEntry.getText(),
				ticketEntry.getTicket());
	}

	public TicketEntry getTicketEntry(final String ticket) {
		return jdbcTemplate.queryForObject(
				"select * from tickets where ticket = ?",
				new Object[]{ ticket },
				teRowMapper);
	}

	private class TicketEntryRowMapper implements RowMapper<TicketEntry> {
		public TicketEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
			final TicketEntry te=new TicketEntry();
			te.setPrio(rs.getInt("prio"));
			te.setText(rs.getString("text"));
			te.setTicket(rs.getString("ticket"));
			return te;
		}
	}

	@Transactional
	public void moveTicketDown(String ticket) {
		TicketEntry thisTicket=getTicketEntry(ticket);
		TicketEntry ticketAfter=jdbcTemplate.queryForObject(
				"select * from tickets where prio > ? order by prio limit 1",
				new Object[]{ thisTicket.getPrio() },
				teRowMapper);
		updatePrio(thisTicket.getTicket(), ticketAfter.getPrio());
		updatePrio(ticketAfter.getTicket(), thisTicket.getPrio());
	}

	@Transactional
	public void moveTicketUp(String ticket) {
		TicketEntry thisTicket=getTicketEntry(ticket);
		// select the ticket just before this ticket
		TicketEntry ticketBefore=jdbcTemplate.queryForObject(
				"select * from tickets where prio < ? order by prio desc limit 1",
				new Object[]{ thisTicket.getPrio() },
				teRowMapper);
		updatePrio(thisTicket.getTicket(), ticketBefore.getPrio());
		updatePrio(ticketBefore.getTicket(), thisTicket.getPrio());
	}
	
	private void updatePrio(String ticket, int prio)  {
		jdbcTemplate.update(
				"update tickets set prio=? where ticket= ?",
				prio, ticket);
	}

	public void removeTicketEntry(String ticket) {
		jdbcTemplate.update(
				"delete from tickets where ticket= ?",
				ticket);
	}

}
