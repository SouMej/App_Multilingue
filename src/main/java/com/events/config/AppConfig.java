package com.events.config;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

/**
 * CONFIGURATION PRINCIPALE — remplace tous les fichiers XML Spring.
 *
 * @Configuration         : cette classe déclare des beans Spring
 * @EnableWebMvc          : active Spring MVC (@Controller, @GetMapping…)
 * @EnableTransactionManagement : active @Transactional
 * @ComponentScan         : scan tous les @Service, @Repository, @Controller
 * @PropertySource        : charge database.properties depuis le classpath
 */
@Configuration
@EnableWebMvc
@EnableTransactionManagement
@ComponentScan("com.events")
@PropertySource("classpath:database.properties")
public class AppConfig implements WebMvcConfigurer {

    /**
     * Environment injecte les valeurs de database.properties.
     * Ex: env.getProperty("db.url") retourne la valeur de la clé db.url.
     */
    @Autowired
    private Environment env;

    /* ============================================================
       THYMELEAF 3.1 — Moteur de rendu des vues HTML
       Les vues .html sont placées dans /WEB-INF/views/
    ============================================================ */

    /**
     * Indique où chercher les templates Thymeleaf.
     * Préfixe + nom retourné par le contrôleur + suffixe
     * Ex: "auth/login" -> /WEB-INF/views/auth/login.html
     */
    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".html");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setTemplateMode("HTML");
        // false en développement pour voir les modifications sans redémarrer
        resolver.setCacheable(false);
        return resolver;
    }

    /**
     * Moteur Thymeleaf : on lui attache le resolver et les dialectes.
     * SpringSecurityDialect = active sec:authorize dans les templates.
     */
    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver());
        engine.setEnableSpringELCompiler(true);
        // Dialecte Spring Security pour th:sec:authorize
        engine.addDialect(new SpringSecurityDialect());
        // Support des messages i18n via #{nav.brand}
        engine.setTemplateEngineMessageSource(messageSource());
        return engine;
    }

    /**
     * ViewResolver : fait le lien entre le nom retourné par le contrôleur
     * et le moteur Thymeleaf.
     */
    @Bean
    public ThymeleafViewResolver viewResolver() {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine());
        resolver.setCharacterEncoding("UTF-8");
        resolver.setContentType("text/html;charset=UTF-8");
        return resolver;
    }

    /* ============================================================
       SOURCE DE DONNÉES MYSQL
    ============================================================ */

    /**
     * DataSource MySQL.
     * Les valeurs sont lues depuis database.properties
     * via @PropertySource + Environment.
     */
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(env.getProperty("db.driver"));
        ds.setUrl(env.getProperty("db.url"));
        ds.setUsername(env.getProperty("db.username"));
        ds.setPassword(env.getProperty("db.password"));
        return ds;
    }

    /* ============================================================
       HIBERNATE 6 — Session Factory
    ============================================================ */

    /**
     * SessionFactory Hibernate : configure la connexion BDD et le mapping
     * entre les entités Java et les tables MySQL.
     */
    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean factory = new LocalSessionFactoryBean();
        factory.setDataSource(dataSource());
        // Scan du package pour trouver les classes @Entity
        factory.setPackagesToScan("com.events.entity");

        Properties props = new Properties();
        props.put("hibernate.dialect",        env.getProperty("hibernate.dialect",
                                                "org.hibernate.dialect.MySQL8Dialect"));
        props.put("hibernate.show_sql",       env.getProperty("hibernate.show_sql",   "true"));
        props.put("hibernate.format_sql",     env.getProperty("hibernate.format_sql", "true"));
        // update = met à jour le schéma MySQL sans supprimer les données
        props.put("hibernate.hbm2ddl.auto",   env.getProperty("hibernate.hbm2ddl.auto", "update"));
        props.put("hibernate.connection.charSet", "UTF-8");

        factory.setHibernateProperties(props);
        return factory;
    }

    /* ============================================================
       TRANSACTIONS
    ============================================================ */

    /**
     * Gestionnaire de transactions Hibernate.
     * Nécessaire pour que @Transactional fonctionne dans les Services.
     */
    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory sf) {
        return new HibernateTransactionManager(sf);
    }

    /* ============================================================
       INTERNATIONALISATION (i18n) — FR / EN / AR
    ============================================================ */

    /**
     * MessageSource : charge les fichiers messages_XX.properties
     * depuis le classpath (src/main/resources/).
     * Utilisé par Thymeleaf via #{nav.brand}.
     */
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource ms =
                new ReloadableResourceBundleMessageSource();
        ms.setBasename("classpath:messages");
        ms.setDefaultEncoding("UTF-8");
        ms.setFallbackToSystemLocale(false);
        return ms;
    }

    /**
     * LocaleResolver basé sur Cookie.
     * La langue choisie est sauvegardée dans un cookie navigateur
     * et persist entre les sessions.
     */
    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver resolver = new CookieLocaleResolver("APP_LOCALE");
        resolver.setDefaultLocale(Locale.FRENCH);
        resolver.setCookieMaxAge(60 * 60 * 24 * 30); // 30 jours
        return resolver;
    }

    /**
     * Intercepteur : lit le paramètre ?lang=XX dans l'URL
     * et change la locale courante.
     * Ex: /events?lang=en  ->  interface en anglais
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor i = new LocaleChangeInterceptor();
        i.setParamName("lang");
        return i;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    /* ============================================================
       RESSOURCES STATIQUES (CSS, JS)
       Servies depuis /WEB-INF/resources/
    ============================================================ */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/WEB-INF/resources/");
    }
}
