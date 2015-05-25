package net.neobp.ticketstack;

import java.util.Enumeration;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

//@WebListener
public class TicketstackInit implements ServletContextListener {
	
	@EJB
	private JdbcTicketDB injectedTicketDB;

	/* Called on startup of the servlet
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextInitialized(final ServletContextEvent evt) {
		final ServletContext servletContext=evt.getServletContext();
		System.err.println("TicketstackInit, servletContext="+servletContext);
		System.err.println("TicketstackInit, servlet name="+servletContext.getServletContextName());
		
		Enumeration<String> params=servletContext.getInitParameterNames();
		System.err.println("TicketstackInit, params:");
		while(params.hasMoreElements())
			System.err.println("init-param: "+params.nextElement());
		System.err.println("TicketstackInit, end params.");

		final String dbDir=evt.getServletContext().getInitParameter("net.neobp.ticketstack.database");
		/*
		 * TicketDB should/must be a Bean of name "FileTicketDB"
		try {
			TicketDB.init(dbDir);
		}catch(Exception e) {
			System.err.println("bad database directory: "+dbDir);
			throw new RuntimeException("bad database directory: "+dbDir, e);
		}
		*/
		
		System.err.println("injected ticketDB: "+injectedTicketDB);
				
		
		try {
			System.err.println("obtaining initial context");
			InitialContext cxt = new InitialContext();
			System.err.println("listing it");
			NamingEnumeration ne = cxt.list("");
			while(ne.hasMore())
				System.err.println("ne: "+ne.next());

			System.err.println("obtaining JdbcTicketDB from initial context");
			JdbcTicketDB ticketDB=(JdbcTicketDB) cxt.lookup( "java:module/JdbcTicketDB" );
			System.err.println("did obtained JdbcTicketDB from initial context");
			if(ticketDB == null )
				throw new RuntimeException("Uh oh -- JdbcTicketDB not found while initialization of Ticketstack application!");
			System.err.println("calling JdbcTicketDB.init()");
			ticketDB.init();
			System.err.println("init() finished");

		}catch(Exception e) {
			System.err.println("error while initialization, StackTrace::");
			e.printStackTrace(System.err);
			//throw new RuntimeException(e);
		}

	}

	@Override
	public void contextDestroyed(ServletContextEvent evt) {
		// ignore
	}

}
