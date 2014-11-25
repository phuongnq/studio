/*******************************************************************************
 * Crafter Studio Web-content authoring solution
 *     Copyright (C) 2007-2013 Crafter Software Corporation.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.craftercms.cstudio.alfresco.dm.service.api;

import org.craftercms.cstudio.alfresco.service.exception.ServiceException;
import org.craftercms.cstudio.alfresco.dm.to.DmDeploymentTaskTO;
import org.craftercms.studio.api.domain.DeploymentSyncHistory;

import java.util.List;

public interface DmDeploymentService {

    /**
     * get deployment history given a specified date range
     *
     * @param daysFromToday
     * @param numberOfItems
     * @param site
     * @param sort
     *            the sort key to sort items within each deployed date
     * @param ascending
     *
     * @return list of deployment items
     */

	public List<DeploymentSyncHistory> getDeploymentHistory(
			String site, int days, int number, String sort, boolean ascending,
			String filterType);
}
