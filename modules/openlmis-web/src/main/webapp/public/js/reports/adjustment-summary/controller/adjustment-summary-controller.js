function AdjustmentSummaryReportController($scope, AdjustmentSummaryReport, Products , ReportPrograms, ProductCategories, RequisitionGroups , ReportFacilityTypes, GeographicZones, AdjustmentTypes,OperationYears,Months, $http, $routeParams,$location) {

        //to minimize and maximize the filter section
        var section = 1;

        $scope.section = function (id) {
            section = id;
        };

        $scope.show = function (id) {
            return section == id;
        };

        // lookups and references

        $scope.pagingOptions = {
            pageSizes: [5, 10, 20, 40, 50, 100],
            pageSize: 20,
            totalServerItems: 0,
            currentPage: 1
        };

        // default to the monthly period type
        $scope.periodType = 'monthly';

        $scope.periodTypes = [
            {'name':'Monthly', 'value':'monthly'},
            {'name':'Quarterly', 'value':'quarterly'},
            {'name':'Semi Anual', 'value':'semi-anual'},
            {'name':'Annual', 'value':'annual'}
       ];
        $scope.startYears = [];
        OperationYears.get(function(data){
            $scope.startYears = $scope.endYears = data.years;
            adjustEndYears();
        });

        Months.get(function(data){
            var months = data.months;

            if(months != null){
                $scope.startMonths = [];
                $scope.endMonths = [];
                $.each(months,function(idx,obj){
                    $scope.startMonths.push({'name':obj.toString(), 'value': idx+1});
                    $scope.endMonths.push({'name':obj.toString(), 'value': idx+1});
                });
            }

        });

        $scope.startQuarters = function(){
            return $scope.quarters;
        };

        $scope.endQuarters  = function(){
            if($scope.startYear == $scope.endYear && $scope.startQuarter != '' ){
                var arr = [];
                for(var i=$scope.startQuarter - 1; i < $scope.quarters.length;i++){
                    arr.push($scope.quarters[i]);
                }
                return arr;
            }
            return $scope.quarters;
        };

        $scope.quarters         = [
            {'name':'One','value':'1'},
            {'name':'Two','value':'2'},
            {'name':'Three','value':'3'},
            {'name':'Four','value':'4'}
        ];

        $scope.semiAnnuals= [
            {'name':'First Half','value':'1'},
            {'name':'Second Half','value':'2'}
        ];

        $scope.product;

        RequisitionGroups.get(function(data){
            $scope.requisitionGroups = data.requisitionGroupList;
            $scope.requisitionGroups.unshift({'name':'All Reporting Groups'});
        });


        // copy over the start month and end months
        // this is just for initial loading.
        $(function (){
            $scope.startQuarters  = $scope.quarters;
            $scope.endQuarters  = $scope.quarters;
            $scope.endYears     = $scope.startYears;
            $scope.startSemiAnnuals = $scope.semiAnnuals;
            $scope.endSemiAnnuals = $scope.semiAnnuals;
            $scope.toQuarter = 1;
            $scope.fromQuarter = 1;
            $scope.startHalf = 1;
            $scope.endHalf = 1;
        });


        $scope.isMonthly = function(){
            return $scope.periodType == 'monthly';
        };

        $scope.isQuarterly = function(){
            return $scope.periodType == 'quarterly';
        };

        $scope.isSemiAnnualy  = function(){
            return $scope.periodType == 'semi-anual';
        };
        $scope.filterGrid = function (){

            $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage);
            //$(".ngFooterPanel").css("margin-left",$(".span3").width() + ($(".span3").width()/3)) ;
        };

        //filter form data section
        $scope.filterOptions = {
            filterText: "",
            useExternalFilter: false
        };



        //filter form data section
        $scope.filterObject =  {
             facilityTypeId : $scope.facilityType,
             zoneId : $scope.zone,
             periodType: $scope.periodType,
             fromYear: $scope.fromYear,
             fromMonth: $scope.fromMonth,
             fromQuarter: $scope.fromQuarter,
             fromSemiAnnual:$scope.startHalf,
             toYear: $scope.toYear,
             toMonth: $scope.toMonth,
             toQuarter: $scope.toQuarter,
             toSemiAnnual:$scope.endHalf,
             productId: $scope.product,
             productCategoryId : $scope.productCategory,
             rgroupId : $scope.rgroup,
             programId : $scope.program,
             facility : $scope.facilityId,
             facilityType : "",
             rgroup : "",
             pdformat : 0,
             adjustmentTypeId : $scope.adjustmentType,
             adjustmentType : "",
             pdformat : 0
        };

        ReportFacilityTypes.get(function(data) {
            $scope.facilityTypes = data.facilityTypes;
            $scope.facilityTypes.unshift({'name': 'All Facility Types'});
        });

        AdjustmentTypes.get(function(data){
            $scope.adjustmentTypes = data.adjustmentTypeList;
            $scope.adjustmentTypes.unshift({'description': 'All Adjustment Types'});
         });

        Products.get(function(data){
            $scope.products = data.productList;
            $scope.products.unshift({'name': 'All Products'});
        });

        ProductCategories.get(function(data){
            $scope.productCategories = data.productCategoryList;
            $scope.productCategories.unshift({'name': 'All Product Categories'});
        });


        $scope.facilities         = [
            {'name':'One','value':'1'},
            {'name':'Two','value':'2'},
            {'name':'Three','value':'3'},
            {'name':'Four','value':'4'}
        ];

        GeographicZones.get(function(data) {
            $scope.zones = data.zones;
            $scope.zones.unshift({'name': 'All Geographic Zones'});
        });

        ReportPrograms.get(function(data){
            $scope.programs = data.programs;
            $scope.programs.unshift({'name':'Select Programs'});
        });

        $scope.currentPage = ($routeParams.page) ? parseInt($routeParams.page) || 1 : 1;

        $scope.$watch('zone.value', function(selection){
            if(selection != undefined || selection == ""){
               $scope.filterObject.zoneId =  selection;
               //$scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage);
            }else{
                $scope.filterObject.zoneId = 0;
            }
            $scope.filterGrid();
        });

        $scope.$watch('status.value', function(selection){
            if(selection != undefined || selection == ""){
                $scope.filterObject.statusId =  selection;
                //$scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage);
            }else{
                $scope.filterObject.statusId ='';
            }
            $scope.filterGrid();
        });
        $scope.$watch('facilityType.value', function(selection){
            if(selection != undefined || selection == ""){
                $scope.filterObject.facilityTypeId =  selection;
                $.each( $scope.facilityTypes,function( item,idx){
                    if(idx.id == selection){
                        $scope.filterObject.facilityType = idx.name;
                    }
                });
                //skillsSelect.options[skillsSelect.selectedIndex].text
                //$scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage);
            }else{
                $scope.filterObject.facilityTypeId =  0;
                $scope.filterObject.facilityType = "";
            }
            $scope.filterGrid();
        });

        $scope.$watch('startYear', function(selection){
            var date = new Date();
        if(selection != undefined || selection == ""){
            $scope.filterObject.fromYear =  selection;
            adjustEndYears();
            adjustEndMonths();
            adjustEndQuarters();
            adjustEndSemiAnnuals();
        }else{
            $scope.startYear  = date.getFullYear().toString();
            $scope.filterObject.fromYear =  date.getFullYear();
        }
            $scope.filterGrid();
        });

        $scope.$watch('endYear', function(selection){
            var date = new Date();
            if(selection != undefined || selection == ""){
                $scope.filterObject.toYear =  selection;
                adjustEndMonths();
                adjustEndQuarters();
                adjustEndSemiAnnuals();
            }else{
                $scope.endYear  = date.getFullYear().toString();
                $scope.filterObject.toYear =  date.getFullYear();
            }
            $scope.filterGrid();
        });

    $scope.$watch('startQuarter', function(selection){
        var date = new Date();
        if(selection != undefined || selection == ""){
            $scope.filterObject.fromQuarter =  selection;
            adjustEndQuarters();
        }else{
            var date = new Date();
            $scope.filterObject.fromQuarter =  1;
        }
        $scope.filterGrid();
    });

    $scope.$watch('endQuarter', function(selection){
        var date = new Date();
        if(selection != undefined || selection == ""){
            $scope.filterObject.toQuarter =  selection;
        }else{
            var date = new Date();
            $scope.filterObject.toQuarter =  $scope.filterObject.fromQuarter;
        }
        $scope.filterGrid();
    });

    $scope.$watch('startHalf', function(selection){

        if(selection != undefined || selection == ""){
            $scope.filterObject.fromSemiAnnual =  selection;
            adjustEndSemiAnnuals();
        }else{
            $scope.filterObject.fromSemiAnnual =  1;
        }
        $scope.filterGrid();
    });
    $scope.$watch('endHalf', function(selection){

        if(selection != undefined || selection == ""){
            $scope.filterObject.toSemiAnnual =  selection;
        }else{
            var date = new Date();
            $scope.filterObject.toSemiAnnual =  1;
        }
        $scope.filterGrid();
    });
        $scope.$watch('startMonth', function(selection){
            var date = new Date();
            if(selection != undefined || selection == ""){
                $scope.filterObject.fromMonth =  selection-1;
                adjustEndMonths();
            }else{
                $scope.startMonth = (date.getMonth()+1 ).toString();
                $scope.filterObject.fromMonth =  (date.getMonth()+1);
            }
            $scope.filterGrid();
        });

        $scope.$watch('endMonth', function(selection){
            var date = new Date();
            if(selection != undefined || selection == ""){
                $scope.filterObject.toMonth =  selection-1;
            }else{
                $scope.endMonth = (date.getMonth() +1 ).toString();
                $scope.filterObject.toMonth =  (date.getMonth()+1);
            }
            $scope.filterGrid();
        });

    var adjustEndMonths = function(){
        if($scope.startMonth != undefined && $scope.startMonths != undefined && $scope.startYear == $scope.endYear ){
            $scope.endMonths = [];
            $.each($scope.startMonths,function(idx,obj){
                if(obj.value >= $scope.startMonth){
                    $scope.endMonths.push({'name':obj.name, 'value': obj.value});
                }
            });
            if($scope.endMonth < $scope.startMonth){
                $scope.endMonth = $scope.startMonth;
            }
        }else{
            $scope.endMonths = $scope.startMonths;
        }
    }

    var adjustEndQuarters = function(){
        if($scope.startYear == $scope.endYear){
            $scope.endQuarters = [];
            $.each($scope.startQuarters, function(idx,obj){
                if(obj.value >= $scope.startQuarter){
                    $scope.endQuarters.push({'name':obj.name, 'value': obj.value});
                }
            });
            if($scope.endQuarter < $scope.startQuarter){
                $scope.endQuarter =  $scope.startQuarter;
            }
        }else{
            $scope.endQuarters = $scope.startQuarters;
        }
    }

    var adjustEndSemiAnnuals = function(){

        if($scope.startYear == $scope.endYear){
            $scope.endSemiAnnuals = [];
            $.each($scope.startSemiAnnuals, function(idx,obj){
                if(obj.value >= $scope.startHalf){
                    $scope.endSemiAnnuals.push({'name':obj.name, 'value': obj.value});
                }
            });
            if($scope.endHalf < $scope.startHalf){
                $scope.endHalf =  $scope.startHalf;
            }
        }else{
            $scope.endSemiAnnuals = $scope.startSemiAnnuals;
        }
    }

    var adjustEndYears = function(){
        $scope.endYears = [];
        $.each( $scope.startYears,function( idx,obj){
            if(obj >= $scope.startYear){
                $scope.endYears.push(obj);
            }
        });
        if($scope.endYear <= $scope.startYear){
            $scope.endYear  = new Date().getFullYear();
        }
    }


    $scope.$watch('periodType', function(selection){
            if(selection != undefined || selection == ""){
                $scope.filterObject.periodType =  selection;

            }else{
                $scope.filterObject.periodType =  "monthly";
            }
        $scope.filterGrid();
        });

        $scope.$watch('productCategory', function(selection){
            if(selection != undefined || selection == ""){
                $scope.filterObject.productCategoryId =  selection;
            }else{
                $scope.filterObject.productCategoryId =  0;
            }
            $scope.filterGrid();
        });

        $scope.$watch('product', function(selection){
            if(selection == "All"){
                $scope.filterObject.productId =  -1;
            }else if(selection != undefined || selection == ""){
                $scope.filterObject.productId =  selection;
            }else{
                $scope.filterObject.productId =  0;
            }
            $scope.filterGrid();
        });


        $scope.$watch('rgroup', function(selection){
            if(selection != undefined || selection == ""){
                $scope.filterObject.rgroupId =  selection;
                $.each( $scope.requisitionGroups,function( item,idx){
                    if(idx.id == selection){
                        $scope.filterObject.rgroup = idx.name;
                    }
                });
            }else{
                $scope.filterObject.rgroupId =  0;
                $scope.filterObject.rgroup = "";
            }
            $scope.filterGrid();
        });

        $scope.$watch('program', function(selection){
            if(selection == "All"){
                $scope.filterObject.programId =  -1;
            }else if(selection != undefined || selection == ""){
                $scope.filterObject.programId =  selection;
            }else{
                $scope.filterObject.programId =  0;
            }
            $scope.filterGrid();
        });

    $scope.$watch('adjustmentType.value', function(selection){
        if(selection == "All"){
            $scope.filterObject.adjustmentTypeId =  -1;
            $scope.filterObject.adjustmentType = "All";

        }else if(selection != undefined || selection == ""){
                $scope.filterObject.adjustmentTypeId =  selection;
                $.each( $scope.adjustmentTypes,function( item,idx){
                    if(idx.name == selection){
                        $scope.filterObject.adjustmentType = idx.description;
                    }
                });

        }else{
            $scope.filterObject.adjustmentTypeId =  "";
            $scope.filterObject.adjustmentType = "";
        }
        $scope.filterGrid();
    });


        $scope.exportReport   = function (type){

            $scope.filterObject.pdformat =1;
            var params = jQuery.param($scope.filterObject);
            var url = '/reports/download/adjustment_summary/' + type +'?' + params;
            window.location.href = url;
        };

        $scope.goToPage = function (page, event) {
            angular.element(event.target).parents(".dropdown").click();
            $location.search('page', page);
        };

        $scope.$watch("currentPage", function () {  //good watch no problem

            if($scope.currentPage != undefined && $scope.currentPage != 1){
              //when clicked using the links they have done updated the paging info no problem here
               //or using the url page param
              //$scope.pagingOptions.currentPage = $scope.currentPage;
                $location.search("page", $scope.currentPage);
            }
        });

        $scope.$on('$routeUpdate', function () {
            if (!utils.isValidPage($routeParams.page, $scope.numberOfPages)) {
                $location.search('page', 1);
                return;
            }
        });


        $scope.sortInfo = { fields:["code","facilityType"], directions: ["ASC"]};

        $scope.setPagingData = function(data, page, pageSize, total){
            $scope.myData = data;
            $scope.pagingOptions.totalServerItems = total;
            $scope.numberOfPages = ( Math.ceil( total / pageSize))  ? Math.ceil( total / pageSize) : 1 ;

        };

        $scope.getPagedDataAsync = function (pageSize, page) {
                        var params  = {};
                        if($scope.program == null || $scope.program == undefined || $scope.program == ''){
                            // do not send a request to the server before the basic selection was done.
                            return;
                        }
                        if(pageSize != undefined && page != undefined ){
                                var params =  {
                                                "max" : pageSize,
                                                "page" : page
                                               };
                        }

                        $.each($scope.filterObject, function(index, value) {
                             if(value != undefined)
                                params[index] = value;
                        });

                        // Add the sorting parameters
                        $.each($scope.sortInfo.fields, function(index, value) {
                            if(value != undefined) {
                                params['sort-' + $scope.sortInfo.fields[index]] = $scope.sortInfo.directions[index];
                            }
                        });
                        // clear existing data
                        $scope.data = [];

                        // try to load the new data based on the selected parameters
                        AdjustmentSummaryReport.get(params, function(data) {
                            if(data.pages != undefined){
                                $scope.setPagingData(data.pages.rows,page,pageSize,data.pages.total);
                                $scope.data = data.pages.rows;
                            }
                        });

        };

        $scope.$watch('pagingOptions.currentPage', function () {
            $scope.currentPage = $scope.pagingOptions.currentPage;
            $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage);
        }, true);

        $scope.$watch('pagingOptions.pageSize', function () {
            $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage);
        }, true);

        $scope.$watch('sortInfo', function () {

            $.each($scope.sortInfo.fields, function(index, value) {
               // if(value != undefined)
                   // $scope.filterObject[$scope.sortInfo.fields[index]] = $scope.sortInfo.directions[index];
            });
           // $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage, $scope.filterOptions.filterText);

        }, true);

    $scope.gridOptions = {
        data: 'myData',
        columnDefs:
            [

                { field: 'facilityType', displayName: 'Facility Type', width : "*"},
                { field: 'facilityName', displayName: 'Facility', width : "**"},
                { field: 'supplyingFacility', displayName: 'Supplying Facility', width: "*" },
                { field: 'productDescription', displayName: 'Product Description', width: "**" },
                { field: 'adjustmentType', displayName: 'Adjustment Type', width : "*"},
                { field: 'adjustment', displayName: 'Adjustment', width : "*"}

            ],
        enablePaging: true,
        enableSorting :false,
        showFooter: true,
        selectWithCheckboxOnly :false,
        pagingOptions: $scope.pagingOptions,
        filterOptions: $scope.filterOptions,
        useExternalSorting: true,
        sortInfo: $scope.sortInfo,
        showColumnMenu: true,
        enableRowReordering: true,
        showFilter: true,
        plugins: [new ngGridFlexibleHeightPlugin()]

    };

}
