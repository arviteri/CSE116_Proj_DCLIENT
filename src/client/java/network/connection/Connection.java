package client.java.network.connection;

import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.ExecutionException;

public class Connection {
    class SessionHandler implements StompSessionHandler {
        public void afterConnected(StompSession stompSession, StompHeaders stompHeaders) { System.out.println("Connected to network."); }

        public void handleException(StompSession stompSession, StompCommand stompCommand, StompHeaders stompHeaders, byte[] bytes, Throwable throwable) { }

        public void handleTransportError(StompSession stompSession, Throwable throwable) { }

        public Type getPayloadType(StompHeaders stompHeaders) { return null; }

        public void handleFrame(StompHeaders stompHeaders, Object o) { }
    }

    public Boolean error = false;

    String uri;
    WebSocketClient clientSocket;
    WebSocketStompClient stompClient;
    SessionHandler sessionHandler;
    StompSession stompSession;
    ListenableFuture<StompSession> future;

    public Connection(String uri) {
        this.uri = uri;
        clientSocket = new StandardWebSocketClient();
        stompClient = new WebSocketStompClient(clientSocket);
        sessionHandler = new SessionHandler();
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        future = stompClient.connect(this.uri, sessionHandler);

        try {
            stompSession = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            System.out.println("Connection error.");
            this.error = true;
        }
    }

    public Session getSession() { return new Session(this.stompSession); }
}
