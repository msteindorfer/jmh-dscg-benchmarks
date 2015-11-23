/*******************************************************************************
 * Copyright (c) 2012-2013 CWI
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *
 *   * Michael Steindorfer - Michael.Steindorfer@cwi.nl - CWI  
 *******************************************************************************/
package nl.cwi.swat.jmh_dscg_benchmarks.impl.persistent.clojure;

import nl.cwi.swat.jmh_dscg_benchmarks.api.JmhMap;
import nl.cwi.swat.jmh_dscg_benchmarks.api.JmhMapWriter;
import nl.cwi.swat.jmh_dscg_benchmarks.api.JmhValue;
import clojure.lang.ITransientMap;
import clojure.lang.PersistentHashMap;

class ClojureMapWriter implements JmhMapWriter {

	protected ITransientMap xs;

	protected ClojureMapWriter() {
		super();

		this.xs = PersistentHashMap.EMPTY.asTransient();
	}

	@Override
	public void put(JmhValue key, JmhValue value) {
		xs = (ITransientMap) xs.assoc(key, value);
	}

	@Override
	public void putAll(JmhMap map) {
		for (JmhValue k : map) {
			xs = (ITransientMap) xs.assoc(k, map.get(k));
		}
	}

	@Override
	public void putAll(java.util.Map<JmhValue, JmhValue> map) {
		for (JmhValue k : map.keySet()) {
			xs = (ITransientMap) xs.assoc(k, map.get(k));
		}
	}

	@Override
	public JmhMap done() {
		return new ClojureMap(xs.persistent());
	}

}