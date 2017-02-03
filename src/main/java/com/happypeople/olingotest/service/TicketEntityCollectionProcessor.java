package com.happypeople.olingotest.service;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/** This class creates the data, and is called upon GET requests
 */
@Component
@Scope("prototype")
public class TicketEntityCollectionProcessor implements EntityCollectionProcessor {

	private OData odata;
	private ServiceMetadata serviceMetadata;

	public void init(final OData odata, final ServiceMetadata serviceMetadata) {
		this.odata=odata;
		this.serviceMetadata=serviceMetadata;
	}

	public void readEntityCollection(final ODataRequest request, final ODataResponse response, final UriInfo uriInfo, final ContentType responseFormat)
			throws ODataApplicationException, ODataLibraryException {

		  // 1st we have retrieve the requested EntitySet from the uriInfo object (representation of the parsed service URI)
		  final List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
		  final UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0); // in our example, the first segment is the EntitySet
		  final EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();

		  // 2nd: fetch the data from backend for this requested EntitySetName
		  // it has to be delivered as EntitySet object
		  final EntityCollection entitySet = getData(edmEntitySet);

		  // 3rd: create a serializer based on the requested format (json)
		  final ODataSerializer serializer = odata.createSerializer(responseFormat);

		  // 4th: Now serialize the content: transform from the EntitySet object to InputStream
		  final EdmEntityType edmEntityType = edmEntitySet.getEntityType();
		  final ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).build();

		  final String id = request.getRawBaseUri() + "/" + edmEntitySet.getName();
		  final EntityCollectionSerializerOptions opts = EntityCollectionSerializerOptions.with().id(id).contextURL(contextUrl).build();
		  final SerializerResult serializerResult = serializer.entityCollection(serviceMetadata, edmEntityType, entitySet, opts);
		  final InputStream serializedContent = serializerResult.getContent();

		  // Finally: configure the response object: set the body, headers and status code
		  response.setContent(serializedContent);
		  response.setStatusCode(HttpStatusCode.OK.getStatusCode());
		  response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
	}

	/** This method reads/creates the raw data of the requested set
	 * @param edmEntitySet
	 * @return the data as an EntityCollection
	 */
	private EntityCollection getData(final EdmEntitySet edmEntitySet) {
		   final EntityCollection ticketsCollection = new EntityCollection();
		   // check for which EdmEntitySet the data is requested
		   if(TicketstackEdmProvider.ES_TICKETS_NAME.equals(edmEntitySet.getName())) {
		       final List<Entity> ticketsList = ticketsCollection.getEntities();

		       // add some sample product entities
		       final Entity e1 = new Entity()
		          .addProperty(new Property(null, "ticket", ValueType.PRIMITIVE, "Ticket-1"))
		          .addProperty(new Property(null, "text", ValueType.PRIMITIVE, "Notebook Basic 15"))
		          .addProperty(new Property(null, "prio", ValueType.PRIMITIVE, 7));
		      e1.setId(createId("Tickets", "Ticket-1"));
		      ticketsList.add(e1);

		      final Entity e2 = new Entity()
		          .addProperty(new Property(null, "ticket", ValueType.PRIMITIVE, "Ticket-2"))
		          .addProperty(new Property(null, "text", ValueType.PRIMITIVE, "1UMTS PDA"))
		          .addProperty(new Property(null, "prio", ValueType.PRIMITIVE, 3));
		      e2.setId(createId("Tickets", "Ticket-2"));
		      ticketsList.add(e2);

		      final Entity e3 = new Entity()
		          .addProperty(new Property(null, "ticket", ValueType.PRIMITIVE, "Ticket-3"))
		          .addProperty(new Property(null, "text", ValueType.PRIMITIVE, "Ergo Screen"))
		          .addProperty(new Property(null, "prio", ValueType.PRIMITIVE, -1));
		      e3.setId(createId("Tickets", "Ticket-3"));
		      ticketsList.add(e3);
		   }

		   return ticketsCollection;
	}

	/** Creates a OData-ID from a domain Id. Should be moved to some utility class.
	 * @param entitySetName name of the entity class (modelId)
	 * @param id Id of the entity object (guid or id)
	 * @return URI referencing that entity object
	 */
	private URI createId(final String entitySetName, final Object domainId) {
	    try {
	        return new URI(entitySetName + "(" + String.valueOf(domainId) + ")");
	    } catch(final URISyntaxException e) {
	        throw new ODataRuntimeException("Unable to create id for entity: " + entitySetName, e);
	    }
	}

}
