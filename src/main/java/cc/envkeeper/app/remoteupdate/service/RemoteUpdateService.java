/*
 * Copyright (c) 2022 Eric Thiebaut-George.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package cc.envkeeper.app.remoteupdate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cc.envkeeper.app.domain.Build;
import cc.envkeeper.app.domain.BuildStatistic;
import cc.envkeeper.app.domain.BuildStep;
import cc.envkeeper.app.domain.Deployment;
import cc.envkeeper.app.domain.Environment;
import cc.envkeeper.app.domain.EnvironmentGroup;
import cc.envkeeper.app.domain.Product;
import cc.envkeeper.app.domain.ProductComponent;
import cc.envkeeper.app.domain.ProductComponentVersion;
import cc.envkeeper.app.domain.ProductVersion;
import cc.envkeeper.app.domain.enumeration.BuildStatus;
import cc.envkeeper.app.remoteupdate.domain.BuildMessage;
import cc.envkeeper.app.remoteupdate.domain.BuildStatMessage;
import cc.envkeeper.app.remoteupdate.domain.BuildStepMessage;
import cc.envkeeper.app.remoteupdate.domain.DeploymentMessage;
import cc.envkeeper.app.remoteupdate.domain.ProductComponentMessage;
import cc.envkeeper.app.remoteupdate.domain.ProductComponentVersionMessage;
import cc.envkeeper.app.remoteupdate.domain.ProductVersionMessage;
import cc.envkeeper.app.repository.BuildRepository;
import cc.envkeeper.app.repository.BuildStatisticRepository;
import cc.envkeeper.app.repository.BuildStepRepository;
import cc.envkeeper.app.repository.DeploymentRepository;
import cc.envkeeper.app.repository.EnvironmentGroupRepository;
import cc.envkeeper.app.repository.EnvironmentRepository;
import cc.envkeeper.app.repository.ProductComponentRepository;
import cc.envkeeper.app.repository.ProductComponentVersionRepository;
import cc.envkeeper.app.repository.ProductRepository;
import cc.envkeeper.app.repository.ProductVersionRepository;
import cc.envkeeper.app.service.mapper.ReleaseNotesParser;

/**
 * Remote update processor
 *
 * Processes remote update messages and updates EnvKeeper.
 */
@Service
@Transactional
public class RemoteUpdateService {

    private static final String NEW_ENVIRONMENTS_GROUP_SHORTNAME = "New";
    private static final String NEW_ENVIRONMENTS_GROUP_FULL_NAME = "New Environments";
    private static final String NEW_ENVIRONMENTS_GROUP_DESCRIPTION = "New environments are created here";
    private static final Logger log = LoggerFactory.getLogger(RemoteUpdateService.class);

    private final BuildRepository buildRepository;
    private final BuildStepRepository buildStepRepository;
    private final BuildStatisticRepository buildStatRepository;
    private final DeploymentRepository deploymentRepository;
    private final EnvironmentRepository environmentRepository;
    private final EnvironmentGroupRepository environmentGroupRepository;
    private final ProductRepository productRepository;
    private final ProductComponentRepository productComponentRepository;
    private final ProductComponentVersionRepository productComponentVersionRepository;
    private final ProductVersionRepository productVersionRepository;

    public RemoteUpdateService(final BuildRepository buildRepository, final BuildStepRepository buildStepRepository, final BuildStatisticRepository buildStatRepository, final DeploymentRepository deploymentRepository,
                               final EnvironmentRepository environmentRepository, final EnvironmentGroupRepository environmentGroupRepository, final ProductRepository productRepository, final ProductComponentRepository productComponentRepository,
                               final ProductComponentVersionRepository productComponentVersionRepository, final ProductVersionRepository productVersionRepository) {
        this.buildRepository = buildRepository;
        this.buildStepRepository = buildStepRepository;
        this.buildStatRepository = buildStatRepository;
        this.deploymentRepository = deploymentRepository;
        this.environmentRepository = environmentRepository;
        this.environmentGroupRepository = environmentGroupRepository;
        this.productRepository = productRepository;
        this.productComponentRepository = productComponentRepository;
        this.productComponentVersionRepository = productComponentVersionRepository;
        this.productVersionRepository = productVersionRepository;
    }

    public Deployment processDeployment(DeploymentMessage deploymentMessage) throws Exception {

        log.info("Received deployment update notification: {}", deploymentMessage);

        // These are all part of the lookup identifier
        if (StringUtils.isEmpty(deploymentMessage.getBuildUrl())) {
            throw new Exception("No build URL specified");
        }
        if (StringUtils.isEmpty(deploymentMessage.getBuildStartTime())) {
            throw new Exception("No build start time specified");
        }
        if (StringUtils.isEmpty(deploymentMessage.getStartTime())) {
            throw new Exception("No start time specified");
        }
        if (StringUtils.isEmpty(deploymentMessage.getEnvironmentShortName())) {
            throw new Exception("No environment shortname specified");
        }
        if (StringUtils.isEmpty(deploymentMessage.getProductShortName())) {
            throw new Exception("No product shortname specified");
        }
        if (StringUtils.isEmpty(deploymentMessage.getProductVersion())) {
            throw new Exception("No product version specified");
        }

        // Find potentially existing build
        Build build = getBuild(
            deploymentMessage.getBuildUrl(),
            deploymentMessage.getBuildStartTime());

        // Find potentially existing deployment
        Deployment deployment = new Deployment();
        deployment.setBuild(build);
        deployment.setStartTime(deploymentMessage.getStartTime());
        deployment.setEnvironment(getEnvironment(deploymentMessage.getEnvironmentShortName()));
        deployment.setProductVersion(getProductVersion(deploymentMessage.getProductShortName(), deploymentMessage.getProductVersion()));
        List<Deployment> existingDeployments = deploymentRepository.findAll(Example.of(deployment));
        if (!existingDeployments.isEmpty()) {
            deployment = existingDeployments.get(0);
        }

        // Update deployment properties
        if (deploymentMessage.getEndTime() != null) {
            deployment.setEndTime(deploymentMessage.getEndTime());
        }
        if (deploymentMessage.getUser() != null) {
            deployment.setUser(deploymentMessage.getUser());
        }
        if (deploymentMessage.getStatus() != null) {
            deployment.setStatus(deploymentMessage.getStatus());
        }
        if (deploymentMessage.getNamespace() != null) {
            deployment.setNamespace(deploymentMessage.getNamespace());
        }
        if (deploymentMessage.getUrl() != null) {
            deployment.setUrl(deploymentMessage.getUrl());
        }
        if (deploymentMessage.getTestUrl() != null) {
            deployment.setTestUrl(deploymentMessage.getTestUrl());
        }

        // Save deployment
        return deploymentRepository.save(deployment);
    }

    public Build processBuild(BuildMessage buildMessage) throws Exception {

        log.info("Received build update notification: {}", buildMessage);

        // These are all part of the lookup identifier
        if (StringUtils.isEmpty(buildMessage.getBuildUrl())) {
            throw new Exception("No build URL specified");
        }
        if (StringUtils.isEmpty(buildMessage.getStartTime())) {
            throw new Exception("No start time specified");
        }

        // Find potentially existing build
        Build build = getBuild(buildMessage.getBuildUrl(), buildMessage.getStartTime());

        // Update build properties
        if (buildMessage.getBuildName() != null) {
            build.setBuildName(buildMessage.getBuildName());
        }
        if (buildMessage.getStatus() != null) {
            build.setStatus(buildMessage.getStatus());
        }
        if (buildMessage.getEndTime() != null) {
            build.setEndTime(buildMessage.getEndTime());
        }
        if (buildMessage.getParentBuildUrl() != null && buildMessage.getParentBuildStartTime() != null) {
            build.setParentBuild(getBuild(buildMessage.getParentBuildUrl(), buildMessage.getParentBuildStartTime()));
        }

        // Upsert build
        return buildRepository.save(build);
    }

    public ProductVersion processProductVersion(ProductVersionMessage productVersionMessage) throws Exception {

        log.info("Received product version update notification: {}", productVersionMessage);

        // These are all part of the lookup identifier
        if (StringUtils.isEmpty(productVersionMessage.getProductShortName())) {
            throw new Exception("No product shortname specified");
        }
        if (StringUtils.isEmpty(productVersionMessage.getProductVersion())) {
            throw new Exception("No version specified");
        }

        // Find potentially existing product version or create it
        ProductVersion productVersion = getProductVersion(productVersionMessage.getProductShortName(), productVersionMessage.getProductVersion());

        // Update productVersion properties
        if (productVersionMessage.getBuildUrl() != null && productVersionMessage.getBuildStartTime() != null) {
            productVersion.setBuild(getBuild(productVersionMessage.getBuildUrl(), productVersionMessage.getBuildStartTime()));
        }
        if (productVersionMessage.getReleaseNotes() != null || productVersionMessage.getReleaseNotesBase64() != null) {
            productVersion.setReleaseNotes(
                ReleaseNotesParser.toReleaseNotes(
                    productVersionMessage.getReleaseNotes(),
                    productVersionMessage.getReleaseNotesBase64()));
        }
        if (productVersionMessage.getComponents() != null && !productVersionMessage.getComponents().isEmpty()) {
            Set<ProductComponentVersion> productComponentVersions = new HashSet<>();
            for (ProductComponentVersionMessage componentVersionMessage: productVersionMessage.getComponents()) {
                ProductComponentVersion productComponentVersion = processProductComponentVersionMessage(componentVersionMessage);
                productComponentVersions.add(productComponentVersionRepository.save(productComponentVersion));
            }
            productVersion.setComponents(productComponentVersions);
        }

        return productVersionRepository.save(productVersion);
    }

    public BuildStep processBuildStep(BuildStepMessage buildStepMessage) throws Exception {

        log.info("Received buildStep update notification: {}", buildStepMessage);

        // These are all part of the lookup identifier
        if (StringUtils.isEmpty(buildStepMessage.getBuildUrl())) {
            throw new Exception("No build URL specified");
        }
        if (StringUtils.isEmpty(buildStepMessage.getBuildStartTime())) {
            throw new Exception("No build start time specified");
        }
        if (StringUtils.isEmpty(buildStepMessage.getStep())) {
            throw new Exception("No step specified");
        }

        // Find potentially existing buildStep
        BuildStep buildStep = new BuildStep();
        buildStep.setBuild(getBuild(buildStepMessage.getBuildUrl(), buildStepMessage.getBuildStartTime()));
        buildStep.setStep(buildStepMessage.getStep());
        List<BuildStep> existingBuildSteps = buildStepRepository.findAll(Example.of(buildStep));
        if (!existingBuildSteps.isEmpty()) {
            buildStep = existingBuildSteps.get(0);
        }

        // Update buildStep properties
        if (buildStepMessage.getStartTime() != null) {
            buildStep.setStartTime(buildStepMessage.getStartTime());
        }
        if (buildStepMessage.getEndTime() != null) {
            buildStep.setEndTime(buildStepMessage.getEndTime());
        }
        if (buildStepMessage.getStatus() != null) {
            buildStep.setStatus(buildStepMessage.getStatus());
        }
        return buildStepRepository.save(buildStep);
    }

    public ProductComponent processProductComponent(ProductComponentMessage productComponentMessage) throws Exception {

        log.info("Received product component update notification: {}", productComponentMessage);

        // These are all part of the lookup identifier
        if (StringUtils.isEmpty(productComponentMessage.getShortName())) {
            throw new Exception("No short name specified");
        }

        // Find potentially existing product component
        ProductComponent component = getComponent(productComponentMessage.getShortName());

        // Update product component properties
        if (productComponentMessage.getFullName() !=null) {
            component.setFullName(productComponentMessage.getFullName());
        }
        if (productComponentMessage.getDescription() !=null) {
            component.setDescription(productComponentMessage.getDescription());
        }
        return productComponentRepository.save(component);
    }

    public ProductComponentVersion processProductComponentVersionMessage(ProductComponentVersionMessage productComponentVersionMessage) throws Exception {

        log.info("Received product component version update notification: {}", productComponentVersionMessage);

        // These are all part of the lookup identifier
        if (StringUtils.isEmpty(productComponentVersionMessage.getComponentShortName())) {
            throw new Exception("No short name specified");
        }
        if (StringUtils.isEmpty(productComponentVersionMessage.getComponentVersion())) {
            throw new Exception("No version specified");
        }

        // Find potentially existing product component version
        ProductComponentVersion componentVersion = getProductComponentVersion(
            productComponentVersionMessage.getComponentShortName(),
            productComponentVersionMessage.getComponentVersion());

        // Update product component version properties
        if (productComponentVersionMessage.getBuildUrl() != null) {
            componentVersion.setBuildUrl(productComponentVersionMessage.getBuildUrl());
        }
        if (productComponentVersionMessage.getBuildStatus() != null) {
            componentVersion.setBuildStatus(productComponentVersionMessage.getBuildStatus());
        }
        if (productComponentVersionMessage.getBuildStartTime() != null) {
            componentVersion.setStartTime(productComponentVersionMessage.getBuildStartTime());
        }
        if (productComponentVersionMessage.getBuildEndTime() != null) {
            componentVersion.setEndTime(productComponentVersionMessage.getBuildEndTime());
        }
        if (productComponentVersionMessage.getComponentReleaseNotes() != null || productComponentVersionMessage.getComponentReleaseNotesBase64() != null) {
            componentVersion.setReleaseNotes(
                ReleaseNotesParser.toReleaseNotes(
                    productComponentVersionMessage.getComponentReleaseNotes(),
                    productComponentVersionMessage.getComponentReleaseNotesBase64()));
        }

        return productComponentVersionRepository.save(componentVersion);
    }

    public BuildStatistic processBuildStat(BuildStatMessage buildStatMessage) throws Exception {

        log.info("Received buildStat update notification: {}", buildStatMessage);

        // These are all part of the lookup identifier
        if (StringUtils.isEmpty(buildStatMessage.getBuildUrl())) {
            throw new Exception("No build URL specified");
        }
        if (StringUtils.isEmpty(buildStatMessage.getBuildStartTime())) {
            throw new Exception("No start time specified");
        }
        if (StringUtils.isEmpty(buildStatMessage.getKey())) {
            throw new Exception("No build key specified");
        }

        // Find potentially existing buildStep
        BuildStatistic buildStat = new BuildStatistic();
        buildStat.setBuild(getBuild(buildStatMessage.getBuildUrl(), buildStatMessage.getBuildStartTime()));
        buildStat.setKey(buildStatMessage.getKey());
        List<BuildStatistic> existingBuildStats = buildStatRepository.findAll(Example.of(buildStat));
        if (!existingBuildStats.isEmpty()) {
            buildStat = existingBuildStats.get(0);
        }

        // Update buildStat properties
        buildStat.setValue(buildStatMessage.getValue());
        return buildStatRepository.save(buildStat);
    }

    private ProductComponentVersion getProductComponentVersion(String productShortName, String version) {
        ProductComponentVersion productComponentVersion = new ProductComponentVersion();
        productComponentVersion.setComponent(getComponent(productShortName));
        productComponentVersion.setVersion(version);
        List<ProductComponentVersion> existingProductComponentVersions = productComponentVersionRepository.findAll(Example.of(productComponentVersion));
        if (existingProductComponentVersions.isEmpty()) {
            // Create a product component on the fly
            productComponentVersion.setBuildStatus(BuildStatus.SUCCEEDED);
            productComponentVersion.setStartTime(Instant.now());
            return productComponentVersionRepository.save(productComponentVersion);
        } else {
            return existingProductComponentVersions.get(0);
        }
    }

    private ProductComponent getComponent(String productShortName) {
        // Find productComponent
        ProductComponent productComponent = new ProductComponent();
        productComponent.setShortName(productShortName);
        List<ProductComponent> existingProductComponents = productComponentRepository.findAll(Example.of(productComponent));
        if (existingProductComponents.isEmpty()) {
            // Create a product on the fly
            productComponent.setFullName(productShortName);
            productComponent.setDescription(productShortName);
            return productComponentRepository.save(productComponent);
        } else {
            return existingProductComponents.get(0);
        }
    }

    private Build getBuild(String buildUrl, Instant buildStartTime) {
        Build build = new Build();
        build.setBuildUrl(buildUrl);
        build.setStartTime(buildStartTime);
        List<Build> existingBuilds = buildRepository.findAll(Example.of(build));
        if (existingBuilds.isEmpty()) {
            build.setStatus(BuildStatus.IN_PROGRESS);
            return buildRepository.save(build);
        } else {
            return existingBuilds.get(0);
        }
    }

    private Environment getEnvironment(String environmentShortName) {
        Environment environment = new Environment();
        environment.setShortName(environmentShortName);
        List<Environment> existingEnvironments = environmentRepository.findAll(Example.of(environment));
        if (existingEnvironments.isEmpty()) {
            // Create an environment on the fly
            environment.setDescription(environmentShortName);
            environment.setFullName(environmentShortName);
            environment.setEnvironmentGroup(getNewEnvironmentGroup());
            environment.setSortOrder(99);
            return environmentRepository.save(environment);
        } else {
            return existingEnvironments.get(0);
        }
    }

    private EnvironmentGroup getNewEnvironmentGroup() {
        EnvironmentGroup environmentGroup = new EnvironmentGroup();
        environmentGroup.setShortName(NEW_ENVIRONMENTS_GROUP_SHORTNAME);
        List<EnvironmentGroup> existingEnvironmentGroups = environmentGroupRepository.findAll(Example.of(environmentGroup));
        if (existingEnvironmentGroups.isEmpty()) {
            // Create an environment on the fly
            environmentGroup.setFullName(NEW_ENVIRONMENTS_GROUP_FULL_NAME);
            environmentGroup.setDescription(NEW_ENVIRONMENTS_GROUP_DESCRIPTION);
            environmentGroup.setHidden(true);
            environmentGroup.setSortOrder(99);
            return environmentGroupRepository.save(environmentGroup);
        } else {
            return existingEnvironmentGroups.get(0);
        }
    }

    private Product getProduct(String productShortname) {
        // Find product
        Product product = new Product();
        product.setShortName(productShortname);
        List<Product> existingProducts = productRepository.findAll(Example.of(product));
        if (existingProducts.isEmpty()) {
            // Create a product on the fly
            product.setDescription(productShortname);
            product.setFullName(productShortname);
            return productRepository.save(product);
        } else {
            return existingProducts.get(0);
        }
    }

    private ProductVersion getProductVersion(String productShortname, String productVersionVersion) {

        // Find product version
        ProductVersion productVersion = new ProductVersion();
        productVersion.setProduct(getProduct(productShortname));
        productVersion.setVersion(productVersionVersion);
        List<ProductVersion> existingProductVersions = productVersionRepository.findAll(Example.of(productVersion));
        if (existingProductVersions.isEmpty()) {
            // Create a product version on the fly
            return productVersionRepository.save(productVersion);
        } else {
            return existingProductVersions.get(0);
        }
    }
}
