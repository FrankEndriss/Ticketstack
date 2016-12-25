package net.neobp.ticketstack.vaadingui;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

import net.neobp.ticketstack.TicketEntry;

public class TicketEntryContainer implements Container {
	private final Map<String, TicketEntryItem> ticketEntries=new HashMap<String, TicketEntryItem>();
	
	private final Map<String, Class<?>> containerProperties=new HashMap<String, Class<?>>();
	{
		containerProperties.put("Up", Button.class);
		containerProperties.put("Down", Button.class);
		containerProperties.put("Ticket", Link.class);
		containerProperties.put("Text", VerticalLayout.class);
		containerProperties.put("State", ComboBox.class);
	}

	@Override
	public Item getItem(Object itemId) {
		return null;
	}

	@Override
	public Collection<?> getContainerPropertyIds() {
		return containerProperties.keySet();
	}

	@Override
	public Collection<?> getItemIds() {
		return ticketEntries.keySet();
	}

	@Override
	public Property<?> getContainerProperty(Object itemId, Object propertyId) {
		TicketEntryItem item=ticketEntries.get(itemId);
		if(item!=null) {
			return item.getItemProperty(propertyId);
		} else
			return null;
	}

	@Override
	public Class<?> getType(Object propertyId) {
		return containerProperties.get(propertyId);
	}

	@Override
	public int size() {
		return ticketEntries.size();
	}

	@Override
	public boolean containsId(Object itemId) {
		return ticketEntries.containsKey(itemId);
	}

	//TODO
	//...
	
	@Override
	public Item addItem(Object itemId) throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object addItem() throws UnsupportedOperationException {
		return null;
	}

	@Override
	public boolean removeItem(Object itemId) throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addContainerProperty(Object propertyId, Class<?> type, Object defaultValue)
			throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeContainerProperty(Object propertyId) throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAllItems() throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return false;
	}

	private class TicketEntryItem implements Item {

		@Override
		public Property getItemProperty(Object id) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Collection<?> getItemPropertyIds() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean addItemProperty(Object id, Property property) throws UnsupportedOperationException {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean removeItemProperty(Object id) throws UnsupportedOperationException {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
}
