/*
 * Copyright (C) 2009 lichtflut Forschungs- und Entwicklungsgesellschaft mbH
 */
package org.arastreju.bindings.neo4j;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.arastreju.bindings.neo4j.impl.Neo4jDataStore;
import org.arastreju.sge.ModellingConversation;
import org.arastreju.sge.apriori.RDFS;
import org.arastreju.sge.model.associations.Association;
import org.arastreju.sge.model.nodes.ResourceNode;
import org.arastreju.sge.model.nodes.SNResource;
import org.arastreju.sge.naming.QualifiedName;
import org.arastreju.sge.naming.SimpleNamespace;
import org.junit.Assert;
import org.junit.Test;


/**
 * <p>
 *  [DESCRIPTION]
 * </p>
 *
 * <p>
 * 	Created Sep 9, 2010
 * </p>
 *
 * @author Oliver Tigges
 */
public class Neo4jModellingConversationTest {
	
	@Test
	public void testInstantiation() throws IOException{
		ModellingConversation mc = new Neo4jModellingConversation(new Neo4jDataStore());
		
		ResourceNode node = new SNResource();
		node.setNamespace(new SimpleNamespace("http://q#"));
		node.setName("N1");
		mc.attach(node);
		
		mc.flush();
		mc.close();
	}
	
	@Test
	public void testFind() throws IOException{
		ModellingConversation mc = new Neo4jModellingConversation(new Neo4jDataStore());
		
		QualifiedName qn = new QualifiedName("http://q#", "N1");
		ResourceNode node = new SNResource(qn);
		mc.attach(node);
		
		ResourceNode node2 = mc.findResource(qn);
		
		assertNotNull(node2);
		
		mc.flush();
		mc.close();
	}
	
	@Test
	public void testMerge() throws IOException{
		ModellingConversation mc = new Neo4jModellingConversation(new Neo4jDataStore());
		
		QualifiedName qn = new QualifiedName("http://q#", "N1");
		ResourceNode node = new SNResource(qn);
		node = mc.attach(node);
		
		node = mc.attach(node);
		
		ResourceNode node2 = mc.findResource(qn);
		
		assertNotNull(node2);
		
		mc.flush();
		mc.close();
	}
	
	
	@Test
	public void testSNViews() throws IOException {
		final Neo4jDataStore store = new Neo4jDataStore();
		final Neo4jModellingConversation mc = new Neo4jModellingConversation(store);
		
		final QualifiedName qnVehicle = new QualifiedName("http://q#", "Verhicle");
		ResourceNode vehicle = new SNResource(qnVehicle);
		vehicle = mc.attach(vehicle);
		
		final QualifiedName qnCar = new QualifiedName("http://q#", "Car");
		ResourceNode car = new SNResource(qnCar);
		car = mc.attach(car);
		
		Association.create(car, RDFS.SUB_CLASS_OF, vehicle, null);
		
		mc.getRegistry().clear();
		
		car = mc.findResource(qnCar);
		vehicle = mc.findResource(qnVehicle);
		
		Assert.assertTrue(car.asClass().isSpecializationOf(vehicle));
		
		mc.flush();
		mc.close();
	}
	
}
