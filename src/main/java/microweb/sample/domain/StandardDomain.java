package microweb.sample.domain;

import io.vertx.core.Vertx;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

// E.1: Abstract class to create business rules:
public abstract class StandardDomain {
    // E.2: Private dependencies, which shouldn't be updated, once assigned:
    private SessionFactory factory;
    private Vertx vertx;

    // E.3: Facility to call sessions and transactions:
    public Session openTransactionSession() {
        Session s = factory.openSession();
        s.beginTransaction();

        return s;
    }

    // E.4: Dependencies getters:
    protected SessionFactory getSessionFactory() {
        return factory;
    }

    // E.4: Dependencies getters:
    protected Vertx getVertx() {
        return vertx;
    }

    // E.5: Business class default constructor:
    public StandardDomain(SessionFactory factory, Vertx vertx) {
        this.factory = factory;
        this.vertx = vertx;
    }
}
