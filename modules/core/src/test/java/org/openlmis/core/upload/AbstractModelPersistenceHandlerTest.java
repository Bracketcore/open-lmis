/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.service.MessageService;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.upload.Importable;
import org.openlmis.upload.model.AuditFields;

import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.openlmis.core.matchers.Matchers.dataExceptionMatcher;


@Category(UnitTests.class)
@RunWith(MockitoJUnitRunner.class)
public class AbstractModelPersistenceHandlerTest {
  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Mock
  MessageService messageService;

  AbstractModelPersistenceHandler handler;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
  }

  @Ignore
  @Test
  public void shouldAppendRowNumberToExceptionMessage() throws Exception {
    handler = instantiateHandlerThrowingExceptionOnSave();
    handler.messageService = messageService;
    Importable importable = new TestImportable();
    expectedEx.expect(dataExceptionMatcher("upload.record.error", "Error Msg", "1"));

    when(messageService.message("error.code")).thenReturn("Error Msg");

    handler.execute(importable, 2, new AuditFields(1L, null));
  }


  @Test
  public void shouldAddAuditInformationToModel() throws Exception {

    Date currentTimestamp = new Date();
    AuditFields auditFields = new AuditFields(1L, currentTimestamp);
    Importable currentRecord = new TestImportable();
    handler = instantiateHandler(null);

    handler.execute(currentRecord, 1, auditFields);

    assertThat(((BaseModel) currentRecord).getModifiedDate(), is(currentTimestamp));
    assertThat(((BaseModel) currentRecord).getModifiedBy(), is(1L));
    assertThat(((BaseModel) currentRecord).getId(), is(nullValue()));
  }

  @Test
  public void shouldAddIdFromExistingModel() throws Exception {

    final Date currentTimestamp = new Date();
    AuditFields auditFields = new AuditFields(1L, currentTimestamp);
    Importable currentRecord = new TestImportable();
    BaseModel existing = new BaseModel() {
    };
    existing.setId(2L);
    existing.setModifiedDate(DateUtils.addDays(currentTimestamp, -1));

    handler = instantiateHandler(existing);

    handler.execute(currentRecord, 1, auditFields);

    assertThat(((BaseModel) currentRecord).getModifiedDate(), is(currentTimestamp));
    assertThat(((BaseModel) currentRecord).getModifiedBy(), is(1L));
    assertThat(((BaseModel) currentRecord).getId(), is(2L));
  }

  @Test
  public void shouldThrowExceptionIfModifiedDateOfExistingRecordIsSameAsCurrentTimeStamp() throws Exception {

    final Date currentTimestamp = new Date();
    AuditFields auditFields = new AuditFields(1L, currentTimestamp);
    Importable currentRecord = new TestImportable();
    BaseModel existing = new BaseModel() {
    };
    existing.setId(2L);
    existing.setModifiedDate(currentTimestamp);

    handler = instantiateHandler(existing);
    handler.messageService = messageService;
    when(messageService.message("duplicate.record.error.code")).thenReturn("Duplicate Record");
    when(messageService.message("upload.record.error", "Duplicate Record", "0")).thenReturn("Upload error, Duplicate Record in row 0");

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Upload error, Duplicate Record in row 0");

    handler.execute(currentRecord, 1, auditFields);

  }

  private AbstractModelPersistenceHandler instantiateHandlerThrowingExceptionOnSave() {
    return new AbstractModelPersistenceHandler() {
      @Override
      protected BaseModel getExisting(BaseModel record) {
        return null;
      }

      @Override
      protected void save(BaseModel record) {
        throw new DataException("error.code");
      }

      @Override
      protected String getDuplicateMessageKey() {
        return "duplicate.record.error.code";
      }

    };
  }

  private AbstractModelPersistenceHandler instantiateHandler(final BaseModel existing) {
    return new AbstractModelPersistenceHandler() {
      @Override
      protected BaseModel getExisting(BaseModel record) {
        return existing;
      }

      @Override
      protected void save(BaseModel record) {
      }

      @Override
      protected String getDuplicateMessageKey() {
        return "duplicate.record.error.code";
      }

    };
  }


  class TestImportable extends BaseModel implements Importable {

  }
}
