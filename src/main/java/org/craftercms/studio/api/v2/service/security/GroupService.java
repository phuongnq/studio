/*
 * Copyright (C) 2007-2020 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.craftercms.studio.api.v2.service.security;

import org.craftercms.studio.api.v1.exception.ServiceLayerException;
import org.craftercms.studio.api.v1.exception.security.AuthenticationException;
import org.craftercms.studio.api.v1.exception.security.GroupAlreadyExistsException;
import org.craftercms.studio.api.v1.exception.security.GroupNotFoundException;
import org.craftercms.studio.api.v1.exception.security.UserNotFoundException;
import org.craftercms.studio.api.v2.dal.Group;
import org.craftercms.studio.api.v2.dal.User;
import org.craftercms.studio.api.v2.exception.OrganizationNotFoundException;

import java.util.List;

public interface GroupService {

    /**
     * Get all groups
     *
     * @param orgId Organization identifier
     * @param offset Result set offset
     * @param limit Result set limit
     * @param sort Sort order
     * @return List of groups
     */
    List<Group> getAllGroups(long orgId, int offset, int limit, String sort)
            throws ServiceLayerException, OrganizationNotFoundException;

    /**
     * Get total number of all groups
     *
     * @param orgId Organization identifier
     * @return Number of groups
     */
    int getAllGroupsTotal(long orgId) throws ServiceLayerException, OrganizationNotFoundException;

    /**
     * Create group
     *
     * @param orgId Organization identifier
     * @param groupName Group name
     * @param groupDescription Group description
     * @return the created group
     */
    Group createGroup(long orgId, String groupName, String groupDescription)
            throws GroupAlreadyExistsException, ServiceLayerException, AuthenticationException;

    /**
     * Update group
     *
     * @param orgId Organization identifier
     * @param group Group to update
     * @return the updated group
     */
    Group updateGroup(long orgId, Group group)
            throws ServiceLayerException, GroupNotFoundException, AuthenticationException;

    /**
     * Delete group(s)
     *
     * @param groupIds Group identifiers
     */
    void deleteGroup(List<Long> groupIds)
            throws ServiceLayerException, GroupNotFoundException, AuthenticationException;

    /**
     * Get group
     *
     * @param groupId Group identifier
     * @return Group
     */
    Group getGroup(long groupId) throws ServiceLayerException, GroupNotFoundException;

    /**
     * Get group members
     *
     * @param groupId Group identifier
     * @param offset Result set offset
     * @param limit Result set limit
     * @param sort Sort order
     * @return List of users
     */
    List<User> getGroupMembers(long groupId, int offset, int limit, String sort)
            throws ServiceLayerException, GroupNotFoundException;

    /**
     * Get total number of group members
     * @param groupId Group identifier
     * @return Number of members
     */
    int getGroupMembersTotal(long groupId) throws ServiceLayerException, GroupNotFoundException;

    /**
     * Add users to the group
     *
     * @param groupId Group identifier
     * @param userIds List of user identifiers
     * @param usernames List of usernames
     * @return users added to the group
     */
    List<User> addGroupMembers(long groupId, List<Long> userIds, List<String> usernames)
            throws ServiceLayerException, UserNotFoundException, GroupNotFoundException, AuthenticationException;

    /**
     * Remove users from the group
     *
     * @param groupId Group identifier
     * @param userIds List of user identifiers
     * @param usernames List of usernames
     */
    void removeGroupMembers(long groupId, List<Long> userIds, List<String> usernames)
            throws ServiceLayerException, UserNotFoundException, GroupNotFoundException, AuthenticationException;

}
