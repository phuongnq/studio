package org.craftercms.studio.api.v2.service.publish;

import org.craftercms.studio.api.v1.exception.SiteNotFoundException;
import org.craftercms.studio.api.v2.dal.PublishingPackage;
import org.craftercms.studio.api.v2.dal.PublishingPackageDetails;

import java.util.List;

public interface PublishService {

    /**
     * Get total number of publishing packages for given search parameters
     *
     * @param siteId site identifier
     * @param environment publishing environment
     * @param path  regular expression for paths
     * @param state publishing package state
     *
     * @return total number of publishing packages
     */
    int getPublishingPackagesTotal(String siteId, String environment, String path, String state)
            throws SiteNotFoundException;

    /**
     * Get publishing packages for given search parameters
     *
     * @param siteId site identifier
     * @param environment publishing environment
     * @param path regular expression for paths
     * @param state publishing package state
     * @param offset offset for pagination
     * @param limit limit for pagination
     * @return list of publishing packages
     */
    List<PublishingPackage> getPublishingPackages(String siteId, String environment, String path, String state,
                                                  int offset, int limit) throws SiteNotFoundException;

    /**
     * Get publishing package details
     *
     * @param siteId site identifier
     * @param packageId package identifier
     *
     * @return publishing package details
     */
    PublishingPackageDetails getPublishingPackageDetails(String siteId, String packageId) throws SiteNotFoundException;

    /**
     * Cancel publishing packages
     *
     * @param siteId site identifier
     * @param packageIds list of package identifiers
     */
    void cancelPublishingPackages(String siteId, List<String> packageIds) throws SiteNotFoundException;
}
