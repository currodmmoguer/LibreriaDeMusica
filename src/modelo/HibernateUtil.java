package modelo;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class HibernateUtil {

	private static SessionFactory sessionFactory;

	/**
	 * Asigna valor al SessionFactory
	 */
	private HibernateUtil() {
		Configuration configuration = new Configuration();
		configuration.configure();
		ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties())
				.buildServiceRegistry();
		sessionFactory = configuration.buildSessionFactory(serviceRegistry);
	}
	
	/**
	 * Obtiene el SessionFactory. En caso de que no tenga valor asignado se lo asigna
	 * @return SessionFactory
	 */
	public static SessionFactory getSessionFactory() {
		if (sessionFactory == null)
			new HibernateUtil();
		return sessionFactory;
	}
	
	/**
	 * Cierra la sesión en caso de que esté inicializada y abiera
	 */
	public static void closeSessionFactory() {
        if ((sessionFactory!=null) && (sessionFactory.isClosed()==false)) {
            sessionFactory.close();
        }
    }
}
