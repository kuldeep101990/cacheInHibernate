package cacheInHibernate;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;

public class CacheImplementation {

    public static void main(String[] args) {
        Configuration configuration = HibernateConfig.getConfig();
        configuration.addAnnotatedClass(Customer.class);
        configuration.setProperty("hibernate.cache.use_second_level_cache", "true");
      //  configuration.setProperty("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.EhCacheRegionFactory"); //before hibernate 5.3
        configuration.setProperty("hibernate.cache.region.factory_class", "org.hibernate.cache.jcache.JCacheRegionFactory"); // hibernate 5.3+
        configuration.setProperty("hibernate.cache.provider_configuration_file_resource_path", "ehcache.xml");

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();

        SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);

        try {
        	insertTestData(sessionFactory);
            firstLevelCacheExample(sessionFactory);
            secondLevelCacheExample(sessionFactory);
            queryCacheExample(sessionFactory);
        } finally {
            sessionFactory.close();
        }
    }

    // First-level cache example
    private static void firstLevelCacheExample(SessionFactory sessionFactory) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        try {
            Customer customer = session.get(Customer.class, 1L);
            if (customer != null) {
                System.out.println("First fetch: " + customer.getName());

                Customer customer2 = session.get(Customer.class, 1L);
                System.out.println("Second fetch: " + customer2.getName());
            } else {
                System.out.println("Customer not found!");
            }
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    // Second-level cache example
    private static void secondLevelCacheExample(SessionFactory sessionFactory) {
        Session session1 = sessionFactory.openSession();
        Transaction transaction1 = session1.beginTransaction();

        try {
            Customer customer = session1.get(Customer.class, 1L);
            if (customer != null) {
                System.out.println("First session fetch: " + customer.getName());
            } else {
                System.out.println("Customer not found in session 1!");
            }
            transaction1.commit();
        } catch (Exception e) {
            transaction1.rollback();
            e.printStackTrace();
        } finally {
            session1.close();
        }

        Session session2 = sessionFactory.openSession();
        Transaction transaction2 = session2.beginTransaction();

        try {
            Customer customer2 = session2.get(Customer.class, 1L);
            if (customer2 != null) {
                System.out.println("Second session fetch (second-level cache): " + customer2.getName());
            } else {
                System.out.println("Customer not found in session 2!");
            }
            transaction2.commit();
        } catch (Exception e) {
            transaction2.rollback();
            e.printStackTrace();
        } finally {
            session2.close();
        }
    }

    // Query cache example
    private static void queryCacheExample(SessionFactory sessionFactory) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        try {
            Query<Customer> query = session.createQuery("from Customer where name = :name", Customer.class);
            query.setParameter("name", "John");
            query.setCacheable(true);

            List<Customer> customers = query.list();
            System.out.println("First query fetch: " + customers.size() + " customers");

            Query<Customer> query2 = session.createQuery("from Customer where name = :name", Customer.class);
            query2.setParameter("name", "John");
            query2.setCacheable(true);

            List<Customer> customers2 = query2.list();
            System.out.println("Second query fetch (query cache): " + customers2.size() + " customers");

            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
    
    private static void insertTestData(SessionFactory sessionFactory) {
    	Session session = sessionFactory.openSession();
                Transaction transaction = session.beginTransaction();
        try {
            Customer customer = new Customer();
            customer.setName("John");
            session.persist(customer);
            transaction.commit();
        }catch (Exception e) {
            System.out.println("READ Lock Exception: " + e.getMessage());
            transaction.rollback();
        } finally {
            session.close();
        }
    }

}
