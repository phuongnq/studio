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

package org.craftercms.studio.impl.v2.upgrade.providers;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.craftercms.studio.api.v1.log.Logger;
import org.craftercms.studio.api.v1.log.LoggerFactory;
import org.craftercms.studio.api.v1.repository.ContentRepository;
import org.craftercms.studio.api.v2.exception.UpgradeException;
import org.craftercms.studio.api.v2.exception.UpgradeNotSupportedException;
import org.craftercms.studio.api.v2.upgrade.VersionProvider;
import org.springframework.beans.factory.annotation.Required;
import org.w3c.dom.Document;

/**
 * Implementation of {@inheritDoc} for XML files.
 * @author joseross
 */
public class XmlFileVersionProvider implements VersionProvider {

    private static final Logger logger = LoggerFactory.getLogger(XmlFileVersionProvider.class);

    /**
     * Name of the site.
     */
    protected String site;

    /**
     * Path of the file containing the version.
     */
    protected String path;

    /**
     * XPath expression to extract the version.
     */
    protected String xpath;

    /**
     * Version returned if none is found.
     */
    protected String defaultVersion;

    /**
     * Indicates if the skip flag should be returned
     */
    protected boolean skipIfMissing = true;

    protected ContentRepository contentRepository;

    public XmlFileVersionProvider(final String site, final String path) {
        this.site = site;
        this.path = path;
    }

    @Required
    public void setContentRepository(final ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    public void setSite(final String site) {
        this.site = site;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    @Required
    public void setXpath(final String xpath) {
        this.xpath = xpath;
    }

    @Required
    public void setDefaultVersion(final String defaultVersion) {
        this.defaultVersion = defaultVersion;
    }

    public void setSkipIfMissing(final boolean skipIfMissing) {
        this.skipIfMissing = skipIfMissing;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCurrentVersion() throws UpgradeException {
        String currentVersion = defaultVersion;
        if(!contentRepository.contentExists(site, "/config/studio")) {
            String firstCommit = contentRepository.getRepoFirstCommitId(site);
            if (StringUtils.isNotEmpty(firstCommit)) {
                throw new UpgradeNotSupportedException("Site '" + site + "' from 2.5.x can't be automatically upgraded");
            }
        } else if(!contentRepository.contentExists(site, path)) {
            logger.debug("Missing file {0} in site {1}", path, site);
            if (skipIfMissing) {
                return SKIP;
            } else {
                return defaultVersion;
            }
        } else {
            try(InputStream is = contentRepository.getContent(site, path)) {
                DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = builderFactory.newDocumentBuilder();
                Document xmlDocument = builder.parse(is);
                XPath xPath = XPathFactory.newInstance().newXPath();
                String fileVersion = (String) xPath.compile(xpath).evaluate(xmlDocument, XPathConstants.STRING);
                if(StringUtils.isNotEmpty(fileVersion)) {
                    currentVersion = fileVersion;
                }
            } catch (Exception e) {
                throw new UpgradeException("Error reading version from file " + path + " in site " + site, e);
            }
        }
        return currentVersion;
    }

}
