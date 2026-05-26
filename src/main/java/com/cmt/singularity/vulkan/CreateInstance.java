// <editor-fold desc="The MIT License" defaultstate="collapsed">
/*
 * The MIT License
 *
 * Copyright 2025 Cryomoretan GmbH.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
//</editor-fold>
package com.cmt.singularity.vulkan;

import com.cmt.singularity.assertion.Assert;
import com.cmt.singularity.compute.Task;
import static com.cmt.singularity.vulkan.VKUtil.*;
import de.s42.log.LogManager;
import de.s42.log.Logger;
import java.nio.ByteBuffer;
import org.lwjgl.PointerBuffer;
import static org.lwjgl.glfw.GLFWVulkan.glfwGetRequiredInstanceExtensions;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.EXTDebugReport.VK_EXT_DEBUG_REPORT_EXTENSION_NAME;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;
import static org.lwjgl.vulkan.VK10.vkCreateInstance;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkApplicationInfo;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkInstanceCreateInfo;

/**
 * Creates a vulkan instance
 *
 * @author Benjamin Schiller
 */
public class CreateInstance implements Task
{

	@SuppressWarnings("unused")
	private final static Logger log = LogManager.getLogger(CreateInstance.class.getName());

	@SuppressWarnings("unused")
	private final static Assert assertion = Assert.getAssert(CreateInstance.class.getName());

	protected PointerBuffer requiredExtensions;
	protected boolean enableDebugLayers = true;
	protected VkInstance instance;

	@Override
	public void execute()
	{
		requiredExtensions = glfwGetRequiredInstanceExtensions();

		assertion.assertNotNull(requiredExtensions, "Failed to find list of required Vulkan extensions");

		VkApplicationInfo appInfo = VkApplicationInfo.calloc()
			.sType$Default()
			.apiVersion(VK14.VK_API_VERSION_1_4);
		PointerBuffer ppEnabledExtensionNames = memAllocPointer(requiredExtensions.remaining() + 1);
		ppEnabledExtensionNames.put(requiredExtensions);
		ByteBuffer VK_EXT_DEBUG_REPORT_EXTENSION = memUTF8(VK_EXT_DEBUG_REPORT_EXTENSION_NAME);
		ppEnabledExtensionNames.put(VK_EXT_DEBUG_REPORT_EXTENSION);
		ppEnabledExtensionNames.flip();
		PointerBuffer ppEnabledLayerNames = enableDebugLayers ? allocateLayerBuffer(DEBUG_LAYERS) : null;
		VkInstanceCreateInfo pCreateInfo = VkInstanceCreateInfo.calloc()
			.sType$Default()
			.pApplicationInfo(appInfo)
			.ppEnabledExtensionNames(ppEnabledExtensionNames)
			.ppEnabledLayerNames(ppEnabledLayerNames);
		PointerBuffer pInstance = memAllocPointer(1);
		int err = vkCreateInstance(pCreateInfo, null, pInstance);
		long instanceId = pInstance.get(0);
		memFree(pInstance);
		if (err != VK_SUCCESS) {
			throw new AssertionError("Failed to create VkInstance: " + translateVulkanResult(err));
		}
		instance = new VkInstance(instanceId, pCreateInfo);
		pCreateInfo.free();
		if (ppEnabledLayerNames != null) {
			memFree(ppEnabledLayerNames);
		}
		memFree(VK_EXT_DEBUG_REPORT_EXTENSION);
		memFree(ppEnabledExtensionNames);
		memFree(appInfo.pApplicationName());
		memFree(appInfo.pEngineName());
		appInfo.free();
	}
}
