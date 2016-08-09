package net.neobp.ticketstack.vaadingui;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Theme;
import com.vaadin.data.Item;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
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

@SpringUI(path="vaadin")
@Theme("valo")
public class VaadinUi extends UI {
	private final TicketEntryDao tedao;
	private final Table table;

	private final TextField ticketid_textfield;
	private final TextArea tickettext_textarea;

	@Autowired
	public VaadinUi(final TicketEntryDao tedao) {
		this.tedao=tedao;
		this.table=createTable();
		this.ticketid_textfield=new TextField("TicketID");
		this.tickettext_textarea=new TextArea("Text");
		this.tickettext_textarea.setRows(4);
	}
	
	private static Table createTable() {
		final Table table=new Table("Tickets");
		table.addContainerProperty("Up", Button.class, null);
		table.addContainerProperty("Down", Button.class, null);
		table.addContainerProperty("Ticket", Link.class, null);
		table.addContainerProperty("Text", VerticalLayout.class, null);
		table.addContainerProperty("Delete", Button.class, null);
		//table.setWidth(80, Unit.PERCENTAGE);
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
		listTickets();
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
				listTickets();
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
	
	private void listTickets() {
		table.removeAllItems();
		Collection<TicketEntry> tickets=tedao.getAllTicketEntries();

		table.setPageLength(Math.min(tickets.size()+1, 8));

		int idx=0;
		for(final TicketEntry entry : tickets) {
			final Object itemid=table.addItem();
			Item row=table.getItem(itemid);
			
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
			
			final Button delBtn=new Button("Del");
			delBtn.addClickListener(new Button.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					tedao.removeTicketEntry(entry.getTicket());
					listTickets();	// repaint all
				}
			});
			row.getItemProperty("Delete").setValue(delBtn);
			
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
