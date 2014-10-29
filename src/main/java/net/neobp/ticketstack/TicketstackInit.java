package net.neobp.ticketstack;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

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
	}

	@Override
	public void contextDestroyed(ServletContextEvent evt) {
		// ignore
	}

}
