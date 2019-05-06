package client.java.network.connection;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;

public class Session {
    String localId;
    String serverId;
    StompSession stompSession;
    ObjectMapper objectMapper;

    public Session(StompSession stompSession) {
        this.stompSession = stompSession;
        localId = this.stompSession.getSessionId();
        objectMapper = new ObjectMapper();
    }

    public void sendToServer(String destination, Object o) {
        try {
            byte[] payload = objectMapper.writeValueAsBytes(o);
            stompSession.send(destination, payload);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            System.out.println("Error sending data to server.");
        }
    }

    public void subscribeToEvent(String destination, StompFrameHandler handler) {
        stompSession.subscribe(destination, handler);
    }

    public void disconnect() {
        stompSession.disconnect();
    }

    public String getLocalId() {
        return this.localId;
    }

    public String getServerId() {
        return this.serverId;
    }

    public void setServerId(String id) {
        this.serverId = id;
    }

    public boolean isConnected() {
        return stompSession.isConnected();
    }
}
