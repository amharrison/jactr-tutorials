package org.jactr.tutorial.unit7.past.internal;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.jactr.core.chunk.IChunk;

public class VerbInfo {

	static final private Map<String, VerbInfo> _database = new TreeMap<>();
	static {
		_database.put("have", new VerbInfo("have", true, 12458, "had"));
		_database.put("do", new VerbInfo("do", true, 4367, "did"));
		_database.put("make", new VerbInfo("make", true, 2312, "made"));
		_database.put("get", new VerbInfo("get", true, 1486, "got"));
		_database.put("use", new VerbInfo("use", false, 1016, "use"));
		_database.put("look", new VerbInfo("look", false, 910, "look"));
		_database.put("seem", new VerbInfo("seem", false, 831, "seem"));
		_database.put("tell", new VerbInfo("tell", true, 759, "told"));
		_database.put("show", new VerbInfo("show", false, 640, "show"));
		_database.put("want", new VerbInfo("want", false, 631, "want"));
		_database.put("call", new VerbInfo("call", false, 627, "call"));
		_database.put("ask", new VerbInfo("ask", false, 612, "ask"));
		_database.put("turn", new VerbInfo("turn", false, 566, "turn"));
		_database.put("follow", new VerbInfo("follow", false, 540, "follow"));
		_database.put("work", new VerbInfo("work", false, 496, "work"));
		_database.put("live", new VerbInfo("live", false, 472, "live"));
		_database.put("try", new VerbInfo("try", false, 472, "try"));
		_database.put("stand", new VerbInfo("stand", true, 468, "stood"));
		_database.put("move", new VerbInfo("move", false, 447, "move"));
		_database.put("need", new VerbInfo("need", false, 413, "need"));
		_database.put("start", new VerbInfo("start", false, 386, "start"));
		_database.put("lose", new VerbInfo("lose", true, 274, "lost"));
	}

	static public Map<String, VerbInfo> getDatabase() {
		return Collections.unmodifiableMap(_database);
	}

	final public String verb, stem;
	final public boolean isIrregular;
	final public int frequency;
	

	public VerbInfo(String verb, boolean isIrregular, int frequency, String stem) {
		this.verb = verb;
		this.stem = stem;
		this.isIrregular = isIrregular;
		this.frequency = frequency;
	}

}
