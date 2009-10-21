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
package org.qualipso.funkyfactory.ui.login.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * 
 * @author <a href="mailto:christophe.bouthier@loria.fr">Christophe Bouthier</a>
 * @date 9 October 2009
 */
public class Funkyfactory_ui_login implements EntryPoint {
    private final LoginServletAsync loginServ = GWT.create(LoginServlet.class);

    public void onModuleLoad() {
        final TextBox username = new TextBox();
        final PasswordTextBox password = new PasswordTextBox();
        final Button login = new Button("login");
        
        RootPanel.get("loginContainer").add(username);
        RootPanel.get("loginContainer").add(password);
        RootPanel.get("loginContainer").add(login);
        
        class MyHandler implements ClickHandler {

            public void onClick(ClickEvent event) {
                login();
            }

            private void login() {
                System.out.println("coucou lapin");
                loginServ.login(username.getText(), password.getText(), new AsyncCallback<Boolean>() {
                    public void onFailure(Throwable caught) {
                        System.out.println("plante : " + caught);
                    }

                    public void onSuccess(Boolean result) {
                        System.out.println("Da Rulez !!! " + result);
                    }
                });
            }
        }
        
      MyHandler handler = new MyHandler();
      login.addClickHandler(handler);
    }
}
