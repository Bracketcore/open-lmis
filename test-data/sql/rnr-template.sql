-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.

delete from program_rnr_columns;
insert into program_rnr_columns
(masterColumnId, programId, visible, source, position, label)
values
(1, (select id from programs where code = 'TB'),  true, 'R', 1,  'Product Code'),
(2, (select id from programs where code = 'TB'),  true, 'R', 2,  'Product'),
(3, (select id from programs where code = 'TB'),  true, 'R', 3,  'Unit/Unit of Issue'),
(4, (select id from programs where code = 'TB'),  true, 'U', 4,  'Beginning Balance'),
(5, (select id from programs where code = 'TB'),  true, 'U', 5,  'Total Received Quantity'),
(6, (select id from programs where code = 'TB'),  true, 'U', 6,  'Total Consumed Quantity'),
(7, (select id from programs where code = 'TB'),  true, 'U', 7,  'Total Losses / Adjustments'),
(8, (select id from programs where code = 'TB'),  true, 'C', 8,  'Stock on Hand'),
(9, (select id from programs where code = 'TB'),  true, 'U', 9, 'New Patients'),
(10, (select id from programs where code = 'TB'), true, 'U', 10, 'Total Stockout days'),
(11, (select id from programs where code = 'TB'), true, 'C', 11, 'Adjusted Total Consumption'),
(12, (select id from programs where code = 'TB'), true, 'C', 12, 'Average Monthly Consumption(AMC)'),
(13, (select id from programs where code = 'TB'), true, 'C', 13, 'Maximum Stock Quantity'),
(14, (select id from programs where code = 'TB'), true, 'C', 14, 'Calculated Order Quantity'),
(15, (select id from programs where code = 'TB'), true, 'U', 15, 'Requested quantity'),
(16, (select id from programs where code = 'TB'), true, 'U', 16, 'Requested quantity explanation'),
(17, (select id from programs where code = 'TB'), true, 'U', 17, 'Approved Quantity'),
(18, (select id from programs where code = 'TB'), true, 'C', 18, 'Packs to Ship'),
(19, (select id from programs where code = 'TB'), true, 'R', 19, 'Price per pack'),
(20, (select id from programs where code = 'TB'), true, 'C', 20, 'Total cost'),
(21, (select id from programs where code = 'TB'), true, 'U', 21, 'Remarks'),
(1, (select id from programs where code = 'ESS_MEDS'),  true, 'R', 1,  'Product Code'),
(2, (select id from programs where code = 'ESS_MEDS'),  true, 'R', 2,  'Product'),
(3, (select id from programs where code = 'ESS_MEDS'),  true, 'R', 3,  'Unit/Unit of Issue'),
(4, (select id from programs where code = 'ESS_MEDS'),  true, 'U', 4,  'Beginning Balance'),
(5, (select id from programs where code = 'ESS_MEDS'),  true, 'U', 5,  'Total Received Quantity'),
(6, (select id from programs where code = 'ESS_MEDS'),  true, 'U', 6,  'Total Consumed Quantity'),
(7, (select id from programs where code = 'ESS_MEDS'),  true, 'U', 7,  'Total Losses / Adjustments'),
(8, (select id from programs where code = 'ESS_MEDS'),  true, 'C', 8,  'Stock on Hand'),
(9, (select id from programs where code = 'ESS_MEDS'),  true, 'U', 9, 'New Patients'),
(10, (select id from programs where code = 'ESS_MEDS'), true, 'U', 10, 'Total Stockout days'),
(11, (select id from programs where code = 'ESS_MEDS'), true, 'C', 11, 'Adjusted Total Consumption'),
(12, (select id from programs where code = 'ESS_MEDS'), true, 'C', 12, 'Average Monthly Consumption(AMC)'),
(13, (select id from programs where code = 'ESS_MEDS'), true, 'C', 13, 'Maximum Stock Quantity'),
(14, (select id from programs where code = 'ESS_MEDS'), true, 'C', 14, 'Calculated Order Quantity'),
(15, (select id from programs where code = 'ESS_MEDS'), true, 'U', 15, 'Requested quantity'),
(16, (select id from programs where code = 'ESS_MEDS'), true, 'U', 16, 'Requested quantity explanation'),
(17, (select id from programs where code = 'ESS_MEDS'), true, 'U', 17, 'Approved Quantity'),
(18, (select id from programs where code = 'ESS_MEDS'), true, 'C', 18, 'Packs to Ship'),
(19, (select id from programs where code = 'ESS_MEDS'), true, 'R', 19, 'Price per pack'),
(20, (select id from programs where code = 'ESS_MEDS'), true, 'C', 20, 'Total cost'),
(21, (select id from programs where code = 'ESS_MEDS'), true, 'U', 21, 'Remarks');


update programs set templateConfigured = true where id in ((select id from programs where code = 'ESS_MEDS'),
(select id from programs where code = 'TB'));
