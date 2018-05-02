package ch.hsr.adv.ui.array.logic;

import ch.hsr.adv.ui.core.logic.GsonProvider;
import ch.hsr.adv.ui.core.logic.Stringifyer;
import ch.hsr.adv.ui.core.logic.domain.Module;
import ch.hsr.adv.ui.core.logic.domain.Session;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates a json representation of an array session.
 */
@Singleton
@Module("array")
public class ArrayStringifyer implements Stringifyer {

    private static final String EXPECTED_MODULE = "array";
    private static final Logger logger = LoggerFactory
            .getLogger(ArrayStringifyer.class);
    private final GsonProvider gsonProvider;

    @Inject
    public ArrayStringifyer(GsonProvider gsonProvider) {
        this.gsonProvider = gsonProvider;
    }

    /**
     * Builds a json string from an array session.
     *
     * @param session the session to be transmitted
     * @return json string representation of the session
     */
    @Override
    public String stringify(final Session session) {
        if (EXPECTED_MODULE.equals(session.getModuleName())) {
            logger.debug("resulting json: {}", gsonProvider.getPrettifyer()
                    .toJson(session));
            return gsonProvider.getPrettifyer().toJson(session);
        } else {
            logger.error("Wrong session for this Stringifyer. Module name is "
                            + "{} but should be {}", session.getSessionName(),
                    EXPECTED_MODULE);
            return null;
        }
    }
}
