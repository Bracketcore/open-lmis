/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

//  Description:
//  Freezing the top header of R&R products table upon scroll

app.directive('fixedTableHeader', function () {
  return {
    restrict: 'EA',
    link: function (scope, element) {
      var fixedHeader = $("<div class='header-fixed'></div>");
      var previousWidth = 0, previousHeight = 0;
      fixedHeader.hide();

      var cloneAndAppendTableHeader = function () {
        var table = $("<table class='table table-bordered'></table>");
        table.append(element.find('thead').clone());
        fixedHeader.append(table);
        element.parent().append(fixedHeader);
      };

      setTimeout(function () {
        cloneAndAppendTableHeader();
        element.parent().scroll(function () {
          fixedHeader.scrollLeft(element.parent().scrollLeft());
        });
      });

      function setWidthAndHeightFromParent() {
        var parentWidth = element.parent().width();
        var parentHeight = element.find('thead').height();

        if (previousWidth != parentWidth) {
          fixedHeader.width(parentWidth);
          previousWidth = parentWidth;
        }

        if (previousHeight != parentHeight) {
          fixedHeader.find('thead tr').height(parentHeight);
          previousHeight = parentHeight;
        }
      }

      angular.element('.rnr-body').scroll(function () {
        fixedHeader.hide();
        if (element.offset().top < 0 && element.is(':visible')) {
          setWidthAndHeightFromParent();
          fixedHeader.show();
          fixedHeader.scrollLeft(element.parent().scrollLeft());
        }
      });

      $(window).on('resize', function () {
        setWidthAndHeightFromParent();
      });
    }
  };
});
