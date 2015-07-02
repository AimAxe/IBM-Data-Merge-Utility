package com.ibm.util.merge.web.rest.sample;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Singleton
public class SampleResourcesRepository {
    private final List<SampleResource> resources;

    private static final List<SampleResource> RESOURCES = Collections.synchronizedList(new ArrayList<SampleResource>());

    public SampleResourcesRepository() {
        resources = RESOURCES;
    }

    public void persist(SampleResource todo) {
        resources.add(todo);
    }

    public List<SampleResource> list() {
        return resources;
    }

    public int count() {
        return resources.size();
    }

    public SampleResource get(String id) {
        for (SampleResource resource : resources) {
            if (resource.getName().equals(id)) return resource;
        }
        return null;
    }

    public SampleResource remove(String id) {
        SampleResource tr = get(id);
        if (resources.remove(tr)) {
            return tr;
        }
        return null;
    }

    public void merge(SampleResource c) {
        remove(c.getName());
        persist(c);
    }
}