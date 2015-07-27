package cn.momia.common.web.http;

import cn.momia.common.web.response.ResponseMessage;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class MomiaHttpRequestExecutor {
    public ResponseMessage execute(MomiaHttpRequest request) {
        HttpClient httpClient = createHttpClient();
        try {
            HttpResponse response = httpClient.execute(request);
            checkResponseStatus(request, response);

            return buildResponseMessage(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpClient createHttpClient() {
        // TODO more configuration of http client
        return HttpClients.createDefault();
    }

    private boolean checkResponseStatus(MomiaHttpRequest request, HttpResponse response) {
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            if (request.isRequired()) throw new RuntimeException("fail to execute request: " + request);
            return false;
        }

        return true;
    }

    private ResponseMessage buildResponseMessage(HttpResponse response) throws IOException {
        String entity = EntityUtils.toString(response.getEntity());
        JSONObject responseJson = JSON.parseObject(entity);

        return ResponseMessage.formJson(responseJson);
    }

    public MomiaHttpResponseCollector execute(List<MomiaHttpRequest> requests) {
        return execute(requests, requests.size());
    }

    public MomiaHttpResponseCollector execute(List<MomiaHttpRequest> requests, int threadCount) {
        final MomiaHttpResponseCollector collector = new MomiaHttpResponseCollector();
        final AtomicBoolean successful = new AtomicBoolean(true);
        final HttpClient httpClient = createHttpClient();
        final ExecutorService executorService = new ThreadPoolExecutor(threadCount, threadCount, 5, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(requests.size()));
        for (final MomiaHttpRequest request : requests) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        HttpResponse response = httpClient.execute(request);
                        if (!checkResponseStatus(request, response)) return;

                        ResponseMessage responseMessage = buildResponseMessage(response);
                        if (!responseMessage.successful()) {
                            collector.addErrno(responseMessage.getErrno());
                            if (request.isRequired()) throw new RuntimeException("fail to execute request: " + request);
                            return;
                        }

                        collector.addResponse(request.getName(), responseMessage.getData());
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
