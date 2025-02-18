package org.acme;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Arrays;
import java.util.List;

@ApplicationScoped
public class SocketService {

    Vertx vertx;
    NetClient netClient;

    @Inject
    public SocketService(Vertx vertx) {
        this.vertx = vertx;

        NetClientOptions options = new NetClientOptions()
            .setLogActivity(true);
        this.netClient = vertx.createNetClient(options);
    }

    public Uni<Void> send() {
        List<Uni<NetSocket>> sockets = Arrays.asList(
            connectSocket(), connectSocket(), connectSocket(), connectSocket(),
            connectSocket(), connectSocket(), connectSocket()
        );
        return Uni.combine().all().unis(sockets)
            .with(objects -> sendRequests((List<NetSocket>) objects)).replaceWithVoid()
        ;
    }

    private Uni<NetSocket> connectSocket() {
        return Uni.createFrom().completionStage(netClient.connect(8080, "127.0.0.1").toCompletionStage());
    }

    private String sendRequests(List<NetSocket> objects) {
        for (NetSocket socket : objects) {
            Log.info("socket connected");
            socket.handler(event -> {
                Log.info("received event: " + event.toString());
                socket.close();
            });
            socket.write("GET / HTTP/1.1\n");
            socket.write("Host: 127.0.0.1\n");
            socket.write("\n");
            socket.write("\n");
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        for (NetSocket socket : objects) {
            socket.close().onComplete(event -> {
                Log.info("socket closed");
            });
        }

        return "";
    }
}
