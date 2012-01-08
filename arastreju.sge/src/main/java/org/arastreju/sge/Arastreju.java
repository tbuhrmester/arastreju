/*
 * Copyright (C) 2012 lichtflut Forschungs- und Entwicklungsgesellschaft mbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.arastreju.sge;

import java.lang.reflect.Constructor;
import java.util.Properties;

import org.arastreju.sge.security.Identity;
import org.arastreju.sge.spi.ArastrejuGateFactory;
import org.arastreju.sge.spi.GateContext;

/**
 * <p>
 *  Central API class for obtaining an {@link ArastrejuGate}.
 * </p>
 * 
 * <p>
 *  You obtain an Arastreju instance by one of the <code>getInstance(...)</code> methods, where you
 *  can pass the profile to be used (see {@link ArastrejuProfile}).
 * </p>
 * 
 * <p>
 *  Created Jan 4, 2011
 * </p>
 * 
 * @author Oliver Tigges
 */
public final class Arastreju {

	private final static Arastreju DEFAULT_INSTANCE = new Arastreju();

	// -----------------------------------------------------

	private final String factoryClass;
	private final ArastrejuGateFactory factory;
	private final ArastrejuProfile profile;

	// -----------------------------------------------------

	/**
	 * Get the Arastreju instance for the default profile.
	 * @return the instance.
	 */
	public static Arastreju getInstance() {
		return DEFAULT_INSTANCE;
	}
	
	/**
	 * Get a Arastreju instance for a given profile.
	 * A profile describes the binding to the graph store (e.g. Neo4j).
	 * @param profile The name/path of the Arastreju profile.
	 * @return the instance
	 */
	public static Arastreju getInstance(final String profile) {
		return new Arastreju(profile);
	}
	
	/**
	 * Get a Arastreju instance for a given profile.
	 * A profile describes the binding to the graph store (e.g. Neo4j).
	 * @param profile The name/path of the Arastreju profile.
	 * @param properties Additional properties.
	 * @return the instance
	 */
	public static Arastreju getInstance(final String profile, final Properties properties) {
		return getInstance(ArastrejuProfile.read(profile).addProperties(properties));
	}
	
	/**
	 * Get a Arastreju instance for a given profile.
	 * A profile describes the binding to the graph store (e.g. Neo4j).
	 * @param profile An initialized ArastrejuProfile.
	 * @return the instance
	 */
	public static Arastreju getInstance(final ArastrejuProfile profile) {
		return new Arastreju(profile);
	}

	// -----------------------------------------------------

	/**
	 * Login into the Gate using given username and credentials.
	 * @param username The unique username.
	 * @param credential The user's credential.
	 * @return The corresponding {@link ArastrejuGate}.
	 */
	public ArastrejuGate login(final String username, final String credential) {
		final GateContext ctx = createGateContext(username, credential);
		return factory.create(ctx);
	}

	/**
	 * Obtain the root context. Use Carefully! No login will be performed but
	 * the ArastrejuGate will be used in root context.
	 * 
	 * <p>
	 *  Specific providers can deny root access. Or allow root access only as long
	 *  as user 'root' has no credential set. 
	 * </p>
	 * 
	 * @return The ArastrejuGate for the root context.
	 */
	public ArastrejuGate rootContext() {
		return rootContext(null);
	}
	
	/**
	 * Obtain an anonymous context for unidentified (guest) users.
	 * 
	 * <p>
	 *  Specific providers can deny anonymous access.
	 * </p>
	 * 
	 * @return The ArastrejuGate for the root context.
	 */
	public ArastrejuGate anonymousContext() {
		return login(Identity.ANONYMOUS, null);
	}
	
	/**
	 * Obtain the root context. Use Carefully! ArastrejuGate will be used in root context.
	 * 
	 * <p>
	 *  Specific providers can deny root access. 
	 * </p>
	 * 
	 * @param credential The credential of user 'root'.
	 * @return The ArastrejuGate for the root context.
	 */
	public ArastrejuGate rootContext(final String credential) {
		return login(Identity.ROOT, credential);
	}

	// -----------------------------------------------------
	
	/**
	 * Create and initialize the Gate Context.
	 */
	private GateContext createGateContext(final String user, final String credential) {
		return new GateContext(profile).setUsername(user).setCredential(credential);
	}
	
	// -- PRIVATE CONSTRUCTORS -----------------------------

	/**
	 * Private constructor.
	 */
	private Arastreju() {
		this(ArastrejuProfile.read());
	}
	
	/**
	 * Private constructor.
	 * @param profile path to the profile file.
	 */
	private Arastreju(final String profile) {
		this(ArastrejuProfile.read(profile));
	}
	
	/**
	 * Private constructor.
	 * @param profile path to the profile file.
	 */
	@SuppressWarnings("rawtypes")
	private Arastreju(final ArastrejuProfile profile) {
		this.profile = profile;
		this.factoryClass = profile.getProperty(ArastrejuProfile.GATE_FACTORY);
		try {
			final Constructor constructor = 
					Class.forName(factoryClass).getConstructor(ArastrejuProfile.class);
			this.factory = (ArastrejuGateFactory) constructor.newInstance(profile);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
