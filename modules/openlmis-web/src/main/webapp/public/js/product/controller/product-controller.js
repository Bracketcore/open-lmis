function ProductController($scope, productGroups, productForms, dosageUnits, programProductData, AddEditProgramProducts, $location) {
  $scope.productGroups = productGroups;
  $scope.productForms = productForms;
  $scope.dosageUnits = dosageUnits;

  if (!isUndefined(programProductData)) {
    if (!isUndefined(programProductData.programProduct)) {
      $scope.programProduct = programProductData.programProduct;
      var product = $scope.programProduct.product;
      $scope.selectedProductGroupCode = isUndefined(product.productGroup) ? undefined : product.productGroup.code;
      $scope.selectedProductFormCode = isUndefined(product.form) ? undefined : product.form.code;
      $scope.selectedProductDosageUnitCode = isUndefined(product.dosageUnit) ? undefined : product.dosageUnit.code;
    }
    else {
      $scope.programProduct = {};
    }
    $scope.productLastUpdated = programProductData.productLastUpdated;
  }

  var success = function (data) {
    $scope.error = "";
    $scope.$parent.message = data.success;
    $scope.$parent.productId = data.productId;
    $scope.showError = false;
    $location.path('');
  };

  var error = function (data) {
    $scope.$parent.message = "";
    $scope.error = data.data.error;
    $scope.showError = true;
  };

  var setProductReferenceData = function () {
    $scope.programProduct.product.productGroup = _.where($scope.productGroups, {code: $scope.selectedProductGroupCode})[0];
    $scope.programProduct.product.form = _.where($scope.productForms, {code: $scope.selectedProductFormCode})[0];
    $scope.programProduct.product.dosageUnit = _.where($scope.dosageUnits, {code: $scope.selectedProductDosageUnitCode})[0];
  };

  $scope.save = function () {
    if ($scope.productForm.$error.required) {
      $scope.showError = true;
      $scope.error = "form.error";
      return;
    }
    setProductReferenceData();

    if ($scope.programProduct.product.id) {
      AddEditProgramProducts.update({id: $scope.programProduct.product.id}, $scope.programProduct, success, error);
    }
    else {
      AddEditProgramProducts.save({}, $scope.programProduct, success, error);
    }
  };

  $scope.cancel = function () {
    $scope.$parent.productId = undefined;
    $scope.$parent.message = "";
    $location.path('#/search');
  };

}

ProductController.resolve = {
  productGroups: function ($q, $timeout, ProductGroups) {
    var deferred = $q.defer();

    $timeout(function () {
      ProductGroups.get({}, function (data) {
        deferred.resolve(data.productGroupList);
      }, {});
    }, 100);
    return deferred.promise;
  },

  productForms: function ($q, $timeout, ProductForms) {
    var deferred = $q.defer();

    $timeout(function () {
      ProductForms.get({}, function (data) {
        deferred.resolve(data.productFormList);
      }, {});
    }, 100);
    return deferred.promise;
  },

  dosageUnits: function ($q, $timeout, DosageUnits) {
    var deferred = $q.defer();

    $timeout(function () {
      DosageUnits.get({}, function (data) {
        deferred.resolve(data.dosageUnitList);
      }, {});
    }, 100);
    return deferred.promise;
  },

  programProductData: function ($q, $route, $timeout, AddEditProgramProducts) {
    if ($route.current.params.id === undefined) return undefined;

    var deferred = $q.defer();
    var productId = $route.current.params.id;

    $timeout(function () {
      AddEditProgramProducts.get({id: productId}, function (data) {
        deferred.resolve(data);
      }, {});
    }, 100);
    return deferred.promise;
  }
};