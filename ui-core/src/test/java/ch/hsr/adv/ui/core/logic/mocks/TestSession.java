package ch.hsr.adv.ui.core.logic.mocks;

import ch.hsr.adv.ui.core.access.FileDatastoreAccess;
import ch.hsr.adv.ui.core.logic.domain.Session;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@Singleton
public class TestSession {

    private Session session;

    @Inject
    public TestSession(FileDatastoreAccess reader, Gson gson)
            throws IOException {

        URL url = getClass().getClassLoader().getResource("session1.json");

        String json = reader.read(new File(url.getPath()));
        session = gson.fromJson(json, Session.class);
    }

    public Session getSession() {
        return session;
    }
}