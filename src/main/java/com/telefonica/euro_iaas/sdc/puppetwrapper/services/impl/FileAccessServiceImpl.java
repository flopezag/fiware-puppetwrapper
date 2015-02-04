/**
 * Copyright 2014 Telefonica Investigación y Desarrollo, S.A.U <br>
 * This file is part of FI-WARE project.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License.
 * </p>
 * <p>
 * You may obtain a copy of the License at:<br>
 * <br>
 * http://www.apache.org/licenses/LICENSE-2.0
 * </p>
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * </p>
 * <p>
 * See the License for the specific language governing permissions and limitations under the License.
 * </p>
 * <p>
 * For those usages not covered by the Apache version 2.0 License please contact with opensource@tid.es
 * </p>
 */

package com.telefonica.euro_iaas.sdc.puppetwrapper.services.impl;

import static java.text.MessageFormat.format;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.NoSuchElementException;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.telefonica.euro_iaas.sdc.puppetwrapper.data.Node;
import com.telefonica.euro_iaas.sdc.puppetwrapper.data.Software;
import com.telefonica.euro_iaas.sdc.puppetwrapper.services.ActionsService;
import com.telefonica.euro_iaas.sdc.puppetwrapper.services.CatalogManager;
import com.telefonica.euro_iaas.sdc.puppetwrapper.services.FileAccessService;

@Service("fileAccessService")
public class FileAccessServiceImpl implements FileAccessService {

    private static final Logger LOG = LoggerFactory.getLogger(FileAccessServiceImpl.class);

    @Resource
    protected CatalogManager catalogManager;

    @Resource
    protected ActionsService actionService;

    private String defaultManifestsPath;

    private String modulesCodeDownloadPath;

    private String defaultHieraPath;

    @Resource
    protected ProcessBuilderFactory processBuilderFactory;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.telefonica.euro_iaas.sdc.puppetwrapper.services.FileAccessService
     * #generateManifestFile(java.lang.String)
     */
    public Node generateManifestFile(String nodeName) throws IOException {

        LOG.info("creating Manifest file for node: " + nodeName);

        Node node = catalogManager.getNode(nodeName);

        String fileContent = catalogManager.generateManifestStr(nodeName);
        String hieraFileContent = node.generateHieraFileStr();
        String path = defaultManifestsPath + node.getGroupName();
        String hieraPath = defaultHieraPath;

        try {

            File f = new File(path);
            f.mkdirs();
            f.createNewFile();
        } catch (IOException ex) {
            LOG.debug("Error creating manifest paths and pp file", ex);
            throw new IOException("Error creating manifest paths and pp file");
        }

        try {
            FileWriter fw = new FileWriter(path + "/" + node.getId() + ".pp", false);
            fw.write(fileContent);
            fw.close();
        } catch (IOException ex) {
            LOG.debug("Error creating manifest paths and pp file", ex);
            throw new IOException("Error creating manifest paths and pp file");
        }

        LOG.debug("Manifest file created");

        if (hasAttributes(node)) {
            LOG.info("Creating hiera file");
            try {

                File f = new File(hieraPath);
                f.mkdirs();
                f.createNewFile();
            } catch (IOException ex) {
                LOG.debug("Error creating hiera path and file", ex);
                throw new IOException("Error creating hiera path and file");
            }

            try {
                FileWriter fw = new FileWriter(hieraPath + actionService.getRealNodeName(node.getId()) + ".yaml", false);
                fw.write(hieraFileContent);
                fw.close();
            } catch (IOException ex) {
                LOG.debug("Error creating hiera path and file", ex);
                throw new IOException("Error creating hiera path and file");
            }

            LOG.debug("Hiera file created");
        } else {
            LOG.info("Node: " + node.getId() + " has no hiera attributes in any software");
        }
        node.setManifestGenerated(true);
        return node;

    }

    private boolean hasAttributes(Node node) {
        boolean result = false;
        for (Software s : node.getSoftwareList()) {
            if (s.getAttributes() != null && s.getAttributes().size() > 0) {
                result = true;
                break;
            }
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.telefonica.euro_iaas.sdc.puppetwrapper.services.FileAccessService
     * #generateSiteFile()
     */
    public void generateSiteFile() throws IOException {

        LOG.info("Generate site.pp");

        String fileContent = catalogManager.generateSiteStr();

        LOG.debug("site content: " + fileContent);
        LOG.debug("defaultManifestsPath: " + defaultManifestsPath);

        try {
            PrintWriter writer = new PrintWriter(defaultManifestsPath + "site.pp", "UTF-8");
            writer.println(fileContent);
            writer.close();
        } catch (IOException ex) {
            LOG.debug("Error creating site.pp file", ex);
            throw new IOException("Error creating site.pp file");
        }

        LOG.debug("Site.pp file created");
    }

    @Value(value = "${defaultManifestsPath}")
    public void setDefaultManifestsPath(String defaultManifestsPath) {
        this.defaultManifestsPath = defaultManifestsPath;
    }

    @Value(value = "${modulesCodeDownloadPath}")
    public void setDefaultModulesPath(String modulesCodeDownloadPath) {
        this.modulesCodeDownloadPath = modulesCodeDownloadPath;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.telefonica.euro_iaas.sdc.puppetwrapper.services.FileAccessService
     * #deleteNodeFiles(java.lang.String)
     */
    public void deleteNodeFiles(String nodeName) throws IOException {

        try {

            Node node = catalogManager.getNode(nodeName);

            String path = defaultManifestsPath + node.getGroupName();

            File file = new File(path + "/" + node.getId() + ".pp");

            if (!file.delete()) {
                LOG.info(format("File {0} could not be deleted. Did it exist?", path + "/" + node.getId() + ".pp"));
            } else {
                LOG.info(format("File {0} deleted.", path + "/" + node.getId() + ".pp"));
            }

            if (catalogManager.isLastGroupNode(node.getGroupName())) {
                deleteGoupFolder(node.getGroupName());
            }

            File fileHiera = new File(defaultHieraPath + "/" + actionService.getRealNodeName(nodeName) + ".yaml");

            if (!fileHiera.delete()) {
                LOG.info(format("File {0} could not be deleted. Did it exist?",
                        defaultHieraPath + "/" + actionService.getRealNodeName(nodeName) + ".yaml"));
            } else {
                LOG.info(format("File {0} deleted.", defaultHieraPath + actionService.getRealNodeName(nodeName)
                        + ".yaml"));
            }

        } catch (NoSuchElementException e) {
            LOG.info(format("Node {0} was not registered in puppet master", nodeName));
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.telefonica.euro_iaas.sdc.puppetwrapper.services.FileAccessService
     * #deleteGoupFolder(java.lang.String)
     */
    public void deleteGoupFolder(String groupName) throws IOException {

        File path = new File(defaultManifestsPath + groupName);

        FileUtils.deleteDirectory(path);

        LOG.info(format("Folder {0} deleted.", path + "/" + groupName));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.telefonica.euro_iaas.sdc.puppetwrapper.services.FileAccessService
     * #deleteModuleFiles(java.lang.String)
     */
    public void deleteModuleFiles(String moduleName) throws IOException {

        File file = new File(modulesCodeDownloadPath + moduleName);

        FileUtils.deleteDirectory(file);

        LOG.info(format("File {0} could not be deleted. Did it exist?", modulesCodeDownloadPath + "/" + moduleName
                + ".pp"));

    }

    @Value(value = "${defaultHieraPath}")
    public void setDefaultHieraPath(String defaultHieraPath) {
        this.defaultHieraPath = defaultHieraPath;
    }

}
