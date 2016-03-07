/*******************************************************************************
 * Copyright (c) 2012-2013 CWI
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *
 *    * Michael Steindorfer - Michael.Steindorfer@cwi.nl - CWI
 ******************************************************************************/
package io.usethesource.criterion.impl.persistent.scala

import scala.collection.JavaConversions.mapAsScalaMap

import io.usethesource.criterion.api.JmhValue
import io.usethesource.criterion.api.JmhValueFactory
import io.usethesource.criterion.api.JmhSetMultimap

class ScalaValueFactory extends JmhValueFactory {
	
	def set() = setBuilder.done

	def set(xs: JmhValue*) = {
		val writer = setBuilder
		writer.insert(xs: _*)
		writer.done
	}

	def setBuilder = new ScalaSetBuilder
	
	def map() = mapBuilder.done

	def mapBuilder = new ScalaMapBuilder
	
	def setMultimapBuilder = new ScalaSetMultimapBuilder
	
	def setMultimap = setMultimapBuilder.done
	
	override def toString = "VF_SCALA"

}
