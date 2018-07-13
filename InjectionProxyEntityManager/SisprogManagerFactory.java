/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.sisprog.common.injections;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author César Júnior
 * @since 29/05/2018
 */

@Singleton
public class SisprogManagerFactory {
    
    private final Map<String, EntityManagerFactory> entityManagerFactories = new HashMap<>();
    
    private void createEntityManagerFactory(final String persistenceUnit) throws Exception {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory(persistenceUnit);
        entityManagerFactories.put(persistenceUnit, factory);
    }
    
    public EntityManagerFactory getFactory(String persistenceUnit) throws Exception {
        if(!entityManagerFactories.containsKey(persistenceUnit)) {
            createEntityManagerFactory(persistenceUnit);
        }
        return entityManagerFactories.get(persistenceUnit);
    }
    
    @PreDestroy
    private void destroyFactories() {
        entityManagerFactories.forEach((key, factory) -> factory.close());
        entityManagerFactories.clear();
        System.out.println("@PreDestroy - Fechando as EntityManagerFactories.");
    }
    
}
