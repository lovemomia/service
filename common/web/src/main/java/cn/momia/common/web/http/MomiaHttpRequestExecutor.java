package cn.momia.common.web.http;

import com.alibaba.fastjson.JSON;
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
    public boolean execute(List<MomiaHttpRequest> requests, final MomiaHttpResponseCollector collector, final List<Throwable> exceptions, int threadCount) {
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
                        collector.add(request.getName(), JSON.parseObject(entity));
                    } catch (Throwable t) {
                        exceptions.add(t);
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
            exceptions.add(e);
            shutdown(executorService, successful);
        }

        return successful.get();
    }

    private void shutdown(ExecutorService executorService, AtomicBoolean successful)
    {
        executorService.shutdownNow();
        successful.compareAndSet(true, false);
    }
}
