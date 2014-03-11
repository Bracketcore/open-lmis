/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

function StockImbalanceController($scope, $filter, ngTableParams,
                                  StockImbalanceReport,RequisitionGroupsByProgramSchedule,AllFacilites,GetFacilityByFacilityType,AllReportPeriods,ReportPeriodsByScheduleAndYear, Products, ProductCategories, ProductsByCategory, ReportFacilityTypes, RequisitionGroups,ReportSchedules,ReportPrograms,ReportPeriods, OperationYears, SettingsByKey,localStorageService, $http, $routeParams, $location) {
    $scope.filterObject = {};
    $scope.showMessage = true;
    $scope.message = "Indicates a required field." ;

    $scope.defaultFlag = true;

    $scope.IndicatorProductsKey = "INDICATOR_PRODUCTS";

    SettingsByKey.get({key: $scope.IndicatorProductsKey},function (data){
         $scope.IndicatorProductsDescription = data.settings.value;
    });


    AllReportPeriods.get(function (data) {
        $scope.periods = data.periods;
        $scope.periods.unshift({'name':'-- Select a Period --'});
    });




    $scope.filterGrid = function () {
        $scope.getPagedDataAsync();
    };

    $scope.startYears = [];

    OperationYears.get(function (data) {
        $scope.startYears = data.years;
        $scope.startYears.unshift('-- All Years --');
    });


    ReportPrograms.get(function(data){
        $scope.programs = data.programs;
        $scope.programs.unshift({'name':'-- Select a Programs --'});
    });

    ReportSchedules.get(function(data){
        $scope.schedules = data.schedules;
        $scope.schedules.unshift({'name':'-- Select a Schedule --', 'id':'0'}) ;

        });

    RequisitionGroups.get(function (data) {
        $scope.requisitionGroups = data.requisitionGroupList;
        $scope.requisitionGroups.unshift({'name':'-- All Requisition Groups --','id':'0'});
    });
   /*
   AllFacilites.get(function(data){
        $scope.allFacilities = data.allFacilities;
        $scope.allFacilities.unshift({'name':'-- All Facilities --','id':'0'});
    }); */

    ReportFacilityTypes.get(function (data) {
        $scope.facilityTypes = data.facilityTypes;
        $scope.facilityTypes.unshift({'name':'-- All Facility Types --','id':'0'});

        $scope.allFacilities = [];
        $scope.allFacilities.unshift({code:'-- Select a Facility --',id:'0'});
    });

    Products.get(function (data) {
        $scope.products = data.productList;
        $scope.products.unshift({'name': '-- All Products --', 'id':'All'});
        var ind_prod = $scope.IndicatorProductsDescription;
        $scope.products.unshift({'name': '-- '.concat(ind_prod).concat(' --'), 'id':'-1'});

    });

    ProductCategories.get(function (data) {
        $scope.productCategories = data.productCategoryList;
        $scope.productCategories.unshift({'name':'-- All Product Categories --','id':'0'});
    });

    $scope.ChangeSchedule = function(scheduleBy){
        if(scheduleBy == 'byYear'){
            ReportPeriodsByScheduleAndYear.get({scheduleId: $scope.filterObject.scheduleId, year: $scope.filterObject.year}, function(data){
                $scope.periods = data.periods;
                $scope.periods.unshift({'name':'-- Select a Period --','id':'0'});
            });

        }else{

            ReportPeriods.get({ scheduleId : $scope.filterObject.scheduleId },function(data) {
                $scope.periods = data.periods;
                $scope.periods.unshift({'name':'-- Select a Period --','id':'0'});

            });

        }

        RequisitionGroupsByProgramSchedule.get({program: $scope.filterObject.programId, schedule:$scope.filterObject.scheduleId}, function(data){
            $scope.requisitionGroups = data.requisitionGroupList;
            $scope.requisitionGroups.unshift({'name':'-- All Requisition Groups --','id':'0'});
        });
        $scope.loadFacilities();
    };

    $scope.$watch('stockImbalance.facilityTypeId', function (selection) {
        if (selection == "All") {
            $scope.filterObject.facilityTypeId = -1;
        } else if (selection !== undefined || selection === "") {
            $scope.filterObject.facilityTypeId = selection;
            $.each($scope.facilityTypes, function (item, idx) {
                if (idx.id == selection) {
                    $scope.filterObject.facilityType = idx.name;
                }
            });
        } else {
            $scope.filterObject.facilityTypeId = 0;
        }
        $scope.ChangeFacility();
        $scope.filterGrid();

    });


    $scope.$watch('stockImbalance.facilityId', function (selection) {
        if (selection == "All") {
            $scope.filterObject.facilityId = -1;
        } else if (selection !== undefined || selection === "") {
            $scope.filterObject.facilityId = selection;
            $.each($scope.allFacilities, function (item, idx) {
                if (idx.id == selection) {
                    $scope.filterObject.facility = idx.name;
                }
            });

        } else {
            $scope.filterObject.facilityId = 0;
            $scope.filterObject.facility = "";
        }
        $scope.filterGrid();

    });

    $scope.ChangeFacility = function(){

        if(isUndefined($scope.filterObject.facilityType) || isUndefined($scope.filterObject.program)){
            return;
        }

        GetFacilityByFacilityType.get({ facilityTypeId : $scope.filterObject.facilityTypeId },function(data) {

            $scope.allFacilities =  data.facilities;
            $scope.allFacilities.unshift({code:'-- Select a Facility --',id:'0'});
        });
    };

    $scope.$watch('stockImbalance.productCategoryId', function (selection) {
        if (selection == "All") {
            $scope.filterObject.productCategoryId = -1;
        } else if (selection !== undefined || selection === "") {
            $scope.filterObject.productCategoryId = selection;
            $.each($scope.productCategories, function(item, idx){
                if(idx.id == selection){
                    $scope.filterObject.productCategory = idx.name;
                }
            });
        } else {
            $scope.filterObject.productCategoryId = 0;
        }
        $scope.ChangeProductList();
        $scope.filterGrid();
    });

    $scope.ChangeProductList = function () {
        ProductsByCategory.get({category: $scope.filterObject.productCategoryId}, function (data) {
            $scope.products = data.productList;
            $scope.products.unshift({'name': '-- All Products --','id':'All'});
            var ind_prod = $scope.IndicatorProductsDescription;
            $scope.products.unshift({'name': '-- '.concat(ind_prod).concat(' --'), 'id':'-1'});
        });
    };

    $scope.$watch('stockImbalance.productId', function (selection) {
        if (selection == "All") {
            $scope.filterObject.productId = 0;
        } else if (selection !== undefined || selection === "") {
            $scope.filterObject.productId = selection;
            $.each($scope.products, function(item, idx){
                if(idx.id == selection){
                    $scope.filterObject.product = idx.name;
                }
            });

        } else {
            $scope.filterObject.productId = -1;
        }
        $scope.filterGrid();
    });

    $scope.$watch('stockImbalance.rgroupId', function (selection) {
        if (selection == "All") {
            $scope.filterObject.rgroupId = -1;
        } else if (selection !== undefined || selection === "") {
            $scope.filterObject.rgroupId = selection;
            $.each($scope.requisitionGroups, function (item, idx) {
                if (idx.id == selection) {
                    $scope.filterObject.rgroup = idx.name;
                }
            });
        } else {
            $scope.filterObject.rgroupId = 0;
        }
        $scope.filterGrid();
    });

    $scope.$watch('stockImbalance.programId', function (selection) {
        if (selection == "All") {
            $scope.filterObject.programId = -1;
        } else if (selection !== undefined || selection === "") {
            $scope.filterObject.programId = selection;
            $.each($scope.programs, function (item, idx) {
                if (idx.id == selection) {
                    $scope.filterObject.program = idx.name;
                }
            });

        } else {
            $scope.filterObject.programId = 0;
        }
        $scope.filterGrid();
    });

    $scope.$watch('stockImbalance.periodId', function (selection) {
        if (selection == "All") {
            $scope.filterObject.periodId = -1;
        } else if (selection !== undefined || selection === "") {
            $scope.filterObject.periodId = selection;
            $.each($scope.periods, function (item, idx) {
                if (idx.id == selection) {
                    $scope.filterObject.period = idx.name;
                }
            });

        } else {
            $scope.filterObject.periodId = 0;
        }
        $scope.filterGrid();
    });

    $scope.$watch('stockImbalance.scheduleId', function (selection) {
        if (selection == "All") {
            $scope.filterObject.scheduleId = -1;
        } else if (selection !== undefined || selection === "") {
            $scope.filterObject.scheduleId = selection;
            $.each($scope.schedules , function (item, idx) {
                if (idx.id == selection) {
                    $scope.filterObject.schedule = idx.name;
                }
            });

        } else {
            $scope.filterObject.scheduleId = 0;
        }
        $scope.ChangeSchedule('');

    });

    $scope.$watch('stockImbalance.year', function (selection) {

        if (selection == "-- All Years --") {
            $scope.filterObject.year = -1;
        } else if (selection !== undefined || selection === "") {
            $scope.filterObject.year = selection;

        } else {
            $scope.filterObject.year = 0;
        }

        if($scope.filterObject.year === -1 || $scope.filterObject.year === 0){

            $scope.ChangeSchedule('bySchedule');
        }else{

            $scope.ChangeSchedule('byYear');
        }
    });


    $scope.exportReport = function (type) {
        $scope.filterObject.pdformat = 1;
        var params = jQuery.param($scope.filterObject);
        var url = '/reports/download/stock_imbalance/' + type + '?' + params;

        localStorageService.remove(localStorageKeys.REPORTS.STOCK_IMBALANCE);
        localStorageService.add(localStorageKeys.REPORTS.STOCK_IMBALANCE, JSON.stringify($scope.filterObject));
        window.open(url);

    };

    // the grid options
    $scope.tableParams = new ngTableParams({
        page: 1,            // show first page
        total: 0,           // length of data
        count: 25           // count per page
    });

    $scope.paramsChanged = function(params) {

        // slice array data on pages
        if($scope.data === undefined ){
            $scope.datarows = [];
            params.total = 0;
        }else{
            var data = $scope.data;
            var orderedData = params.filter ? $filter('filter')(data, params.filter) : data;
            orderedData = params.sorting ?  $filter('orderBy')(orderedData, params.orderBy()) : data;

            params.total = orderedData.length;
            $scope.datarows = orderedData.slice( (params.page - 1) * params.count,  params.page * params.count );
            var i = 0;
            var baseIndex = params.count * (params.page - 1) + 1;
            while(i < $scope.datarows.length){
                $scope.datarows[i].no = baseIndex + i;
                i++;
            }
        }
    };

    // watch for changes of parameters
    $scope.$watch('tableParams', $scope.paramsChanged , true);

    $scope.getPagedDataAsync = function () {
        if(isUndefined($scope.filterObject.periodId) || isUndefined($scope.filterObject.programId)){
            return;
        }

        $scope.filterObject.max = 10000;
        $scope.filterObject.page = 1;

        StockImbalanceReport.get($scope.filterObject, function (data) {
            $scope.data = data.pages.rows;
            $scope.paramsChanged($scope.tableParams);
        });

    };

    $scope.formatNumber = function (value, format) {
        return utils.formatNumber(value, format);
    };

}
