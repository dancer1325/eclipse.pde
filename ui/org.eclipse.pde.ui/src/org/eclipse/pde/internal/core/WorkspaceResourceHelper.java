package org.eclipse.pde.internal.core;
/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.pde.core.IModelChangeProvider;
import org.eclipse.pde.internal.ui.PDEPlugin;
 
public class WorkspaceResourceHelper extends NLResourceHelper
implements IResourceChangeListener {
	private IFile file;
	private IModelChangeProvider changeProvider;

	/**
	 * Constructor for WorkspaceResourceHelper
	 */
	public WorkspaceResourceHelper(String name, URL [] locations) {
		super(name, locations);
		PDEPlugin.getWorkspace().addResourceChangeListener(this);
	}
	
	public void dispose() {
		PDEPlugin.getWorkspace().removeResourceChangeListener(this);
	}
	
	public void setFile(IFile file) {
		this.file = file;
	}
	
	public IFile getFile() {
		return file;
	}
	/**
	 * @see IResourceChangeListener#resourceChanged(IResourceChangeEvent)
	 */
	public void resourceChanged(IResourceChangeEvent event) {
		try {
		   IResourceDelta delta = event.getDelta();
		   if (delta!=null) {
		      event.getDelta().accept(new IResourceDeltaVisitor() {
		         public boolean visit(IResourceDelta delta) {
				    return WorkspaceResourceHelper.this.visit(delta);
			     }
		      });
		   }
		}
		catch (CoreException e) {
		}
	}
	
	private boolean visit(IResourceDelta delta) {
		IResource resource = delta.getResource();
		if (resource.equals(file)) {
			bundle = null;
			/*
			if (changeProvider!=null) {
				// fire 'world changed' in the model
				// to force views to refresh because
				// translatable names changed
				ModelChangedEvent e= new ModelChangedEvent(IModelChangedEvent.WORLD_CHANGED, null, null);
				changeProvider.fireModelChanged(e);
			}
			*/
		}
		return true;
	}
	
	public void setModelChangeProvider(IModelChangeProvider changeProvider) {
		this.changeProvider = changeProvider;
	}
}
