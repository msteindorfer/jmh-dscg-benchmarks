package dom;

import static dom.AllDominatorsRunner.CURRENT_DATA_SET_FILE_NAME;
import static dom.AllDominatorsRunner.DATA_SET_SINGLE_FILE_NAME;
import static dom.AllDominatorsRunner.LOG_BINARY_RESULTS;
import static dom.AllDominatorsRunner.LOG_TEXTUAL_RESULTS;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.imp.pdb.facts.IConstructor;
import org.eclipse.imp.pdb.facts.IMap;
import org.eclipse.imp.pdb.facts.IMapWriter;
import org.eclipse.imp.pdb.facts.ISet;
import org.eclipse.imp.pdb.facts.ISetWriter;
import org.eclipse.imp.pdb.facts.ITuple;
import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.IValueFactory;
import org.eclipse.imp.pdb.facts.io.BinaryValueReader;
import org.eclipse.imp.pdb.facts.io.BinaryValueWriter;
import org.eclipse.imp.pdb.facts.io.StandardTextWriter;
import org.eclipse.imp.pdb.facts.util.DefaultTrieMap;
import org.eclipse.imp.pdb.facts.util.DefaultTrieSet;
import org.eclipse.imp.pdb.facts.util.ImmutableMap;
import org.eclipse.imp.pdb.facts.util.ImmutableSet;
import org.eclipse.imp.pdb.facts.util.TransientMap;
import org.eclipse.imp.pdb.facts.util.TransientSet;
import org.rascalmpl.interpreter.utils.Timing;


@SuppressWarnings("deprecation")
public class DominatorsPDB {
	public final IValueFactory vf;
	public final ISet EMPTY;
	
	public DominatorsPDB(IValueFactory vf) {
		this.vf = vf;
		this.EMPTY = vf.set();
	}
	
    public ISet intersect(ISet sets) {
		if (sets.isEmpty() || sets.contains(EMPTY)) {
			return EMPTY;
		}
		
		ISet first = (ISet) sets.iterator().next();
		sets = sets.delete(first);
		ISet result = first;
		for (IValue elem : sets) {
			result = result.intersect((ISet) elem);
		}
		
		return result;
	}
    
    private ISet setofdomsets(IMap dom, ISet preds) {
    	ISetWriter result = vf.setWriter();
    	
    	for (IValue p : preds) {
    		ISet ps = (ISet) dom.get(p);
    		
    		result.insert(ps == null ? EMPTY : ps);
    	}
    	
    	return result.done();
    }
    
    public ISet top(ISet graph) {
    	return graph.asRelation().project(0).subtract(graph.asRelation().project(1));
    }
    
    public IValue getTop(ISet graph) {
    	for (IValue candidate : top(graph)) {
    		switch (((IConstructor) candidate).getName()) {
    		case "methodEntry":
    		case "functionEntry":
    		case "scriptEntry":
    			return candidate;
    		}
    	}
    	
    	throw new RuntimeException("no entry?");
    }
	
    public IMap jDominators(ISet graph) {
    	IValue n0 = getTop(graph);
    	ISet nodes = graph.asRelation().carrier();
//    	if (!nodes.getElementType().isAbstractData()) {
//    		throw new RuntimeException("nodes is not the right type");
//    	}
    	IMap preds = (IMap) toMap(graph.asRelation().project(1,0));
//    	nodes = nodes.delete(n0);

    	IMapWriter w = vf.mapWriter();
    	w.put(n0, vf.set(n0));
    	for (IValue n : nodes.delete(n0)) {
    		w.put(n, nodes);
    	}
    	IMap dom = w.done();
    	IMap prev = vf.mapWriter().done();
    	
    	/*
		 *   solve (dom) 
         *      for (n <- nodes) 
         *         dom[n] = {n} + intersect({dom[p] | p <- preds[n]?{}});
		 */
    	while (!prev.equals(dom)) {
    		prev = dom;
    		IMapWriter newDom = vf.mapWriter();
    	
    		for (IValue n : nodes) {
    			ISet ps = (ISet) preds.get(n);
    			if (ps == null) {
    				ps = EMPTY;
    			}
    			ISet sos = setofdomsets(dom, ps);
//    			if (!sos.getType().isSet()  || !sos.getType().getElementType().isSet() || !sos.getType().getElementType().getElementType().isAbstractData()) {
//    				throw new RuntimeException("not the right type: " + sos.getType());
//    			}
				ISet intersected = intersect(sos);
//				if (!intersected.getType().isSet() || !intersected.getType().getElementType().isAbstractData()) {
//					throw new RuntimeException("not the right type: " + intersected.getType());
//				}
				ISet newValue = vf.set(n).union(intersected);
//				if (!newValue.getElementType().isAbstractData()) {
//					System.err.println("problem");
//				}
				newDom.put(n, newValue);
    		}
    		
//    		if (!newDom.done().getValueType().getElementType().isAbstractData()) {
//    			System.err.println("not good");
//    		}
    		dom = newDom.done();
    	}
    	
    	return dom;
    }
    
	public static void main(String[] args) throws FileNotFoundException, IOException {
		testAll(org.eclipse.imp.pdb.facts.impl.persistent.ValueFactory.getInstance());
		testAll(org.eclipse.imp.pdb.facts.impl.fast.ValueFactory.getInstance());
	}

	public static IMap testOne(IValueFactory vf) throws IOException, FileNotFoundException {
		ISet data = (ISet) new BinaryValueReader().read(vf, new FileInputStream(DATA_SET_SINGLE_FILE_NAME));
		
		long before = Timing.getCpuTime();
		IMap pdbResults = new DominatorsPDB(vf).jDominators(data);		
		
		System.err.println(vf.toString() + "\nDuration: " + ((Timing.getCpuTime() - before) / 1000000000) + " seconds\n");
		
		if (LOG_BINARY_RESULTS)
			new BinaryValueWriter().write(pdbResults, new FileOutputStream("data/dominators-java-"
							+ vf.toString() + "-single.bin"));
		
		if (LOG_TEXTUAL_RESULTS)
			new StandardTextWriter()
							.write(pdbResults,
											new FileWriter("data/dominators-java-" + vf.toString()
															+ "-single.txt"));
		
		return pdbResults;
	}
	
	public static ISet testAll(IValueFactory vf) throws IOException, FileNotFoundException {
			IMap data = (IMap) new BinaryValueReader().read(vf, new FileInputStream(CURRENT_DATA_SET_FILE_NAME));
			ISetWriter result = vf.setWriter();
			
			long before = Timing.getCpuTime();
			for (IValue key : data) {
				try {
					result.insert(new DominatorsPDB(vf).jDominators((ISet) data.get(key)));
				} catch (RuntimeException e) {
					System.err.println(e.getMessage());
				}
			}
			System.err.println(vf.toString() + "\nDuration: " + ((Timing.getCpuTime() - before) / 1000000000) + " seconds\n");
			
			ISet pdbResults = result.done();
			
		if (LOG_BINARY_RESULTS)
			new BinaryValueWriter().write(pdbResults, new FileOutputStream(
							"data/dominators-java.bin"));

		if (LOG_TEXTUAL_RESULTS)
			new StandardTextWriter().write(pdbResults,
							new FileWriter("data/dominators-java-" + vf.toString() + ".txt"));
						
			return pdbResults;
		}
	
	/*
	 * Convert a set of tuples to a map; value in old map is associated with a
	 * set of keys in old map.
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> ImmutableMap<K, ImmutableSet<V>> toMap(ISet st) {
		Map<K, TransientSet<V>> hm = new HashMap<>();

		for (IValue v : st) {
			ITuple t = (ITuple) v;
			K key = (K) t.get(0);
			V val = (V) t.get(1);
			TransientSet<V> wValSet = hm.get(key);
			if (wValSet == null) {
				wValSet = DefaultTrieSet.transientOf();
				hm.put(key, wValSet);
			}
			wValSet.__insert(val);
		}

		TransientMap<K, ImmutableSet<V>> w = DefaultTrieMap.transientOf();
		for (K k : hm.keySet()) {
			w.__put(k, hm.get(k).freeze());
		}
		return w.freeze();
	}
	
}