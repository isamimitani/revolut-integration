package com.tibber.dev.revolutintegration.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;

/**
 * A listener class to define tasks to execute before / after batch job.
 *
 * @version 1.0
 * @auther Isami Mitani
 */
public class ItemCountListener implements ChunkListener {

    private static final Logger log = LoggerFactory.getLogger(ItemCountListener.class);

    @Override
    public void beforeChunk(ChunkContext context) {
    }

    @Override
    public void afterChunk(ChunkContext context) {
        int count = context.getStepContext().getStepExecution().getReadCount();
        log.info("Processed Item count: " + count);
    }

    @Override
    public void afterChunkError(ChunkContext context) {
    }
}