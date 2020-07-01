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

package org.craftercms.studio.impl.v2.upgrade;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.craftercms.commons.config.YamlConfiguration;
import org.craftercms.commons.entitlements.exception.EntitlementException;
import org.craftercms.commons.entitlements.validator.DbIntegrityValidator;
import org.craftercms.studio.api.v1.log.Logger;
import org.craftercms.studio.api.v1.log.LoggerFactory;
import org.craftercms.studio.api.v1.repository.ContentRepository;
import org.craftercms.studio.api.v1.repository.RepositoryItem;
import org.craftercms.studio.api.v2.exception.UpgradeException;
import org.craftercms.studio.api.v2.upgrade.UpgradeManager;
import org.craftercms.studio.api.v2.upgrade.UpgradePipeline;
import org.craftercms.studio.api.v2.upgrade.UpgradePipelineFactory;
import org.craftercms.studio.api.v2.upgrade.VersionProvider;
import org.craftercms.studio.api.v2.utils.StudioConfiguration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

import static java.nio.file.Paths.get;
import static org.craftercms.studio.api.v2.upgrade.UpgradeConstants.CONFIG_KEY_CONFIGURATIONS;
import static org.craftercms.studio.api.v2.upgrade.UpgradeConstants.CONFIG_KEY_ENVIRONMENT;
import static org.craftercms.studio.api.v2.upgrade.UpgradeConstants.CONFIG_KEY_MODULE;
import static org.craftercms.studio.api.v2.upgrade.UpgradeConstants.CONFIG_KEY_PATH;
import static org.craftercms.studio.api.v2.upgrade.UpgradeConstants.VERSION_3_0_0;
import static org.craftercms.studio.api.v2.utils.StudioConfiguration.CONFIGURATION_ENVIRONMENT_ACTIVE;
import static org.craftercms.studio.api.v2.utils.StudioConfiguration.CONFIGURATION_SITE_CONFIG_BASE_PATH_PATTERN;
import static org.craftercms.studio.api.v2.utils.StudioConfiguration.CONFIGURATION_SITE_MUTLI_ENVIRONMENT_CONFIG_BASE_PATH_PATTERN;

/**
 * Default implementation for {@link UpgradeManager}.
 * @author joseross
 */
@SuppressWarnings("unchecked, rawtypes")
public class DefaultUpgradeManagerImpl implements UpgradeManager, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(DefaultUpgradeManagerImpl.class);

    private static final ThreadLocal<String> currentFile = new InheritableThreadLocal<>();

    public static final String SQL_QUERY_SITES_3_0_0 = "select site_id from cstudio_site where system = 0";
    public static final String SQL_QUERY_SITES = "select site_id from site where system = 0";

    public static final String CONFIG_PIPELINE_SUFFIX = ".pipeline";

    /**
     * The git path of the version file.
     */
    protected String siteVersionFilePath;

    protected VersionProvider dbVersionProvider;
    protected UpgradePipelineFactory dbPipelineFactory;

    protected UpgradePipelineFactory bpPipelineFactory;

    protected Resource configurationFile;

    protected DataSource dataSource;
    protected ApplicationContext appContext;
    protected DbIntegrityValidator integrityValidator;
    protected ContentRepository contentRepository;
    protected StudioConfiguration studioConfiguration;

    public static String getCurrentFile() {
        return currentFile.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void upgradeDatabaseAndConfiguration() throws UpgradeException {
        logger.info("Checking upgrades for the database and configuration");

        UpgradePipeline pipeline = dbPipelineFactory.getPipeline(dbVersionProvider);
        pipeline.execute();

    }

    protected VersionProvider getVersionProvider(String name, Object... args) {
        return (VersionProvider) appContext.getBean(name, args);
    }

    protected UpgradePipeline getPipeline(VersionProvider versionProvider, String factoryName, Object... args)
        throws UpgradeException {
        UpgradePipelineFactory pipelineFactory =
            (UpgradePipelineFactory) appContext.getBean(factoryName, args);
        return pipelineFactory.getPipeline(versionProvider);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void upgradeSite(final String site) {
        logger.info("Checking upgrades for site {0}", site);

        try {
            VersionProvider versionProvider = getVersionProvider("siteVersionProvider", site, siteVersionFilePath);
            UpgradePipeline pipeline = getPipeline(versionProvider, "sitePipelineFactory");

            pipeline.execute(site);

            upgradeSiteConfiguration(site);
        } catch (UpgradeException e) {
            logger.error("Error during upgrade for site " + site, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void upgradeSiteConfiguration(final String site) throws UpgradeException {
        logger.info("Checking upgrades for configuration in site {0}", site);

        String defaultEnvironment = studioConfiguration.getProperty(CONFIGURATION_ENVIRONMENT_ACTIVE);
        HierarchicalConfiguration config = loadUpgradeConfiguration();
        List<HierarchicalConfiguration> managedFiles = config.childConfigurationsAt(CONFIG_KEY_CONFIGURATIONS);
        String configPath = null;

        try {
            for (HierarchicalConfiguration configFile : managedFiles) {
                String module = configFile.getString(CONFIG_KEY_MODULE);
                String file = configFile.getString(CONFIG_KEY_PATH);
                List<String> environments = getExistingEnvironments(site);

                for (String env : environments) {
                    Map<String, String> values = new HashMap<>();
                    values.put(CONFIG_KEY_MODULE, module);
                    values.put(CONFIG_KEY_ENVIRONMENT, env);
                    String basePath;

                    if (StringUtils.isEmpty(env) || env.equals(defaultEnvironment)) {
                        basePath = studioConfiguration.getProperty(CONFIGURATION_SITE_CONFIG_BASE_PATH_PATTERN);
                    } else {
                        basePath = studioConfiguration.getProperty(
                                CONFIGURATION_SITE_MUTLI_ENVIRONMENT_CONFIG_BASE_PATH_PATTERN);
                    }
                    configPath = get(StrSubstitutor.replace(basePath, values, "{", "}"), file).toString();
                    logger.info("Checking upgrades for file {0}", configPath);
                    currentFile.set(configPath);

                    VersionProvider versionProvider = getVersionProvider("fileVersionProvider", site, configPath);
                    UpgradePipeline pipeline = getPipeline(versionProvider, "filePipelineFactory",
                            configFile.getRootElementName() + CONFIG_PIPELINE_SUFFIX);

                    pipeline.execute(site);
                }
            }
        } catch (Exception e) {
            logger.error("Error upgrading configuration file {0}", e, configPath);
        } finally {
            currentFile.remove();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void upgradeExistingSites() throws UpgradeException {
        String currentDbVersion = dbVersionProvider.getCurrentVersion();

        List<String> sites;
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        if(currentDbVersion.equals(VERSION_3_0_0)) {
            sites = jdbcTemplate.queryForList(SQL_QUERY_SITES_3_0_0, String.class);
        } else {
            sites = jdbcTemplate.queryForList(SQL_QUERY_SITES, String.class);
        }

        for(String site : sites) {
            if (checkIfSiteRepoExists(site)) {
                upgradeSite(site);
            }
        }
    }

    protected boolean checkIfSiteRepoExists(String site) {
        boolean toRet = false;
        String firstCommitId = contentRepository.getRepoFirstCommitId(site);
        if (!StringUtils.isEmpty(firstCommitId)) {
            toRet = true;
        }
        return toRet;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void upgradeBlueprints() throws UpgradeException {
        logger.info("Checking upgrades for the blueprints");

        // The version is fixed for now so bp are always updates, in the future this should be replaced with a proper
        // version provider
        UpgradePipeline pipeline = bpPipelineFactory.getPipeline(() -> VERSION_3_0_0);
        pipeline.execute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getExistingEnvironments(String site) {
        logger.debug("Looking for existing environments in site {0}", site);
        List<String> result = new LinkedList<>();

        // add the default env that will always exist
        result.add(studioConfiguration.getProperty(CONFIGURATION_ENVIRONMENT_ACTIVE));

        String basePath = studioConfiguration.getProperty(CONFIGURATION_SITE_CONFIG_BASE_PATH_PATTERN);
        String envPath = studioConfiguration.getProperty(CONFIGURATION_SITE_MUTLI_ENVIRONMENT_CONFIG_BASE_PATH_PATTERN);

        RepositoryItem[] modules = contentRepository.getContentChildren(site,
                StrSubstitutor.replace(basePath, Collections.singletonMap(CONFIG_KEY_MODULE, StringUtils.EMPTY), "{", "}"));

        for (RepositoryItem module : modules) {
            logger.debug("Looking for existing environments for module {0} in site {1}", module.name, site);

            Map<String, String> values = new HashMap<>();
            values.put(CONFIG_KEY_MODULE, module.name);
            values.put(CONFIG_KEY_ENVIRONMENT, StringUtils.EMPTY);

            RepositoryItem[] environments =
                    contentRepository.getContentChildren(site, StrSubstitutor.replace(envPath, values, "{", "}"));

            for (RepositoryItem env : environments) {
                logger.debug("Adding environment {0}", env.name);
                result.add(env.name);
            }
        }

        return result;
    }

    /**
     * Obtains the current version and starts the upgrade process.
     * @throws UpgradeException if there is any error in the upgrade process
     * @throws EntitlementException if there is any validation error after the upgrade process
     */
    public void init() throws UpgradeException, EntitlementException {

        upgradeBlueprints();
        upgradeDatabaseAndConfiguration();
        upgradeExistingSites();

        try {
            integrityValidator.validate(dataSource.getConnection());
        } catch (SQLException e) {
            logger.error("Could not connect to database for integrity validation", e);
            throw new UpgradeException("Could not connect to database for integrity validation", e);
        }
    }

    protected HierarchicalConfiguration loadUpgradeConfiguration() throws UpgradeException {
        YamlConfiguration configuration = new YamlConfiguration();
        try (InputStream is = configurationFile.getInputStream()) {
            configuration.read(is);
        } catch (Exception e) {
            throw  new UpgradeException("Error reading configuration file", e);
        }
        return configuration;
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.appContext = applicationContext;
    }

    @Required
    public void setDataSource(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Required
    public void setIntegrityValidator(final DbIntegrityValidator integrityValidator) {
        this.integrityValidator = integrityValidator;
    }

    @Required
    public void setContentRepository(final ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    @Required
    public void setDbPipelineFactory(final UpgradePipelineFactory dbPipelineFactory) {
        this.dbPipelineFactory = dbPipelineFactory;
    }

    @Required
    public void setDbVersionProvider(final VersionProvider dbVersionProvider) {
        this.dbVersionProvider = dbVersionProvider;
    }

    @Required
    public void setConfigurationFile(final Resource configurationFile) {
        this.configurationFile = configurationFile;
    }

    @Required
    public void setSiteVersionFilePath(final String siteVersionFilePath) {
        this.siteVersionFilePath = siteVersionFilePath;
    }

    @Required
    public void setBpPipelineFactory(final UpgradePipelineFactory bpPipelineFactory) {
        this.bpPipelineFactory = bpPipelineFactory;
    }

    @Required
    public void setStudioConfiguration(StudioConfiguration studioConfiguration) {
        this.studioConfiguration = studioConfiguration;
    }

}
