package cn.momia.service.im.push.impl;

import cn.momia.common.service.AbstractService;
import cn.momia.service.im.push.PushMsg;
import cn.momia.service.im.push.PushService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Queue;

public abstract class AbstractPushService extends AbstractService implements PushService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPushService.class);
    protected static final long SYSTEM_PUSH_USERID = 10000;

    private Queue<PushMsg> msgs = new LinkedList<PushMsg>();
    private boolean isRunning = false;
    private PushMsg currentMsg = null;

    @Override
    public boolean push(long userId, PushMsg msg) {
        return doPush(userId, msg);
    }

    protected abstract boolean doPush(long userId, PushMsg msg);

    @Override
    public void pushAll(PushMsg msg) {
        synchronized (msgs) {
            msgs.offer(msg);
            if (!isRunning) {
                isRunning = true;
                new Thread(new PushTaskRunner()).start();
            }
        }
    }

    private class PushTaskRunner implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    synchronized (msgs) {
                        if (msgs.isEmpty()) {
                            isRunning = false; // 尽管finally里有设为false，但这里还是不能少
                            break;
                        } else {
                            currentMsg = msgs.poll();
                        }
                    }

                    if (currentMsg == null) continue;

                    Thread.sleep(5000); // 发送间隔5s

                    try {
                        doBatchPush(currentMsg);
                    } catch (Exception e) {
                        LOGGER.error("fail to publish msg: {}", currentMsg, e);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("exception when publish msgs", e);
            } finally {
                isRunning = false;
                currentMsg = null;
            }
        }
    }

    protected abstract void doBatchPush(PushMsg msg);
}
