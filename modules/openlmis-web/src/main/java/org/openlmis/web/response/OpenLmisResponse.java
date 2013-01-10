package org.openlmis.web.response;

import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.openlmis.core.message.OpenLmisMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

@NoArgsConstructor
public class OpenLmisResponse {
  public static final String ERROR = "error";
  public static final String SUCCESS = "success";
  private static ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", Locale.ENGLISH);


  private Map<String, Object> data = new HashMap<>();

  public OpenLmisResponse(String key, Object data) {
    setData(key, data);
  }

  public static ResponseEntity<OpenLmisResponse> success(String successMsg) {
    return new ResponseEntity<>(new OpenLmisResponse(SUCCESS, successMsg), HttpStatus.OK);
  }

  public static ResponseEntity<OpenLmisResponse> success(OpenLmisMessage openLmisMessage) {
    return new ResponseEntity<>(new OpenLmisResponse(SUCCESS, getDisplayText(openLmisMessage)), HttpStatus.OK);
  }

  public static ResponseEntity<OpenLmisResponse> error(String errorMsg, HttpStatus statusCode) {
    return new ResponseEntity<>(new OpenLmisResponse(ERROR, errorMsg), statusCode);
  }

  public static ResponseEntity<OpenLmisResponse> error(OpenLmisMessage openLmisMessage, HttpStatus httpStatus) {
    return new ResponseEntity<>(new OpenLmisResponse(ERROR, getDisplayText(openLmisMessage)), httpStatus);
  }

  public static ResponseEntity<OpenLmisResponse> response(String key, Object value) {
    return new ResponseEntity<>(new OpenLmisResponse(key, value), HttpStatus.OK);
  }

  @JsonAnyGetter
  @SuppressWarnings("unused")
  public Map<String, Object> getData() {
    return data;
  }

  @JsonAnySetter
  public void setData(String key, Object data) {
    this.data.put(key, data);
  }

  @JsonIgnore
  public String getErrorMsg() {
    return (String) data.get(ERROR);
  }

  @JsonIgnore
  public String getSuccessMsg() {
    return (String) data.get(SUCCESS);
  }


  private static String getDisplayText(OpenLmisMessage openLmisMessage) {
    try {
      return resourceBundle.getString(openLmisMessage.getCode());
    } catch (MissingResourceException e) {
      return openLmisMessage.getCode();
    }
  }
}
