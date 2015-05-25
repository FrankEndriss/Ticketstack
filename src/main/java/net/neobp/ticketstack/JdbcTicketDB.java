package net.neobp.ticketstack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Stateless;
import javax.sql.DataSource;

@Singleton
//@Local
// use default Transaction: @TransactionAttribute(value=TransactionAttributeType.REQUIRED)
public class JdbcTicketDB implements TicketDBIf {
	
	@Resource(name="jdbc/DefaultDataSource")
	private DataSource dataSource;
	
	public int getVersion() {
		return 1;
	}

	@PostConstruct
	void init() {
		System.err.println("in JdbcTicketDB.init()");
		Connection con=null;
		Statement stat=null;
		final Map<String, String> dbConfig=new HashMap<String, String>();
		try {
			con = dataSource.getConnection();
			stat=con.createStatement();
			ResultSet rs=stat.executeQuery("select * from dbinfo");
			while(rs.next())
				dbConfig.put(rs.getString(1), rs.getString(2));
			int dbVersion=Integer.parseInt(dbConfig.get("version"));
			if(dbVersion!=getVersion())
				doUpdate(dbVersion, con);
		}catch(SQLException e) {
			try {
				basicInit(con);
				// try again
				init();
			} catch (SQLException e1) {
				throw new RuntimeException("Exception while basicInit()", e1);
			}
		} finally {
			if(stat!=null)
				try {
					stat.close();
				} catch (SQLException e) {
					// ignore
				}
		}
	}
	
	private void doUpdate(final int oldDbVersion, final Connection con) throws SQLException {
		if(oldDbVersion<1) { // init first time
			try(Statement stat=con.createStatement()) {
				stat.executeQuery("CREATE TABLE tickets(id char(256) primary key, text varchar(2048), prio integer);");
				stat.executeQuery("CREATE TABLE ticket_time(id char(256), from char(32) not null, to char(32));");
				stat.executeQuery("UPDATE dbinfo SET confValue='1' WHERE confKey='version' ;");
			}
		}
	}

	private void basicInit(Connection con) throws SQLException {

		Statement stat=null;
		try {
			stat=con.createStatement();
			stat.executeQuery("create table dbinfo( confKey varchar(1024) primary key, confValue varchar(1024));");
			stat.executeQuery("insert into dbinfo values('version', '0'); ");
			stat.close();

		} finally {
			if(stat!=null)
				try {
					stat.close();
				} catch (SQLException e) {
					// ignore
				}
		}
	}

	@Override
	public TicketEntry getTicketEntry(String id) {
		Connection con;
		PreparedStatement stat=null;
		try {
			// should use Connection pool or single connection
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
		Connection con;
		Statement stat=null;
		try {
			// should use Connection pool or single connection
			con = dataSource.getConnection();
			stat=con.createStatement();
			final ResultSet rs=stat.executeQuery("select * from tickets order by prio ;");
			List<TicketEntry> retList=new ArrayList<TicketEntry>();
			while(rs.next()) {
				TicketEntry te=new TicketEntry();
				te.setTicket(rs.getString("id"));
				te.setText(rs.getString("text"));
				te.setPrio(rs.getInt("prio"));
				retList.add(te);
			}
			return retList;
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
