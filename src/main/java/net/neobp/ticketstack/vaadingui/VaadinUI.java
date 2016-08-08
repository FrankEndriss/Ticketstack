package net.neobp.ticketstack.vaadingui;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

@SpringUI
@Theme("valo")
public class VaadinUI extends UI {

	@Override
	protected void init(VaadinRequest request) {
		Button button=new Button("Hello world!");
		button.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				Notification.show("some notification, caused by click");
			}
		});
		setContent(button);
	}

}
