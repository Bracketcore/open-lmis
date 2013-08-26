/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

function ProductController($scope, $location, $dialog, messageService, ProductDetail , ReportPrograms, ProductList, RemoveProduct, RestoreProduct, DosageUnits, ProductForms) {


    $scope.newProduct = {};
    $scope.products = {};
    $scope.editProduct = {};
    $scope.product={};
    $scope.creationError = '';
    $scope.title = 'Products';
    $scope.demoproducts = {};
    $scope.AddEditMode = '';
    $scope.programProductsCost = [];
    $scope.title = "Manage Products";

    // all products list
    ProductList.get({}, function (data) {
        $scope.productsList = data.productList;
        $scope.filteredProducts = $scope.productsList;

        $scope.initialProducts = angular.copy(data.productList, $scope.initialProducts);
        $scope.products = $scope.productsList;

        //alert(JSON.stringify($scope.filteredProducts, null, 4));
        for (var productIndex in data.productList) {
            var product = data.productList[productIndex];
        }

        //alert(JSON.stringify($scope.products, null, 4));
    }, function (data) {
        $location.path($scope.$parent.sourceUrl);

    });




    // apply filter
    $scope.filterProductsByProgram = function () {
        query = $scope.program;
        $scope.filteredProducts = [];
        query = query || "";
        if (query == 'NaN' || query == '') {
            $scope.filteredProducts = $scope.productsList;
            return;
        }
        $scope.query = query;
        angular.forEach($scope.productsList, function (product) {
             $scope.product = product;
            console.log('Query is ' + $scope.query);
            console.log(' Product is ' + $scope.product.primaryname);
             angular.forEach($scope.product.programs, function (pp){
                 console.log('  Program id is ' + pp.id + ' ' + pp.name);
                 if(pp.id == $scope.query){
                        $scope.filteredProducts.push($scope.product);
                 }
            });
        });

        $scope.resultCount = $scope.filteredProducts.length;
    };

//  scope is undefined,
    $scope.productLoaded = function () {
        return !($scope.products == undefined || $scope.products == null);
    };

    // Programs list
    ReportPrograms.get(function (data) {
        var tmp = data.programs;
        $scope.programs = data.programs;
    })

    $scope.YesNo = function (tf) {
        var retval = '';
        if (tf == true) {
            retval = 'Yes';
        } else
        {
            retval = 'No';
        }
        return retval;
    };


};
