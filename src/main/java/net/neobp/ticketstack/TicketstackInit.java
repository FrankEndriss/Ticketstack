package net.neobp.ticketstack;

import java.util.Enumeration;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

//@WebListener
public class TicketstackInit implements ServletContextListener {

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
		try {
			TicketDB.init(dbDir);
		}catch(Exception e) {
			System.err.println("bad database directory: "+dbDir);
			throw new RuntimeException("bad database directory: "+dbDir, e);
		}
		
		
		// Init hsqldb database
		try {
			InitialContext cxt = new InitialContext();

			DataSource ds=(DataSource) cxt.lookup( "java:/comp/env/jdbc/TicketstackDB" );
			if ( ds == null )
				throw new RuntimeException("Uh oh -- Data source not found while initialization of Ticketstack application!");

			servletContext.setAttribute("net.neobp.ticketstack.TicketstackDB", new JdbcTicketDB(ds));

		} catch (NamingException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public void contextDestroyed(ServletContextEvent evt) {
		// ignore
	}

}
