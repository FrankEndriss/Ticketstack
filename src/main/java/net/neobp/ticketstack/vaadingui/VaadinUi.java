package net.neobp.ticketstack.vaadingui;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Theme;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Grid;
import com.vaadin.ui.UI;

import net.neobp.ticketstack.TicketEntry;
import net.neobp.ticketstack.TicketEntryDao;

@SpringUI
@Theme("valo")
public class VaadinUi extends UI {
	private final TicketEntryDao tedao;
	private final Grid grid;

	@Autowired
	public VaadinUi(final TicketEntryDao tedao) {
		this.tedao=tedao;
		this.grid=new Grid();
	}

	@Override
	protected void init(VaadinRequest request) {
		/*
		Button button=new Button("Hello world!");
		button.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				Notification.show("some notification, caused by click");
			}
		});
		*/
		setContent(grid);
		listTickets();
	}
	
	private void listTickets() {
		grid.setContainerDataSource(new BeanItemContainer<TicketEntry>(TicketEntry.class, tedao.getAllTicketEntries()));
	}

}
