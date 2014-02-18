/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 *  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *  You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org.
 */

package org.openlmis.distribution.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.openlmis.core.domain.Facility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

@Data
@NoArgsConstructor
public class VaccinationChildCoverage {

  private List<ChildCoverageLineItem> childCoverageLineItems = new ArrayList<>();
  private List<OpenedVialLineItem> openedVialLineItems = new ArrayList<>();

  public VaccinationChildCoverage(FacilityVisit facilityVisit, Facility facility,
                                  List<TargetGroupProduct> targetGroupProducts, List<ProductVial> productVials) {

    List<String> validVaccinations = Collections.unmodifiableList(
      asList("BCG", "Polio (Newborn)", "Polio 1st dose", "Polio 2nd dose",
        "Polio 3rd dose", "Penta 1st dose", "Penta 2nd dose", "Penta 3rd dose",
        "PCV10 1st dose", "PCV10 2nd dose", "PCV10 3rd dose", "Measles"));

    List<String> validProductVials = Collections.unmodifiableList(asList(
      "BCG", "Polio10", "Polio20",
      "Penta1", "Penta10", "PCV", "Measles"));

    createChildCoverageLineItems(facilityVisit, facility, targetGroupProducts, validVaccinations);

    createOpenedVialLineItems(facilityVisit, facility, productVials, validProductVials);
  }

  private void createOpenedVialLineItems(FacilityVisit facilityVisit,
                                         Facility facility, List<ProductVial> productVials,
                                         List<String> validProductVials) {

    ProductVial productVial;

    for (final String productVialName : validProductVials) {
      productVial = (ProductVial) CollectionUtils.find(productVials, new Predicate() {
        @Override
        public boolean evaluate(Object o) {
          return ((ProductVial) o).getVial().equalsIgnoreCase(productVialName);
        }
      });
      this.openedVialLineItems.add(new OpenedVialLineItem(facilityVisit, facility, productVial, productVialName));
    }
  }

  private void createChildCoverageLineItems(FacilityVisit facilityVisit,
                                            Facility facility,
                                            List<TargetGroupProduct> targetGroupProducts,
                                            List<String> validVaccinations) {
    TargetGroupProduct targetGroupProduct;

    for (final String vaccination : validVaccinations) {
      targetGroupProduct = (TargetGroupProduct) CollectionUtils.find(targetGroupProducts, new Predicate() {
        @Override
        public boolean evaluate(Object o) {
          return ((TargetGroupProduct) o).getTargetGroupEntity().equalsIgnoreCase(vaccination);
        }
      });
      this.childCoverageLineItems.add(new ChildCoverageLineItem(facilityVisit, facility, targetGroupProduct, vaccination));
    }
  }

  public VaccinationChildCoverage(List<ChildCoverageLineItem> childCoverageLineItems, List<OpenedVialLineItem> openedVialLineItems) {
    this.childCoverageLineItems = childCoverageLineItems;
    this.openedVialLineItems = openedVialLineItems;
  }
}
