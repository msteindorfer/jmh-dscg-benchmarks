/*******************************************************************************
 * Copyright (c) 2016 CWI All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *
 * * Michael Steindorfer - Michael.Steindorfer@cwi.nl - CWI
 *******************************************************************************/
package io.usethesource.criterion.impl.persistent.pcollections;

import java.util.Iterator;
import java.util.Map.Entry;

import org.pcollections.HashPMap;

import io.usethesource.criterion.api.JmhMap;
import io.usethesource.criterion.api.JmhValue;

public final class PcollectionsMap implements JmhMap {

  private final HashPMap<JmhValue, JmhValue> content;

  protected PcollectionsMap(HashPMap<JmhValue, JmhValue> content) {
    this.content = content;
  }

  @Override
  public boolean isEmpty() {
    return content.isEmpty();
  }

  @Override
  public int size() {
    return content.size();
  }

  @Override
  public JmhMap put(JmhValue key, JmhValue value) {
    return new PcollectionsMap(content.plus(key, value));
  }

  @Override
  public JmhMap removeKey(JmhValue key) {
    return new PcollectionsMap(content.minus(key));
  }

  @Override
  public boolean containsKey(JmhValue key) {
    return content.containsKey(key);
  }

  @Override
  public boolean containsValue(JmhValue value) {
    return content.containsValue(value);
  }

  @Override
  public JmhValue get(JmhValue key) {
    return content.get(key);
  }

  @Override
  public int hashCode() {
    return content.hashCode();
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (other == null) {
      return false;
    }

    if (other instanceof PcollectionsMap) {
      PcollectionsMap that = (PcollectionsMap) other;

      if (this.size() != that.size()) {
        return false;
      }

      return content.equals(that.content);
    }

    return false;
  }

  @Override
  public Iterator<JmhValue> iterator() {
    return content.keySet().iterator();
  }

  @Override
  public Iterator<JmhValue> valueIterator() {
    return content.values().iterator();
  }

  @Override
  public Iterator<Entry<JmhValue, JmhValue>> entryIterator() {
    return content.entrySet().iterator();
  }

}