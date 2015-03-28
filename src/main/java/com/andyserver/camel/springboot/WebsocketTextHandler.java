package com.andyserver.camel.springboot;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class WebsocketTextHandler extends TextWebSocketHandler {
	
	private Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<WebSocketSession>());
	private final Log logger = LogFactory.getLog(getClass());
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		sessions.add(session);
		logger.info("Connection Opened: " + session);
	}
	
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message)
			throws Exception {
		logger.info(message);
		
		for(WebSocketSession s : sessions) {
			try {
				s.sendMessage(message);
			}
			catch(Exception e) {
				logger.error("Error sending message to client", e);
			}
		}
	}
	
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		sessions.remove(session);
		logger.info("Connection Closed: " + session);
	}

	@Override
	public void handleTransportError(WebSocketSession session, Throwable exception)
			throws Exception {
		logger.error(exception.getMessage(), exception);
		sessions.remove(session);
		session.close(CloseStatus.SERVER_ERROR);
	}
}
