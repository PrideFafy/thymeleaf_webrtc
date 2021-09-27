package com.mvp.webrtcthyme.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mvp.webrtcthyme.domain.Room;
import com.mvp.webrtcthyme.domain.RoomService;
import com.mvp.webrtcthyme.domain.WebSocketMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class SocketHandlerTest {
    @Autowired
    private RoomService service;
    @Autowired private SocketHandler handler;

    private String name;
    private WebSocketSession session;
    private Room room;

    @Before
    public void setup() {
        Long id = 1L;
        name = UUID.randomUUID().toString();
        session = mock(WebSocketSession.class);
        room = new Room(id);
        service.addRoom(room);
    }

    @Test
    public void shouldRemoveClient_whenConnectionClosed() throws Exception {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        WebSocketMessage message = new WebSocketMessage(name,"join", room.getId().toString(), null, null);
        handler.handleTextMessage(session, new TextMessage(ow.writeValueAsString(message)));
        message = new WebSocketMessage(name, "leave", room.getId().toString(), null, null);
        handler.handleTextMessage(session, new TextMessage(ow.writeValueAsString(message)));
        handler.afterConnectionClosed(session, CloseStatus.NORMAL);

        assertThat(service.getClients(room))
                .isEmpty();
    }

    @After
    public void teardown() {
        name = null;
        session = null;
        room = null;
    }
}