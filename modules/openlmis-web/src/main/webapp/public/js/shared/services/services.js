var services = angular.module('openlmis.services', ['ngResource']);

services.factory('ActivePrograms', function ($resource) {
    return $resource('/active/programs.json', {}, {});
});

services.factory('Programs', function ($resource) {
    return $resource('/programs.json', {}, {});
});

services.factory('FacilityList', function ($resource) {
    return $resource('/reports/reportdata/facilities.json', {}, {});
});

services.factory('RnRColumnList', function ($resource) {
    return $resource('/program/:programId/rnr-template.json', {}, {});
});

services.factory('ProgramRnRColumnList', function ($resource) {
    return $resource('/rnr/:programId/columns.json', {}, {});
});

services.factory('Facilities', function ($resource) {
    return $resource('/facilities.json', {}, {});
});

services.factory('Facility', function ($resource) {
    return $resource('/facilities/:id.json', {}, {update: {method: 'PUT'}});
});

services.factory('UserContext', function ($resource) {
    return $resource('/user-context.json', {}, {});
});

services.factory('Users', function ($resource) {
  return $resource('/users.json', {}, {});
});

services.factory('User', function($resource) {
  return $resource('/users/:id.json', {}, {update: {method:'PUT'}});
});


services.factory('UserById', function ($resource) {
  return $resource('/admin/user/:id.json', {}, {});
});

services.factory('UserFacilityList', function ($resource) {
    return $resource('/logistics/user/facilities.json', {}, {});
});

services.factory('UserFacilityWithViewRequisition', function ($resource) {
    return $resource('/user/facilities/view.json', {}, {});
});

//todo add right/operation code as param
services.factory('UserSupportedProgramInFacilityForAnOperation', function ($resource) {
    return $resource('/facility/:facilityId/user/programs.json', {}, {});
});

services.factory('RequisitionHeader', function ($resource) {
    return $resource('/logistics/facility/:facilityId/requisition-header.json', {}, {});
});

services.factory('ProgramSupportedByFacility', function($resource) {
  return $resource('/facilities/:facilityId/programs.json', {}, {});
});

services.factory('FacilityReferenceData', function ($resource) {
    return $resource('/facilities/reference-data.json', {}, {});
});

services.factory('AllFacilities', function ($resource) {
    return $resource('/facilities.json', {}, {});
});

services.factory('Rights', function ($resource) {
    return $resource('/rights.json', {}, {});
});

services.factory('Role', function ($resource) {
    return $resource('/roles/:id.json', {}, {update: {method:'PUT'}});
});

services.factory('Roles', function ($resource) {
    return $resource('/roles.json', {}, {});
});

services.factory('UserSupervisedProgramList', function ($resource) {
    return $resource('/create/requisition/supervised/programs.json', {}, {})
});

services.factory('UserSupervisedFacilitiesForProgram', function ($resource) {
    return $resource('/create/requisition/supervised/:programId/facilities.json', {}, {})
});

services.factory('ReferenceData', function ($resource) {
    return $resource('/reference-data/currency.json', {}, {});
});

services.factory('Requisitions', function($resource) {
    return $resource('/requisitions/:id/:operation.json', {}, {update : {method:'PUT'}});
});

services.factory('Requisition', function($resource) {
  return $resource('/requisitions.json', {}, {});
});

services.factory('RequisitionById', function ($resource) {
  return $resource('/requisitions/:id.json', {}, {});
});

services.factory('RequisitionForApproval', function($resource) {
  return $resource('/requisitions-for-approval.json', {}, {});
});

services.factory('RequisitionsForViewing', function($resource) {
  return $resource('/requisitions-list.json', {}, {});
});

services.factory('RequisitionForConvertToOrder', function($resource) {
  return $resource('/requisitions-for-convert-to-order.json', {}, {});
});

services.factory('RequisitionForApprovalById', function($resource) {
  return $resource('/requisitions-for-approval/:id.json', {}, {});
});


services.factory('LossesAndAdjustmentsReferenceData', function($resource) {
  return $resource('/requisitions/lossAndAdjustments/reference-data.json', {}, {})
});

services.factory('Schedules', function ($resource) {
    return $resource('/schedules.json', {});
});

services.factory('Schedule', function ($resource) {
  return $resource('/schedules/:id.json', {}, {update: {method:'PUT'}});
});

services.factory('Periods', function ($resource) {
  return $resource('/schedules/:scheduleId/periods.json', {}, {});
});

services.factory('PeriodsForFacilityAndProgram', function ($resource) {
  return $resource('/logistics/facility/:facilityId/program/:programId/periods.json', {}, {});
});

services.factory('Period', function ($resource) {
  return $resource('/periods/:id.json', {}, {});
});

services.factory('SupportedUploads', function ($resource) {
  return $resource('/supported-uploads.json', {}, {});
});

services.factory('ForgotPassword', function ($resource) {
  return $resource('/forgot-password.json', {}, {});
});

services.factory('FacilityApprovedProducts', function ($resource) {
  return $resource('/facilityApprovedProducts/facility/:facilityId/program/:programId/nonFullSupply.json', {}, {});
});

services.factory('RequisitionLineItem', function ($resource) {
  return $resource('/logistics/requisition/lineItem.json', {}, {});
});

services.factory('UpdateUserPassword', function ($resource) {
  return $resource('/user/resetPassword/:token.json', {}, {update: {method:'PUT'}});
});

services.factory('ValidatePasswordToken', function ($resource) {
  return $resource('/user/validatePasswordResetToken/:token.json', {}, {});
});

services.factory('Messages', function ($resource) {
  return $resource('/messages.json', {}, {});
});

services.factory('SupervisoryNodes', function ($resource) {
  return $resource('/supervisory-nodes.json', {}, {});
});
services.factory('RequisitionOrder', function ($resource) {
  return $resource('/requisitionOrder.json', {}, {});
});

services.factory('FacilityProgramRights', function($resource){
  return $resource('/facility/:facilityId/program/:programId/rights.json');
});

