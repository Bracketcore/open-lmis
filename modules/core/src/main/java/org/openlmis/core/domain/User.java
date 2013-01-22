package org.openlmis.core.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.hash.Encoder;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;

import java.util.Date;
import java.util.regex.Pattern;

@Data
@NoArgsConstructor
public class User implements Importable {
  private Integer id;
  @ImportField(mandatory = true, name = "User Name")
  private String userName;
  @ImportField(mandatory = true, name = "Password")
  private String password;
  @ImportField(mandatory = true, name = "First Name")
  private String firstName;
  @ImportField(mandatory = true, name = "Last Name")
  private String lastName;
  @ImportField(name = "Employee Id")
  private String employeeId;
  @ImportField(name = "Job Title")
  private String jobTitle;
  @ImportField(name = "Primary Notification Method")
  private String primaryNotificationMethod;
  @ImportField(name = "Office Phone")
  private String officePhone;
  @ImportField(name = "Cell Phone")
  private String cellPhone;
  @ImportField(mandatory = true, name = "Email")
  private String email;
  @ImportField(name = "Supervisor User Name", nested = "userName")
  private User supervisor;

  private Integer facilityId;
  private String modifiedBy;
  private Date modifiedDate;

  private static final String INVALID_EMAIL_ERROR_CODE = "user.email.invalid";

  private static final String INVALID_USER_NAME_ERROR_CODE = "user.userName.invalid";

  public void validate() {
    validateEmail();
    validateUserName();
  }

  public void setPassword(String password){
    this.password = Encoder.hash(password);
  }

  private void validateEmail() {
    final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    if (email!=null && !pattern.matcher(email).matches())
      throw new DataException(INVALID_EMAIL_ERROR_CODE);
  }

  private void validateUserName() {
    if(userName!=null && userName.trim().contains(" "))
      throw new DataException(INVALID_USER_NAME_ERROR_CODE);
  }
}
