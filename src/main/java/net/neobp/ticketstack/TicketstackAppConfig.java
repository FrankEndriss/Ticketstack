package net.neobp.ticketstack;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;


@ApplicationPath("rest")
public class TicketstackAppConfig extends Application {

	/*
	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> set=new HashSet<Class<?>>();
		set.add(net.neobp.ticketstack.TicketEntryResource.class);
		set.add(net.neobp.ticketstack.JdbcTicketDB.class);
		return set;
	}
	*/
}
