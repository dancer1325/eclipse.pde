package org.eclipse.pde.internal.ui.editor.feature;
/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */

import java.io.*;
import java.util.Vector;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.pde.core.*;
import org.eclipse.pde.internal.core.IModelProvider;
import org.eclipse.pde.internal.ui.PDEPlugin;
import org.eclipse.pde.internal.ui.editor.*;
import org.eclipse.pde.internal.ui.model.ifeature.*;
import org.eclipse.ui.*;

public class FeatureEditor extends PDEMultiPageXMLEditor {
	public static final String FEATURE_PAGE = "ComponentPage";
	public static final String INFO_PAGE = "InfoPage";
	public static final String REFERENCE_PAGE = "ReferencePage";
	public static final String SOURCE_PAGE = "SourcePage";
	public static final String UNRESOLVED_TITLE = "FeatureEditor.Unresolved.title";
	public static final String VERSION_TITLE = "FeatureEditor.Version.title";
	public static final String VERSION_MESSAGE = "FeatureEditor.Version.message";
	public static final String VERSION_EXISTS = "FeatureEditor.Version.exists";
	public static final String UNRESOLVED_MESSAGE =
		"FeatureEditor.Unresolved.message";
	public static final String FEATURE_PAGE_TITLE =
		"FeatureEditor.FeaturePage.title";
	public static final String REFERENCE_PAGE_TITLE =
		"FeatureEditor.ReferencePage.title";
	public static final String INFO_PAGE_TITLE = "FeatureEditor.InfoPage.title";

	public FeatureEditor() {
		super();
	}
	private void checkStaleReferences(IFeatureModel model) {
		IFeature feature = model.getFeature();
		Vector unresolved = new Vector();

		IFeaturePlugin[] plugins = feature.getPlugins();
		for (int i = 0; i < plugins.length; i++) {
			IFeaturePlugin reference = plugins[i];
			if (feature.getReferencedModel(reference) == null) {
				unresolved.add(reference);
			}
		}
		if (unresolved.size() > 0 && model.isEditable()) {
			try {
				IFeaturePlugin[] removed =
					(IFeaturePlugin[]) unresolved.toArray(new IFeaturePlugin[unresolved.size()]);
				feature.removePlugins(removed);
			} catch (CoreException e) {
				PDEPlugin.logException(e);
			}
			MessageDialog.openWarning(
				PDEPlugin.getActiveWorkbenchShell(),
				PDEPlugin.getResourceString(UNRESOLVED_TITLE),
				PDEPlugin.getResourceString(UNRESOLVED_MESSAGE));
		}
	}
	protected Object createModel(Object input) throws CoreException {
		if (input instanceof IFile)
			return createResourceModel((IFile) input);
		return null;
	}
	protected void createPages() {
		firstPageId = FEATURE_PAGE;
		formWorkbook.setFirstPageSelected(false);
		FeatureFormPage featurePage =
			new FeatureFormPage(this, PDEPlugin.getResourceString(FEATURE_PAGE_TITLE));
		FeatureReferencePage referencePage =
			new FeatureReferencePage(
				featurePage,
				PDEPlugin.getResourceString(REFERENCE_PAGE_TITLE));
		InfoFormPage infoPage =
			new InfoFormPage(featurePage, PDEPlugin.getResourceString(INFO_PAGE_TITLE));
		addPage(FEATURE_PAGE, featurePage);
		addPage(INFO_PAGE, infoPage);
		addPage(REFERENCE_PAGE, referencePage);
		addPage(SOURCE_PAGE, new FeatureSourcePage(this));
	}
	private IFeatureModel createResourceModel(IFile file) throws CoreException {
		InputStream stream = null;
		stream = file.getContents(false);

		IModelProvider provider = PDEPlugin.getDefault().getWorkspaceModelManager();
		provider.connect(file, this);
		IFeatureModel model = (IFeatureModel) provider.getModel(file, this);
		boolean cleanModel = true;
		try {
			model.load(stream, false);
			checkStaleReferences(model);
		} catch (CoreException e) {
			cleanModel = false;
		}
		try {
			stream.close();
		} catch (IOException e) {
			PDEPlugin.logException(e);
		}
		return cleanModel ? model : null;
	}
	public void dispose() {
		super.dispose();
		IModelProvider provider = PDEPlugin.getDefault().getWorkspaceModelManager();
		IModel model = (IModel) getModel();
		provider.disconnect(model.getUnderlyingResource(), this);
	}
	public void doSave(IProgressMonitor monitor) {
		super.doSave(monitor);
		/*
			IFeatureModel model = (IFeatureModel) getModel();
			IFeature component = model.getFeature();
			String version = component.getVersion();
			String id = component.getId();
			IFile file = (IFile) model.getUnderlyingResource();
			IContainer parent = file.getParent();
			if (parent instanceof IFolder) {
				String name = parent.getName();
				String expectedName = id + "_"+ version;
				if (name.equals(expectedName)==false) {
					MessageDialog.openInformation(
						PDEPlugin.getActiveWorkbenchShell(),
						PDEPlugin.getResourceString(VERSION_TITLE),
						PDEPlugin.getFormattedMessage(VERSION_MESSAGE, name));
				}
			}
		*/
	}

	public IPDEEditorPage getHomePage() {
		return getPage(FEATURE_PAGE);
	}
	protected String getSourcePageId() {
		return SOURCE_PAGE;
	}
	public String getTitle() {
		if (!isModelCorrect(getModel()))
			return super.getTitle();
		IFeatureModel model = (IFeatureModel) getModel();
		String name = model.getFeature().getLabel();
		if (name == null)
			return super.getTitle();
		return model.getResourceString(name);
	}
	protected boolean isModelDirty(Object model) {
		return model != null
			&& model instanceof IEditable
			&& model instanceof IModel
			&& ((IModel) model).isEditable()
			&& ((IEditable) model).isDirty();
	}
	protected boolean isValidContentType(IEditorInput input) {
		String name = input.getName().toLowerCase();
		if (name.equals("feature.xml"))
			return true;
		return false;
	}
	protected boolean updateModel() {
		IFeatureModel model = (IFeatureModel) getModel();
		IDocument document = getDocumentProvider().getDocument(getEditorInput());
		boolean cleanModel = true;
		String text = document.get();
		try {
			InputStream stream = new ByteArrayInputStream(text.getBytes("UTF8"));
			try {
				model.reload(stream, false);
			} catch (CoreException e) {
				cleanModel = false;
			}
			try {
				stream.close();
			} catch (IOException e) {
			}
		} catch (UnsupportedEncodingException e) {
			PDEPlugin.logException(e);
		}
		return cleanModel;
	}
	public void updateTitle() {
		firePropertyChange(IWorkbenchPart.PROP_TITLE);
	}
}