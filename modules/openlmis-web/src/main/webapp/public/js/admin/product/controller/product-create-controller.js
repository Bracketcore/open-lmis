function ProductCreateController($scope, $location, $dialog, messageService, CreateProduct, ProductGroups, ProductCategories, ReportPrograms, DosageUnits, ProductForms) {

    $scope.productsBackupMap = [];
    $scope.newProduct = {};
    $scope.products = {};
    $scope.editProduct = {};
    $scope.product={};
    $scope.creationError = '';
    $scope.title = 'Products';
    $scope.AddEditMode = '';
    $scope.programProductsCost = [];
    $scope.AddEditMode = true;

    // clear the parent confirmation message if there was any
    $scope.$parent.message = '';

    // Programs list
    ReportPrograms.get(function (data) {
        var tmp = data.programs;

        $scope.product.programProducts = [];
        $scope.programs = data.programs;

        for(var i = 0; i <= data.programs.length; i++){
            var program = data.programs[i];
            $scope.product.programProducts.push({program:program , currentPrice: 0, dosesPerMonth:1, active:true, programName:program.name });
        }
        // default the program to be active & not a tracer drug.
        $scope.product.active = true;
        $scope.product.tracer = false;
        $scope.apply();
    });

    // create product
    $scope.createProduct = function () {
        $scope.error = "";

        if ($scope.createProductForm.$invalid) {

            $scope.showErrorForCreate = true;
            return;
        }
        $scope.showErrorForCreate = false;
        CreateProduct.save( $scope.product, function (data) {
               $scope.$parent.message = 'New product created successfully';
               $location.path('');
                $scope.newProduct = {};
            },  function (data) {
            $scope.message = "";
            $scope.creationError = data.data.error;
        });
    };

//  switch to new mode
    $scope.startAddNewProduct = function () {
        if ($scope.AddEditMode) return false;
        $scope.title='Add Product';
        $scope.AddEditMode = true;
        $scope.$parent.newProductMode = true;
        $scope.$parent.formActive = "product-form-active";
    };

    //  backup record
    $scope.completeAddNewProduct = function (product) {
        $scope.productsBackupMap[product.id] = $scope.getBackupProduct(product);
        $scope.$parent.newProductMode = false;
        $scope.showErrorForCreate = false;
        $scope.AddEditMode = false;
        $('html, body').animate({ scrollTop: 0 }, 'fast');
        $scope.title='Products';
    };

// cancel record
    $scope.cancelAddNewProduct = function (product) {
        $scope.$parent.newProductMode = false;
        $scope.AddEditMode = false;
        $scope.showErrorForCreate = false;
        $location.path('');
    };



    // drop down lists
    ProductCategories.get(function (data) {
        $scope.productCategories = data.productCategoryList;
    });


  // drop down lists
    DosageUnits.get(function (data) {
        $scope.dosageUnits = data.dosageUnits;
    });

   // load the product form dropdown list
    ProductForms.get(function (data) {
        $scope.productForms = data.productForms;
    });

    // load the product group dropdown list
    ProductGroups.get(function (data){
       $scope.productGroups = data.productGroups;
    });

    $scope.YesNo = function (tf) {
        var retval = '';
        if (tf === true) {
            retval = 'Yes';
        } else
        {
            retval = 'No';
        }
        return retval;
    };

}
