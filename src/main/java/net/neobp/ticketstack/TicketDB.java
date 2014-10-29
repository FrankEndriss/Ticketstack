package net.neobp.ticketstack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/** Simple database for TicketEntry objects.
 */
public class TicketDB {
	
	/** TODO use a List sorted by prio.
	 * This is a map TicketEntry.getTicket() -> TicketEntry
	 **/
	private final static Map<String, TicketEntry> tickets=new HashMap<String, TicketEntry>();
	
	/** Database directory, injected in init() */
	private static File dbDir;
	private static File dbFile;
	
	/** Loads data from persistence to member tickets
	 */
	private static void load() {
		InputStream in=null;
		try {
			in=new FileInputStream(dbFile);
		}catch(Exception e) {
			throw new RuntimeException("PersistenceException while loading, data not readable", e) ;
		}
		
		Properties props=new Properties();
		try {
			props.load(in);
		} catch (IOException e) {
			throw new RuntimeException("PersistenceException while loading data", e) ;
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				// ignore
			}
		}
		
		int i=0;
		while(true) {
			final String propId="_ticket_"+(i++);
			String ticketId=props.getProperty(propId);
			if(ticketId==null)
				break;
			
			TicketEntry ticketEntry=new TicketEntry();
			ticketEntry.setTicket(ticketId);
			ticketEntry.setText(props.getProperty(propId+".text", ""));
			ticketEntry.setPrio(Integer.parseInt(props.getProperty(propId+".prio", "0")));
			tickets.put(ticketId, ticketEntry);
		}
	}
	
	/** Saves member tickets to persistence
	 */
	private static void save() {
		// fairly simple format using Properties
		// for every ticket there is an entry "_ticket_<some_number>=<ticketId>"
		// and for every entry of _ticket_<number> there are two more entries
		// _ticket_<some_number>.prio=<number>, and
		// _ticket_<some_number>.text=<text>

		final Properties props=new Properties();
		int i=0;
		for(TicketEntry t : tickets.values()) {
			final String propId="_ticket_"+(i++);
			props.put(propId, t.getTicket());
			props.put(propId+".prio", ""+t.getPrio());
			props.put(propId+".text", ""+t.getText());
		}
		
		OutputStream out=null;
		try {
			out=new FileOutputStream(dbFile);
			props.store(out, "saved on "+new Date());
			out.close();
		}catch(Exception e) {
			throw new RuntimeException("PersistenceException while saving to: "+dbFile.getAbsolutePath(), e) ;
		}
	}

	public static TicketEntry getTicketEntry(final String id) {
		return tickets.get(id);
	}
	
	public static List<TicketEntry> getAllTicketEntries() {
		List<TicketEntry> retList=new ArrayList<TicketEntry>(tickets.values());
		Collections.sort(retList, prioComparator);
		return retList;
	}
	
	/** Removes a TicketEntry
	 * @param ticket
	 * @return true if found and removed, false if notfound
	 */
	public static synchronized void removeTicketEntry(final String id) {
		if(tickets.remove(id)==null)
			throw new IllegalArgumentException("notfound: "+id);
		save();
	}
	
	/** Inserts the ticket ticketEntry as first ticket.
	 * Prios of all other Tickets are adjusted
	 * @param ticketEntry
	 */
	public synchronized static void insertTicket(final TicketEntry ticketEntry) {
		if(tickets.get(ticketEntry.getTicket())!=null)
			throw new IllegalArgumentException("ticket exists");
			
		final List<TicketEntry> ltickets=getAllTicketEntries();

		// Prio of new ticket
		final int firstPrio=ltickets.size()>0?
				ltickets.get(0).getPrio():
				1;
		ticketEntry.setPrio(firstPrio);
		// adjust prio of all other tickets
		for(TicketEntry te : ltickets) {
			te.setPrio(te.getPrio()+1);
		}
		
		tickets.put(ticketEntry.getTicket(), ticketEntry);
		save();
	}
	
	/** Update/Insert of ticket. ticket.getTicket() must not be null, primaryKey.
	 * @param ticket the updated ticket
	 */
	public static synchronized void updateTicketEntry(final TicketEntry ticket) {
		final TicketEntry lTicket=tickets.get(ticket.getTicket());
		if(lTicket==null)
			throw new RuntimeException("Notfound ticket: "+ticket.getTicket());

		lTicket.setText(ticket.getText()!=null?ticket.getText():"");
		save();
	}
	

	private final static Comparator<TicketEntry> prioComparator=new Comparator<TicketEntry>() {
			@Override
			public int compare(TicketEntry o1, TicketEntry o2) {
				return o1.getPrio()-o2.getPrio();
			}
		};

	/** Swap priority with the ticket in the list just after this ticket.
	 * Synchronized since two tickets are updated atomically
	 * @param id of the ticket to move down
	 */
	public static synchronized void moveTicketDown(final String id) {
		final TicketEntry ticketEntry=getTicketEntry(id);
		if(ticketEntry==null)
			throw new IllegalArgumentException("notfound: "+id);
		
		List<TicketEntry> entries=new ArrayList<TicketEntry>(tickets.values());
		Collections.sort(entries, prioComparator);
		
		int idx=Collections.binarySearch(entries, ticketEntry, prioComparator);
		System.out.println("found at: "+idx);
		if(idx>=0 && idx<entries.size()-1) { // found and not last, swap prio with next ticket
			int nextPrio=entries.get(idx+1).getPrio();
			entries.get(idx+1).setPrio(ticketEntry.getPrio());
			ticketEntry.setPrio(nextPrio);
		}
		save();
	}

	/** Swap priority with the ticket in the list just before this ticket.
	 * Synchronized since two tickets are updated atomically
	 * @param id of the ticket to move up
	 */
	public static synchronized void moveTicketUp(final String id) {
		final TicketEntry ticketEntry=getTicketEntry(id);
		if(ticketEntry==null)
			throw new IllegalArgumentException("notfound: "+id);
		
		List<TicketEntry> entries=new ArrayList<TicketEntry>(tickets.values());
		Collections.sort(entries, prioComparator);
		
		int idx=Collections.binarySearch(entries, ticketEntry, prioComparator);
		System.out.println("found at: "+idx);
		if(idx>0) { // found and not first, swap prio with previous ticket
			int prevPrio=entries.get(idx-1).getPrio();
			entries.get(idx-1).setPrio(ticketEntry.getPrio());
			ticketEntry.setPrio(prevPrio);
		}
		save();
	}

	/** Wipes out the complete database.
	 * TODO: Wenn Mandantenfähig, dann wipe(mandant)
	 */
	/*
	public static void wipe() {
		tickets.clear();

		// Testdata
		TicketEntry t1=new TicketEntry();
		t1.setTicket("SENVION_W-73");
		t1.setText("text zu senvion-w73");
		t1.setPrio(4);
		tickets.put(t1.getTicket(), t1);

		TicketEntry t2=new TicketEntry();
		t2.setTicket("SENVION_W-65");
		t2.setText("text zu senvion-w65");
		t2.setPrio(3);
		tickets.put(t2.getTicket(), t2);

		TicketEntry t3=new TicketEntry();
		t3.setTicket("SENVION_N-25");
		t3.setText("text zu senvion-n25.... langer text");
		t3.setPrio(1);
		tickets.put(t3.getTicket(), t3);
	}
	*/

	/** Injection of database directory path.
	 * @param pdbDir where the data is stored, this needs to be a path to a directory, or a path where a 
	 * directory can be created. Obviously the directory must be readable and writable.
	 * @throws IOException 
	 */
	public static void init(final String pdbDir) throws IOException {
		dbDir=new File(pdbDir);
		if(!dbDir.exists()) {
			dbDir.mkdirs();
		}
		if(!dbDir.isDirectory() || !dbDir.canRead() || !dbDir.canWrite())
			throw new IllegalArgumentException("something wrong with dbDir: "+dbDir.getAbsolutePath());
		
		dbFile=new File(dbDir, "ticketstack.db");
		if(!dbFile.exists()) {
			FileWriter fw=new FileWriter(dbFile);
			fw.write("// Ticketstack init");
			fw.close();
		}
		if(dbFile.isDirectory() || !dbFile.canRead() || !dbFile.canWrite())
			throw new IllegalArgumentException("something wrong with dbFile: "+dbFile.getAbsolutePath());

		load();
	}
}
