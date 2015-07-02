package com.ibm.util.merge.web.rest.app;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;



@ApplicationPath("/rest")
public class RestApp extends Application {
    public Set<Class<?>> getClasses() {
        return new HashSet<Class<?>>(Arrays.asList(CollectionsResource.class, CollectionResource.class, TemplatesResource.class, TemplateResource.class));
    }
}