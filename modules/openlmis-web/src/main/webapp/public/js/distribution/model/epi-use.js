function EpiUse(epiUse) {
  var DATE_REGEXP = /^(0[1-9]|1[012])[/]((2)\d\d\d)$/;
  var fieldList = ['stockAtFirstOfMonth', 'received', 'distributed', 'loss', 'stockAtEndOfMonth', 'expirationDate'];

  function init() {
    $.extend(true, this, epiUse);
    $(this.productGroups).each(function (i, group) {
      group.reading = group.reading || {};
      $(fieldList).each(function (i, fieldName) {
        group.reading[fieldName] = group.reading[fieldName] || {};
      });
    });
  }

  init.call(this);

  EpiUse.prototype.setNotRecorded = function () {
    $(this.productGroups).each(function (i, group) {
      $.each(group.reading, function (key) {
        group.reading[key].notRecorded = true;
      });
    });
  };

  EpiUse.prototype.computeStatus = function () {
    var _this = this;
    var complete = 'is-complete';
    var incomplete = 'is-incomplete';
    var empty = 'is-empty';

    var statusClass = complete;

    function isEmpty(field, obj) {
      if (isUndefined(obj[field])) {
        return true;
      }
      return (isUndefined(obj[field].value) && !obj[field].notRecorded);
    }

    function isValid(field, obj) {
      return field != 'expirationDate' || (obj[field].notRecorded || DATE_REGEXP.test(obj[field].value));
    }

    statusClass = empty;

    $(_this.productGroups).each(function (i, productGroup) {
      $(fieldList).each(function (i, fieldName) {
        if (productGroup.reading && !isEmpty(fieldName, productGroup.reading) && isValid(fieldName, productGroup.reading) && statusClass != incomplete) {
          statusClass = complete;
        } else if (productGroup.reading && (isEmpty(fieldName, productGroup.reading) || !isValid(fieldName, productGroup.reading)) && statusClass == complete) {
          statusClass = incomplete;
          return false;
        }
        return true;
      })
    });

    _this.status = statusClass;

    return statusClass;
  }
}
