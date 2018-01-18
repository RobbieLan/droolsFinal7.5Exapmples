/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.examples.helloworld;

import org.drools.core.base.mvel.MVELDebugHandler;
import org.kie.api.KieServices;
import org.kie.api.event.rule.DebugAgendaEventListener;
import org.kie.api.event.rule.DebugRuleRuntimeEventListener;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.mvel2.MVELRuntime;
import org.mvel2.debug.Debugger;
import org.mvel2.debug.Frame;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a sample file to launch a rule package from a rule source file.
 */
public class HelloWorldExample {
	static KieServices ks = null;

	public static final void main(final String[] args) {
		// KieServices is the factory for all KIE services
		ks = KieServices.Factory.get();

		// Creates a KieContainer from the project classpath.
		// This will look for a /META-INF/kmodule.xml file to configure
		// and instantiate the KieModule into the KieContainer.
		KieContainer kc = ks.getKieClasspathContainer();

		execute(kc);
	}

	public static void execute(KieContainer kc) {
		// From the container, a session is created based on
		// its definition and configuration in the META-INF/kmodule.xml file
		KieSession ksession = kc.newKieSession("HelloWorldKS");

		// Once the session is created, the application can interact with it
		// In this case it is setting a global as defined in the
		// org/drools/examples/helloworld/HelloWorld.drl file
		ksession.setGlobal("list", new ArrayList<Object>());

		// The application can also setup listeners
		ksession.addEventListener(new DebugAgendaEventListener());
		ksession.addEventListener(new DebugRuleRuntimeEventListener());
		
		// To setup a file based audit logger, uncomment the next line
//		KieRuntimeLogger logger = ks.getLoggers().newFileLogger(ksession, "./helloworld");

		// To setup a ThreadedFileLogger, so that the audit view reflects events whilst
		// debugging,
		// uncomment the next line
		// KieRuntimeLogger logger = ks.getLoggers().newThreadedFileLogger( ksession,
		// "./helloworld", 1000 );

		// The application can insert facts into the session
		final Message message = new Message();
		message.setMessage("Hola World");
		message.setStatus(Message.HELLO);
		ksession.insert(message);

		// and fire the rules
		ksession.fireAllRules();

		// Remove comment if using logging
		// logger.close();

		// and then dispose the session
		// Stateful rule session must always be disposed when finished
		ksession.dispose();
	}

	public static class Message {
		public static final int HELLO = 0;
		public static final int GOODBYE = 1;

		private String message;

		private int status;

		public Message() {

		}

		public String getMessage() {
			return this.message;
		}

		public void setMessage(final String message) {
			this.message = message;
		}

		public int getStatus() {
			return this.status;
		}

		public void setStatus(final int status) {
			this.status = status;
		}

		public static Message doSomething(Message message) {
			return message;
		}

		public boolean isSomething(String msg, List<Object> list) {
			list.add(this);
			return this.message.equals(msg);
		}
	}

}
