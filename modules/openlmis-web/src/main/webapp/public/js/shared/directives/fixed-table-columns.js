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
//  Freezing the first two columns of R&R products table upon scroll left

app.directive('fixedTableColumns', function () {
  return {
    restrict: 'A',
    link: function (scope, element, attr, ctrl) {

      var fixedColumns = $("<div class='column-fixed'></div>");
      var fixedTable = $("<table class='table table-bordered'></table>");
      var fixedTableHead = $("<thead><tr></tr></thead>");

      var setFixedTableColumnsOffset = function () {
        fixedColumns.css({
          top: eval(element.offset().top - $(window).scrollTop() - 1) + 'px',
          left: eval(element.offset().left + element.parent().scrollLeft()) + 'px'
        });
      };


      setTimeout(function () {
        if ($('#' + $(element).attr('id')).is(':visible')) {
          cloneAndAppendFixedTableColumns();
          setFixedTableColumnsOffset();
          setColumnWidthAndHeight();
        }
      });

      var setColumnWidthAndHeight = function () {
        $('.column-fixed th').each(function (index, tableHeaderElement) {
          copyWidthAndHeight($(element.find('th')[index]), $(tableHeaderElement));
        });
      }

      scope.$watch('pageLineItems', function () {
        setTimeout(function () {
          if ($('#' + $(element).attr('id')).is(':visible')) {
            cloneAndAppendFixedTableColumns();
            setColumnWidthAndHeight();
          }
          $(window).trigger('scroll');
        });
      });

      $(window).on('scroll resize', setFixedTableColumnsOffset);

      var cloneAndAppendFixedTableColumns = function () {
        fixedTableHead.html('');
        fixedTableHead.append($(element.find('thead th')[0]).clone());
        fixedTableHead.append($(element.find('thead th')[1]).clone());
        fixedTable.html('');
        fixedTable.append(fixedTableHead);

        cloneAndAppendTableBody();

        fixedColumns.html('');
        fixedColumns.append(fixedTable);
        element.parent().append(fixedColumns);
      }

      var cloneAndAppendTableBody = function () {
        var tableBodyList = element.find('tbody');
        tableBodyList.each(function (index, body) {
          var fixedTableBody = $('<tbody></tbody>');
          var tableRows = $(body).find('tr');
          tableRows.each(function (index, row) {
            if (index == 0) {
              var fixedTableRows = $(row).clone();
              fixedTableRows.find('td').attr('colspan', '2');
            } else {
              var firstTableCell = $($(row).find('td')[0]).clone();
              copyWidthAndHeight($($(row).find('td')[0]), firstTableCell);

              var secondTableCell = $($(row).find('td')[1]).clone();
              copyWidthAndHeight($($(row).find('td')[1]), secondTableCell);

              var fixedTableRows = $("<tr></tr>").append(firstTableCell).append(secondTableCell);
            }
            $(fixedTableBody).append(fixedTableRows);
          });

          fixedTable.append($(fixedTableBody));
        });
      }

    }
  };
});

var copyWidthAndHeight = function (fromElement, toElement) {
  toElement.css({
    width: parseInt(fromElement.width()) + 'px',
    height: parseInt(fromElement.height()) + 'px'
  })
}

