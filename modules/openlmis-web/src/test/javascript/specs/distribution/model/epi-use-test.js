/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

describe('EPI Use', function () {

  it('should set status as empty if expiration date format is invalid and rest of the form is empty', function () {
    var epiUse = new EpiUse({productGroups: [
      {reading: {expirationDate: {value: 'sdfghjk'}}}
    ]});

    var status = epiUse.computeStatus();

    expect(status).toEqual('is-empty');
  });

  it('should set status as incomplete if expiration date format is invalid and at least one other field is filled', function () {
    var epiUse = new EpiUse({productGroups: [
      {reading: {expirationDate: {value: 'sdfghjk'}, stockAtFirstOfMonth: {notRecorded: true}}}
    ]});

    var status = epiUse.computeStatus();

    expect(status).toEqual('is-incomplete');
  });

  it('should set status as incomplete if expiration date format is invalid and rest of the form is valid', function () {
    var epiUse = new EpiUse({productGroups: [
      {reading: {expirationDate: {value: 'sdfghjk'}, stockAtFirstOfMonth: {notRecorded: true}, distributed: {value: 100},
        received: {value: 80}, stockAtEndOfMonth: {value: 200}, loss: {value: 50}
      }}
    ]});

    var status = epiUse.computeStatus();

    expect(status).toEqual('is-incomplete');
  });

  it('should set status as incomplete if expiration date is not recorded and rest of the form is valid', function () {
    var epiUse = new EpiUse({productGroups: [
      {reading: {expirationDate: {notRecorded: true}, stockAtFirstOfMonth: {notRecorded: true}, distributed: {value: 100},
        received: {value: 80}, stockAtEndOfMonth: {value: 200}, loss: {value: 50}
      }}
    ]});

    var status = epiUse.computeStatus();

    expect(status).toEqual('is-complete');
  });

  it('should set status as complete if the form is valid', function () {
    var epiUse = new EpiUse({productGroups: [
      {reading: {expirationDate: {value: '11/2012'}, stockAtFirstOfMonth: {notRecorded: true}, distributed: {value: 100},
        received: {value: 80}, stockAtEndOfMonth: {value: 200}, loss: {value: 50}
      }}
    ]});

    var status = epiUse.computeStatus();

    expect(status).toEqual('is-complete');
  });

  it('should set status as incomplete if the only last form field valid', function () {
    var epiUse = new EpiUse({productGroups: [{reading: {expirationDate: {value: '11/2012'}}}]});

    var status = epiUse.computeStatus();

    expect(status).toEqual('is-incomplete');
  });

  it('should set not recorded checkbox for epi use', function () {
    var epiUse = new EpiUse({productGroups: [
      {reading: {expirationDate: {value: '11/2012'}, stockAtFirstOfMonth: {notRecorded: true}, distributed: {value: 100},
        received: {value: 80}, stockAtEndOfMonth: {value: 200}, loss: {value: 50}
      }}
    ]});

    epiUse.setNotRecorded();

    expect(epiUse.productGroups[0].reading.expirationDate.notRecorded).toBeTruthy();
    expect(epiUse.productGroups[0].reading.stockAtEndOfMonth.notRecorded).toBeTruthy();
    expect(epiUse.productGroups[0].reading.stockAtFirstOfMonth.notRecorded).toBeTruthy();
    expect(epiUse.productGroups[0].reading.distributed.notRecorded).toBeTruthy();
    expect(epiUse.productGroups[0].reading.loss.notRecorded).toBeTruthy();
    expect(epiUse.productGroups[0].reading.expirationDate.notRecorded).toBeTruthy();
  });
});