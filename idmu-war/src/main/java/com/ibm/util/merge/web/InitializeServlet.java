/*
 * Copyright 2015, 2015 IBM
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.ibm.util.merge.web;

import com.ibm.util.merge.RuntimeContext;
import com.ibm.util.merge.TemplateFactory;
import com.ibm.util.merge.json.PrettyJsonProxy;
import com.ibm.util.merge.persistence.FilesystemPersistence;
import com.ibm.util.merge.web.rest.servlet.RequestHandler;
import com.ibm.util.merge.web.rest.servlet.handler.*;
import com.ibm.util.merge.web.rest.servlet.writer.TextResponseWriter;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class InitializeServlet extends HttpServlet {
    private Logger log = Logger.getLogger(InitializeServlet.class);
    private String warTemplatesPath = "/WEB-INF/templates";
    private String outputDirPath = "/tmp/merge";
    private final List<RequestHandler> handlerChain = new ArrayList<>();
    private Map<String, String> servletInitParameters;

    public InitializeServlet() {
    }

    /**
     * Initialize Logging, Template and Zip Factory objects
     */
    public void init(ServletConfig cfg) {
        servletInitParameters = RestServlet.initParametersToMap(cfg);
        applyInitParameters();
        initializeApp(cfg.getServletContext());
    }

    private void initializeApp(ServletContext servletContext) {
        handlerChain.clear();
        handlerChain.addAll(createHandlerInstances());
        String fullPath = servletContext.getRealPath(warTemplatesPath);
        PrettyJsonProxy jsonProxy = new PrettyJsonProxy();
        FilesystemPersistence fs = new FilesystemPersistence(fullPath, jsonProxy);
        TemplateFactory tf = new TemplateFactory(fs);
        RuntimeContext rtc = new RuntimeContext(tf);
        rtc.initialize(outputDirPath);
        servletContext.setAttribute("rtc", rtc);
        for (RequestHandler handler : handlerChain) {
            log.info("Initializing handler " + handler.getClass().getName());
            handler.initialize(servletInitParameters, rtc);
        }
        servletContext.setAttribute("handlerChain", handlerChain);
    }

    private ArrayList<RequestHandler> createHandlerInstances() {
        return new ArrayList<>(Arrays.asList(new AllTemplatesResourceHandler(), new AllDirectivesResourceHandler(), new CollectionTemplatesResourceHandler(), new CollectionTemplatesForTypeResourceHandler(), new CollectionForCollectionTypeColumnValueResourceHandler(), new TemplateForFullnameResourceHandler(), new CreateTemplateResourceHandler(), new UpdateTemplateResourceHandler(), new PerformMergeResourceHandler(), new DeleteTemplateResourceHandler()));
    }

    private void applyInitParameters() {
        String mergeTemplatesFolder = servletInitParameters.get("merge-templates-folder");
        if (mergeTemplatesFolder != null) {
            log.info("Setting from ServletConfig: warTemplatesPath=" + mergeTemplatesFolder);
            warTemplatesPath = mergeTemplatesFolder;
        }
        String outputRootDir = servletInitParameters.get("merge-output-root");
        if (outputRootDir != null) {
            log.info("Setting from ServletConfig: outputDirPath=" + outputRootDir);
            outputDirPath = outputRootDir;
        }
    }

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String textResult = "Last initialized successfully at " + RestServlet.findRuntimeContext(req.getServletContext()).getInitialized() + "\n" +
                "Make a POST request to " + req.getRequestURL() + " to reinitialize and reload templates";
        new TextResponseWriter(res, textResult).write();
    }

    /**
     * Reinitializes IDMU upon POST to this servlet
     */
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        initializeApp(req.getServletContext());
        new TextResponseWriter(res, "Successfully reinitialized IDMU.").write();
    }
}