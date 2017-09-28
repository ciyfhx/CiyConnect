package com.ciyfhx.builder;

import com.ciyfhx.network.PipeLine;
import com.ciyfhx.network.PipeLineStream;

public class PipeLineStreamBuilder {

    private PipeLineStream stream = new PipeLineStream();

    public static PipeLineStreamBuilder newInstance(){
        return new PipeLineStreamBuilder();
    }

    public PipeLineStreamBuilder addPipeLine(PipeLine... pipeLines){
        for(PipeLine pipeLine : pipeLines)stream.addPipeLine(pipeLine);
        return this;
    }

    public PipeLineStream build(){
        return stream;
    }

}
