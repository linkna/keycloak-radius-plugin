package com.github.vzakharchenko.radius.dm.jpa;

import com.github.vzakharchenko.radius.dm.models.DMKeycloakEndModel;
import com.github.vzakharchenko.radius.dm.models.DisconnectMessageModel;
import com.github.vzakharchenko.radius.dm.test.AbstractJPATest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class DisconnectMessageManagerTest extends AbstractJPATest {
    private DisconnectMessageManager disconnectMessageManager;

    @BeforeMethod
    public void beforeMethods() {
        disconnectMessageManager = new DisconnectMessageManager(session);
    }

    @Test
    public void saveTest() {
        DisconnectMessageModel disconnectMessageModel = new DisconnectMessageModel();
        disconnectMessageManager.saveRadiusSession(disconnectMessageModel);
        verify(entityManager).persist(any());
    }
    @Test
    public void endSessionTest() {
        DisconnectMessageModel disconnectMessageModel = new DisconnectMessageModel();
        disconnectMessageManager.successEndSession(disconnectMessageModel);
        verify(entityManager).persist(any());
    }

    @Test
    public void endSessionTestWithCause() {
        DisconnectMessageModel disconnectMessageModel = new DisconnectMessageModel();
        disconnectMessageManager.successEndSessionWithCause(disconnectMessageModel, "test");
        verify(entityManager).persist(any());
    }

    @Test
    public void failedEndSessionTest() {
        DisconnectMessageModel disconnectMessageModel = new DisconnectMessageModel();
        disconnectMessageManager.failEndSession(disconnectMessageModel,"error");
        verify(entityManager).persist(any());
    }
    @Test
    public void increaseEndAttemptsTestNull() {
        DisconnectMessageModel disconnectMessageModel = new DisconnectMessageModel();
        disconnectMessageManager.increaseEndAttempts(disconnectMessageModel,"" );
        verify(entityManager).persist(any());
    }

    @Test
    public void increaseEndAttemptsTestNotNull() {
        DisconnectMessageModel disconnectMessageModel = new DisconnectMessageModel();
        DMKeycloakEndModel dmKeycloakEndModel = new DMKeycloakEndModel();
        dmKeycloakEndModel.setAttempts(1);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(dmKeycloakEndModel));
        disconnectMessageManager.increaseEndAttempts(disconnectMessageModel, "");
        verify(entityManager).persist(dmKeycloakEndModel);
        assertEquals(dmKeycloakEndModel.getAttempts().intValue(),2);
    }
    @Test
    public void increaseEndAttemptsTestMax() {

        DisconnectMessageModel disconnectMessageModel = new DisconnectMessageModel();
        DMKeycloakEndModel dmKeycloakEndModel = new DMKeycloakEndModel();
        dmKeycloakEndModel.setAttempts(3);
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(dmKeycloakEndModel));
        disconnectMessageManager.increaseEndAttempts(disconnectMessageModel, "max connection attempts");
        verify(entityManager).persist(dmKeycloakEndModel);
        assertEquals(dmKeycloakEndModel.getAttempts().intValue(),3);
        assertEquals(dmKeycloakEndModel.getEndMessage(),"max connection attempts");
        assertEquals(dmKeycloakEndModel.getEndStatus(),"MAX_ATTEMPTS");
    }

    @Test
    public void getDisconnectMessageTestNull() {
        DisconnectMessageModel disconnectMessage = disconnectMessageManager
                .getDisconnectMessage("test", "sessionId");
        assertNull(disconnectMessage);
    }

    @Test
    public void getDisconnectMessageTest() {
        DisconnectMessageModel disconnectMessageModel = new DisconnectMessageModel();
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(disconnectMessageModel));
        DisconnectMessageModel disconnectMessage = disconnectMessageManager
                .getDisconnectMessage("test", "sessionId");
        assertEquals(disconnectMessage, disconnectMessageModel);
    }

    @Test
    public void getDisconnectMessages() {
        DisconnectMessageModel disconnectMessageModel = new DisconnectMessageModel();
        when(typedQuery.getResultList()).thenReturn(Arrays.asList(disconnectMessageModel));
        List<DisconnectMessageModel> disconnectMessages = disconnectMessageManager
                .getAllActivedSessions();
        assertEquals(disconnectMessages.size(), 1);
    }


}
