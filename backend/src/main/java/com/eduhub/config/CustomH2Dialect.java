package com.eduhub.config;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.service.ServiceRegistry;

/**
 * Custom H2 Dialect for demo mode without PostgreSQL.
 * Currently not used since normal/demo modes use PostgreSQL.
 */
public class CustomH2Dialect extends H2Dialect {
    
    @Override
    public void contributeTypes(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
        super.contributeTypes(typeContributions, serviceRegistry);
    }
}
