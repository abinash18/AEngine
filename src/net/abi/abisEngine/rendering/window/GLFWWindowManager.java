/*******************************************************************************
 * Copyright 2020 Abinash Singh | ABI INC.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.abi.abisEngine.rendering.window;

import static net.abi.abisEngine.rendering.window.GLFWWindow.NULL;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.opengl.GL;

import net.abi.abisEngine.core.CoreEngine;
import net.abi.abisEngine.handlers.logging.LogManager;
import net.abi.abisEngine.handlers.logging.Logger;
import net.abi.abisEngine.rendering.asset.AssetStore;
import net.abi.abisEngine.rendering.pipeline.RenderingEngine;
import net.abi.abisEngine.rendering.shader.compiler.AEShaderCompiler;
import net.abi.abisEngine.util.Expendable;
import net.abi.abisEngine.util.exceptions.AEGLFWWindowInitializationException;

/**
 * GLFW Implementation Of Window. This Implementation supports shared contexts.
 * 
 * @author abinash
 */
public class GLFWWindowManager implements Expendable {
	/*
	 * NOTE to self: After about 6 windows it will start having a visible impact on
	 * performance, maybe render clusters of windows on different threads at a time
	 * if a context contains 6 or more windows, or even render each window on a
	 * different thread, or even render windows on a different thread than the core
	 * engine.
	 */

	private static final Logger logger = LogManager.getLogger(GLFWWindowManager.class.getName());

	/*
	 * Bucket list of Window contexts, which are mapped to their child windows.
	 */
	private Map<GLFWWindowContext, CopyOnWriteArrayList<GLFWWindow>> windows;

	/*
	 * Core engine really has no use in this class for now since we have a separate
	 * rendering engine for each context.
	 */
	private CoreEngine coreEngine;
	/* If this is set to true the engine will terminate. */
	private static boolean engineStopFlag = false;

	/**
	 * Class defining GLContext. This is the key to all mapped windows it contains
	 * the parent context that all windows that are mapped to it will share. The
	 * first window added to the map will have it's handle stored as the context
	 * value.
	 * 
	 * @author abinash
	 *
	 */
	public class GLFWWindowContext {
		/* Handle of the stored context */
		public long context = NULL;
		/* Name of the window which this context belongs to. */
		public String name;

		/*
		 * The Rendering Engine needs to be referenced too, since my engine yet dose not
		 * support independent engines for rendering, such as 2d and 3d contexts, so
		 * each window will either have its own engine if it dose not share context. or
		 * it will inherit an engine from the parent window's GLContext. So if you only
		 * want to render 2d on a window you can do so, thus saving resources, by not
		 * allocating unnecessary openGL context for 3d rendering, if there are multiple
		 * windows which you know will all render in either 2d or 3d just let them share
		 * context with one window which already has the 2d or 3d rendering engine
		 * allocated to it.
		 */
		public RenderingEngine renderEngine;

		public AssetStore store;

		public GLFWWindowContext(String name, long context, RenderingEngine rndEng) {
			this.context = context;
			this.name = name;
			this.renderEngine = rndEng;
			this.store = new AssetStore();
		}

		public GLFWWindowContext(String name, long context, RenderingEngine rndEng, AssetStore store) {
			this.context = context;
			this.name = name;
			this.renderEngine = rndEng;
			this.store = store;
		}

	}

	public GLFWWindowManager(CoreEngine core) {
		this.windows = new ConcurrentHashMap<GLFWWindowContext, CopyOnWriteArrayList<GLFWWindow>>();

		this.setCoreEngine(core);
	}

	/**
	 * Sets the current context to the one provided.
	 * 
	 * @param context the context to set to.
	 * @return Returns true if the context has been set successfully. Otherwise
	 *         false is returned if the context invalid.
	 */
	private void setContext(GLFWWindowContext context) {
		glfwMakeContextCurrent(context.context);
	}

	private void setContext(long context) {
		glfwMakeContextCurrent(context);
	}

	/**
	 * Renders all windows stored in the bucket list, and the windows which inherit
	 * their context.
	 */
	public void render() {

		/*
		 * Since using a for loop iterating over the hash map will cause a concurrent
		 * modification exception, we use the iterator which iterates over the entris in
		 * the map.
		 */
		for (Iterator<Map.Entry<GLFWWindowContext, CopyOnWriteArrayList<GLFWWindow>>> entries = windows.entrySet()
				.iterator(); entries.hasNext();) {
			/*
			 * Current Entry, we cannot call next() on the iterator every time we need the
			 * entry because then it will skip to the next entry in line.
			 */
			Map.Entry<GLFWWindowContext, CopyOnWriteArrayList<GLFWWindow>> entry = entries.next();

			/* Iterates over the windows which share the context. */
			for (Iterator<GLFWWindow> subEntrys = entry.getValue().iterator(); subEntrys.hasNext();) {
				GLFWWindow wnd = subEntrys.next();
				if (wnd.getGlfw_Handle() == NULL) {
					continue;
				}

				/*
				 * TODO: Add Background Render optimization by breaking out of the loop if the
				 * window is not in focus or iconified.
				 */
				/*
				 * if (!wnd.isFocused()) { break; }
				 */

				/*
				 * Sets the current context to the one provided in the entry so we can render
				 * that context.
				 */
				setContext(wnd.getGlfw_Handle());
				/*
				 * This will set the capabilities to render the current context on the window.
				 */
				GL.setCapabilities(wnd.getCapabilities());
				/* We are ready to render the context now. */
				wnd.render();
			}

		}
		setContext(NULL);
		/*
		 * We must set the capabilities to null, because we dont want any operations
		 * done any where else to affect the last window we just rendered.
		 */
		GL.setCapabilities(null);
	}

	/**
	 * Returns either true or false if the window has requested close or not,
	 * respectively.
	 */
	private boolean checkClose(GLFWWindow wnd) {
		if (wnd.isCloseRequested()) {
			return true;
		}
		return false;
	}

	public void input(float delta) {
		/* If there are no contexts left stop the engine. */
		if (windows.size() == 0) {
			raiseStopFlag();
			return;
		}

		/* Iterates over everything contained in the hash map to */
		for (Iterator<Map.Entry<GLFWWindowContext, CopyOnWriteArrayList<GLFWWindow>>> entries = windows.entrySet()
				.iterator(); entries.hasNext();) {

			Map.Entry<GLFWWindowContext, CopyOnWriteArrayList<GLFWWindow>> entry = entries.next();

			for (int i = 0; i < entry.getValue().size(); i++) {
				GLFWWindow wnd = entry.getValue().get(i);

				/* Check if the window is destroyed already or not */
				if (wnd.getGlfw_Handle() != NULL) {
					if (entry.getKey().context == NULL) {
						entry.getKey().context = wnd.getGlfw_Handle();
					}
					/*
					 * Sets the current context to the one provided in the entry so we can render
					 * that context.
					 */
					setContext(wnd.getGlfw_Handle());
					GL.setCapabilities(wnd.getCapabilities());
					/*
					 * These operations are done in here because I call input first core engine. It
					 * would not make sense and have terrible consequences if it was done in a
					 * method called later.
					 */
					if (checkClose(wnd)) {
						/* Dispose the window destroying its context and capability's. */
						wnd.dispose();
						logger.debug("Window Destroyed. " + wnd.getWindowName());

						/*
						 * Break, so we don't cause a null pointer exception if we try updating the
						 * input for a empty window and one we have removed from the array list.
						 */
						continue;
					}
					/* Updates the input for the window. */
					wnd.input(delta);
				} else { /* If so then delete the entry. */
					/* Remove the window from the list. */
					entry.getValue().remove(i);
				}
			}
		}
		/*
		 * Resets the context and capabilities to NULL so no action called on the
		 * current thread after the loop will have effect.
		 */
		setContext(NULL);
		GL.setCapabilities(null);
	}

	public void update(float delta) {
		for (Iterator<Map.Entry<GLFWWindowContext, CopyOnWriteArrayList<GLFWWindow>>> entries = windows.entrySet()
				.iterator(); entries.hasNext();) {

			Map.Entry<GLFWWindowContext, CopyOnWriteArrayList<GLFWWindow>> entry = entries.next();

			for (Iterator<GLFWWindow> subEntrys = entry.getValue().iterator(); subEntrys.hasNext();) {
				GLFWWindow wnd = subEntrys.next();
				/*
				 * Sets the current context to the one provided in the entry so we can render
				 * that context.
				 */
				setContext(wnd.getGlfw_Handle());
				GL.setCapabilities(wnd.getCapabilities());
				wnd.update(delta);
			}

		}
		setContext(NULL);
		GL.setCapabilities(null);

	}

	/**
	 * Gets the window specified.
	 * 
	 * @param name
	 * @return
	 */
	public GLFWWindow getGLFWWindow(String name) {
		for (Iterator<Map.Entry<GLFWWindowContext, CopyOnWriteArrayList<GLFWWindow>>> entries = windows.entrySet()
				.iterator(); entries.hasNext();) {
			Map.Entry<GLFWWindowContext, CopyOnWriteArrayList<GLFWWindow>> entry = entries.next();
			for (Iterator<GLFWWindow> subEntrys = entry.getValue().iterator(); subEntrys.hasNext();) {
				GLFWWindow wnd = subEntrys.next();
				if (name.equals(wnd.getWindowName())) {
					return wnd;
				}
			}
		}
		logger.debug("No Window Found With Name: " + name);
		return null;
	}

	/**
	 * Finds the specific context in the shared contexts bucket list.
	 * 
	 * @param name Name of the context to search for.
	 * @return Returns null if no context was found with the specified name, else
	 *         returns the entry in the sharedContexts Bucket list.
	 */
	public Map.Entry<GLFWWindowContext, CopyOnWriteArrayList<GLFWWindow>> getContext(String name) {
		for (Iterator<Map.Entry<GLFWWindowContext, CopyOnWriteArrayList<GLFWWindow>>> entries = windows.entrySet()
				.iterator(); entries.hasNext();) {
			Map.Entry<GLFWWindowContext, CopyOnWriteArrayList<GLFWWindow>> entry = entries.next();
			if (entry.getKey().name.equals(name)) {
				return entry;
			}
		}
		logger.debug("No Window Found With Name: " + name);
		return null;
	}

	private void setCoreEngine(CoreEngine creng) {
		this.coreEngine = creng;
	}

	public void init(CoreEngine coreEngine) {
		setCoreEngine(coreEngine);
	}

	/**
	 * Destroys all active contexts. This is executed before the engine terminates.
	 * Throws Exception.
	 */
	public void disposeAllWindows() throws Exception {
		for (Iterator<Map.Entry<GLFWWindowContext, CopyOnWriteArrayList<GLFWWindow>>> entries = windows.entrySet()
				.iterator(); entries.hasNext();) {
			Map.Entry<GLFWWindowContext, CopyOnWriteArrayList<GLFWWindow>> entry = entries.next();
			for (GLFWWindow subEntrys : entry.getValue()) {
				GLFWWindow wnd = subEntrys;
				wnd.dispose();
				entry.getValue().remove(wnd);
				break;
			}
			entries.remove();
		}
	}

	/**
	 * Destroys all windows in a active context. Throws Exception.
	 */
	public void disposeAllWindows(GLFWWindowContext context) throws Exception {
		for (Iterator<Map.Entry<GLFWWindowContext, CopyOnWriteArrayList<GLFWWindow>>> entries = windows.entrySet()
				.iterator(); entries.hasNext();) {
			Map.Entry<GLFWWindowContext, CopyOnWriteArrayList<GLFWWindow>> entry = entries.next();
			/*
			 * If the context being iterated over is the same as the one provided then
			 * execute.
			 */
			if (entry.getKey().name.equals(context.name)) {
				for (GLFWWindow subEntrys : entry.getValue()) {
					GLFWWindow wnd = subEntrys;
					wnd.dispose();
					entry.getValue().remove(wnd);
					break;
				}
				/* Remove the row because the context related to that is destroyed. */
				entries.remove();
			}
			break;
		}
	}

	/**
	 * Returns a GLContext, if no GLContext is found null is returned.
	 * 
	 * @param context
	 * @return
	 */
	public GLFWWindowContext findContext(String name) {
		for (Iterator<Map.Entry<GLFWWindowContext, CopyOnWriteArrayList<GLFWWindow>>> entries = windows.entrySet()
				.iterator(); entries.hasNext();) {
			Map.Entry<GLFWWindowContext, CopyOnWriteArrayList<GLFWWindow>> entry = entries.next();

			if (entry.getKey().name.equals(name)) {
				return entry.getKey();
			}
		}
		return null;
	}

	public GLFWWindowContext findContext(long context) {
		for (Iterator<Map.Entry<GLFWWindowContext, CopyOnWriteArrayList<GLFWWindow>>> entries = windows.entrySet()
				.iterator(); entries.hasNext();) {
			Map.Entry<GLFWWindowContext, CopyOnWriteArrayList<GLFWWindow>> entry = entries.next();

			if (entry.getKey().context == context) {
				return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * Opens the window using the create() method in GLFWWindow and adds it to the
	 * bucket list. With Shared GLContext. Using the name, if the context with that
	 * name exists already then it adds the window to that shared context, if not
	 * then creates a new context with that name and sets the context handle to the
	 * window handle.
	 * 
	 * @param wnd
	 * @throws Exception
	 */
	public void openWindow(GLFWWindow wnd, String sharedContextName) throws AEGLFWWindowInitializationException {
		openWindow(wnd, getContext(sharedContextName).getKey().context);
	}

	public void openWindow(GLFWWindow wnd, long sharedContext) throws AEGLFWWindowInitializationException {

		if (wnd.getMonitor() == NULL) {
			logger.info("Monitor Provided Is NULL, Defaulting To Primary Monitor.");
		}

		GLFWWindowContext tempContext;
		if ((tempContext = findContext(sharedContext)) != null) {
			CopyOnWriteArrayList<GLFWWindow> wnds = windows.get(tempContext);
			wnd.setRenderEngine(tempContext.renderEngine);
			wnds.add(wnd);
			wnd.setAssetStore(tempContext.store);
			wnd.create(tempContext.context, tempContext.renderEngine);
			return;
		}
		/*Weird but OK.*/
		throw new AEGLFWWindowInitializationException(
				"New window could not be initialized. Of desired shared context: " + sharedContext + "", wnd);
	}

	/**
	 * Opens the window using the create() method in GLFWWindow and adds it to the
	 * bucket list. Without Shared GLContext. And creates a entry in the shared
	 * context bucket list using the name of the window as context name and handle
	 * as context handle, and if in the future you wish to add a window to this
	 * context use the window's name as the sharedContextName.
	 */
	public void openWindow(GLFWWindow wnd) throws AEGLFWWindowInitializationException {
		if (wnd.getMonitor() == NULL) {
			logger.info("Monitor Provided Is NULL, Defaulting To Primary Monitor.");
		}

		if (findContext(wnd.getWindowName()) != null) {
			throw new AEGLFWWindowInitializationException("Window Already Exits With The Same Name.");
		}

		GLFWWindowContext context = new GLFWWindowContext(wnd.getWindowName(), wnd.getGlfw_Handle(),
				wnd.getRenderEngine());
		AssetStore store = context.store;
		CopyOnWriteArrayList<GLFWWindow> wnds = new CopyOnWriteArrayList<GLFWWindow>();
		wnd.setAssetStore(store);
		wnds.add(wnd);
		windows.put(context, wnds);
		wnd.setAssetStore(store);
		wnd.create(wnd.getMonitor(), wnd.getRenderEngine());
	}

	public void printWindows() {

		StringBuilder opnWnds = new StringBuilder();

		logger.debug("Open Windows: ");
		for (Iterator<Map.Entry<GLFWWindowContext, CopyOnWriteArrayList<GLFWWindow>>> entries = windows.entrySet()
				.iterator(); entries.hasNext();) {
			Map.Entry<GLFWWindowContext, CopyOnWriteArrayList<GLFWWindow>> entry = entries.next();

			opnWnds.append("\n GLContext Name: " + entry.getKey().name + ": " + "\n");

			for (GLFWWindow wnd : entry.getValue()) {
				opnWnds.append("\t - " + wnd.getWindowName() + " " + wnd.getGlfw_Handle() + "\n");
			}
		}
		logger.debug(opnWnds.toString());
	}

	public static boolean isStopRequested() {
		return engineStopFlag;
	}

	public static void raiseStopFlag() {
		engineStopFlag = true;
	}

	public CoreEngine getCoreEngine() {
		return coreEngine;
	}

	@Override
	public void dispose() {
		try {
			disposeAllWindows();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
