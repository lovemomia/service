package cn.momia.service.im.push.impl;

import cn.momia.common.service.AbstractService;
import cn.momia.service.im.push.PushMsg;
import cn.momia.service.im.push.PushService;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class AbstractPushService extends AbstractService implements PushService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPushService.class);

    protected static final long SYSTEM_PUSH_USERID = 10000;

    private Object signal = new Object();
    private ExecutorService executorService = new ThreadPoolExecutor(5, 10, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10000));
    private Queue<PushTask> tasksQueue = new LinkedList<PushTask>();

    public void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        consume();
                    } catch (InterruptedException e) {
                        LOGGER.error("InterruptedException", e);
                    }
                }
            }
        }).start();
    }

    private void consume() throws InterruptedException {
        synchronized (signal) {
            if (tasksQueue.isEmpty()) {
                LOGGER.info("no tasks, waiting...");
                signal.wait();
            }

            int count = 0;
            while (!tasksQueue.isEmpty() && count++ < 1000) {
                PushTask task = tasksQueue.poll();
                if (task == null) continue;
                executorService.submit(task);
            }
        }
    }

    @Override
    public boolean push(long userId, PushMsg msg) {
        return push(Sets.newHashSet(userId), msg);
    }

    @Override
    public boolean push(Collection<Long> userIds, PushMsg msg) {
        synchronized (signal) {
            tasksQueue.add(new PushTask(userIds, msg));

            LOGGER.info("notify new tasks");
            signal.notify();
        }

        return true;
    }

    private class PushTask implements Runnable {
        private Collection<Long> targets;
        private PushMsg msg;

        public PushTask(Collection<Long> targets, PushMsg msg) {
            this.targets = targets;
            this.msg = msg;
        }

        @Override
        public void run() {
            doPush(targets, msg);
        }
    }

    protected abstract boolean doPush(Collection<Long> userIds, PushMsg msg);
}
