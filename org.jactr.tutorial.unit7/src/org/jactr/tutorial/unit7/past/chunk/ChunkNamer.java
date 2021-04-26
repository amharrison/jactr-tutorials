package org.jactr.tutorial.unit7.past.chunk;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.module.declarative.basic.chunk.IChunkNamer;

public class ChunkNamer implements IChunkNamer {

	public ChunkNamer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String generateName(IChunk chunk) {
		ISymbolicChunk sc = chunk.getSymbolicChunk();
		String name = sc.getName();
		
		if("past-tense".equals(sc.getChunkType().getSymbolicChunkType().getName()))
			name = String.format("%s-%s", sc.getSlot("verb").getValue(), sc.getSlot("stem").getValue());
		
		return name;
	}

}
