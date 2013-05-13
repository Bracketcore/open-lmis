/**
 * User: Elias
 * Date: 5/4/13
 * Time: 2:33 PM
 */

//var services = angular.module('openlmis.services', ['ngResource']);

services.factory('MailingLabels', function ($resource) {
    return $resource('/reports/reportdata/mailingLabels.json', {}, {});
});

services.factory('ConsumptionReport', function ($resource) {
    return $resource('/reports/reportdata/consumption.json', {}, {});
});

services.factory('AverageConsumptionReport', function ($resource) {
    return $resource('/reports/reportdata/averageConsumption.json', {}, {});
});

services.factory('Products', function($resource){
    return $resource('/reports/products.json', {}, {});
}) ;

services.factory('ProductCategories', function($resource){
    return $resource('/reports/productCategories.json', {}, {});
}) ;

services.factory('SummaryReport', function($resource){
    return $resource('/reports/summary.json', {}, {});
}) ;

services.factory('NonReportingFacilities', function($resource){
    return $resource('/reports/non_reporting.json', {}, {});
});

services.factory('RequisitionGroups', function($resource){
    return $resource('/reports/rgroups.json', {}, {});
});

services.factory('RequisitionGroupsByProgramSchedule', function($resource){
    return $resource('/reports/reporting_groups_by_program_schedule.json', {}, {});
});

services.factory('AdjustmentSummaryReport', function ($resource) {
    return $resource('/reports/reportdata/adjustmentSummary.json', {}, {});
});