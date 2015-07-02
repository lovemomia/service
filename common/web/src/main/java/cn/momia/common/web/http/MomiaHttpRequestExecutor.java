package cn.momia.common.web.http;

import cn.momia.common.web.response.ErrorCode;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class MomiaHttpRequestExecutor {
    public MomiaHttpResponseCollector execute(List<MomiaHttpRequest> requests) {
        return execute(requests, requests.size());
    }

    public MomiaHttpResponseCollector execute(List<MomiaHttpRequest> requests, int threadCount) {
        final MomiaHttpResponseCollector collector = new MomiaHttpResponseCollector();
        final AtomicBoolean successful = new AtomicBoolean(true);
        // TODO more configuration of http client
        final HttpClient httpClient = HttpClients.createDefault();
        final ExecutorService executorService = new ThreadPoolExecutor(threadCount, threadCount, 5, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(requests.size()));
        for (final MomiaHttpRequest request : requests) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        HttpResponse response = httpClient.execute(request);
                        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                            if (request.isRequired()) throw new RuntimeException("fail to execute request: " + request);
                            return;
                        }

                        String entity = EntityUtils.toString(response.getEntity());
                        JSONObject responseJson = JSON.parseObject(entity);
                        int errorno = responseJson.getInteger("errno");
                        if (errorno != ErrorCode.SUCCESS) {
                            collector.addErrno(errorno);
                            if (request.isRequired()) throw new RuntimeException("fail to execute request: " + request);
                            return;
                        }

                        collector.addResponse(request.getName(), responseJson.get("data"));
                    } catch (Throwable t) {
                        collector.addException(t);
                        if (request.isRequired()) shutdown(executorService, successful);
                    }
                }
            });
        }

        try {
            executorService.shutdown();
            // TODO configurable timeout
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                shutdown(executorService, successful);
            }
        }
        catch (InterruptedException e) {
            collector.addException(e);
            shutdown(executorService, successful);
        }

        collector.setSuccessful(successful.get());

        return collector;
    }

    private void shutdown(ExecutorService executorService, AtomicBoolean successful)
    {
        executorService.shutdownNow();
        successful.compareAndSet(true, false);
    }
}
