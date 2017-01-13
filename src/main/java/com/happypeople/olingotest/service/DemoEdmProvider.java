package com.happypeople.olingotest.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainerInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.apache.olingo.commons.api.ex.ODataException;

/** This class is the main service to provide the metadata for our OData service implementation.
 * see https://olingo.apache.org/doc/odata4/tutorials/read/tutorial_read.html
 * @author fendriss
 */
public class DemoEdmProvider extends CsdlAbstractEdmProvider {

	// Service Namespace
	public static final String NAMESPACE = "happypeople.OData.Demo";

	// EDM Container
	public static final String CONTAINER_NAME = "Container";
	public static final FullQualifiedName CONTAINER = new FullQualifiedName(NAMESPACE, CONTAINER_NAME);

	// Entity Types Names
	public static final String ET_TICKET_NAME = "Ticket";
	public static final FullQualifiedName ET_TICKET_FQN = new FullQualifiedName(NAMESPACE, ET_TICKET_NAME);

	// Entity Set Names
	public static final String ES_TICKETS_NAME = "Tickets";


	@Override
	public CsdlEntityContainer getEntityContainer() throws ODataException {

		  // create EntitySets
		  final List<CsdlEntitySet> entitySets = new ArrayList<CsdlEntitySet>();
		  entitySets.add(getEntitySet(CONTAINER, ES_TICKETS_NAME));

		  // create EntityContainer
		  final CsdlEntityContainer entityContainer = new CsdlEntityContainer();
		  entityContainer.setName(CONTAINER_NAME);
		  entityContainer.setEntitySets(entitySets);

		  return entityContainer;
	}

	@Override
	public CsdlEntityContainerInfo getEntityContainerInfo(final FullQualifiedName entityContainerName) throws ODataException {

	    // This method is invoked when displaying the Service Document at e.g. http://localhost:8080/DemoService/DemoService.svc
	    if (entityContainerName == null || entityContainerName.equals(CONTAINER)) {
	        final CsdlEntityContainerInfo entityContainerInfo = new CsdlEntityContainerInfo();
	        entityContainerInfo.setContainerName(CONTAINER);
	        return entityContainerInfo;
	    }

	    return null;
	}

	@Override
	public CsdlEntitySet getEntitySet(final FullQualifiedName entityContainer, final String entitySetName) throws ODataException {
		if(entityContainer.equals(CONTAINER)){
			if(entitySetName.equals(ES_TICKETS_NAME)){
				final CsdlEntitySet entitySet = new CsdlEntitySet();
				entitySet.setName(ES_TICKETS_NAME);
				entitySet.setType(ET_TICKET_FQN);
				return entitySet;
			}
		}
		return null;
	}

	@Override
	public CsdlEntityType getEntityType(final FullQualifiedName entityTypeName) throws ODataException {

		if(entityTypeName.equals(ET_TICKET_FQN)) {

			//create EntityType properties
			final CsdlProperty ticket = new CsdlProperty().setName("ticket").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
			final CsdlProperty text = new CsdlProperty().setName("text").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
			final CsdlProperty  prio = new CsdlProperty().setName("prio").setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());

			// create CsdlPropertyRef for Key element
			final CsdlPropertyRef propertyRef = new CsdlPropertyRef();
			propertyRef.setName("ticket");

			// configure EntityType
			final CsdlEntityType entityType = new CsdlEntityType();
			entityType.setName(ET_TICKET_NAME);
			entityType.setProperties(Arrays.asList(ticket, text , prio));
			entityType.setKey(Collections.singletonList(propertyRef));

			return entityType;
		} else
			throw new ODataException("unknown type in getEntityType(): "+entityTypeName);

		// return null;
	}

	@Override
	public List<CsdlSchema> getSchemas() throws ODataException {
		// create Schema
		final CsdlSchema schema = new CsdlSchema();
		schema.setNamespace(NAMESPACE);

		// add EntityTypes
		final List<CsdlEntityType> entityTypes = new ArrayList<CsdlEntityType>();
		entityTypes.add(getEntityType(ET_TICKET_FQN));
		schema.setEntityTypes(entityTypes);

		// add EntityContainer
		schema.setEntityContainer(getEntityContainer());

		// finally, we got only one schema
		return Arrays.asList(schema);
	}
}
