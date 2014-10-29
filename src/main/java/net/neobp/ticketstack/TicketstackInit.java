package net.neobp.ticketstack;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class TicketstackInit implements ServletContextListener {

	/* Called on startup of the servlet
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextInitialized(final ServletContextEvent evt) {
		String dbDir=evt.getServletContext().getInitParameter("net.neobp.ticketstack.database");
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
