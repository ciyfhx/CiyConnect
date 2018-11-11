/*
 * Copyright (c) 2018.
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */


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
