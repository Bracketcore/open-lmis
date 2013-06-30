/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

describe('Save Regimen Template Controller', function () {

  var scope, ctrl, $httpBackend, location, messageService, regimenList1, regimenList2, program, newRegimenForm;

  beforeEach(module('openlmis.services'));
  beforeEach(module('openlmis.localStorage'));

  beforeEach(inject(function ($rootScope, _$httpBackend_, $controller, $location, _messageService_) {
    scope = $rootScope.$new();
    $httpBackend = _$httpBackend_;
    location = $location;
    messageService = _messageService_;

    regimen1 = {'id': 1, 'code': 'REG1', 'category': {'id': 1}, editable: false, $$hashKey: "efg"};
    regimen2 = {'id': 2, 'code': 'REG2', 'category': {'id': 2}, editable: false};
    regimen3 = {'id': 3, 'code': 'REG3', 'category': {'id': 1}, editable: false};
    regimen4 = {'id': 4, 'code': 'REG4', 'category': {'id': 2}, editable: false};
    regimenList1 = [regimen1, regimen3];
    regimenList2 = [regimen2, regimen4];
    program = {id: 1, name: 'HIV'};
    var regimens = [regimen1, regimen2, regimen3, regimen4];
    var regimenCategories = [
      {'id': 1},
      {'id': 2}
    ];

    scope.newRegimenForm = {$error: {}};

    ctrl = $controller(SaveRegimenTemplateController, {$scope: scope, $location: location, program: program, regimens: regimens, regimenCategories: regimenCategories, newRegimenForm: newRegimenForm});
  }));

  it('should filter regimen by categories', function () {
    expect(scope.regimensByCategory).toEqual([undefined, regimenList1, regimenList2]);
    expect(scope.error).toEqual("");
  });

  it('should get regimen values by category', function () {
    scope.regimensByCategory = [regimenList1, regimenList2];
    var regimenLists = [regimenList1, regimenList2];
    var expectedLists = scope.getRegimenValuesByCategory();

    expect(expectedLists).toEqual(regimenLists);
  });

  it('should set error message if regimen form fields are not filled', function () {
    scope.newRegimenForm.$error.required = true;
    spyOn(messageService, 'get');
    scope.addNewRegimen();

    expect(scope.inputClass).toBeTruthy();
    expect(messageService.get).toHaveBeenCalledWith('label.missing.values');
  });

  it('should not add regimen if it is duplicate', function () {
    scope.newRegimen = {'id': 1, 'code': 'REG1', 'category': {'id': 1}, $$hashKey: "abc"};
    spyOn(messageService, 'get');
    scope.regimensByCategory[1] = regimenList1
    scope.regimensByCategory[2] = regimenList2;
    scope.addNewRegimen();

    expect(messageService.get).toHaveBeenCalledWith('error.duplicate.regimen.code');
    expect(scope.newRegimenError).toEqual("");
    expect(scope.regimensByCategory.length).toEqual(3);
    expect(scope.regimensByCategory[1].length).toEqual(2);
    expect(scope.regimensByCategory[2].length).toEqual(2);
  });

  it('should add regimen in appropriate category', function () {
    scope.newRegimen = {'code': 'REG5', 'category': {'id': 1}};
    scope.regimensByCategory[1] = regimenList1
    scope.regimensByCategory[2] = regimenList2;
    scope.addNewRegimen();

    expect(scope.regimensByCategory.length).toEqual(3);
    expect(scope.regimensByCategory[1].length).toEqual(3);
    expect(scope.regimensByCategory[2].length).toEqual(2);
    expect(scope.newRegimen.active).toBeTruthy();
    expect(scope.newRegimenError).toBeNull();
    expect(scope.inputClass).toBeFalsy()
  });

  it('should add regimen in different category if category id is different', function () {
    scope.newRegimen = {'code': 'REG5', 'category': {'id': 3}};
    scope.regimensByCategory[1] = regimenList1
    scope.regimensByCategory[2] = regimenList2;
    scope.addNewRegimen();

    expect(scope.regimensByCategory.length).toEqual(4);
    expect(scope.regimensByCategory[1].length).toEqual(2);
    expect(scope.regimensByCategory[2].length).toEqual(2);
    expect(scope.regimensByCategory[3].length).toEqual(1);
  });

  it('should save editable row on clicking Done', function () {
    var regimen = {'code': 'REG5', name: 'Regimen One', 'category': {'id': 1}};
    scope.regimensByCategory[1] = regimenList1
    scope.regimensByCategory[2] = regimenList2;

    scope.saveRow(regimen);

    expect(scope.error).toEqual("");
    expect(regimen.doneRegimenError).toBeFalsy();
    expect(regimen.editable).toBeFalsy();
  });

  it('should not save editable row on clicking Done if name not defined', function () {
    var regimen = {'code': 'REG5', 'category': {'id': 1}};
    scope.regimensByCategory[1] = regimenList1
    scope.regimensByCategory[2] = regimenList2;

    scope.saveRow(regimen);

    expect(regimen.doneRegimenError).toBeTruthy();
  });


  it('should not save editable row on clicking Done if code not defined', function () {
    var regimen = {'name': 'REG5', 'category': {'id': 1}};
    scope.regimensByCategory[1] = regimenList1
    scope.regimensByCategory[2] = regimenList2;

    scope.saveRow(regimen);

    expect(regimen.doneRegimenError).toBeTruthy();
  });

  it('should not save regimens if any row is editable', function () {
    var regimenList3 = [
      {'code': 'REG5', 'category': {'id': 1}, editable: true}
    ];
    scope.regimensByCategory[1] = regimenList1
    scope.regimensByCategory[2] = regimenList2;
    scope.regimensByCategory[3] = regimenList3;
    spyOn(messageService, 'get');

    scope.save();
    expect(messageService.get).toHaveBeenCalledWith('error.regimens.not.done');
  });

  it('should save regimens', function () {
    scope.regimensByCategory[1] = regimenList1
    scope.regimensByCategory[2] = regimenList2;
    spyOn(messageService, 'get').andCallFake(function(arg) {
      return 'success';
    });
    $httpBackend.expectPOST('/programId/1/regimens.json').respond(200);
    scope.save();
    $httpBackend.flush();

    expect(scope.error).toEqual("");
    expect(scope.program.regimenTemplateConfigured).toBeTruthy();
    expect(scope.$parent.message).toEqual('success');
  });

});