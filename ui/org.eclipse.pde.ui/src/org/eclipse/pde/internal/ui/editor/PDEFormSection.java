package org.eclipse.pde.internal.ui.editor;
/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */

import org.eclipse.pde.core.*;
import org.eclipse.update.ui.forms.internal.*;

public abstract class PDEFormSection extends FormSection implements IModelChangedListener {
	private PDEFormPage formPage;

public PDEFormSection(PDEFormPage formPage) {
	this.formPage = formPage;
}
public PDEFormPage getFormPage() {
	return formPage;
}
public void modelChanged(IModelChangedEvent e) {
}

protected void reflow() {
	super.reflow();
	AbstractSectionForm form = formPage.getForm();
	if (form instanceof ScrollableSectionForm) {
		((ScrollableSectionForm)form).updateScrollBars();
	}
}
}
