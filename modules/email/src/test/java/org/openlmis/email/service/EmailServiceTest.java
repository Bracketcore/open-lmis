/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.email.service;

import com.mchange.net.MailSender;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.email.domain.EmailAttachment;
import org.openlmis.email.domain.EmailMessage;
import org.openlmis.email.repository.EmailNotificationRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.natpryce.makeiteasy.MakeItEasy.*;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static org.junit.rules.ExpectedException.none;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.openlmis.email.builder.EmailMessageBuilder.defaultEmailMessage;
import static org.openlmis.email.builder.EmailMessageBuilder.receiver;

@Category(UnitTests.class)
public class EmailServiceTest {
  @Rule
  public ExpectedException expectedException = none();

  private JavaMailSenderImpl mailSender;

  @Mock
  MailSender sender;

  @Mock
  EmailNotificationRepository repository;

  @Before
  public void setUp() throws Exception {
    mailSender = mock(JavaMailSenderImpl.class);
  }

  @Test
  public void shouldSendEmailMessage() throws Exception {
    SimpleMailMessage message = make(a(defaultEmailMessage,
      with(receiver, "alert.open.lmis@gmail.com")));

    EmailService service = new EmailService(mailSender, repository, true);
    boolean status = service.send(message).get();
    assertTrue(status);
    verify(mailSender).send(any(SimpleMailMessage.class));
  }

  @Test
  public void shouldNotSendEmailIfMailSendingFlagIsFalse() throws ExecutionException, InterruptedException {
    SimpleMailMessage message = make(a(defaultEmailMessage,
      with(receiver, "alert.open.lmis@gmail.com")));
    EmailService service = new EmailService(mailSender, repository, false);
    boolean status = service.send(message).get();
    assertTrue(status);
  }

  @Test
  public void shouldSendMailsFromAListOfMailMessages() throws Exception {
    EmailService service = new EmailService(mailSender, repository, true);

    EmailMessage mockEmailMessage = mock(EmailMessage.class);
    List<EmailMessage> emailMessages = asList(mockEmailMessage);
    when(mockEmailMessage.isHtml()).thenReturn(false);
    service.processEmails(emailMessages);

    verify(mailSender).send(any(SimpleMailMessage.class));
  }

  @Test
  public void shouldInsertAttachmentListOfMailMessage() throws Exception {
    repository = mock(EmailNotificationRepository.class);
    EmailService service = new EmailService(mailSender, repository, true);

    List<EmailAttachment> attachments = asList(new EmailAttachment(), new EmailAttachment());
    service.insertEmailAttachmentList(attachments);

    verify(repository, times(2)).insertEmailAttachment(any(EmailAttachment.class));
  }

}
