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
import javax.ejb.Local;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Stateless;
import javax.sql.DataSource;

@Stateless
@LocalBean
// use default Transaction: @TransactionAttribute(value=TransactionAttributeType.REQUIRED)
public class JdbcTicketDB implements TicketDBIf {
	
	@Resource(name="TicketstackDB")
	private DataSource dataSource;
	
	public int getVersion() {
		return 1;
	}

	@PostConstruct
	void init() {
		System.err.println("in JdbcTicketDB.init(), dataSource="+dataSource);
		final Map<String, String> dbConfig=new HashMap<String, String>();
		try(Connection con=dataSource.getConnection(); Statement stat=con.createStatement()) {
			final ResultSet rs=stat.executeQuery("select confKey, confValue from neodbinfo");
			while(rs.next()) {
				final String s1=rs.getString(1);
				final String s2=rs.getString(2);
				System.err.println("s1="+s1+" s2="+s2);
				dbConfig.put(s1, s2);
			}
		}catch(SQLException e) {
			try(Connection con=dataSource.getConnection()) {
				basicInit(con);
			} catch (SQLException e1) {
				throw new RuntimeException("Exception while basicInit()", e1);
			}
			// try again
			init();
		}
		try(Connection con=dataSource.getConnection()) {
			int dbVersion=Integer.parseInt(dbConfig.get("version"));
			if(dbVersion<getVersion())
				doUpdate(dbVersion, con);
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private void doUpdate(final int oldDbVersion, final Connection con) throws SQLException {
		System.err.println("Updating version from "+oldDbVersion+" to 1");
		if(oldDbVersion<1) { // init first time
			try(Statement stat=con.createStatement()) {
				stat.executeQuery("UPDATE neodbinfo SET confValue='1' WHERE confKey='version' ;");
				stat.executeQuery("CREATE TABLE tickets(id char(256) primary key, text varchar(2048), prio integer);");
				stat.executeQuery("CREATE TABLE ticket_time(id char(256), ttfrom char(32) not null, ttto char(32));");
			}
		}
	}

	private void basicInit(Connection con) throws SQLException {

		Statement stat=null;
		try {
			stat=con.createStatement();
			stat.executeQuery("create table neodbinfo( confKey varchar(1024) primary key, confValue varchar(1024));");
			stat.executeQuery("insert into neodbinfo values('version', '0'); ");
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
		final String statement="INSERT INTO tickets VALUES(?, ?, ?)";
		try(Connection con=dataSource.getConnection(); PreparedStatement stat=con.prepareStatement(statement)) {
			stat.setString(1, ticketEntry.getTicket());
			stat.setString(2, ticketEntry.getText());
			stat.setInt(3, ticketEntry.getPrio());
			stat.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
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
