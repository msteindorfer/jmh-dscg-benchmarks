/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.criterion.impl.persistent.pcollections;

import java.util.Iterator;
import java.util.Map.Entry;

import org.pcollections.HashPMap;
import org.pcollections.HashTreePMap;

import io.usethesource.criterion.api.JmhMap;
import io.usethesource.criterion.api.JmhValue;

final class PcollectionsMapBuilder implements JmhMap.Builder {

  protected HashPMap<JmhValue, JmhValue> mapContent;
  protected JmhMap constructedMap;

  PcollectionsMapBuilder() {
    mapContent = HashTreePMap.empty();
    constructedMap = null;
  }

  @Override
  public void put(JmhValue key, JmhValue value) {
    checkMutation();
    mapContent = mapContent.plus(key, value);
  }

  @Override
  public void putAll(JmhMap map) {
    putAll(map.entryIterator());
  }

  @Override
  public void putAll(java.util.Map<JmhValue, JmhValue> map) {
    putAll(map.entrySet().iterator());
  }

  private void putAll(Iterator<Entry<JmhValue, JmhValue>> entryIterator) {
    checkMutation();

    while (entryIterator.hasNext()) {
      final Entry<JmhValue, JmhValue> entry = entryIterator.next();
      final JmhValue key = entry.getKey();
      final JmhValue value = entry.getValue();

      mapContent = mapContent.plus(key, value);
    }
  }

  protected void checkMutation() {
    if (constructedMap != null) {
      throw new UnsupportedOperationException("Mutation of a finalized map is not supported.");
    }
  }

  @Override
  public JmhMap done() {
    if (constructedMap == null) {
      constructedMap = new PcollectionsMap(mapContent);
    }

    return constructedMap;
  }

}
