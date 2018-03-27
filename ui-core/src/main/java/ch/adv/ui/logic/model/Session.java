package ch.adv.ui.logic.model;

import ch.adv.ui.ADVModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

public class Session {

    private final List<Snapshot> snapshots = new ArrayList<>();
    private long sessionId;
    private String sessionName;
    private String moduleName;
    private transient ADVModule module;

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(final Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(final String sessionName) {
        this.sessionName = sessionName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public ADVModule getModule() {
        return module;
    }

    public void setModule(final ADVModule module) {
        this.module = module;
        if (module != null) {
            this.moduleName = module.getName();
        } else {
            moduleName = "";
        }
    }

    public List<Snapshot> getSnapshots() {
        return snapshots;
    }

    /**
     * Returns the first Snapshot (index 0) of all snapshots if it exists.
     * Otherwise returns a new Snapshot.
     *
     * @return the first Snapshot or a new Snapshot
     */
    public Snapshot getFirstSnapshot() {
        if (snapshots.isEmpty()) {
            return new Snapshot();
        }
        return snapshots.get(0);
    }

    @Override
    public String toString() {
        String time = String.format("%tT", sessionId - TimeZone.getDefault()
                .getRawOffset());
        return sessionName + " - " + time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Session session = (Session) o;
        return sessionId == session.sessionId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(sessionId);
    }
}
