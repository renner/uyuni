/**
 * Copyright (c) 2017 SUSE LLC
 *
 * This software is licensed to you under the GNU General Public License,
 * version 2 (GPLv2). There is NO WARRANTY for this software, express or
 * implied, including the implied warranties of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. You should have received a copy of GPLv2
 * along with this software; if not, see
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt.
 *
 * Red Hat trademarks are not licensed under GPLv2. No permission is
 * granted to use or replicate Red Hat trademarks that are incorporated
 * in this software or its documentation.
 */

package com.suse.manager.webui.services.test;

import com.redhat.rhn.domain.config.ConfigChannel;
import com.redhat.rhn.testing.BaseTestCaseWithUser;
import com.suse.manager.webui.services.ConfigChannelSaltManager;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Test for {@link ConfigChannelSaltManagerTest}
 * - tests file structure on the disk generated by ConfigChannelSaltManager.
 */
public class ConfigChannelSaltManagerFileSystemTest extends BaseTestCaseWithUser {

    /** the instance used for testing **/
    private ConfigChannelSaltManager manager;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.manager = ConfigChannelSaltManager.getInstance();
        manager.setBaseDirPath(tmpSaltRoot.toAbsolutePath().toString());
    }

    /**
     * Tests storing a configuration file on the disk and contents of the init.sls.
     *
     * @throws Exception - if anything goes wrong
     */
    public void testStoreConfigFile() throws Exception {
        ConfigChannel channel = ConfigChannelSaltManagerTestUtils.createTestChannel(user);
        ConfigChannelSaltManagerTestUtils.addFileToChannel(channel);

        manager.generateConfigChannelFiles(channel);

        File generatedFile = getGeneratedFile(channel,
                channel.getConfigFiles().first().getConfigFileName().getPath());
        assertTrue(generatedFile.exists());
        assertTrue(generatedFile.isFile());
        assertEquals("aoeuaoeuao", FileUtils.readFileToString(generatedFile));

        File initSlsFile = getGeneratedFile(channel, "init.sls");
        initSlsAssertions(initSlsFile,
                "file.managed",
                generatedFile.getName());
    }

    /**
     * Tests storing a configuration directory on the disk and contents of the init.sls.
     *
     * @throws Exception - if anything goes wrong
     */
    public void testStoreConfigDir() throws Exception {
        ConfigChannel channel = ConfigChannelSaltManagerTestUtils.createTestChannel(user);
        ConfigChannelSaltManagerTestUtils.addDirToChannel(channel);

        manager.generateConfigChannelFiles(channel);

        String configFilePath = channel.getConfigFiles().first().getConfigFileName().getPath();
        File generatedFile = getGeneratedFile(channel,
                configFilePath);
        // for configuration directories we don't generate anything except state in init.sls
        assertFalse(generatedFile.exists());

        File initSlsFile = getGeneratedFile(channel, "init.sls");
        initSlsAssertions(initSlsFile,
                "file.directory",
                configFilePath);
    }

    /**
     * Tests storing a configuration symlink on the disk and contents of the init.sls.
     *
     * @throws Exception - if anything goes wrong
     */
    public void testStoreConfigSymlink() throws Exception {
        ConfigChannel channel = ConfigChannelSaltManagerTestUtils.createTestChannel(user);
        ConfigChannelSaltManagerTestUtils.addSymlinkToChannel(channel);

        manager.generateConfigChannelFiles(channel);

        File generatedFile = getGeneratedFile(channel,
                channel.getConfigFiles().first().getConfigFileName().getPath());
        // for symlinks we don't generate anything except state in init.sls
        assertFalse(generatedFile.exists());

        File initSlsFile = getGeneratedFile(channel, "init.sls");
        initSlsAssertions(initSlsFile,
                "file.symlink",
                generatedFile.getName());
    }

    private File getGeneratedFile(ConfigChannel channel, String filePathInChannel) {
        return Paths.get(tmpSaltRoot.toAbsolutePath().toString(),
                manager.getOrgNamespace(channel.getOrgId()),
                channel.getLabel(),
                filePathInChannel)
                .toFile();
    }

    /**
     * Common assertions on a init.sls file.
     *
     * @param initSlsFile the init.sls File object
     * @param contentChunks optional chunks of content of the init.sls file
     * @throws IOException if anything goes wrong
     */
    private static void initSlsAssertions(File initSlsFile, String ... contentChunks)
            throws IOException {
        assertTrue(initSlsFile.exists());
        assertTrue(initSlsFile.isFile());
        String initSlsContents = FileUtils.readFileToString(initSlsFile);
        for (String contentChunk : contentChunks) {
            assertContains(initSlsContents, contentChunk);
        }
    }
}