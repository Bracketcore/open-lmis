/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.upload;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.exception.DataException;
import org.openlmis.upload.Importable;
import org.openlmis.upload.model.AuditFields;

import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class AbstractModelPersistenceHandlerTest {
  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Test
  public void shouldAppendRowNumberToExceptionMessage() throws Exception {
    AbstractModelPersistenceHandler handler = instantiateHandlerThrowingExceptionOnSave();

    Importable importable = new TestImportable();
    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("code: upload.record.error, params: { error; 1 }");

    handler.execute(importable, 2, new AuditFields(1L, null));
  }


  @Test
  public void shouldAddAuditInformationToModel() throws Exception {

    Date currentTimestamp = new Date();
    AuditFields auditFields = new AuditFields(1L, currentTimestamp);
    Importable currentRecord = new TestImportable();
    AbstractModelPersistenceHandler handler = instantiateHandler(null);

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

    AbstractModelPersistenceHandler handler = instantiateHandler(existing);

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

    AbstractModelPersistenceHandler handler = instantiateHandler(existing);

    expectedEx.expect(DataException.class);
    expectedEx.expectMessage("Duplicate Record");

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
        throw new DataException("error");
      }

      @Override
      protected String getDuplicateMessageKey() {
        return "Duplicate Record";
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
        return "Duplicate Record";
      }

    };
  }


  class TestImportable extends BaseModel implements Importable {

  }
}
