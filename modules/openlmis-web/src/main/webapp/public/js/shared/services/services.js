var services = angular.module('openlmis.services', ['ngResource']);

services.factory('User', function ($resource) {
    return $resource('/user.json', {}, {});
});

services.factory('Program', function ($resource) {
    return $resource('/admin/programs.json', {}, {});
});

services.factory('RnRColumnList', function ($resource) {
    return $resource('/admin/rnr/:programCode/columns.json', {}, {});
});

services.factory('ProgramRnRColumnList', function ($resource) {
    return $resource('/logistics/rnr/:programCode/columns.json', {}, {});
});

services.factory('Facility', function ($resource) {
    return $resource('/admin/facility/:id.json', {}, {});
});

services.factory('UserFacilityList', function ($resource) {
    return $resource('/logistics/user/facilities.json', {}, {});
});

//todo add right/operation code as param
services.factory('UserSupportedProgramInFacilityForAnOperation', function ($resource) {
    return $resource('/logistics/facility/:facilityId/user/programs.json', {}, {});
});

services.factory('RequisitionHeader', function ($resource) {
    return $resource('/logistics/facility/:facilityId/requisition-header.json', {}, {});
});

services.factory('FacilityReferenceData', function ($resource) {
    return $resource('/admin/facility/reference-data.json', {}, {});
});

services.factory('AllFacilities', function ($resource) {
    return $resource('/admin/facilities.json', {}, {});
});

services.factory('UserRights', function ($resource) {
    return $resource('/user/rights.json', {}, {});
});