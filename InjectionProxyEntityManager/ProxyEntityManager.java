/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sisprog.common.injections;

import br.com.sisprog.common.exceptions.GenericException;
import java.lang.reflect.Proxy;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;



/**
 *
 * @author César Júnior
 * @since 28/05/2018
 * Classe responsável por empacotar a regra de criação de EntityManager e ser injetada nos Beans.
 */

@Stateless
@Dependent
public class ProxyEntityManager {
    
    private EntityManager entityManager;
    private String persistenceUnitName;
    
    @Inject
    private SisprogManagerFactory entityManagerFactory;
    
    private void createEntityManager() throws Exception{
        if(persistenceUnitName == null) {
            throw new GenericException("Erro ao criar o contexto de pensistencia, persistenceUnit inexistente.");
        }
        final EntityManagerFactory entityManagerFactory = this.entityManagerFactory.getFactory(persistenceUnitName);
        entityManagerFactory.getCache().evictAll();
        this.entityManager = entityManagerFactory.createEntityManager();
        //this.entityManager.setProperty("javax.persistence.storeMode", "REFRESH");
    }
    
    public EntityManager getEntityManager(String persistenceUnitName) throws Exception {
        if(this.entityManager == null || !this.entityManager.isOpen() || !persistenceUnitName.equals(this.persistenceUnitName)) {
            this.persistenceUnitName = persistenceUnitName;
            this.createEntityManager();
        }
        
        return (EntityManager) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[]{EntityManager.class},
            (proxy, method, args) -> {
                //this.entityManager.joinTransaction(); // Une a transação do Entitymanager com a do Container.
                return method.invoke(this.entityManager, args);
            });
    }
    
    public void closeEntityManager() {
        if(entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
        entityManager = null;
    }
    
    @PreDestroy
    public void preDestroy() {
        this.closeEntityManager();
        System.out.println("@PreDestroy - Fechando EntityManager");
    }
}
