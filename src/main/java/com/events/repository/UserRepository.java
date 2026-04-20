package com.events.repository;

import com.events.entity.User;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Repository Hibernate pour les utilisateurs.
 * Utilise la SessionFactory injectée par Spring.
 */
@Repository
@Transactional
public class UserRepository {

    @Autowired
    private SessionFactory sessionFactory;

    public void save(User user) {
        sessionFactory.getCurrentSession().persist(user);
    }

    public Optional<User> findByEmail(String email) {
        return sessionFactory.getCurrentSession()
            .createQuery("FROM User u WHERE u.email = :email", User.class)
            .setParameter("email", email.toLowerCase().trim())
            .uniqueResultOptional();
    }

    public boolean existsByEmail(String email) {
        Long n = sessionFactory.getCurrentSession()
            .createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
            .setParameter("email", email.toLowerCase().trim())
            .uniqueResult();
        return n != null && n > 0;
    }

    public long count() {
        return sessionFactory.getCurrentSession()
            .createQuery("SELECT COUNT(u) FROM User u", Long.class)
            .uniqueResult();
    }
}
