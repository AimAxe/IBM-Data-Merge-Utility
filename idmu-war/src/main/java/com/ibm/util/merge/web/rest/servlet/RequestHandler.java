package com.ibm.util.merge.web.rest.servlet;

import com.ibm.util.merge.RuntimeContext;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;

/**
 *
 */
public interface RequestHandler {

    void initialize(ServletConfig servletConfig, RuntimeContext runtimeContext);

    boolean canHandle(RequestData rd);

    Result handle(RequestData rd, HttpServletRequest request);
}
