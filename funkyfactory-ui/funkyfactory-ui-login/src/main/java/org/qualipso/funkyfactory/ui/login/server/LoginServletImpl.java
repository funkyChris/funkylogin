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
package org.qualipso.funkyfactory.ui.login.server;

import java.util.Hashtable;

import javax.ejb.EJB;
import javax.ejb.EJBAccessException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.security.auth.callback.UsernamePasswordHandler;
import org.qualipso.factory.FactoryNamingConvention;
import org.qualipso.factory.membership.MembershipService;
import org.qualipso.factory.membership.MembershipServiceException;
import org.qualipso.funkyfactory.ui.login.client.LoginServlet;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Implementation of the ClockService RPC service.
 * 
 * @author <a href="mailto:christophe.bouthier@loria.fr">Christophe Bouthier</a>
 * @date 9 October 2009
 */
@SuppressWarnings("serial")
public class LoginServletImpl extends RemoteServiceServlet implements LoginServlet {

    private static Log logger = LogFactory.getLog(LoginServletImpl.class);
    
    @EJB(mappedName = FactoryNamingConvention.JNDI_SERVICE_PREFIX + "MembershipService")
    private MembershipService membership;

    public Boolean login(String username, String password) {

        logger.info("login: USERNAME=" + username + "   --- PASSWORD=" + password);
        
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
            logger.info("Profile Path" + membership.getProfilePathForConnectedIdentifier());
        } catch (MembershipServiceException e) {
            logger.info("ca pete dans le membership");
            e.printStackTrace();
        } catch (EJBAccessException e1) {
            logger.info("Thou Shalt Not Pass !!!");
            return new Boolean(false);
        }        
        
        HttpServletRequest request = this.getThreadLocalRequest();
        HttpSession session = request.getSession();
        session.setAttribute("username", username);
        session.setAttribute("password", password);
        logger.info("session stored: " + username + " " + password);

        String sessionid = session.getId();
        logger.info("login session: " + sessionid);
        Cookie ssocookie = new Cookie("SSOSESSIONID", sessionid);
        ssocookie.setPath("/");
        this.getThreadLocalResponse().addCookie(ssocookie);
        
        storeDataInContext(sessionid, username, password);
        
        return true;
    }

    
    @SuppressWarnings("unchecked")
    public void storeDataInContext(String ssosessionid, String username, String password) {
        logger.info("shared context: " + this.getServletContext().getContextPath());
        Hashtable shareddata = (Hashtable) this.getServletContext().getAttribute("shared_data");
        if (shareddata == null) {
            shareddata = new Hashtable();
        }
        
        Hashtable userinfo = new Hashtable();
        userinfo.put("username", username);
        userinfo.put("password", password);

        shareddata.put(ssosessionid, userinfo);

        this.getServletContext().setAttribute("shared_data", shareddata);
    }
    
}
