describe('Rnr Template controllers', function () {

  describe('SaveRnrTemplateController', function () {

    var scope, ctrl, $httpBackend, location,rnrColumnList;

    beforeEach(module('openlmis.services'));
    beforeEach(inject(function ($rootScope,_$httpBackend_,$controller,$location) {
      scope = $rootScope.$new();
      $httpBackend=_$httpBackend_;
      location=$location;
      scope.program={code:"programCode"};

      rnrColumnList = [{"id":1,"name":"product_code","description":"Unique identifier for each commodity","position":1,"label":"Product Code","defaultValue":"","dataSource":"Reference Value (Product Table)","availableColumnTypes":["Calculated"],"selectedColumnType":"Calculated","formula":"","indicator":"O","used":true,"visible":true,"mandatory":true,"cyclicDependencies":[]},{"id":2,"name":"product","description":"Primary name of the product","position":2,"label":"Product","defaultValue":"","dataSource":"Reference Value (Product Table)","availableColumnTypes":["Calculated"],"selectedColumnType":"Calculated","formula":"","indicator":"R","used":true,"visible":true,"mandatory":true,"cyclicDependencies":[]}];
      $httpBackend.expectGET('/admin/rnr/programCode/columns.json').respond
        ({"rnrColumnList":[{"id":1,"name":"product_code","description":"Unique identifier for each commodity","position":1,"label":"Product Code","defaultValue":"","dataSource":"Reference Value (Product Table)","availableColumnTypes":["Calculated"],"selectedColumnType":"Calculated","formula":"","indicator":"O","used":true,"visible":true,"mandatory":true,"cyclicDependencies":[]},{"id":2,"name":"product","description":"Primary name of the product","position":2,"label":"Product","defaultValue":"","dataSource":"Reference Value (Product Table)","availableColumnTypes":["Calculated"],"selectedColumnType":"Calculated","formula":"","indicator":"R","used":true,"visible":true,"mandatory":true,"cyclicDependencies":[]}]});
      ctrl = $controller(SaveRnrTemplateController, {$scope:scope, $location:location});
    }));

    it('should get list of rnr columns for configuring', function() {
      $httpBackend.flush();
      expect(scope.rnrColumnList).toEqual(rnrColumnList);
    });

  });
});