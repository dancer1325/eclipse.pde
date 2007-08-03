/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.pde.internal.ui.editor.toc;

import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.pde.internal.core.text.IDocumentAttribute;
import org.eclipse.pde.internal.core.text.IDocumentNode;
import org.eclipse.pde.internal.core.text.IDocumentRange;
import org.eclipse.pde.internal.core.text.toc.TocModel;
import org.eclipse.pde.internal.core.text.toc.TocObject;
import org.eclipse.pde.internal.ui.IHelpContextIds;
import org.eclipse.pde.internal.ui.PDEPlugin;
import org.eclipse.pde.internal.ui.PDEUIMessages;
import org.eclipse.pde.internal.ui.editor.PDEFormEditor;
import org.eclipse.pde.internal.ui.editor.XMLSourcePage;

/**
 * TocSourcePage
 */
public class TocSourcePage extends XMLSourcePage {

	/**
	 * @param editor
	 * @param id
	 * @param title
	 */
	public TocSourcePage(PDEFormEditor editor, String id, String title) {
		super(editor, id, title);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.ui.editor.PDEProjectionSourcePage#isQuickOutlineEnabled()
	 */
	public boolean isQuickOutlineEnabled() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.ui.editor.PDESourcePage#createOutlineComparator()
	 */
	public ViewerComparator createOutlineComparator() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.ui.editor.PDESourcePage#createOutlineContentProvider()
	 */
	public ITreeContentProvider createOutlineContentProvider() {
		return new TocContentProvider();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.ui.editor.PDESourcePage#createOutlineLabelProvider()
	 */
	public ILabelProvider createOutlineLabelProvider() {
		return PDEPlugin.getDefault().getLabelProvider();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#setPartName(java.lang.String)
	 */
	protected void setPartName(String partName) {
		super.setPartName(PDEUIMessages.EditorSourcePage_name);
	}

	
	protected boolean isSelectionListener() {
		return true;
	}

	public Object getAdapter(Class adapter) {
		if (IHyperlinkDetector.class.equals(adapter))
			return new TocHyperlinkDetector(this);
		return super.getAdapter(adapter);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.ui.editor.PDESourcePage#updateSelection(java.lang.Object)
	 */
	public void updateSelection(Object object)
	{	if ((object instanceof IDocumentNode) && 
				!((IDocumentNode)object).isErrorNode()) {
			fSelection = object;
			setHighlightRange((IDocumentNode)object, true);
			setSelectedRange((IDocumentNode)object, false);
		}
	}
	
	protected IDocumentRange findRange() {
		if (fSelection instanceof IDocumentNode)
		{	return (IDocumentNode)fSelection;
		}

		return null;
	}
	
	public IDocumentRange getRangeElement(int offset, boolean searchChildren) {
		TocObject toc = ((TocModel)getInputContext().getModel()).getToc();
		return findNode(toc, offset, searchChildren);
	}
	
	protected void synchronizeOutlinePage(int offset) {
		IDocumentRange rangeElement = getRangeElement(offset, true);
		updateHighlightRange(rangeElement);
		// TODO: MP: TEO-SIMPLECS: Generalize for parent - search children = true and handle attributes
		if(rangeElement instanceof IDocumentAttribute)
		{	rangeElement = ((IDocumentAttribute)rangeElement).getEnclosingElement();
		}
		updateOutlinePageSelection(rangeElement);
	}
	
	protected void initializeEditor() {
		super.initializeEditor();
		setHelpContextId(IHelpContextIds.TOC_EDITOR);
	}
}
