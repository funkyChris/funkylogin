/*
 * Qualipso Funky Factory
 * Copyright (C) 2006-2010 INRIA
 * http://www.inria.fr - molli@loria.fr
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation. See the GNU
 * Lesser General Public License in LGPL.txt for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 * Initial authors :
 *
 * Jérôme Blanchard / INRIA
 * Christophe Bouthier / INRIA
 * Pascal Molli / Nancy Université
 * Gérald Oster / Nancy Université
 */
package org.qualipso.funkyfactory.ui.clock.server;

import java.util.Hashtable;

import javax.ejb.EJB;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.security.auth.callback.UsernamePasswordHandler;
import org.qualipso.funkyfactory.service.clock.ClockService;
import org.qualipso.funkyfactory.service.clock.ClockServiceException;
import org.qualipso.funkyfactory.ui.clock.client.ClockServlet;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Implementation of the ClockService RPC service.
 * 
 * @author <a href="mailto:christophe.bouthier@loria.fr">Christophe Bouthier</a>
 * @date 16 September 2009
 */
@SuppressWarnings("serial")
public class ClockServletImpl extends RemoteServiceServlet implements ClockServlet {
    @EJB(mappedName = "ClockService")
    private ClockService clock;

    private static Log logger = LogFactory.getLog(ClockServletImpl.class);
    
    @SuppressWarnings("unchecked")
    public String getTime() {
        String serverTime = "";
        
        String ssoid = getCookieValue(this.getThreadLocalRequest(), "SSOSESSIONID");
        logger.info("clock sso session: " + ssoid);
        String username = null;
        String password = null;
        
        if (ssoid != null) {
            Hashtable userinfo = getUserInfo(ssoid);
            username = (String) userinfo.get("username");
            password = (String) userinfo.get("password");
            logger.info("session read: username = " + username + "  password = " + password);
        } else {
            logger.info("la session SSO est NULLE");
        }
            
        UsernamePasswordHandler uph = new UsernamePasswordHandler(username, password);
        LoginContext loginContext;
        try {
            loginContext = new LoginContext("client-login", uph);
            loginContext.login();
        } catch (LoginException e) {
            logger.info("ca pete dans le login");
            e.printStackTrace();
        }

        try {
            serverTime = clock.getTime();
        } catch (ClockServiceException e) {
            logger.error("ClockServiceException");
            logger.error(e.getMessage(), e);
        }
        
        logger.info("time: " + serverTime);

        return serverTime;
    }
    
    @SuppressWarnings("unchecked")
    public Hashtable getUserInfo(String ssosessionid) {
        Hashtable shareddata = (Hashtable) this.getServletContext().getContext("/funkyfactory-ui-login").getAttribute("shared_data");
        Hashtable userinfo = null;
        if (shareddata != null) {
            userinfo = (Hashtable) shareddata.get(ssosessionid); 
        }
        return userinfo;
    }
 
    public static String getCookieValue(HttpServletRequest request, String name) {
        boolean found = false;
        String result = null;
        Cookie[] cookies = request.getCookies();
        if (cookies!=null) {
            int i = 0;
            while (!found && i < cookies.length) {
                if (cookies[i].getName().equals(name)) {
                    found=true;
                    result = cookies[i].getValue();
                }
                i++;
              }
        }

        return (result);
    }
}
