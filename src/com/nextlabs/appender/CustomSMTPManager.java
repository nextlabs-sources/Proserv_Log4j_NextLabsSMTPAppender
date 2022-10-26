package com.nextlabs.appender;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import javax.activation.DataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;

import org.apache.logging.log4j.LoggingException;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractManager;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.impl.MutableLogEvent;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;
import org.apache.logging.log4j.core.layout.AbstractStringLayout.Serializer;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.net.MimeMessageBuilder;
import org.apache.logging.log4j.core.util.CyclicBuffer;
import org.apache.logging.log4j.core.util.NameUtil;
import org.apache.logging.log4j.core.util.NetUtils;
import org.apache.logging.log4j.message.ReusableMessage;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.Strings;

public class CustomSMTPManager extends AbstractManager {
	private static final SMTPManagerFactory FACTORY = new SMTPManagerFactory();

	private final Session session;

	private final CyclicBuffer<LogEvent> buffer;

	private volatile MimeMessage message;
	private final FactoryData data;

	private static String emailHeader;
	private static String emailFooter;

	private static MimeMessage createMimeMessage(FactoryData data, Session session, LogEvent appendEvent)
			throws MessagingException {
		return new MimeMessageBuilder(session).setFrom(data.from).setReplyTo(data.replyto)
				.setRecipients(Message.RecipientType.TO, data.to).setRecipients(Message.RecipientType.CC, data.cc)
				.setRecipients(Message.RecipientType.BCC, data.bcc).setSubject(data.subject.toSerializable(appendEvent))
				.build();
	}

	protected CustomSMTPManager(String name, Session session, MimeMessage message, FactoryData data) {
		super(null, name);
		this.session = session;
		this.message = message;
		this.data = data;
		buffer = new CyclicBuffer(LogEvent.class, data.numElements);
	}

	public void add(LogEvent event) {
		if (((event instanceof Log4jLogEvent)) && ((event.getMessage() instanceof ReusableMessage))) {
			((Log4jLogEvent) event).makeMessageImmutable();
		} else if ((event instanceof MutableLogEvent)) {
			event = ((MutableLogEvent) event).createMemento();
		}
		buffer.add(event);
	}

	public static CustomSMTPManager getSmtpManager(Configuration config, String to, String cc, String bcc, String from,
			String replyTo, String subject, String protocol, String host, int port, String username, String password,
			boolean isDebug, String filterName, int numElements, String header, String footer) {
		if (Strings.isEmpty(protocol)) {
			protocol = "smtp";
		}

		StringBuilder sb = new StringBuilder();
		if (to != null) {
			sb.append(to);
		}
		sb.append(':');
		if (cc != null) {
			sb.append(cc);
		}
		sb.append(':');
		if (bcc != null) {
			sb.append(bcc);
		}
		sb.append(':');
		if (from != null) {
			sb.append(from);
		}
		sb.append(':');
		if (replyTo != null) {
			sb.append(replyTo);
		}
		sb.append(':');
		if (subject != null) {
			sb.append(subject);
		}
		sb.append(':');
		sb.append(protocol).append(':').append(host).append(':').append("port").append(':');
		if (username != null) {
			sb.append(username);
		}
		sb.append(':');
		if (password != null) {
			sb.append(password);
		}
		sb.append(isDebug ? ":debug:" : "::");
		sb.append(filterName);

		String name = "SMTP:" + NameUtil.md5(sb.toString());
		AbstractStringLayout.Serializer subjectSerializer = PatternLayout.newSerializerBuilder()
				.setConfiguration(config).setPattern(subject).build();

		emailHeader = header;
		emailFooter = footer;

		return (CustomSMTPManager) getManager(name, FACTORY, new FactoryData(to, cc, bcc, from, replyTo,
				subjectSerializer, protocol, host, port, username, password, isDebug, numElements));
	}

	public void sendEvents(Layout<?> layout, LogEvent appendEvent) {
		if (message == null) {
			connect(appendEvent);
		}
		try {
			LogEvent[] priorEvents = (LogEvent[]) buffer.removeAll();

			byte[] rawBytes = formatContentToBytes(priorEvents, appendEvent, layout);

			String contentType = layout.getContentType();

			System.out.println("Content type is " + contentType);

			String encoding = getEncoding(rawBytes, contentType);
			byte[] encodedBytes = encodeContentToBytes(rawBytes, encoding);

			// String temp = new String(encodedBytes);
			// System.out.println(temp);

			// InternetHeaders headers = getHeaders(contentType, encoding);

			// MimeMultipart mp = getMimeMultipart(encodedBytes, headers);

			// message.setText(appendEvent.getMessage().getFormat());

			// sendMultipartMessage(message, mp);

			message.setContent(new String(encodedBytes), layout.getContentType());
			message.setSentDate(new Date());
			Transport.send(message);
		} catch (MessagingException | IOException | RuntimeException e) {
			logError("Caught exception while sending e-mail notification.", e);
			throw new LoggingException("Error occurred while sending email", e);
		}
	}

	protected byte[] formatContentToBytes(LogEvent[] priorEvents, LogEvent appendEvent, Layout<?> layout)
			throws IOException {
		ByteArrayOutputStream raw = new ByteArrayOutputStream();
		writeContent(priorEvents, appendEvent, layout, raw);
		return raw.toByteArray();
	}

	private void writeContent(LogEvent[] priorEvents, LogEvent appendEvent, Layout<?> layout, ByteArrayOutputStream out)
			throws IOException {

		writeHeader(layout, out);
		writeBuffer(priorEvents, appendEvent, layout, out);
		writeFooter(layout, out);
	}

	protected void writeHeader(Layout<?> layout, OutputStream out) throws IOException {

		if (CustomSMTPManager.emailHeader != null) {
			out.write(CustomSMTPManager.emailHeader.getBytes());
		}

		byte[] header = layout.getHeader();
		if (header != null) {
			out.write(header);
		}
	}

	protected void writeBuffer(LogEvent[] priorEvents, LogEvent appendEvent, Layout<?> layout, OutputStream out)
			throws IOException {
		for (LogEvent priorEvent : priorEvents) {
			byte[] bytes = layout.toByteArray(priorEvent);
			out.write(bytes);
		}

		// String message = appendEvent.getMessage().getFormattedMessage();
		// message = "\r" + message + "\r";
		// System.out.println(message);
		// byte[] customBytes = message.getBytes(Charset.forName("UTF-8"));
		byte[] bytes = layout.toByteArray(appendEvent);
		out.write(bytes);
	}

	protected void writeFooter(Layout<?> layout, OutputStream out) throws IOException {
		byte[] footer = layout.getFooter();
		if (footer != null) {
			out.write(footer);
		}
		
		if (CustomSMTPManager.emailFooter != null) {
			out.write(CustomSMTPManager.emailFooter.getBytes());
		}
	}

	protected String getEncoding(byte[] rawBytes, String contentType) {
		DataSource dataSource = new ByteArrayDataSource(rawBytes, contentType);
		return MimeUtility.getEncoding(dataSource);
	}

	protected byte[] encodeContentToBytes(byte[] rawBytes, String encoding) throws MessagingException, IOException {
		ByteArrayOutputStream encoded = new ByteArrayOutputStream();
		encodeContent(rawBytes, encoding, encoded);
		return encoded.toByteArray();
	}

	protected void encodeContent(byte[] bytes, String encoding, ByteArrayOutputStream out)
			throws MessagingException, IOException {
		OutputStream encoder = MimeUtility.encode(out, encoding);
		Throwable localThrowable2 = null;
		try {
			encoder.write(bytes);
		} catch (Throwable localThrowable1) {
			localThrowable2 = localThrowable1;
			throw localThrowable1;
		} finally {
			if (encoder != null)
				if (localThrowable2 != null)
					try {
						encoder.close();
					} catch (Throwable x2) {
						localThrowable2.addSuppressed(x2);
					}
				else
					encoder.close();
		}
	}

	protected InternetHeaders getHeaders(String contentType, String encoding) {
		InternetHeaders headers = new InternetHeaders();
		headers.setHeader("Content-Type", contentType + "; charset=UTF-8");
		headers.setHeader("Content-Transfer-Encoding", encoding);
		return headers;
	}

	protected MimeMultipart getMimeMultipart(byte[] encodedBytes, InternetHeaders headers) throws MessagingException {
		MimeMultipart mp = new MimeMultipart();
		MimeBodyPart part = new MimeBodyPart(headers, encodedBytes);
		mp.addBodyPart(part);
		/*
		 * try { System.out.println("getMimeMultipart() " +
		 * mp.getBodyPart(0).getContent().toString()); } catch (IOException e) {
		 * // TODO Auto-generated catch block e.printStackTrace(); }
		 */
		return mp;
	}

	protected void sendMultipartMessage(MimeMessage msg, MimeMultipart mp) throws MessagingException {
		synchronized (msg) {
			msg.setContent(mp);
			// msg.setSentDate(new Date());
			Transport.send(msg);
		}
	}

	private synchronized void connect(LogEvent appendEvent) {
		if (message != null) {
			return;
		}
		try {
			message = createMimeMessage(data, session, appendEvent);
		} catch (MessagingException e) {
			logError("Could not set SmtpAppender message options", e);
			message = null;
		}
	}

	private static class FactoryData {
		private final String to;
		private final String cc;
		private final String bcc;
		private final String from;
		private final String replyto;
		private final Serializer subject;
		private final String protocol;
		private final String host;
		private final int port;
		private final String username;
		private final String password;
		private final boolean isDebug;
		private final int numElements;

		public FactoryData(final String to, final String cc, final String bcc, final String from, final String replyTo,
				final Serializer subjectSerializer, final String protocol, final String host, final int port,
				final String username, final String password, final boolean isDebug, final int numElements) {
			this.to = to;
			this.cc = cc;
			this.bcc = bcc;
			this.from = from;
			this.replyto = replyTo;
			this.subject = subjectSerializer;
			this.protocol = protocol;
			this.host = host;
			this.port = port;
			this.username = username;
			this.password = password;
			this.isDebug = isDebug;
			this.numElements = numElements;
		}
	}

	/**
	 * Factory to create the SMTP Manager.
	 */
	private static class SMTPManagerFactory implements ManagerFactory<CustomSMTPManager, FactoryData> {

		@Override
		public CustomSMTPManager createManager(final String name, final FactoryData data) {
			final String prefix = "mail." + data.protocol;

			final Properties properties = PropertiesUtil.getSystemProperties();
			properties.put("mail.transport.protocol", data.protocol);
			if (properties.getProperty("mail.host") == null) {
				// Prevent an UnknownHostException in Java 7
				properties.put("mail.host", NetUtils.getLocalHostname());
			}

			if (null != data.host) {
				properties.put(prefix + ".host", data.host);
			}
			if (data.port > 0) {
				properties.put(prefix + ".port", String.valueOf(data.port));
			}

			final Authenticator authenticator = buildAuthenticator(data.username, data.password);
			if (null != authenticator) {
				properties.put(prefix + ".auth", "true");
			}

			final Session session = Session.getInstance(properties, authenticator);
			session.setProtocolForAddress("rfc822", data.protocol);
			session.setDebug(data.isDebug);
			return new CustomSMTPManager(name, session, null, data);
		}

		private Authenticator buildAuthenticator(final String username, final String password) {
			if (null != password && null != username) {
				return new Authenticator() {
					private final PasswordAuthentication passwordAuthentication = new PasswordAuthentication(username,
							password);

					@Override
					protected PasswordAuthentication getPasswordAuthentication() {
						return passwordAuthentication;
					}
				};
			}
			return null;
		}

	}

}
