package org.openlmis.web.controller;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.openlmis.authentication.web.UserAuthenticationSuccessHandler;
import org.openlmis.upload.Importable;
import org.openlmis.upload.RecordHandler;
import org.openlmis.upload.parser.CSVParser;
import org.openlmis.web.controller.upload.MandatoryFields;
import org.openlmis.web.controller.upload.NonMandatoryFields;
import org.openlmis.web.handler.UploadHandlerFactory;
import org.openlmis.web.response.OpenLmisResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext.xml")
public class UploadControllerIT {

  public static final String USER = "user";

  @Autowired
  CSVParser csvParser;
  @Mock
  RecordHandler handler;

  @Mock
  @SuppressWarnings("unused")
  private UploadHandlerFactory uploadHandlerFactory;

  private MockHttpServletRequest request;

  @Autowired
  UploadController controller;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    Map<String, Class<? extends Importable>> modelMap = new HashMap<String, Class<? extends Importable>>() {{
      put("mandatoryFields", MandatoryFields.class);
      put("nonMandatoryFields", NonMandatoryFields.class);
    }};
    when(uploadHandlerFactory.getHandler(any(String.class))).thenReturn(handler);
    request = new MockHttpServletRequest();
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(UserAuthenticationSuccessHandler.USER, USER);
    request.setSession(session);
    controller = new UploadController(csvParser, uploadHandlerFactory, modelMap);
  }

  @Test
  public void shouldParseCsvWithMandatoryFields() throws Exception {
    InputStream in = UploadControllerIT.class.getClassLoader()
        .getResourceAsStream("mandatory-fields.csv");

    MockMultipartFile multiPart = new MockMultipartFile("csvFile", "mock.csv", null, in);
    ResponseEntity<OpenLmisResponse> responseEntity = controller.upload(multiPart, "mandatoryFields", request);
    ArgumentCaptor<MandatoryFields> validUploadTypeArgumentCaptor = ArgumentCaptor.forClass(MandatoryFields.class);
    verify(handler).execute(validUploadTypeArgumentCaptor.capture(), eq(2), eq("user"));

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals("File uploaded successfully. Total records uploaded: 2", responseEntity.getBody().getSuccessMsg());
    assertEquals("Val11", validUploadTypeArgumentCaptor.getValue().getFieldA());
    assertEquals("Val12", validUploadTypeArgumentCaptor.getValue().getFieldB());
    assertEquals("Nested Val13", validUploadTypeArgumentCaptor.getValue().getNestedValidUploadType().getNestedField());
  }

  @Test
  public void shouldParseCsvWithNonMandatoryFields() throws Exception {
    InputStream in = UploadControllerIT.class.getClassLoader()
        .getResourceAsStream("non-mandatory-fields.csv");
    MockMultipartFile multiPart = new MockMultipartFile("csvFile", "mock.csv", null, in);

    ResponseEntity<OpenLmisResponse> responseEntity = controller.upload(multiPart, "nonMandatoryFields", request);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    ArgumentCaptor<NonMandatoryFields> nonMandatoryFieldsArgumentCaptor = ArgumentCaptor.forClass(NonMandatoryFields.class);
    verify(handler).execute(nonMandatoryFieldsArgumentCaptor.capture(), eq(4), eq("user"));

    assertEquals("File uploaded successfully. Total records uploaded: 3", responseEntity.getBody().getSuccessMsg());
  }


}
