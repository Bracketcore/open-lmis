function RegimenSummaryControllers($scope, $filter, ngTableParams,
                                 RegimenSummaryReport,GeographicZones,ReportRegimens,ReportRegimenCategories,ReportRegimensByCategory,RequisitionGroupsByProgramSchedule,FacilitiesByProgramParams,ReportPeriodsByScheduleAndYear,  RequisitionGroups,ReportSchedules,ReportPrograms,ReportPeriods, OperationYears, SettingsByKey,localStorageService, $http, $routeParams, $location) {
    //to minimize and maximize the filter section
    var section = 1;
    $scope.showMessage = true;
    $scope.message = "Indicates a required field.";

    $scope.defaultFlag = true;
    // $scope.reporting = "quarterly";
    $scope.IndicatorProductsKey = "INDICATOR_PRODUCTS";

    SettingsByKey.get({key: $scope.IndicatorProductsKey},function (data){
        $scope.IndicatorProductsDescription = data.settings.value;
    });

    $scope.section = function (id) {
        section = id;
    };


    $scope.show = function (id) {
        return section == id;
    };



    $scope.filterGrid = function () {
        $scope.getPagedDataAsync(0, 0);
    };

    $scope.years = [];
    OperationYears.get(function (data) {
        $scope.years = data.years;
        $scope.years.unshift('-- All Years --');
    });




    ReportPrograms.get(function(data){

        $scope.programs = data.programs;
        $scope.programs.unshift({'name':'-- Select a Program --','id':'0'});
    });

    ReportRegimenCategories.get(function(data){

        $scope.regimenCategories = data.regimenCategories;
        $scope.regimenCategories.unshift({'name':'-- Select Regimen Category --','id':'0'});
    });

  $scope.RegimenCategoryChanged= function(){

      ReportRegimensByCategory.get({regimenCategoryId: $scope.filterObject.regimenCategoryId}, function(data){
          $scope.regimens = data.regimens;
          $scope.regimens.unshift({'name':'All Regimens'});
      });

  };
    GeographicZones.get(function(data) {
        $scope.zones = data.zones;
        $scope.zones.unshift({'name': '-- All Geographic Zones --'});
    });
    ReportRegimens.get(function(data){
    $scope.regimens = data.regimens;
    $scope.regimens.unshift({'name': '-- All Regimens --'});

    });
    ReportSchedules.get(function(data){
        $scope.schedules = data.schedules;
        $scope.schedules.unshift({'name':'-- Select a Schedule --', 'id':'0'}) ;
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
    };

    $scope.$watch('filterObject.regimenCategoryId', function (selection) {
        if (selection === "All") {
            $scope.filterObject.regimenCategoryId = 0;
        } else if (selection !== undefined || selection === "") {
            $scope.filterObject.regimenCategoryId = selection;
            $.each($scope.regimenCategories, function (item, idx) {
                if (idx.id == selection) {
                    $scope.filterObject.regimenCategory = idx.name;
                }
            });

        } else {
            $scope.filterObject.regimenCategoryId = -1;
            $scope.filterObject.regimenCategory="";

        }
        $scope.filterGrid();

    });

    $scope.$watch('zone.value', function(selection){
        if(selection !== undefined || selection === ""){
            $scope.filterObject.zoneId =  selection;
            $.each( $scope.zones,function( item,idx){
                if(idx.id == selection){
                    $scope.filterObject.zone = idx.name;
                }
            });
        }else{
            $scope.filterObject.zoneId = 0;
            $scope.filterObject.zone = "";
        }
        $scope.filterGrid();
    });

    $scope.$watch('regimen.value', function(selection){
        if (selection == "All") {
            $scope.filterObject.regimenId = " ";
        }
        if(selection !== undefined || selection === ""){
            $scope.filterObject.regimenId =  selection;
            $.each( $scope.regimens,function( item,idx){
                if(idx.id == selection){
                    $scope.filterObject.regimen = idx.name;
                }
            });
        }else{
            $scope.filterObject.regimenId = " ";
            $scope.filterObject.regimen = "";
        }
        $scope.filterGrid();
    });



    $scope.$watch('filterObject.rgroupId', function (selection) {
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

    $scope.$watch('filterObject.programId', function (selection) {
        if (selection == "All") {
            $scope.filterObject.programId = 0;
        } else if (selection !== undefined || selection === "") {
            $scope.filterObject.programId = selection;
            $.each($scope.programs, function (item, idx) {
                if (idx.id == selection) {
                    $scope.filterObject.program = idx.name;
                }
            });

        } else {
            $scope.filterObject.programId = 0;
            $scope.filterObject.program = "";
        }
        $scope.filterGrid();
    });

    $scope.$watch('filterObject.periodId', function (selection) {
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

    $scope.$watch('filterObject.scheduleId', function (selection) {
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
    $scope.$watch('filterObject.year', function (selection) {

        if (selection == "-- All Years --") {
            $scope.filterObject.year = -1;
        } else if (selection !== undefined || selection === "") {
            $scope.filterObject.year = selection;

        } else {
            $scope.filterObject.year = 0;
        }

        if($scope.filterObject.year == -1 || $scope.filterObject.year === 0){

            $scope.ChangeSchedule('bySchedule');
        }else{

            $scope.ChangeSchedule('byYear');
        }
    });


    $scope.exportReport = function (type) {

        $scope.filterObject.pdformat = 1;
        var params = jQuery.param($scope.filterObject);
        var url = '/reports/download/regimen_summary/' + type + '?' + params;


        window.open(url);

    };

    //filter form data section
    $scope.filterObject = {
        periodId : "",
        period : "",
        programId: "",
        program: "",
        scheduleId: "",
        schedule: "",
        regimenCategoryId: "",
        regimenCategory : "",

        year : "",
        zoneId : $scope.zone,
        zone:"" ,
        regimenId: $scope.regimen,
        regimen:""
    };

    //filter form data section
    $scope.filterOptions = {
        period: $scope.filterObject.periodId,
        filterText: "",
        useExternalFilter: false
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

    $scope.getPagedDataAsync = function (pageSize, page) {

        pageSize = 10000;
        page = 1;
        var params = {};
        //alert('xxx');
        if (pageSize !== undefined && page !== undefined) {
            params = {
                "max": pageSize,
                "page": page
            };
        }

        $.each($scope.filterObject, function (index, value) {
            //if(value != undefined)
            params[index] = value;
        });

        RegimenSummaryReport.get(params, function (data) {
            $scope.data = data.pages.rows;
            $scope.paramsChanged($scope.tableParams);
        });
    };

    $scope.formatNumber = function (value, format) {
        return utils.formatNumber(value, format);
    };


}
