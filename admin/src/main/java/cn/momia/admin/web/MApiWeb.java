package cn.momia.admin.web;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MApiWeb {
    private static final Logger LOGGER = LoggerFactory.getLogger(MApiWeb.class);

    private static final int DEFAULT_SERVER_PORT = 9090;

    public static void main(String[] args) {
        int port = getServerPort(args);

        Server server = new Server(port);

        WebAppContext context = new WebAppContext();
        context.setDescriptor("web/WEB-INF/web.xml");
        context.setResourceBase("web");
        context.setContextPath("/");
        context.setParentLoaderPriority(true);

        server.setHandler(context);

        try {
            server.start();
            server.join();
        } catch (Throwable t) {
            LOGGER.error("start server error", t);
            System.exit(-1);
        }
    }

    private static int getServerPort(String[] args) {
        if (args.length > 1) {
            try {
                return Integer.valueOf(args[1]);
            } catch (NumberFormatException e) {
                LOGGER.error("invalid server port argument: {}", args[1]);
            }
        }

        return DEFAULT_SERVER_PORT;
    }
}
