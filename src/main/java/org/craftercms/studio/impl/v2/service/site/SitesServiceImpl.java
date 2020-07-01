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

package org.craftercms.studio.impl.v2.service.site;

import org.craftercms.commons.plugin.model.PluginDescriptor;
import org.craftercms.studio.api.v2.service.site.SitesService;
import org.craftercms.studio.api.v2.service.site.internal.SitesServiceInternal;

import java.util.List;

public class SitesServiceImpl implements SitesService {

    private SitesServiceInternal sitesServiceInternal;

    @Override
    public List<PluginDescriptor> getAvailableBlueprints() {
        return sitesServiceInternal.getAvailableBlueprints();
    }

    @Override
    public PluginDescriptor getBlueprintDescriptor(final String id) {
        return sitesServiceInternal.getBlueprintDescriptor(id);
    }

    @Override
    public String getBlueprintLocation(String blueprintId) {
        return sitesServiceInternal.getBlueprintLocation(blueprintId);
    }

    @Override
    public PluginDescriptor getSiteBlueprintDescriptor(final String id) {
        return sitesServiceInternal.getSiteBlueprintDescriptor(id);
    }

    public SitesServiceInternal getSitesServiceInternal() {
        return sitesServiceInternal;
    }

    public void setSitesServiceInternal(SitesServiceInternal sitesServiceInternal) {
        this.sitesServiceInternal = sitesServiceInternal;
    }
}
