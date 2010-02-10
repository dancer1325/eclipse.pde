/*******************************************************************************
 *  Copyright (c) 2010 IBM Corporation and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.ui.shared;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.progress.WorkbenchJob;

/**
 * A FilteredCheckboxTree implementation to be used internally in PDE UI code.  This tree stores 
 * all the tree elements internally, and keeps the check state in sync.  This way, even if an 
 * element is filtered, the caller can get and set the checked state.
 * 
 * @since 3.6
 */
public class FilteredCheckboxTree extends FilteredTree {

	private static final long FILTER_DELAY = 400;

	FormToolkit fToolkit;
	CachedCheckboxTreeViewer checkboxViewer;

	/**
	 * Constructor that creates a tree with preset style bits and a CachedContainerCheckedTreeViewer for the tree.
	 * 
	 * @param parent parent composite
	 * @param contentProvider Used to determine which elements are leaf nodes
	 * @param toolkit optional toolkit to create UI elements with, required if the tree is being created in a form editor
	 */
	public FilteredCheckboxTree(Composite parent, ITreeContentProvider contentProvider, FormToolkit toolkit) {
		super(parent, true);
		fToolkit = toolkit;
		init(SWT.NONE, new PatternFilter());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.FilteredTree#doCreateTreeViewer(org.eclipse.swt.widgets.Composite, int)
	 */
	protected TreeViewer doCreateTreeViewer(Composite parent, int style) {
		Tree tree = null;

		if (fToolkit != null) {
			tree = fToolkit.createTree(parent, SWT.CHECK | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		} else {
			tree = new Tree(parent, SWT.CHECK | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
		}

		checkboxViewer = new CachedCheckboxTreeViewer(tree);
		return checkboxViewer;
	}

	/*
	 * Overridden to hook a listener on the job and set the deferred content provider
	 * to synchronous mode before a filter is done.
	 * @see org.eclipse.ui.dialogs.FilteredTree#doCreateRefreshJob()
	 */
	protected WorkbenchJob doCreateRefreshJob() {
		WorkbenchJob filterJob = super.doCreateRefreshJob();
		filterJob.addJobChangeListener(new JobChangeAdapter() {
			public void done(IJobChangeEvent event) {
				if (event.getResult().isOK()) {
					getDisplay().asyncExec(new Runnable() {
						public void run() {
							if (checkboxViewer.getTree().isDisposed())
								return;
							checkboxViewer.restoreLeafCheckState();
						}
					});
				}
			}
		});
		return filterJob;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.FilteredTree#doCreateFilterText(org.eclipse.swt.widgets.Composite)
	 */
	protected Text doCreateFilterText(Composite parent) {
		// Overridden so the text gets create using the toolkit if we have one
		Text parentText = super.doCreateFilterText(parent);
		if (fToolkit != null) {
			int style = parentText.getStyle();
			parentText.dispose();
			return fToolkit.createText(parent, null, style);
		}
		return parentText;
	}

	public CachedCheckboxTreeViewer getCheckboxTreeViewer() {
		return checkboxViewer;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.FilteredTree#getRefreshJobDelay()
	 */
	protected long getRefreshJobDelay() {
		return FILTER_DELAY;
	}
}