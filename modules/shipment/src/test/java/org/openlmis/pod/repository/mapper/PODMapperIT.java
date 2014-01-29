/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.pod.repository.mapper;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openlmis.context.ApplicationTestContext;
import org.openlmis.core.query.QueryExecutor;
import org.openlmis.db.categories.UnitTests;
import org.openlmis.order.domain.Order;
import org.openlmis.pod.domain.OrderPOD;
import org.openlmis.pod.domain.OrderPODLineItem;
import org.openlmis.rnr.domain.Rnr;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;

@Category(UnitTests.class)
public class PODMapperIT extends ApplicationTestContext {

  @Autowired
  PODMapper podMapper;

  @Autowired
  QueryExecutor queryExecutor;

  String productCode;
  Order order;
  private String dispensingUnit;
  private String productCategory;
  private String productName;
  private Integer productCategoryDisplayOrder;
  private Integer productDisplayOrder;

  @Before
  public void setUp() throws Exception {
    productCode = "P10";
    dispensingUnit = "Tablets";
    productCategory = "productCategory";
    productName = "productName";
    order = insertOrder(productCode);
    productCategoryDisplayOrder = 10;
    productDisplayOrder = 10;
  }

  @Test
  public void shouldInsertPOD() {
    OrderPOD orderPod = new OrderPOD();
    orderPod.setOrderId(order.getId());
    Rnr rnr = order.getRnr();
    orderPod.fillPOD(rnr);
    podMapper.insertPOD(orderPod);

    OrderPOD savedOrderPod = podMapper.getPODByOrderId(orderPod.getOrderId());

    assertThat(savedOrderPod.getId(), is(notNullValue()));
    assertThat(savedOrderPod.getFacilityId(), is(rnr.getFacility().getId()));
    assertThat(savedOrderPod.getProgramId(), is(rnr.getProgram().getId()));
    assertThat(savedOrderPod.getPeriodId(), is(rnr.getPeriod().getId()));
  }

  @Test
  public void shouldInsertPODLineItem() {
    OrderPOD orderPod = new OrderPOD();
    orderPod.setOrderId(order.getId());
    podMapper.insertPOD(orderPod);

    Integer quantityShipped = 1000;
    OrderPODLineItem orderPodLineItem = new OrderPODLineItem(orderPod.getId(), productCode, productCategory,
      productCategoryDisplayOrder, productDisplayOrder, 100, productName, dispensingUnit, 10, quantityShipped, true,
      "notes");
    podMapper.insertPODLineItem(orderPodLineItem);

    List<OrderPODLineItem> orderPodLineItems = podMapper.getPODLineItemsByPODId(orderPod.getId());
    assertThat(orderPodLineItems.size(), is(1));
    assertThat(orderPodLineItems.get(0).getProductCode(), is(productCode));
    assertThat(orderPodLineItems.get(0).getDispensingUnit(), is(dispensingUnit));
    assertThat(orderPodLineItems.get(0).getPacksToShip(), is(10));
    assertThat(orderPodLineItems.get(0).getProductName(), is(productName));
    assertThat(orderPodLineItems.get(0).getProductCategory(), is(productCategory));
    assertThat(orderPodLineItems.get(0).getProductCategoryDisplayOrder(), is(productCategoryDisplayOrder));
    assertThat(orderPodLineItems.get(0).getProductDisplayOrder(), is(productDisplayOrder));
    assertThat(orderPodLineItems.get(0).getQuantityShipped(), is(quantityShipped));
  }

  @Test
  public void shouldGetPodLineItemsByOrderId() throws SQLException {
    OrderPOD orderPod = new OrderPOD();
    orderPod.setOrderId(order.getId());
    podMapper.insertPOD(orderPod);
    String productCode1 = "productCode 1";
    String productCode2 = "ProductCode 2";
    String productCode3 = "productCode 3";
    insertProduct(productCode1);
    insertProduct(productCode2);
    insertProduct(productCode3);
    String productCategory = "product Category";
    Integer productCategoryDisplayOrder1 = 1;
    Integer productDisplayOrder1 = 1;
    queryExecutor.executeUpdate(
      "INSERT INTO pod_line_items (podId, productCode, productCategory, productCategoryDisplayOrder, productDisplayOrder, quantityReceived, createdBy, modifiedBy) values(?, ?, ?, ?, ?, ?, ?, ?)",
      orderPod.getId(), productCode, this.productCategory, this.productCategoryDisplayOrder, productDisplayOrder, 100,
      1, 1);
    queryExecutor.executeUpdate(
      "INSERT INTO pod_line_items (podId, productCode, productCategory, productCategoryDisplayOrder, productDisplayOrder, quantityReceived, createdBy, modifiedBy) values(?, ?, ?, ?, ?, ?, ?, ?)",
      orderPod.getId(), productCode1, productCategory, productCategoryDisplayOrder1, productDisplayOrder1, 100, 1, 1);
    queryExecutor.executeUpdate(
      "INSERT INTO pod_line_items (podId, productCode, productCategory, productCategoryDisplayOrder, productDisplayOrder, quantityReceived, createdBy, modifiedBy) values(?, ?, ?, ?, ?, ?, ?, ?)",
      orderPod.getId(), productCode3, productCategory, productCategoryDisplayOrder1, productDisplayOrder, 100, 1, 1);
    queryExecutor.executeUpdate(
      "INSERT INTO pod_line_items (podId, productCode, productCategory, productCategoryDisplayOrder, productDisplayOrder, quantityReceived, createdBy, modifiedBy) values(?, ?, ?, ?, ?, ?, ?, ?)",
      orderPod.getId(), productCode2, productCategory, productCategoryDisplayOrder1, productDisplayOrder1, 100, 1, 1);

    List<OrderPODLineItem> orderPodLineItems = podMapper.getPODLineItemsByPODId(orderPod.getId());

    assertThat(orderPodLineItems.size(), is(4));
    assertThat(orderPodLineItems.get(0).getProductCode(), is(productCode1));
    assertThat(orderPodLineItems.get(0).getProductCategoryDisplayOrder(), is(productCategoryDisplayOrder1));
    assertThat(orderPodLineItems.get(0).getProductDisplayOrder(), is(productDisplayOrder1));
    assertThat(orderPodLineItems.get(1).getProductCode(), is(productCode2));
    assertThat(orderPodLineItems.get(1).getProductCategoryDisplayOrder(), is(productCategoryDisplayOrder1));
    assertThat(orderPodLineItems.get(1).getProductDisplayOrder(), is(productDisplayOrder1));
    assertThat(orderPodLineItems.get(2).getProductCode(), is(productCode3));
    assertThat(orderPodLineItems.get(2).getProductCategoryDisplayOrder(), is(productCategoryDisplayOrder1));
    assertThat(orderPodLineItems.get(2).getProductDisplayOrder(), is(productDisplayOrder));
    assertThat(orderPodLineItems.get(3).getProductCode(), is(productCode));
    assertThat(orderPodLineItems.get(3).getProductCategoryDisplayOrder(), is(productCategoryDisplayOrder));
  }

  @Test
  public void shouldGetPODByOrderId() throws SQLException {
    OrderPOD orderPod = new OrderPOD();
    orderPod.setOrderId(order.getId());
    queryExecutor.executeUpdate("INSERT INTO pod(orderId) values(?)", order.getId());

    OrderPOD savedOrderPOD = podMapper.getPODByOrderId(order.getId());
    assertThat(savedOrderPOD, is(notNullValue()));
    assertThat(savedOrderPOD.getOrderId(), is(order.getId()));
  }

  @Test
  public void shouldGetNPreviousPODLineItemsAfterGivenTrackingDateForGivenProgramPeriodAndProduct() {
    OrderPOD orderPod = new OrderPOD();
    orderPod.setOrderId(order.getId());
    orderPod.fillPOD(order.getRnr());
    Rnr requisition = order.getRnr();
    podMapper.insertPOD(orderPod);
    OrderPODLineItem orderPodLineItem = new OrderPODLineItem(orderPod.getId(), productCode, 100);
    podMapper.insertPODLineItem(orderPodLineItem);

    List<OrderPODLineItem> nOrderPodLineItems = podMapper.getNPodLineItems(productCode, requisition, 1,
      DateTime.now().minusDays(5).toDate());

    assertThat(nOrderPodLineItems, hasItems(orderPodLineItem));
  }

  @Test
  public void shouldGetPODWithLineItemsByPODId() throws Exception {
    OrderPOD expectedOrderPod = new OrderPOD();
    expectedOrderPod.setOrderId(order.getId());
    podMapper.insertPOD(expectedOrderPod);

    OrderPODLineItem lineItem1 = new OrderPODLineItem(expectedOrderPod.getId(), productCode, productCategory,
      productCategoryDisplayOrder, productDisplayOrder, 100, productName, dispensingUnit, 10, null, true, null);
    podMapper.insertPODLineItem(lineItem1);

    OrderPOD orderPOD = podMapper.getPODById(expectedOrderPod.getId());

    assertThat(orderPOD.getPodLineItems().size(), is(1));
    assertThat(orderPOD.getPodLineItems().get(0), is(lineItem1));
  }
}
