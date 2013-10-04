function SummaryReportController($scope,$filter ,ngTableParams , SummaryReport, ReportSchedules, ReportPrograms , ReportPeriods , Products ,ReportFacilityTypes,GeographicZones, RequisitionGroups, AllFacilites , $http, $routeParams,$location) {
        //to minimize and maximize the filter section



        $scope.tableParams = new ngTableParams({
            page: 1,            // show first page
            total: 0,           // length of data
            count: 25           // count per page
        });

        $scope.paramsChanged = function(params) {
            // slice array data on pages
            if($scope.data == undefined || $scope.data.pages == undefined || $scope.data.pages.rows == undefined){
                $scope.datarows = [];
                params.total = 0;
            }else{
                var data = $scope.data.pages.rows;
                var orderedData = params.filter ? $filter('filter')(data, params.filter) : data;
                orderedData = params.sorting ?  $filter('orderBy')(orderedData, params.orderBy()) : data;

                params.total = orderedData.length;
                $scope.datarows = orderedData.slice( (params.page - 1) * params.count,  params.page * params.count );
            }
        };

        // watch for changes of parameters
        $scope.$watch('tableParams', $scope.paramsChanged , true);

        // lookups and references
        $scope.pagingOptions = {
            pageSizes: [5, 10, 20, 40, 50, 100],
            pageSize: 20,
            totalServerItems: 0,
            currentPage: 1
        };

        // initialize the defaults for each of the modules
        $scope.filterGrid = function (){
           $scope.getPagedDataAsync($scope.pagingOptions.pageSize, $scope.pagingOptions.currentPage);
        };


        //filter form data section
        $scope.filterObject =  {
             facilityTypeId : $scope.facilityType,
             facilityType : "",
             programId : $scope.program,
             periodId : $scope.period,
             zoneId : $scope.zone,
             productId : $scope.productId,
             scheduleId : $scope.schedule,
             rgroupId : $scope.rgroup,
             rgroup : "",
             facilityName : $scope.facilityNameFilter
        };

        ReportPrograms.get(function(data){
            $scope.programs = data.programs;
            $scope.programs.unshift({'name':'Select a Program'});
        });

        RequisitionGroups.get(function(data){
            $scope.requisitionGroups = data.requisitionGroupList;
            $scope.requisitionGroups.unshift({'name':'All Reporting Groups'});
        });

        ReportFacilityTypes.get(function(data) {
            $scope.facilityTypes = data.facilityTypes;
            $scope.facilityTypes.unshift({'name': 'All Facility Types'});
        });

        ReportSchedules.get(function(data){
        $scope.schedules = data.schedules;
        $scope.schedules.unshift({'name':'Select a Schedule'});
    });

        Products.get(function(data){
            $scope.products = data.productList;
            $scope.products.unshift({'name': 'All Products'});
        });

        AllFacilites.get(function(data){
            $scope.facilities = data.allFacilities;
            $scope.facilities.unshift({name:'All facilities'})
        });

        $scope.ChangeSchedule = function(){
            if($scope.schedule == undefined || $scope.schedule == ""){
                return;
            }

            ReportPeriods.get({ scheduleId : $scope.schedule },function(data) {
                $scope.periods = data.periods;
                $scope.periods.unshift({'name': 'Select Period'});
            });
        }

        GeographicZones.get(function(data) {
            $scope.zones = data.zones;
            $scope.zones.unshift({'name': '- All Zones -'});
        });

        $scope.$watch('facilityType', function(selection){
            if(selection == "All"){
                $scope.filterObject.facilityTypeId =  -1;
            }else if(selection != undefined || selection == ""){
                $scope.filterObject.facilityTypeId =  selection;
                $.each( $scope.facilityTypes,function( item,idx){
                    if(idx.id == selection){
                        $scope.filterObject.facilityType = idx.name;
                    }
                });
            }else{
                $scope.filterObject.facilityTypeId =  0;
            }
            $scope.filterGrid();
        });

        $scope.$watch('facilityNameFilter', function(selection){
            if(selection != undefined || selection == ""){
                $scope.filterObject.facilityName =  selection;

            }else{
                $scope.filterObject.facilityName = "";
            }
            $scope.filterGrid();
        });

        $scope.$watch('product', function(selection){
            if(selection == ""){
                $scope.filterObject.productId =  -1;
            }else if(selection != undefined || selection == ""){
                $scope.filterObject.productId =  selection;
            }else{
                $scope.filterObject.productId =  0;
            }
            $scope.filterGrid();
        });

        $scope.$watch('rgroup', function(selection){
            if(selection == ""){
                $scope.filterObject.rgroupId =  -1;
            }else if(selection != undefined || selection == ""){
                $scope.filterObject.rgroupId =  selection;
                $.each( $scope.requisitionGroups,function( item,idx){
                    if(idx.id == selection){
                        $scope.filterObject.rgroup = idx.name;
                    }
                });
            }else{
                $scope.filterObject.rgroupId =  0;
            }
            $scope.filterGrid();
        });

        $scope.$watch('period', function(selection){
            if(selection == "All"){
                $scope.filterObject.periodId =  -1;
            }else if(selection != undefined || selection == ""){
                $scope.filterObject.periodId =  selection;
            }else{
                $scope.filterObject.periodId =  0;
            }
            $scope.filterGrid();
        });

        $scope.$watch('program', function(selection){
            if(selection == ""){
                $scope.filterObject.programId =  -1;
            }else if(selection != undefined || selection == ""){
                $scope.filterObject.programId =  selection;
            }else{
                $scope.filterObject.programId =  0;
            }
            $scope.filterGrid();
        });

        $scope.$watch('schedule', function(selection){
             if(selection != undefined || selection == ""){
                $scope.filterObject.scheduleId =  selection;
            }else{
                $scope.filterObject.scheduleId =  0;
            }
            $scope.filterGrid();
        });

        $scope.$watch('zone', function(selection){
            if(selection == "All"){
                $scope.filterObject.zoneId =  -1;
            }else if(selection != undefined || selection == ""){
                $scope.filterObject.zoneId =  selection;
            }else{
                $scope.filterObject.zoneId =  0;
            }
            $scope.filterGrid();
        });

        $scope.$watch('facility', function(selection){
            $scope.filterGrid();
        });


        $scope.currentPage = ($routeParams.page) ? parseInt($routeParams.page) || 1 : 1;

        $scope.exportReport   = function (type){
            $scope.filterObject.pdformat =1;
            var params = jQuery.param($scope.filterObject);
            var url = '/reports/download/summary/' + type +'?' + params;
            window.open(url);

        }

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
                        if($scope.period == undefined){
                            return;
                        }
                        $scope.data = [];
                        var params  = {};
                        if(pageSize != undefined && page != undefined ){
                            var params =  {
                                "max" : 10000,
                                "page" : page
                            };
                        }

                        $.each($scope.filterObject, function(index, value) {
                            //if(value != undefined)
                                params[index] = value;
                        });

                        params['facilityId'] = $scope.facility;

                        // put out the sort order
                        $.each($scope.sortInfo.fields, function(index, value) {
                            if(value != undefined) {
                                params['sort-' + $scope.sortInfo.fields[index]] = $scope.sortInfo.directions[index];
                            }
                        });

                        SummaryReport.get(params, function(data) {
                           $scope.setPagingData(data.pages.rows,page,pageSize,data.pages.total);
                           $scope.data = data;
                           $scope.paramsChanged($scope.tableParams);
                        });

        };





}
