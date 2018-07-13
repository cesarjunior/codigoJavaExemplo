package br.com.sisprog.auth.security;

import java.lang.reflect.Method;
import javax.servlet.ServletContext;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author César Júnior
 */
@Provider
public class SecurityCheckInterceptor implements DynamicFeature {

    @Context
    ServletContext context;
    
    @Override
    public void configure(ResourceInfo ri, FeatureContext fc) {
        Method method = ri.getResourceMethod();
        SecurityCheck check = method.getAnnotation(SecurityCheck.class);
        if (check == null) return;
        SecurityRequestFilter filterReq = new SecurityRequestFilter(check);
        fc.register(filterReq);       
        SecurityResponseFilter filterRes = new SecurityResponseFilter(check);
        fc.register(filterRes);       
    }
    
}
