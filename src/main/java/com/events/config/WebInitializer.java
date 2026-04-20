package com.events.config;

import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import jakarta.servlet.Filter;

/**
 * REMPLACEMENT DU web.xml — Pure Java Config.
 *
 * Tomcat 10.1+ détecte automatiquement cette classe au démarrage
 * grâce à l'interface ServletContainerInitializer de Jakarta EE.
 *
 * Ce fichier fait exactement ce que faisait web.xml :
 *  - Enregistre le DispatcherServlet de Spring MVC
 *  - Configure Spring Security via DelegatingFilterProxy
 *  - Force l'encodage UTF-8 sur toutes les requêtes
 */
public class WebInitializer
        extends AbstractAnnotationConfigDispatcherServletInitializer {

    /**
     * Classes de configuration "racine" : Services, Repositories,
     * Sécurité, DataSource, Hibernate...
     * Équivalent de l'ancien contextConfigLocation dans web.xml.
     */
    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{ AppConfig.class, SecurityConfig.class };
    }

    /**
     * Classes de configuration du DispatcherServlet : contrôleurs,
     * ViewResolver Thymeleaf, i18n, ressources statiques...
     * On met null car tout est dans AppConfig pour simplifier.
     */
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return null;
    }

    /**
     * Le DispatcherServlet intercepte toutes les requêtes "/".
     */
    @Override
    protected String[] getServletMappings() {
        return new String[]{ "/" };
    }

    /**
     * Filtres appliqués à TOUTES les requêtes, dans cet ordre :
     * 1. CharacterEncodingFilter : force UTF-8 (accents, arabe...)
     * 2. DelegatingFilterProxy   : délègue à Spring Security
     */
    @Override
    protected Filter[] getServletFilters() {
        // Filtre 1 : encodage UTF-8
        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
        encodingFilter.setEncoding("UTF-8");
        encodingFilter.setForceEncoding(true);

        // Filtre 2 : Spring Security
        // Le nom "springSecurityFilterChain" est obligatoire —
        // Spring Security recherche ce bean par son nom.
        DelegatingFilterProxy securityFilter =
                new DelegatingFilterProxy("springSecurityFilterChain");

        return new Filter[]{ encodingFilter, securityFilter };
    }
}
