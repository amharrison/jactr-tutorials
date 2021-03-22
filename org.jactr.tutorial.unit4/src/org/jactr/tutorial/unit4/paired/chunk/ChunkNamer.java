package org.jactr.tutorial.unit4.paired.chunk;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.module.declarative.basic.chunk.IChunkNamer;

/**
 * This little snippet is useful for creating custom chunk names for your model.
 * This is applied to all chunks, so never return null.
 * 
 * @author harrison
 *
 */
public class ChunkNamer implements IChunkNamer {

	@Override
	public String generateName(IChunk chunk) {
		ISymbolicChunk sc = chunk.getSymbolicChunk();
		if (sc.getChunkType().getSymbolicChunkType().getName().equals("pair")) {
			// for paired associates
			return String.format("pair-%s-%s", sc.getSlot("probe").getValue(), sc.getSlot("answer").getValue());
		} else if (sc.getChunkType().getSymbolicChunkType().getName().equals("problem")) {
			// for zbrodoff
			return String.format("problem-%s%s%s", sc.getSlot("arg1").getValue(), sc.getSlot("arg2").getValue(),
					sc.getSlot("result").getValue());

		} else if (sc.getChunkType().getSymbolicChunkType().getName().equals("args")) {
			// for unit 7
			return String.format("pair-%s%s", sc.getSlot("arg1").getValue(), sc.getSlot("arg2").getValue());
		} else
			return sc.getName();
	}

}
