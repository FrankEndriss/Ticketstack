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
		TicketDB.init(dbDir);
	}

	@Override
	public void contextDestroyed(ServletContextEvent evt) {
		// ignore
	}

}
