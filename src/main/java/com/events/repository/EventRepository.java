package com.events.repository;

import com.events.entity.Event;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Repository Hibernate pour les événements.
 */
@Repository
@Transactional
public class EventRepository {

    @Autowired
    private SessionFactory sessionFactory;

    public void save(Event event) {
        sessionFactory.getCurrentSession().persist(event);
    }

    public List<Event> findAll() {
        return sessionFactory.getCurrentSession()
            .createQuery("FROM Event e ORDER BY e.eventDate ASC", Event.class)
            .getResultList();
    }

    /**
     * Recherche multicritères : mot-clé + type optionnel.
     * HQL avec paramètres nommés pour éviter les injections SQL.
     */
    public List<Event> search(String keyword, Event.EventType type) {
        StringBuilder hql = new StringBuilder("FROM Event e WHERE 1=1 ");
        if (keyword != null && !keyword.isBlank()) {
            hql.append("AND (LOWER(e.title) LIKE :kw "
                     + "  OR LOWER(e.location) LIKE :kw "
                     + "  OR LOWER(e.description) LIKE :kw) ");
        }
        if (type != null) {
            hql.append("AND e.type = :type ");
        }
        hql.append("ORDER BY e.eventDate ASC");

        var q = sessionFactory.getCurrentSession()
                    .createQuery(hql.toString(), Event.class);

        if (keyword != null && !keyword.isBlank())
            q.setParameter("kw", "%" + keyword.toLowerCase() + "%");
        if (type != null)
            q.setParameter("type", type);

        return q.getResultList();
    }

    public long count() {
        return sessionFactory.getCurrentSession()
            .createQuery("SELECT COUNT(e) FROM Event e", Long.class)
            .uniqueResult();
    }
}
