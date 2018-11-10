package com.ciyfhx.network;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

public class PipeLineStream {

	private List<PipeLine> pipeLines = new LinkedList<PipeLine>();

	protected ByteBuffer streamRead(ByteBuffer data) throws Exception {
		ByteBuffer pipeData = data;
		for (int i = pipeLines.size()-1; i >= 0; i--) {
			pipeData = pipeLines.get(i).read(pipeData);
		}
		return pipeData;
	}

	protected ByteBuffer streamWrite(ByteBuffer data) throws Exception {
		ByteBuffer pipeData = data;
		for (int i = 0; i < pipeLines.size(); i++) {
			pipeData =  pipeLines.get(i).write(pipeData);
		}
		return pipeData;
	}

	public void addPipeLine(PipeLine pipeLine) {
		pipeLines.add(pipeLine);
	}
	
	public void addPipeLine(int index, PipeLine pipeLine){
		pipeLines.add(index, pipeLine);
	}

	public void removePipeLine(PipeLine pipeLine) {
		pipeLines.remove(pipeLine);
	}

	public List<PipeLine> getPipeLines(){
		return pipeLines;
	}


}
