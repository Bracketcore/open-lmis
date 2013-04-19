/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.core.domain;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openlmis.core.exception.DataException;

import java.util.HashSet;

import static java.lang.Boolean.*;
import static java.util.Arrays.asList;
import static org.openlmis.core.domain.Right.CREATE_REQUISITION;

public class RoleTest {

    Role role;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        role = new Role("role test", FALSE, " description");
    }

    @Test
    public void shouldGiveErrorIfRoleDoesNotHaveAnyRights() throws Exception {
        expectedEx.expect(DataException.class);
        expectedEx.expectMessage("Role can not be created without any rights assigned to it.");
        role.validate();
    }

    @Test
    public void shouldGiveErrorIfRoleHasEmptyRightsList() {
        role.setRights(new HashSet<Right>());
        expectedEx.expect(DataException.class);
        expectedEx.expectMessage("Role can not be created without any rights assigned to it.");
        role.validate();
    }


    @Test
    public void shouldGiveErrorIfRoleDoesNotHaveAnyName() throws Exception {
        Role role = new Role("", FALSE, " description");
        role.setRights(new HashSet<>(asList(CREATE_REQUISITION)));
        expectedEx.expect(DataException.class);
        expectedEx.expectMessage("Role can not be created without name.");
        role.validate();

        role.setName(null);
        expectedEx.expect(DataException.class);
        expectedEx.expectMessage("Role can not be created without name.");
        role.validate();
    }
}
