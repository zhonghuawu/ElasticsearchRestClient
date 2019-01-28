package com.huaa.rest.core;

import lombok.extern.log4j.Log4j2;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

@Log4j2
public class ESBulkProcessor {
    private BulkProcessor bulkProcessor;

    public ESBulkProcessor(RestHighLevelClient client) {
        initBulkProcessor(client);
    }

    public BulkProcessor getBulkProcessor() {
        return bulkProcessor;
    }

    private void initBulkProcessor(RestHighLevelClient client) {
        bulkProcessor = BulkProcessor.builder(
                ((bulkRequest, bulkResponseActionListener) ->
                        client.bulkAsync(bulkRequest, RequestOptions.DEFAULT, bulkResponseActionListener)),
                new BulkProcessor.Listener() {
                    @Override
                    public void beforeBulk(long executionId, BulkRequest request) {

                    }

                    @Override
                    public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                        if (response.hasFailures()) {
                            log.error(response.buildFailureMessage());
                        }
                    }

                    @Override
                    public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                        log.error("bulk failed, ", failure);
                    }
                }).build();
    }

}
