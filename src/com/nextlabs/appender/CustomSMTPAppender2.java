/*
 *
 *
============================================================================
 *     This license is based on the Apache Software License, Version 1.1
 *
============================================================================
 *
 *    Copyright (C) 2001-2003 Mark Masterson. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modifica-
 * tion, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of  source code must  retain the above copyright  notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The end-user documentation included with the redistribution, if any, must
 *    include  the following  acknowledgment:  "This product includes  software
 *    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
 *    Alternately, this  acknowledgment may  appear in the software itself,  if
 *    and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names Mark Masterson, M2 Technologies, SNMPTrapAppender or log4j must
 *    not be used to endorse or promote products derived  from this  software
 *    without  prior written permission. For written permission, please contact
 *    m.masterson@computer.org.
 *
 * 5. Products  derived from this software may not  be called "Apache", nor may
 *    "Apache" appear  in their name,  without prior written permission  of the
 *    Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 * APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 * DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 * ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 * (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 */

package com.nextlabs.appender;

import java.io.Serializable;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.ValidPort;
import org.apache.logging.log4j.core.filter.ThresholdFilter;
import org.apache.logging.log4j.core.layout.HtmlLayout;
import org.apache.logging.log4j.core.util.Booleans;

@Plugin(name = "CustomSMTPAppender", category = Node.CATEGORY, elementType = "appender", printObject = true)
public class CustomSMTPAppender2 extends AbstractAppender {

	private static final int DEFAULT_BUFFER_SIZE = 512;
	private final CustomSMTPManager manager;

	protected CustomSMTPAppender2(String name, Filter filter, Layout<? extends Serializable> layout, CustomSMTPManager manager,
			boolean ignoreExceptions) {
		super(name, filter, layout, ignoreExceptions);
		this.manager = manager;
	}

	@PluginFactory
	public static CustomSMTPAppender2 createAppender(@PluginConfiguration Configuration config,
			@PluginAttribute("name") @Required String name, @PluginAttribute("to") String to,
			@PluginAttribute("cc") String cc, @PluginAttribute("bcc") String bcc, @PluginAttribute("from") String from,
			@PluginAttribute("replyTo") String replyTo, @PluginAttribute("subject") String subject,
			@PluginAttribute("smtpProtocol") String smtpProtocol, @PluginAttribute("smtpHost") String smtpHost,
			@PluginAttribute(value = "smtpPort", defaultString = "0") @ValidPort String smtpPortStr,
			@PluginAttribute("smtpUsername") String smtpUsername,
			@PluginAttribute(value = "smtpPassword", sensitive = true) String smtpPassword,
			@PluginAttribute("smtpDebug") String smtpDebug, @PluginAttribute("bufferSize") String bufferSizeStr,
			@PluginElement("Layout") Layout<? extends Serializable> layout, @PluginElement("Filter") Filter filter,
			@PluginAttribute("ignoreExceptions") String ignore,
			@PluginAttribute("header") String header,
			@PluginAttribute("footer") String footer) {
		if (name == null) {
			LOGGER.error("No name provided for SmtpAppender");
			return null;
		}

		boolean ignoreExceptions = Booleans.parseBoolean(ignore, true);
		int smtpPort = AbstractAppender.parseInt(smtpPortStr, 0);
		boolean isSmtpDebug = Boolean.parseBoolean(smtpDebug);
		int bufferSize = bufferSizeStr == null ? 512 : Integer.parseInt(bufferSizeStr);

		if (layout == null) {
			layout = HtmlLayout.createDefaultLayout();
		}
		if (filter == null) {
			filter = ThresholdFilter.createFilter(null, null, null);
		}
		Configuration configuration = config != null ? config : new DefaultConfiguration();

		CustomSMTPManager manager = CustomSMTPManager.getSmtpManager(configuration, to, cc, bcc, from, replyTo, subject,
				smtpProtocol, smtpHost, smtpPort, smtpUsername, smtpPassword, isSmtpDebug, filter.toString(),
				bufferSize, header, footer);

		if (manager == null) {
			return null;
		}

		return new CustomSMTPAppender2(name, filter, layout, manager, ignoreExceptions);
	}

	public boolean isFiltered(LogEvent event) {
		boolean filtered = super.isFiltered(event);
		if (filtered) {
			manager.add(event);
		}
		return filtered;
	}

	public void append(LogEvent event) {
		manager.sendEvents(getLayout(), event);
	}
}