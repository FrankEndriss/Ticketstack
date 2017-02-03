package com.happypeople.olingotest.web;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.edmx.EdmxReference;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.happypeople.olingotest.service.TicketstackEdmProvider;

/** Preferred way to instantiate an Olingo- OData-Service is to create a Servlet,
 * and configure/activate it in the web.xml
 */
@Component
public class OlingoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(OlingoServlet.class);

	private final ApplicationContext applicationContext;
	
	public OlingoServlet(final @Autowired ApplicationContext applicationContext) {
		this.applicationContext=applicationContext;
	}

	@Override
	protected void service(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		try {
			// create odata handler and configure it with CsdlEdmProvider and Processor
			final OData odata = OData.newInstance();
			final ServiceMetadata edm = odata.createServiceMetadata(
					applicationContext.getBean(TicketstackEdmProvider.class),
					new ArrayList<EdmxReference>());
			final ODataHttpHandler handler = odata.createHandler(edm);
			handler.register(applicationContext.getBean(EntityCollectionProcessor.class));

			// let the handler do the work
			handler.process(req, resp);
		} catch (final RuntimeException e) {
			LOG.error("Server Error occurred in OlingoServlet", e);
			throw new ServletException(e);
		}
	}
}
