package top.ienjoy.cybergarage.http;

import java.net.Socket;

public class HTTPServerThread extends Thread {
    private HTTPServer httpServer;
    private Socket sock;

    ////////////////////////////////////////////////
    //	Constructor
    ////////////////////////////////////////////////

    public HTTPServerThread(HTTPServer httpServer, Socket sock) {
        super("Cyber.HTTPServerThread");
        this.httpServer = httpServer;
        this.sock = sock;
    }

    ////////////////////////////////////////////////
    //	run
    ////////////////////////////////////////////////

    public void run() {
        HTTPSocket httpSock = new HTTPSocket(sock);
        if (!httpSock.open())
            return;
        HTTPRequest httpReq = new HTTPRequest();
        httpReq.setSocket(httpSock);
        while (httpReq.read()) {
            httpServer.performRequestListener(httpReq);
            if (!httpReq.isKeepAlive())
                break;
        }
        httpSock.close();
    }
}
