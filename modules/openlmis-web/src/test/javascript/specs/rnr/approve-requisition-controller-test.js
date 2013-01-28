describe('Approve Requisition controller', function () {

  var scope, ctrl, httpBackend, location, routeParams, requisitionHeader, controller, requisition,
    programRnRColumnList, lineItems, columnDefinitions;

  beforeEach(module('openlmis.services'));

  beforeEach(inject(function ($httpBackend, $rootScope, $location, $controller) {
    scope = $rootScope.$new();
    location = $location;
    controller = $controller;
    httpBackend = $httpBackend;
    routeParams = {"rnr":"1", "facility":"1", "program":"1"};
    lineItems = [];
    requisition = {'status':"AUTHORIZED", 'lineItems':lineItems};
    programRnRColumnList = [
      {'name':'ProductCode', 'label':'Product Code'},
      {'name':'quantityApproved', 'label':'quantity approved'},
      {'name':'remarks', 'label':'remarks'}
    ];
    ctrl = controller(ApproveRnrController, {$scope:scope, requisition:requisition, programRnRColumnList:programRnRColumnList, $location:location, $routeParams:routeParams});
  }));

  it('should set rnr in scope', function () {
    expect(scope.requisition).toEqual(requisition);
  });

  it('should set line-items in scope', function () {
    expect(scope.lineItems).toEqual(lineItems);
  });

  it('should set columns list in scope', function () {
    expect(scope.programRnRColumnList).toEqual(programRnRColumnList);
  });

  it('should set line items as  data in grid options', function () {
    expect(scope.gridOptions.data).toEqual('lineItems');
  });

  it('should save work in progress for rnr', function () {
    scope.requisition = {"id":"rnrId"};
    httpBackend.expect('PUT', '/requisitions/rnrId/save.json').respond({'success':"R&R saved successfully!"});
    scope.saveRnr();
    httpBackend.flush();
    expect(scope.message).toEqual("R&R saved successfully!");
  });

  it('should not save work in progress if any line item has invalid approved quantity', function () {
    var lineItems = [
      {'quantityApproved':'aaas'}
    ];
    scope.lineItems = lineItems;
    scope.requisition = {"id":"rnrId", 'lineItems':lineItems};
    scope.saveRnr();
    expect(scope.error).toEqual("Please correct errors before saving.");
  });

  it('should not approve if any line item has empty approved quantity', function () {
    var lineItems = [
      {'quantityApproved':''}
    ];
    scope.lineItems = lineItems;
    scope.requisition = {"id":"rnrId", 'lineItems':lineItems};
    scope.approveRnr();
    expect(scope.error).toEqual("Please complete the highlighted fields on the R&R form before approving");
    lineItems = [
      {'quantityApproved':null}
    ];
    scope.approveRnr();
    expect(scope.error).toEqual("Please complete the highlighted fields on the R&R form before approving");

  });

  it('should approve a valid rnr', function () {
    var lineItems = [
      {'quantityApproved':123}
    ];
    scope.lineItems = lineItems;
    scope.requisition = {"id":"rnrId", 'lineItems':lineItems};
    httpBackend.expect('PUT', '/requisitions/rnrId/approve.json').respond({'success':"R&R approved successfully!"});

    scope.approveRnr();
    httpBackend.flush();

    expect(scope.message).toEqual("R&R approved successfully!");
  });

})
;
