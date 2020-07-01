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

package org.craftercms.studio.impl.v2.upgrade.operations.site;

import java.io.IOException;
import java.io.InputStream;

import org.craftercms.studio.api.v2.exception.UpgradeException;
import org.springframework.core.io.Resource;

/**
 * Implementation of {@link org.craftercms.studio.api.v2.upgrade.UpgradeOperation} that updates or adds the version
 * file for the given site.
 * @author joseross
 */
public class SiteVersionUpgradeOperation extends XsltFileUpgradeOperation {

    /**
     * Path of the default file.
     */
    protected Resource defaultFile;

    public void setDefaultFile(final Resource defaultFile) {
        this.defaultFile = defaultFile;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(final String site) throws UpgradeException {
        if(!contentRepository.contentExists(site, path)) {
            try(InputStream is = defaultFile.getInputStream()) {
                writeToRepo(site, path, is);
            } catch (IOException e) {
                throw new UpgradeException("Error adding version file to site " + site, e);
            }
        }
        super.execute(site);
    }

}
