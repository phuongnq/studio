/*
 * Copyright (C) 2007-2019 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.craftercms.studio.api.v1.deployment;

import org.craftercms.studio.api.v1.ebus.PreviewEventContext;

public interface PreviewDeployer {

    String ENV_PREVIEW = "preview";
    String ENV_AUTHORING = "authoring";

    void onPreviewSync(PreviewEventContext context);

    boolean createTarget(String site, String searchEngine);

    boolean deleteTarget(String site);
}
