package org.openlmis.pod.repository.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openlmis.context.ApplicationTestContext;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.db.categories.IntegrationTests;
import org.openlmis.order.domain.Order;
import org.openlmis.pod.domain.POD;
import org.openlmis.pod.domain.PODLineItem;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@Category(IntegrationTests.class)
public class PODMapperIT extends ApplicationTestContext {

  @Autowired
  PODMapper podMapper;

  @Autowired
  QueryExecutor queryExecutor;

  String productCode;
  Order order;

  @Before
  public void setUp() throws Exception {

    productCode = "P10";
    order = insertOrder(productCode);
  }

  @Test
  public void shouldInsertPOD() {
    POD pod = new POD();
    pod.setOrderId(order.getId());

    podMapper.insertPOD(pod);

    assertThat(pod.getId(), is(notNullValue()));

  }

  @Test
  public void shouldInsertPODLineItem() {
    POD pod = new POD();
    pod.setOrderId(order.getId());
    podMapper.insertPOD(pod);

    PODLineItem podLineItem = new PODLineItem(pod.getId(), productCode, 100);
    podMapper.insertPODLineItem(podLineItem);

    List<PODLineItem> podLineItems = podMapper.getPODLineItemsByPODId(pod.getId());
    assertThat(podLineItems.size(), is(1));
    assertThat(podLineItems.get(0).getProductCode(), is(productCode));
  }


  @Test
  public void shouldGetPodLineItemsByOrderId() throws SQLException {
    POD pod = new POD();
    pod.setOrderId(order.getId());
    podMapper.insertPOD(pod);
    queryExecutor.executeUpdate("INSERT INTO pod_line_items (podId, productCode, quantityReceived, createdBy, modifiedBy) values(?, ?, ?, ?, ?)",
      pod.getId(), productCode, 100, 1, 1);

    List<PODLineItem> podLineItems = podMapper.getPODLineItemsByPODId(pod.getId());
    assertThat(podLineItems.size(), is(1));
    assertThat(podLineItems.get(0).getProductCode(), is(productCode));
  }
  
  @Test
  public void shouldGetPODByOrderId() throws SQLException {
    POD pod = new POD();
    pod.setOrderId(order.getId());
    queryExecutor.executeUpdate("INSERT INTO pod(orderId) values(?)",order.getId());

    POD savedPOD = podMapper.getPODByOrderId(order.getId());
    assertThat(savedPOD, is(notNullValue()));
    assertThat(savedPOD.getOrderId(), is(order.getId()));
  }


}
