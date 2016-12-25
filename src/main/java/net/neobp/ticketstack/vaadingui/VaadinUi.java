package net.neobp.ticketstack.vaadingui;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Theme;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import net.neobp.ticketstack.TicketEntry;
import net.neobp.ticketstack.TicketEntryDao;
import net.neobp.ticketstack.TicketEntryState;

@SpringUI(path="vaadin")
@Theme("valo")
public class VaadinUi extends UI {
	private final TicketEntryDao tedao;
	private final Table table;
	private final IndexedContainer tableDataSource;

	private final TextField ticketid_textfield;
	private final TextArea tickettext_textarea;

	@Autowired
	public VaadinUi(final TicketEntryDao tedao) {
		this.tedao=tedao;
		this.tableDataSource=new IndexedContainer();
		this.table=createTable(tableDataSource);
		this.ticketid_textfield=new TextField("TicketID");
		this.tickettext_textarea=new TextArea("Text");
		this.tickettext_textarea.setRows(4);
	}
	
	private static Table createTable(IndexedContainer container) {
		final Table table=new Table("Tickets", container);
		table.addContainerProperty("Up", Button.class, null);
		table.addContainerProperty("Down", Button.class, null);
		table.addContainerProperty("Ticket", Link.class, null);
		table.addContainerProperty("Text", VerticalLayout.class, null);
		table.addContainerProperty("State", ComboBox.class, null);
		return table;
	}

	@Override
	protected void init(VaadinRequest request) {
		VerticalLayout vertical_layout=new VerticalLayout();
		Label titel=new Label("<h2>Ticketstack<h2>", ContentMode.HTML);
		
		vertical_layout.addComponent(titel);
		final Panel tablePanel=new Panel("Tickets");
		tablePanel.setWidth(98, Unit.PERCENTAGE);
		table.setWidth(100, Unit.PERCENTAGE);
		tablePanel.setContent(table);
		vertical_layout.addComponent(tablePanel);
		vertical_layout.addComponent(createEditor());
		
		setContent(vertical_layout);
		reloadTickets();
	}
	
	private Component createEditor() {
		final FormLayout form=new FormLayout();
		form.addComponent(ticketid_textfield);
		form.addComponent(tickettext_textarea);

		Button addBtn=new Button("Add/Upd");
		addBtn.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				final String ticketid=ticketid_textfield.getValue();
				final String tickettext=tickettext_textarea.getValue();

				// upsert ticket
				TicketEntry ent=tedao.getTicketEntry(ticketid);
				if(ent!=null) {
					tedao.updateTicketText(ent, tickettext);
				} else {
					ent=new TicketEntry();
					ent.setTicket(ticketid);
					ent.setText(tickettext);
					tedao.insertTicket(ent);
				}
				reloadTickets();
			}
		});


		tickettext_textarea.setWidth(100, Unit.PERCENTAGE);
		//form.setWidth(100, Unit.PERCENTAGE);
		final VerticalLayout vlayout=new VerticalLayout();
		//vlayout.setWidth(80, Unit.PERCENTAGE);
		vlayout.addComponent(form);
		vlayout.addComponent(addBtn);
		final Panel panel=new Panel("Edit");
		panel.setContent(vlayout);
		panel.setWidth(98, Unit.PERCENTAGE);
		return panel;
	}
	
	/**
	 * This method loads all Tickets from the backend and synchronizes
	 * the current list of all tickets to the new list.
	 */
	private void reloadTickets() {
		Collection<TicketEntry> tickets=tedao.getAllTicketEntries();

		// first index the existing tickets by ID
		Map<String, TicketEntry> backendticketmap=new HashMap<String, TicketEntry>();
		for(TicketEntry entry : tickets)
			backendticketmap.put(entry.getTicket(), entry);
		
		// second run throug the table container, 
		// * updating all items that needs to be updated
		// * removing all items that needs to be removed
		for(Object itemid : tableDataSource.getItemIds()) {
			final Item item=tableDataSource.getItem(itemid);
			final TicketEntry entry=backendticketmap.get(itemid);
			if(entry==null)	// removed
				tableDataSource.removeItem(itemid);
			else {
				updateItem(item, entry);
				backendticketmap.remove(itemid);
			}
		}
		
		// add the remaining ticketEntry, since these are new ones
		for(TicketEntry entry : backendticketmap.values()) {
			Item newitem=tableDataSource.addItem(entry.getTicket());
			updateItem(newitem, entry);
		}
	}
	
	/**
	 * This method puts the data of ticketentry into item if any data in
	 * item needs to be updated.
	 * @param item The row in the tables model
	 * @param ticketentry The real data model
	 */
	private void updateItem(final Item item, final TicketEntry ticketentry) {
		/*
		final Property<String> textprop=item.getItemProperty("text");
		final String itemtext=textprop.getValue();
		final String entryText=ticketentry.getText();
		if(!entryText.equals(itemtext))
			textprop.setValue(entryText);
			*/
		
		// put ticketentry into item as invisible property "_data"
		Property<TicketEntry> dataprop=(Property<TicketEntry>) item.getItemProperty("_data");
		dataprop.setValue(ticketentry);
		
		Property<Button> upprop=item.getItemProperty("up");
		Button upBtn=upprop.getValue();
		if(upBtn==null) {
			upBtn=new Button("Up");
			upBtn.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					tedao.moveTicketUp(ticketentry.getTicket());
					reloadTickets();	// repaint all
				}
			});
			upprop.setValue(upBtn);
		}
		
		Property<Button> downprop=item.getItemProperty("down");
		Button downBtn=downprop.getValue();
		if(downBtn==null) {
			downBtn=new Button("Down");
			downBtn.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					tedao.moveTicketDown(ticketentry.getTicket());
					reloadTickets();	// repaint all
				}
			});
			downprop.setValue(downBtn);
		}
		
		Property<Link> ticketprop=item.getItemProperty("Ticket");
		Link ticketlink=ticketprop.getValue();
		if(ticketlink==null) {
			ticketlink=new Link(ticketentry.getTicket(),
				new ExternalResource("https://support.neo-business.info/browse/"+ticketentry.getTicket()));
			ticketprop.setValue(ticketlink);
		}

		Property<VerticalLayout> textprop=item.getItemProperty("Text");
		String textstring=((TicketEntry)(item.getItemProperty("_data").getValue())).getText();
		VerticalLayout textlayout=textprop.getValue();
		if(textlayout==null || !textstring.equals(ticketentry.getText())) {
			VerticalLayout klickableLayout=new VerticalLayout();
			Label textlabel=new Label(ticketentry.getText());
			klickableLayout.addComponent(textlabel);
			klickableLayout.addLayoutClickListener(new LayoutClickListener()  {
				@Override
				public void layoutClick(LayoutClickEvent event) {
					ticketid_textfield.setValue(ticketentry.getTicket());
					tickettext_textarea.setValue(ticketentry.getText());
				}
			});
			textprop.setValue(klickableLayout);
		}	
		
		Property<ComboBox> stateprop=item.getItemProperty("State");
		TicketEntryState state=(TicketEntryState)stateprop.getValue().getValue();

		ComboBox combobox=stateprop.getValue();
		if(combobox==null) {
			combobox=new ComboBox();
			combobox.addItems(TicketEntryState.DO, TicketEntryState.WAIT, TicketEntryState.RESUB, TicketEntryState.DONE);
			combobox.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					TicketEntryState newstate=(TicketEntryState) event.getProperty().getValue();
					if(newstate!=ticketentry.getState()) {
						tedao.updateTicketState(ticketentry, newstate);
						reloadTickets();	// repaint all
					}
				}
			});
			combobox.setNullSelectionAllowed(false);
			combobox.setTextInputAllowed(false);
		}
		combobox.setValue(ticketentry.getState());

	}

	private void listTickets() {
		tableDataSource.removeAllItems();
		Collection<TicketEntry> tickets=tedao.getAllTicketEntries();

		table.setPageLength(Math.min(tickets.size()+1, 8));

		int idx=0;
		for(final TicketEntry entry : tickets) {
			Item row=tableDataSource.getItem(entry.getTicket());
			if(row==null)
				row=tableDataSource.addItem(entry.getTicket());
			
			if(idx>0) { // not on first row
				final Button upBtn=new Button("Up");
				upBtn.addClickListener(new Button.ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						tedao.moveTicketUp(entry.getTicket());
						listTickets();	// repaint all
					}
				});
				row.getItemProperty("Up").setValue(upBtn);
			}
			
			if(idx<tickets.size()-1) { // not on last row
				final Button downBtn=new Button("Down");
				downBtn.addClickListener(new Button.ClickListener() {
					@Override
					public void buttonClick(ClickEvent event) {
						tedao.moveTicketDown(entry.getTicket());
						listTickets();	// repaint all
					}
				});
				row.getItemProperty("Down").setValue(downBtn);
			}
			
			idx+=1;
			
			final ComboBox combobox=new ComboBox();
			combobox.addItems(TicketEntryState.DO, TicketEntryState.WAIT, TicketEntryState.RESUB, TicketEntryState.DONE);
			combobox.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChange(ValueChangeEvent event) {
					TicketEntryState newstate=(TicketEntryState) event.getProperty().getValue();
					if(newstate!=entry.getState()) {
						tedao.updateTicketState(entry, newstate);
						//listTickets();	// repaint all
					}
				}
			});
			combobox.setNullSelectionAllowed(false);
			combobox.setTextInputAllowed(false);
			combobox.setValue(entry.getState());

			/*
			combobox.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					tedao.removeTicketEntry(entry.getTicket());
					listTickets();	// repaint all
				}
			});
			*/
			row.getItemProperty("State").setValue(combobox);
			
			final Link link=new Link(entry.getTicket(),
					new ExternalResource("https://support.neo-business.info/browse/"+entry.getTicket()));
			row.getItemProperty("Ticket").setValue(link);

			Label textLabel=new Label(entry.getText());
			VerticalLayout klickableLayout=new VerticalLayout();
			klickableLayout.addComponent(textLabel);
			klickableLayout.addLayoutClickListener(new LayoutClickListener()  {
				@Override
				public void layoutClick(LayoutClickEvent event) {
					ticketid_textfield.setValue(entry.getTicket());
					tickettext_textarea.setValue(entry.getText());
				}
			});
			row.getItemProperty("Text").setValue(klickableLayout);
			
		}
	}

}
