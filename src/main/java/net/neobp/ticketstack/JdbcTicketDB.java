package net.neobp.ticketstack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

@Stateless
public class JdbcTicketDB implements TicketDBIf {
	
	private final DataSource dataSource;

	JdbcTicketDB(DataSource dataSource) {
		this.dataSource=dataSource;
	}

	@Override
	public TicketEntry getTicketEntry(String id) {
		Connection con;
		PreparedStatement stat=null;
		try {
			con = dataSource.getConnection();
			stat=con.prepareStatement("select * from tickets where id=?");
			stat.setString(1, id);
			final ResultSet rs=stat.executeQuery();
			if(rs.next()) {
				TicketEntry te=new TicketEntry();
				te.setTicket(rs.getString("id"));
				te.setText(rs.getString("text"));
				te.setPrio(rs.getInt("prio"));
				return te;
			}
		} catch(SQLException e) {
			throw new RuntimeException(e);
		}finally{
			if(stat!=null)
				try {
					stat.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		return null;
	}

	@Override
	public List<TicketEntry> getAllTicketEntries() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeTicketEntry(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertTicket(TicketEntry ticketEntry) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void moveTicketDown(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void moveTicketUp(String id) {
		// TODO Auto-generated method stub
		
	}

}
