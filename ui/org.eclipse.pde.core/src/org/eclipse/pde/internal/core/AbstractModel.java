package org.eclipse.pde.internal.core;
/*
 * (c) Copyright IBM Corp. 2000, 2002.
 * All Rights Reserved.
 */

import java.io.File;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.pde.core.IModel;
import org.eclipse.pde.core.IModelChangeProvider;
import org.eclipse.pde.core.IModelChangedEvent;
import org.eclipse.pde.core.IModelChangedListener;
import org.eclipse.pde.core.ModelChangedEvent;

public abstract class AbstractModel
	extends PlatformObject
	implements IModel, IModelChangeProvider, Serializable {
	private Vector listeners = new Vector();
	protected boolean loaded;
	protected NLResourceHelper nlHelper;
	protected boolean disposed;
	private long timeStamp;

	public AbstractModel() {
		super();
	}
	public void addModelChangedListener(IModelChangedListener listener) {
		listeners.add(listener);
	}
	protected NLResourceHelper createNLResourceHelper() {
		return null;
	}
	public void dispose() {
		if (nlHelper != null) {
			nlHelper.dispose();
			nlHelper = null;
		}
		disposed = true;
	}
	public void fireModelChanged(IModelChangedEvent event) {
		for (Iterator iter = listeners.iterator(); iter.hasNext();) {
			IModelChangedListener listener =
				(IModelChangedListener) iter.next();
			listener.modelChanged(event);
		}
	}

	public void fireModelObjectChanged(
		Object object,
		String property,
		Object oldValue,
		Object newValue) {
		fireModelChanged(
			new ModelChangedEvent(object, property, oldValue, newValue));
	}

	public String getResourceString(String key) {
		if (nlHelper == null) {
			nlHelper = createNLResourceHelper();
		}
		if (nlHelper == null)
			return key;
		if (key == null)
			return "";
		return nlHelper.getResourceString(key);
	}
	public IResource getUnderlyingResource() {
		return null;
	}

	protected boolean isInSync(File localFile) {
		if (!localFile.exists()) {
			return false;
		}
		if (localFile.lastModified() != getTimeStamp()) {
			return false;
		}
		return true;
		//	return  (localFile.lastModified()==getTimeStamp());
	}

	public final long getTimeStamp() {
		return timeStamp;
	}

	protected abstract void updateTimeStamp();

	protected void updateTimeStamp(File localFile) {
		if (localFile.exists())
			this.timeStamp = localFile.lastModified();
	}

	public boolean isDisposed() {
		return disposed;
	}
	public boolean isLoaded() {
		return loaded;
	}
	public void release() {
	}
	public void removeModelChangedListener(IModelChangedListener listener) {
		listeners.remove(listener);
	}

}
