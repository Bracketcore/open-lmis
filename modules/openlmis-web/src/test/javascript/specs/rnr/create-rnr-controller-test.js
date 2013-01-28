describe('CreateRnrController', function () {

  var scope, ctrl, httpBackend, location, routeParams, requisitionHeader, controller, localStorageService;

  beforeEach(module('openlmis.services'));
  beforeEach(module('openlmis.localStorage'));

  describe("initialization", function () {

    beforeEach(inject(function ($httpBackend, $rootScope, $location, $controller) {
      controller = $controller;
      scope = $rootScope.$new();
      scope.$parent.facility = "10134";
      scope.$parent.program = {code:"programCode", "id":1};
      scope.saveRnrForm = {$error:{ rnrError:false }};
      routeParams = {"facility":"1", "program":"1", "period":2};
      httpBackend.expect('GET', '/requisitions/lossAndAdjustments/reference-data.json').respond({"lossAdjustmentTypes":{}});

    }));

    it('should set rnr in scope after successful initialization', function () {
      var mockedRequisition = {'status':"INITIATED"};
      httpBackend.when('GET', '/requisitions.json?facilityId=1&periodId=2&programId=1').respond({'rnr':mockedRequisition});

      controller(CreateRnrController, {$scope:scope, $routeParams:routeParams});

      httpBackend.flush();

      expect(scope.rnr).toEqual(mockedRequisition);
    });

    it('should calculated and set 2 decimal rounded cost in rnrLineItem', function () {
      var mockedRequisition = {'status':"INITIATED", 'lineItems':[
        {'id':456, 'product':'Name', 'lossesAndAdjustments':[], 'packsToShip':10.333, 'price':2 }
      ]};
      httpBackend.when('GET', '/requisitions.json?facilityId=1&periodId=2&programId=1').respond({'rnr':mockedRequisition});
      controller(CreateRnrController, {$scope:scope, $routeParams:routeParams});
      httpBackend.flush();

      expect(scope.rnrLineItems[0].id).toEqual(mockedRequisition.lineItems[0].id);
      expect(scope.rnrLineItems[0].cost).toEqual(20.67);
    });

    it('should initialize losses and adjustments, if not present in R&R', function () {
      var mockedRequisition = {'status':"INITIATED",
        'lineItems':[
          {'id':123, 'product':'Commodity Name' },
          {'id':456, 'product':'2nd Commodity', 'lossesAndAdjustments':[
            {'quantity':33}
          ] }
        ]
      };
      httpBackend.when('GET', '/requisitions.json?facilityId=1&periodId=2&programId=1').respond({'rnr':mockedRequisition});
      controller(CreateRnrController, {$scope:scope, $routeParams:routeParams});
      httpBackend.flush();

      expect(scope.rnrLineItems[0].lossesAndAdjustments).toEqual([]);
      expect(scope.rnrLineItems[1].lossesAndAdjustments).toEqual([
        {'quantity':33}
      ]);
    });
  });


  beforeEach(inject(function ($httpBackend, $rootScope, $location, $controller, $routeParams, _localStorageService_) {
    scope = $rootScope.$new();
    $rootScope.hasPermission = function () {
    };
    location = $location;
    controller = $controller;
    httpBackend = $httpBackend;
    scope.$parent.facility = "10134";
    scope.$parent.program = {code:"programCode", "id":1};
    scope.saveRnrForm = {$error:{ rnrError:false }};
    localStorageService = _localStorageService_;
    routeParams = {"facility":"1", "program":"1", "period":2};

    requisitionHeader = {"requisitionHeader":{"facilityName":"National Warehouse",
      "facilityCode":"10134", "facilityType":{"code":"Warehouse"}, "facilityOperatedBy":"MoH", "maximumStockLevel":3, "emergencyOrderPoint":0.5,
      "zone":{"label":"state", "value":"Arusha"}, "parentZone":{"label":"state", "value":"Arusha"}}};


    httpBackend.when('GET', '/logistics/facility/1/requisition-header.json').respond(requisitionHeader);
    httpBackend.when('GET', '/facilityApprovedProducts/facility/1/program/1/nonFullSupply.json').respond(200);
    httpBackend.when('POST', '/requisitions.json?facilityId=1&periodId=2&programId=1').respond({"rnr":{"status":"CREATED"}});
    httpBackend.when('GET', '/logistics/rnr/1/columns.json').respond({"rnrColumnList":[
      {"testField":"test"}
    ]});
    httpBackend.when('GET', '/reference-data/currency.json').respond({"currency":"$"});
    httpBackend.expect('GET', '/requisitions.json?facilityId=1&periodId=2&programId=1').respond({"rnr":{"status":"CREATED"}});
    httpBackend.expect('GET', '/requisitions/lossAndAdjustments/reference-data.json').respond({"lossAdjustmentTypes":{}});
    $rootScope.fixToolBar = function () {
    };
    ctrl = controller(CreateRnrController, {$scope:scope, $location:location, $routeParams:routeParams, localStorageService:localStorageService});

    scope.allTypes = [
      {"name":"some name"},
      {"name":"some other name"}
    ];
  }));

  it('should get list of Rnr Columns for program', function () {
    httpBackend.flush();
    expect([
      {"testField":"test"}
    ]).toEqual(scope.programRnRColumnList);
  });

  it('should save work in progress for rnr', function () {
    scope.rnr = {"id":"rnrId"};
    httpBackend.expect('PUT', '/requisitions/rnrId/save.json').respond({'success':"R&R saved successfully!"});
    scope.saveRnr();
    httpBackend.flush();
    expect(scope.message).toEqual("R&R saved successfully!");
  });

  it('should not save work in progress when invalid form', function () {
    scope.saveRnrForm.$error.rnrError = true;
    scope.saveRnr();
    expect(scope.error).toEqual("Please correct errors before saving.");
  });

  it('should get Currency from service', function () {
    httpBackend.flush();
    expect(scope.currency).toEqual("$");
  });

  it("should get undefined when the column name is quantityApproved and status is INITIATED", function () {
    scope.rnr = {"status":"INITIATED"};
    var isShown = scope.showSelectedColumn("quantityApproved");
    expect(isShown).toEqual(undefined);
  });


  it("should get 'defined' when the column name is not quantityApproved", function () {
    scope.rnr = {"status":"whatever"};
    var isShown = scope.showSelectedColumn("anyOtherColumn");
    expect(isShown).toEqual("defined");
  });

  it("should get 'defined' when the column name is quantityApproved and status is SUBMITTED", function () {
    scope.rnr = {"status":"SUBMITTED"};
    var isShown = scope.showSelectedColumn("quantityApproved");
    expect(isShown).toEqual(undefined);
  });

  it("should get 'undefined' when the column name is quantityApproved and status is APPROVED", function () {
    scope.rnr = {"status":"APPROVED"};
    var isShown = scope.showSelectedColumn("quantityApproved");
    expect(isShown).toEqual("defined");
  });

  it("should display modal window with appropriate type options to add losses and adjustments", function () {
    var lineItem = { "id":"1", lossesAndAdjustments:[
      {"type":{"name":"some name"}, "quantity":"4"}
    ]};
    scope.showLossesAndAdjustmentModalForLineItem(lineItem);
    expect(scope.lossesAndAdjustmentsModal[1]).toBeTruthy();
    expect(scope.lossesAndAdjustmentTypesToDisplay).toEqual([
      {"name":"some other name"}
    ]);
  });

  it('should not submit rnr with required fields missing', function () {
    scope.rnr = {"id":"rnrId"};
    scope.saveRnrForm = {$error:{required:true}};
    httpBackend.expect('PUT', '/requisitions/rnrId/save.json').respond(200);
    scope.submitRnr();
    httpBackend.flush();
    expect(scope.submitError).toEqual("Please complete the highlighted fields on the R&R form before submitting");
  });

  it('should not submit rnr with error in the form', function () {
    scope.rnr = {"id":"rnrId"};
    scope.saveRnrForm = {$error:{rnrError:true}};
    scope.submitRnr();
    expect(scope.submitError).toEqual("Please correct the errors on the R&R form before submitting");
  });

  it('should not submit rnr with formula validation error but should save', function () {
    var lineItem = { "beginningBalance":1, totalLossesAndAdjustments:1, quantityDispensed:1,
      quantityReceived:1, stockInHand:1};

    jQuery.extend(true, lineItem, new RnrLineItem());
    scope.rnrLineItems.push(lineItem);
    scope.rnr = {"id":1};
    scope.programRnRColumnList = [
      {"indicator":"A", "name":"beginningBalance", "source":{"name":"USER_INPUT"}, "formulaValidationRequired":true},
      {"indicator":"B", "name":"quantityReceived", "source":{"name":"USER_INPUT"}},
      {"indicator":"C", "name":"quantityDispensed", "source":{"name":"CALCULATED"}},
      {"indicator":"D", "name":"lossesAndAdjustments", "source":{"name":"USER_INPUT"}},
      {"indicator":"E", "name":"stockInHand", "source":{"name":"USER_INPUT"}}
    ];
    httpBackend.expect('PUT', '/requisitions/1/save.json').respond(200);
    scope.submitRnr();
    httpBackend.flush();
  });

  it('should submit valid rnr', function () {
    var lineItem = { "beginningBalance":1, totalLossesAndAdjustments:1, quantityDispensed:2,
      quantityReceived:1, stockInHand:1};
    jQuery.extend(true, lineItem, new RnrLineItem());
    scope.rnrLineItems.push(lineItem);

    scope.rnr = {"id":"rnrId", lineItems:[lineItem]};
    scope.programRnrColumnList = [
      {"indicator":"A", "name":"beginningBalance", "source":{"name":"USER_INPUT"}, "formulaValidationRequired":true},
      {"indicator":"B", "name":"quantityReceived", "source":{"name":"USER_INPUT"}},
      {"indicator":"C", "name":"quantityDispensed", "source":{"name":"CALCULATED"}},
      {"indicator":"D", "name":"lossesAndAdjustments", "source":{"name":"USER_INPUT"}},
      {"indicator":"E", "name":"stockInHand", "source":{"name":"USER_INPUT"}}
    ];
    httpBackend.expect('PUT', '/requisitions/rnrId/submit.json').respond(200, {success:"R&R submitted successfully!"});
    scope.submitRnr();
    httpBackend.flush();
    expect(scope.submitMessage).toEqual("R&R submitted successfully!");
    expect(scope.rnr.status).toEqual("SUBMITTED");
  });

  it('should return cell error class', function () {
    var lineItem = { "beginningBalance":1, totalLossesAndAdjustments:1, quantityDispensed:2,
      quantityReceived:1, stockInHand:1};
    jQuery.extend(true, lineItem, new RnrLineItem());

    var programRnrColumnList = [
      {"indicator":"A", "name":"beginningBalance", "source":{"name":"USER_INPUT"}, "formulaValidationRequired":true},
      {"indicator":"B", "name":"quantityReceived", "source":{"name":"USER_INPUT"}},
      {"indicator":"C", "name":"quantityDispensed", "source":{"name":"CALCULATED"}},
      {"indicator":"D", "name":"lossesAndAdjustments", "source":{"name":"USER_INPUT"}},
      {"indicator":"E", "name":"stockInHand", "source":{"name":"USER_INPUT"}}
    ];
    spyOn(lineItem, 'getErrorMessage').andReturn("error");
    var errorMsg = scope.getCellErrorClass(lineItem, programRnrColumnList);
    expect(errorMsg).toEqual("cell-error-highlight");
  });

  it('should not return cell error class', function () {
    var lineItem = { "beginningBalance":1, totalLossesAndAdjustments:1, quantityDispensed:2,
      quantityReceived:1, stockInHand:1};
    jQuery.extend(true, lineItem, new RnrLineItem());

    var programRnrColumnList = [
      {"indicator":"A", "name":"beginningBalance", "source":{"name":"USER_INPUT"}, "formulaValidationRequired":true},
      {"indicator":"B", "name":"quantityReceived", "source":{"name":"USER_INPUT"}},
      {"indicator":"C", "name":"quantityDispensed", "source":{"name":"CALCULATED"}},
      {"indicator":"D", "name":"lossesAndAdjustments", "source":{"name":"USER_INPUT"}},
      {"indicator":"E", "name":"stockInHand", "source":{"name":"USER_INPUT"}}
    ];
    spyOn(lineItem, 'getErrorMessage').andReturn("");
    var errorMsg = scope.getCellErrorClass(lineItem, programRnrColumnList);
    expect(errorMsg).toEqual("");
  });

  it('should return row error class', function () {
    var lineItem = { "beginningBalance":1, totalLossesAndAdjustments:1, quantityDispensed:2,
      quantityReceived:1, stockInHand:1};
    jQuery.extend(true, lineItem, new RnrLineItem());

    var programRnrColumnList = [
      {"indicator":"A", "name":"beginningBalance", "source":{"name":"USER_INPUT"}, "formulaValidationRequired":true},
      {"indicator":"B", "name":"quantityReceived", "source":{"name":"USER_INPUT"}},
      {"indicator":"C", "name":"quantityDispensed", "source":{"name":"CALCULATED"}},
      {"indicator":"D", "name":"lossesAndAdjustments", "source":{"name":"USER_INPUT"}},
      {"indicator":"E", "name":"stockInHand", "source":{"name":"USER_INPUT"}}
    ];
    spyOn(scope, 'getCellErrorClass').andReturn("error");
    var errorMsg = scope.getRowErrorClass(lineItem, programRnrColumnList);
    expect(errorMsg).toEqual("row-error-highlight");
  });


  it('should not return row error class', function () {
    var lineItem = { "beginningBalance":1, totalLossesAndAdjustments:1, quantityDispensed:2,
      quantityReceived:1, stockInHand:1};
    jQuery.extend(true, lineItem, new RnrLineItem());

    var programRnrColumnList = [
      {"indicator":"A", "name":"beginningBalance", "source":{"name":"USER_INPUT"}, "formulaValidationRequired":true},
      {"indicator":"B", "name":"quantityReceived", "source":{"name":"USER_INPUT"}},
      {"indicator":"C", "name":"quantityDispensed", "source":{"name":"CALCULATED"}},
      {"indicator":"D", "name":"lossesAndAdjustments", "source":{"name":"USER_INPUT"}},
      {"indicator":"E", "name":"stockInHand", "source":{"name":"USER_INPUT"}}
    ];
    spyOn(scope, 'getCellErrorClass').andReturn("");
    var errorMsg = scope.getRowErrorClass(lineItem, programRnrColumnList);
    expect(errorMsg).toEqual("");
  });

  it('should save Losses and Adjustments and close modal if valid', function () {
    var lineItem = { "id":"1", "beginningBalance":1, lossesAndAdjustments:[
      {"type":{"name":"some name"}, "quantity":"4"}
    ]};
    jQuery.extend(true, lineItem, new RnrLineItem());

    scope.rnrLineItems.push(lineItem);
    spyOn(lineItem, 'fill');

    scope.rnr = {"id":"rnrId", lineItems:[lineItem]};
    scope.programRnrColumnList = [
      {"indicator":"D", "name":"lossesAndAdjustments", "source":{"name":"USER_INPUT"}}
    ];

    scope.lossesAndAdjustmentsModal[1] = true;
    scope.saveLossesAndAdjustmentsForRnRLineItem(scope.rnrLineItems[0], scope.rnr, scope.programRnrColumnList);

    expect(lineItem.fill).toHaveBeenCalledWith(scope.rnr, scope.programRnrColumnList);
    expect(scope.lossesAndAdjustmentsModal[1]).toBeFalsy();
    expect(scope.modalError).toEqual('');
  });

  it('should not save Losses and Adjustments and close modal if not valid', function () {
    var lineItem = { "id":"1", "beginningBalance":1, lossesAndAdjustments:[
      {"type":{"name":"some name"}, "quantity":null}
    ]};
    jQuery.extend(true, lineItem, new RnrLineItem());

    scope.rnrLineItems.push(lineItem);
    spyOn(lineItem, 'fill');

    scope.rnr = {"id":"rnrId", lineItems:[lineItem]};
    scope.programRnrColumnList = [
      {"indicator":"D", "name":"lossesAndAdjustments", "source":{"name":"USER_INPUT"}}
    ];
    scope.lossesAndAdjustmentsModal[1] = true;
    scope.saveLossesAndAdjustmentsForRnRLineItem(scope.rnrLineItems[0], scope.rnr, scope.programRnRColumnList);

    expect(lineItem.fill).not.toHaveBeenCalledWith(scope.rnr, scope.programRnrColumnList);
    expect(scope.lossesAndAdjustmentsModal[1]).toBeTruthy();
    expect(scope.modalError).toEqual('Please correct the highlighted fields before submitting');
  });

  it('should highlight required field for modal dialog', function () {
    expect(scope.highlightRequiredFieldInModal(null)).toEqual("required-error");
    expect(scope.highlightRequiredFieldInModal(undefined)).toEqual("required-error");
    expect(scope.highlightRequiredFieldInModal('')).toEqual(null);
    expect(scope.highlightRequiredFieldInModal(3)).toEqual(null);
  });

  it('should return if the rnrLineItem is fullSupply', function () {
    var rnrLineItemFullSupply = jQuery.extend(true, {fullSupply:true}, new RnrLineItem());
    var rnrLineItemNonFullSupply = jQuery.extend(true, {fullSupply:false}, new RnrLineItem());

    expect(scope.isFullSupply(rnrLineItemFullSupply)).toEqual(true);
    expect(scope.isFullSupply(rnrLineItemNonFullSupply)).toEqual(false);
  });
  it('should return if the rnrLineItem is nonFullSupply', function () {
    var rnrLineItemFullSupply = jQuery.extend(true, {fullSupply:true}, new RnrLineItem());
    var rnrLineItemNonFullSupply = jQuery.extend(true, {fullSupply:false}, new RnrLineItem());

    expect(scope.isNonFullSupply(rnrLineItemFullSupply)).toEqual(false);
    expect(scope.isNonFullSupply(rnrLineItemNonFullSupply)).toEqual(true);
  });
});

