package org.openlmis.restapi.controller;

import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;
import org.openlmis.pod.domain.OrderPOD;
import org.openlmis.restapi.response.RestResponse;
import org.openlmis.restapi.service.RestPODService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@NoArgsConstructor
public class RestPODController extends BaseController {

  @Autowired
  private RestPODService restPODService;

  @RequestMapping(value = "/rest-api/orders/{orderId}/pod", method = POST, headers = ACCEPT_JSON)
  public ResponseEntity<RestResponse> savePOD(@RequestBody OrderPOD orderPod, @PathVariable Long orderId, Principal principal) {
    try {
      orderPod.setOrderId(orderId);
      restPODService.updatePOD(orderPod, loggedInUserId(principal));
      return RestResponse.success("message.success.pod.updated");
    } catch (DataException e) {
      return RestResponse.error(e.getOpenLmisMessage(), BAD_REQUEST);
    }
  }

}
