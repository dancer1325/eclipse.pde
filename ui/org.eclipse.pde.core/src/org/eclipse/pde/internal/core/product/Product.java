/*******************************************************************************
 * Copyright (c) 2005, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     EclipseSource Corporation - ongoing enhancements
 *     Benjamin Cabe <benjamin.cabe@anyware-tech.com> - bug 265931
 *     Rapicorp Corporation - ongoing enhancements
 *******************************************************************************/
package org.eclipse.pde.internal.core.product;

import java.io.PrintWriter;
import java.util.*;
import org.eclipse.pde.core.IModelChangedEvent;
import org.eclipse.pde.internal.core.iproduct.*;
import org.w3c.dom.*;

public class Product extends ProductObject implements IProduct {

	private static final long serialVersionUID = 1L;
	private String fId;
	private String fProductId;
	private String fName;
	private String fApplication;
	private String fVersion;
	private IAboutInfo fAboutInfo;

	private TreeMap<String, IProductObject> fPlugins = new TreeMap<String, IProductObject>();
	private TreeMap<String, IProductObject> fPluginConfigurations = new TreeMap<String, IProductObject>();
	private TreeMap<String, IProductObject> fConfigurationProperties = new TreeMap<String, IProductObject>();
	private List<IProductObject> fFeatures = new ArrayList<IProductObject>();
	private IConfigurationFileInfo fConfigIniInfo;
	private IJREInfo fJVMInfo;
	private boolean fUseFeatures;
	private boolean fIncludeLaunchers;
	private IWindowImages fWindowImages;
	private ISplashInfo fSplashInfo;
	private ILauncherInfo fLauncherInfo;
	private IArgumentsInfo fLauncherArgs;
	private IIntroInfo fIntroInfo;
	private ILicenseInfo fLicenseInfo;
	private List<IProductObject> fRepositories = new ArrayList<IProductObject>();
	private IPreferencesInfo fPreferencesInfo;
	private ICSSInfo fCSSInfo;

	public Product(IProductModel model) {
		super(model);
		fIncludeLaunchers = true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.iproduct.IProduct#getId()
	 */
	@Override
	public String getId() {
		return fId;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.iproduct.IProduct#getProductId()
	 */
	@Override
	public String getProductId() {
		return fProductId;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.iproduct.IProduct#getName()
	 */
	@Override
	public String getName() {
		return fName;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.iproduct.IProduct#getVersion()
	 */
	@Override
	public String getVersion() {
		return fVersion;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.iproduct.IProduct#getApplication()
	 */
	@Override
	public String getApplication() {
		return fApplication;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.iproduct.IProduct#getDefiningPluginId()
	 */
	@Override
	public String getDefiningPluginId() {
		if (fProductId == null)
			return null;
		int dot = fProductId.lastIndexOf('.');
		return (dot != -1) ? fProductId.substring(0, dot) : null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.iproduct.IProduct#setId(java.lang.String)
	 */
	@Override
	public void setId(String id) {
		String old = fId;
		fId = id;
		if (isEditable())
			firePropertyChanged(P_UID, old, fId);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.iproduct.IProduct#setProductId(java.lang.String)
	 */
	@Override
	public void setProductId(String id) {
		String old = fProductId;
		fProductId = id;
		if (isEditable())
			firePropertyChanged(P_ID, old, fProductId);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.iproduct.IProduct#setVersion(java.lang.String)
	 */
	@Override
	public void setVersion(String version) {
		String old = fVersion;
		fVersion = version;
		if (isEditable())
			firePropertyChanged(P_VERSION, old, fVersion);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.iproduct.IProduct#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		String old = fName;
		fName = name;
		if (isEditable())
			firePropertyChanged(P_NAME, old, fName);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.iproduct.IProduct#setAboutInfo(org.eclipse.pde.internal.core.iproduct.IAboutInfo)
	 */
	@Override
	public void setAboutInfo(IAboutInfo info) {
		fAboutInfo = info;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.iproduct.IProduct#setApplication(java.lang.String)
	 */
	@Override
	public void setApplication(String application) {
		String old = fApplication;
		fApplication = application;
		if (isEditable())
			firePropertyChanged(P_APPLICATION, old, fApplication);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.product.ProductObject#write(java.lang.String, java.io.PrintWriter)
	 */
	@Override
	public void write(String indent, PrintWriter writer) {
		writer.print(indent + "<product"); //$NON-NLS-1$
		if (fName != null && fName.length() > 0)
			writer.print(" " + P_NAME + "=\"" + getWritableString(fName) + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		if (fId != null && fId.length() > 0)
			writer.print(" " + P_UID + "=\"" + fId + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		if (fProductId != null && fProductId.length() > 0)
			writer.print(" " + P_ID + "=\"" + fProductId + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		if (fApplication != null && fApplication.length() > 0)
			writer.print(" " + P_APPLICATION + "=\"" + fApplication + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		if (fVersion != null && fVersion.length() > 0)
			writer.print(" " + P_VERSION + "=\"" + fVersion + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		writer.print(" " + P_USEFEATURES + "=\"" + Boolean.toString(fUseFeatures) + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		writer.print(" " + P_INCLUDE_LAUNCHERS + "=\"" + Boolean.toString(fIncludeLaunchers) + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		writer.println(">"); //$NON-NLS-1$

		if (fAboutInfo != null) {
			writer.println();
			fAboutInfo.write(indent + "   ", writer); //$NON-NLS-1$
		}

		if (fConfigIniInfo != null) {
			writer.println();
			fConfigIniInfo.write(indent + "   ", writer); //$NON-NLS-1$
		}

		if (fLauncherArgs != null) {
			writer.println();
			fLauncherArgs.write(indent + "   ", writer); //$NON-NLS-1$
		}

		if (fWindowImages != null) {
			writer.println();
			fWindowImages.write(indent + "   ", writer); //$NON-NLS-1$
		}

		if (fSplashInfo != null) {
			writer.println();
			fSplashInfo.write(indent + "   ", writer); //$NON-NLS-1$
		}

		if (fLauncherInfo != null) {
			writer.println();
			fLauncherInfo.write(indent + "   ", writer); //$NON-NLS-1$
		}

		if (fIntroInfo != null) {
			writer.println();
			fIntroInfo.write(indent + "   ", writer); //$NON-NLS-1$
		}

		if (fJVMInfo != null) {
			writer.println();
			fJVMInfo.write(indent + "   ", writer); //$NON-NLS-1$
		}

		if (fLicenseInfo != null) {
			writer.println();
			fLicenseInfo.write(indent + "   ", writer); //$NON-NLS-1$
		}

		writer.println();
		writer.println(indent + "   <plugins>"); //$NON-NLS-1$
		Iterator<IProductObject> iter = fPlugins.values().iterator();
		while (iter.hasNext()) {
			IProductPlugin plugin = (IProductPlugin) iter.next();
			plugin.write(indent + "      ", writer); //$NON-NLS-1$
		}
		writer.println(indent + "   </plugins>"); //$NON-NLS-1$

		if (fFeatures.size() > 0) {
			writer.println();
			writer.println(indent + "   <features>"); //$NON-NLS-1$
			iter = fFeatures.iterator();
			while (iter.hasNext()) {
				IProductFeature feature = (IProductFeature) iter.next();
				feature.write(indent + "      ", writer); //$NON-NLS-1$
			}
			writer.println(indent + "   </features>"); //$NON-NLS-1$
		}

		writer.println();

		if (fConfigurationProperties.size() > 0 || fPluginConfigurations.size() > 0) {
			writer.println(indent + "   <configurations>"); //$NON-NLS-1$
			iter = fPluginConfigurations.values().iterator();
			while (iter.hasNext()) {
				IPluginConfiguration configuration = (IPluginConfiguration) iter.next();
				configuration.write(indent + "      ", writer); //$NON-NLS-1$
			}
			iter = fConfigurationProperties.values().iterator();
			while (iter.hasNext()) {
				IConfigurationProperty property = (IConfigurationProperty) iter.next();
				property.write(indent + "      ", writer); //$NON-NLS-1$
			}
			writer.println(indent + "   </configurations>"); //$NON-NLS-1$
		}

		if (fRepositories.size() > 0) {
			writer.println();
			writer.println(indent + "   <repositories>"); //$NON-NLS-1$
			Iterator<IProductObject> iterator = fRepositories.iterator();
			while (iterator.hasNext()) {
				IRepositoryInfo repo = (IRepositoryInfo) iterator.next();
				repo.write(indent + "      ", writer); //$NON-NLS-1$
			}
			writer.println(indent + "   </repositories>"); //$NON-NLS-1$
		}

		if (fPreferencesInfo != null) {
			writer.println();
			fPreferencesInfo.write(indent + "   ", writer); //$NON-NLS-1$
		}

		if (fCSSInfo != null) {
			writer.println();
			fCSSInfo.write(indent + "   ", writer); //$NON-NLS-1$
		}

		writer.println();
		writer.println("</product>"); //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.iproduct.IProduct#getAboutInfo()
	 */
	@Override
	public IAboutInfo getAboutInfo() {
		return fAboutInfo;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.iproduct.IProduct#reset()
	 */
	@Override
	public void reset() {
		fApplication = null;
		fId = null;
		fProductId = null;
		fName = null;
		fUseFeatures = false;
		fIncludeLaunchers = true;
		fAboutInfo = null;
		fPlugins.clear();
		fPluginConfigurations.clear();
		fConfigurationProperties.clear();
		fFeatures.clear();
		fConfigIniInfo = null;
		fWindowImages = null;
		fSplashInfo = null;
		fLauncherInfo = null;
		fLauncherArgs = null;
		fIntroInfo = null;
		fJVMInfo = null;
		fLicenseInfo = null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.iproduct.IProductObject#parse(org.w3c.dom.Node)
	 */
	@Override
	public void parse(Node node) {
		if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("product")) { //$NON-NLS-1$
			Element element = (Element) node;
			fApplication = element.getAttribute(P_APPLICATION);
			fProductId = element.getAttribute(P_ID);
			fId = element.getAttribute(P_UID);
			fName = element.getAttribute(P_NAME);
			fVersion = element.getAttribute(P_VERSION);
			fUseFeatures = "true".equals(element.getAttribute(P_USEFEATURES)); //$NON-NLS-1$
			String launchers = element.getAttribute(P_INCLUDE_LAUNCHERS);
			fIncludeLaunchers = ("true".equals(launchers) || launchers.length() == 0); //$NON-NLS-1$
			NodeList children = node.getChildNodes();
			IProductModelFactory factory = getModel().getFactory();
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					String name = child.getNodeName();
					if (name.equals("aboutInfo")) { //$NON-NLS-1$
						fAboutInfo = factory.createAboutInfo();
						fAboutInfo.parse(child);
					} else if (name.equals("plugins")) { //$NON-NLS-1$
						parsePlugins(child.getChildNodes());
					} else if (name.equals("features")) { //$NON-NLS-1$
						parseFeatures(child.getChildNodes());
					} else if (name.equals("configurations")) { //$NON-NLS-1$
						parseConfigations(child.getChildNodes());
					} else if (name.equals("configIni")) { //$NON-NLS-1$
						fConfigIniInfo = factory.createConfigFileInfo();
						fConfigIniInfo.parse(child);
					} else if (name.equals("windowImages")) { //$NON-NLS-1$
						fWindowImages = factory.createWindowImages();
						fWindowImages.parse(child);
					} else if (name.equals("splash")) { //$NON-NLS-1$
						fSplashInfo = factory.createSplashInfo();
						fSplashInfo.parse(child);
					} else if (name.equals("launcher")) { //$NON-NLS-1$
						fLauncherInfo = factory.createLauncherInfo();
						fLauncherInfo.parse(child);
					} else if (name.equals("launcherArgs")) { //$NON-NLS-1$
						fLauncherArgs = factory.createLauncherArguments();
						fLauncherArgs.parse(child);
					} else if (name.equals("intro")) { //$NON-NLS-1$
						fIntroInfo = factory.createIntroInfo();
						fIntroInfo.parse(child);
					} else if (name.equals("vm")) { //$NON-NLS-1$
						fJVMInfo = factory.createJVMInfo();
						fJVMInfo.parse(child);
					} else if (name.equals("license")) { //$NON-NLS-1$
						fLicenseInfo = factory.createLicenseInfo();
						fLicenseInfo.parse(child);
					} else if (name.equals("repositories")) { //$NON-NLS-1$
						parseRepositories(child.getChildNodes());
					} else if (name.equals("preferencesInfo")) { //$NON-NLS-1$
						fPreferencesInfo = factory.createPreferencesInfo();
						fPreferencesInfo.parse(child);
					} else if (name.equals("cssInfo")) { //$NON-NLS-1$
						fCSSInfo = factory.createCSSInfo();
						fCSSInfo.parse(child);
					}
				}
			}
		}
	}

	private void parsePlugins(NodeList children) {
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				if (child.getNodeName().equals("plugin")) { //$NON-NLS-1$
					IProductPlugin plugin = getModel().getFactory().createPlugin();
					plugin.parse(child);
					fPlugins.put(plugin.getId(), plugin);
				}
			}
		}
	}

	private void parseConfigations(NodeList children) {
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				if (child.getNodeName().equals("plugin")) { //$NON-NLS-1$
					IPluginConfiguration configuration = getModel().getFactory().createPluginConfiguration();
					configuration.parse(child);
					fPluginConfigurations.put(configuration.getId(), configuration);
				}
				if (child.getNodeName().equals("property")) { //$NON-NLS-1$
					IConfigurationProperty property = getModel().getFactory().createConfigurationProperty();
					property.parse(child);
					fConfigurationProperties.put(property.getName(), property);
				}
			}
		}
	}

	private void parseFeatures(NodeList children) {
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				if (child.getNodeName().equals("feature")) { //$NON-NLS-1$
					IProductFeature feature = getModel().getFactory().createFeature();
					feature.parse(child);
					fFeatures.add(feature);
				}
			}
		}
	}

	private void parseRepositories(NodeList children) {
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				if (child.getNodeName().equals("repository")) { //$NON-NLS-1$
					IRepositoryInfo repo = getModel().getFactory().createRepositoryInfo();
					repo.parse(child);
					fRepositories.add(repo);
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.iproduct.IProduct#addPlugin(org.eclipse.pde.internal.core.iproduct.IProductPlugin)
	 */
	@Override
	public void addPlugins(IProductPlugin[] plugins) {
		boolean modified = false;
		for (int i = 0; i < plugins.length; i++) {
			if (plugins[i] == null)
				continue;
			String id = plugins[i].getId();
			if (id == null || fPlugins.containsKey(id)) {
				plugins[i] = null;
				continue;
			}

			plugins[i].setModel(getModel());
			fPlugins.put(id, plugins[i]);
			modified = true;
		}
		if (modified && isEditable())
			fireStructureChanged(plugins, IModelChangedEvent.INSERT);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.iproduct.IProduct#addPluginConfigurations(org.eclipse.pde.internal.core.iproduct.IPluginConfiguration[])
	 */
	@Override
	public void addPluginConfigurations(IPluginConfiguration[] configuration) {
		boolean modified = false;
		for (int i = 0; i < configuration.length; i++) {
			if (configuration[i] == null)
				continue;
			String id = configuration[i].getId();
			if (id == null || fPluginConfigurations.containsKey(id)) {
				configuration[i] = null;
				continue;
			}

			configuration[i].setModel(getModel());
			fPluginConfigurations.put(id, configuration[i]);
			modified = true;
		}
		if (modified && isEditable())
			fireStructureChanged(configuration, IModelChangedEvent.INSERT);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.iproduct.IProduct#addConfigurationProperties(org.eclipse.pde.internal.core.iproduct.IConfigurationProperty[])
	 */
	@Override
	public void addConfigurationProperties(IConfigurationProperty[] properties) {
		boolean modified = false;
		for (int i = 0; i < properties.length; i++) {
			if (properties[i] == null)
				continue;
			String name = properties[i].getName();
			if (name == null || fConfigurationProperties.containsKey(name)) {
				continue;
			}

			properties[i].setModel(getModel());
			fConfigurationProperties.put(name, properties[i]);
			modified = true;
		}
		if (modified && isEditable())
			fireStructureChanged(properties, IModelChangedEvent.INSERT);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.iproduct.IProduct#removePlugins(org.eclipse.pde.internal.core.iproduct.IProductPlugin[])
	 */
	@Override
	public void removePlugins(IProductPlugin[] plugins) {
		boolean modified = false;
		LinkedList<Object> removedConfigurations = new LinkedList<Object>();
		for (int i = 0; i < plugins.length; i++) {
			final String id = plugins[i].getId();
			if (fPlugins.remove(id) != null) {
				modified = true;
				Object configuration = fPluginConfigurations.remove(id);
				if (configuration != null)
					removedConfigurations.add(configuration);
			}
		}
		if (isEditable()) {
			if (modified)
				fireStructureChanged(plugins, IModelChangedEvent.REMOVE);
			if (!removedConfigurations.isEmpty()) {
				fireStructureChanged(removedConfigurations.toArray(new IProductObject[removedConfigurations.size()]), IModelChangedEvent.REMOVE);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.iproduct.IProduct#removePluginConfigurations(org.eclipse.pde.internal.core.iproduct.IProductPluginConfiguration[])
	 */
	@Override
	public void removePluginConfigurations(IPluginConfiguration[] configurations) {
		boolean modified = false;
		for (int i = 0; i < configurations.length; i++) {
			if (fPluginConfigurations.remove(configurations[i].getId()) != null) {
				modified = true;
			}
		}
		if (isEditable() && modified)
			fireStructureChanged(configurations, IModelChangedEvent.REMOVE);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.iproduct.IProduct#removeConfigurationProperties(org.eclipse.pde.internal.core.iproduct.IConfigurationProperty[])
	 */
	@Override
	public void removeConfigurationProperties(IConfigurationProperty[] properties) {
		boolean modified = false;
		for (int i = 0; i < properties.length; i++) {
			if (fConfigurationProperties.remove(properties[i].getName()) != null) {
				modified = true;
			}
		}
		if (isEditable() && modified)
			fireStructureChanged(properties, IModelChangedEvent.REMOVE);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.iproduct.IProduct#getPlugins()
	 */
	@Override
	public IProductPlugin[] getPlugins() {
		return fPlugins.values().toArray(new IProductPlugin[fPlugins.size()]);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.iproduct.IProduct#getPluginConfigurations()
	 */
	@Override
	public IPluginConfiguration[] getPluginConfigurations() {
		return fPluginConfigurations.values().toArray(new IPluginConfiguration[fPluginConfigurations.size()]);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.iproduct.IProduct#getConfigurationProperties()
	 */
	@Override
	public IConfigurationProperty[] getConfigurationProperties() {
		return fConfigurationProperties.values().toArray(new IConfigurationProperty[fConfigurationProperties.size()]);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.iproduct.IProduct#getRepositories()
	 */
	@Override
	public IRepositoryInfo[] getRepositories() {
		return fRepositories.toArray(new IRepositoryInfo[fRepositories.size()]);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.iproduct.IProduct#addRepositories(org.eclipse.pde.internal.core.iproduct.IRepositoryInfo[])
	 */
	@Override
	public void addRepositories(IRepositoryInfo[] repos) {
		boolean modified = false;
		for (int i = 0; i < repos.length; i++) {
			modified = modified || fRepositories.add(repos[i]);
		}
		if (modified && isEditable())
			fireStructureChanged(repos, IModelChangedEvent.INSERT);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.iproduct.IProduct#removeRepositories(org.eclipse.pde.internal.core.iproduct.IRepositoryInfo[])
	 */
	@Override
	public void removeRepositories(IRepositoryInfo[] repos) {
		boolean modified = false;
		for (int i = 0; i < repos.length; i++) {
			modified = fRepositories.remove(repos[i]) || modified;
		}
		if (modified && isEditable())
			fireStructureChanged(repos, IModelChangedEvent.REMOVE);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.iproduct.IProduct#getPreferencesInfo()
	 */
	@Override
	public IPreferencesInfo getPreferencesInfo() {
		return fPreferencesInfo;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.iproduct.IProduct#setPreferencesInfo(org.eclipse.pde.internal.core.iproduct.IPreferencesInfo)
	 */
	@Override
	public void setPreferencesInfo(IPreferencesInfo info) {
		fPreferencesInfo = info;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.iproduct.IProduct#getCSSInfo()
	 */
	@Override
	public ICSSInfo getCSSInfo() {
		return fCSSInfo;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.iproduct.IProduct#setCSSInfo(org.eclipse.pde.internal.core.iproduct.ICSSInfo)
	 */
	@Override
	public void setCSSInfo(ICSSInfo info) {
		fCSSInfo = info;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.iproduct.IProduct#getConfigurationFileInfo()
	 */
	@Override
	public IConfigurationFileInfo getConfigurationFileInfo() {
		return fConfigIniInfo;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.iproduct.IProduct#setConfigurationFileInfo(org.eclipse.pde.internal.core.iproduct.IConfigurationFileInfo)
	 */
	@Override
	public void setConfigurationFileInfo(IConfigurationFileInfo info) {
		fConfigIniInfo = info;
	}

	@Override
	public boolean useFeatures() {
		return fUseFeatures;
	}

	@Override
	public void setUseFeatures(boolean use) {
		boolean old = fUseFeatures;
		fUseFeatures = use;
		if (isEditable())
			firePropertyChanged(P_USEFEATURES, Boolean.toString(old), Boolean.toString(fUseFeatures));
	}

	@Override
	public boolean containsPlugin(String id) {
		return fPlugins.containsKey(id);
	}

	@Override
	public boolean containsFeature(String id) {
		IProductFeature[] features = getFeatures();
		for (int i = 0; i < features.length; i++) {
			if (features[i].getId().equals(id))
				return true;
		}
		return false;
	}

	@Override
	public IWindowImages getWindowImages() {
		return fWindowImages;
	}

	@Override
	public void setWindowImages(IWindowImages images) {
		fWindowImages = images;
	}

	@Override
	public ISplashInfo getSplashInfo() {
		return fSplashInfo;
	}

	@Override
	public void setSplashInfo(ISplashInfo info) {
		fSplashInfo = info;
	}

	@Override
	public ILauncherInfo getLauncherInfo() {
		return fLauncherInfo;
	}

	@Override
	public void setLauncherInfo(ILauncherInfo info) {
		fLauncherInfo = info;
	}

	@Override
	public void addFeatures(IProductFeature[] features) {
		boolean modified = false;
		for (int i = 0; i < features.length; i++) {
			if (features[i] == null)
				continue;
			String id = features[i].getId();
			if (fFeatures.contains((id))) {
				features[i] = null;
				continue;
			}

			features[i].setModel(getModel());
			fFeatures.add(features[i]);
			modified = true;
		}

		if (modified && isEditable())
			fireStructureChanged(features, IModelChangedEvent.INSERT);
	}

	@Override
	public void removeFeatures(IProductFeature[] features) {
		boolean modified = false;
		for (int i = 0; i < features.length; i++) {
			if (features[i].getId() != null) {
				fFeatures.remove(features[i]);
				modified = true;
			}
		}
		if (modified && isEditable())
			fireStructureChanged(features, IModelChangedEvent.REMOVE);
	}

	@Override
	public IProductFeature[] getFeatures() {
		return fFeatures.toArray(new IProductFeature[fFeatures.size()]);
	}

	@Override
	public IArgumentsInfo getLauncherArguments() {
		return fLauncherArgs;
	}

	@Override
	public void setLauncherArguments(IArgumentsInfo info) {
		fLauncherArgs = info;
	}

	@Override
	public IIntroInfo getIntroInfo() {
		return fIntroInfo;
	}

	@Override
	public void setIntroInfo(IIntroInfo introInfo) {
		fIntroInfo = introInfo;
	}

	@Override
	public IJREInfo getJREInfo() {
		return fJVMInfo;
	}

	@Override
	public void setJREInfo(IJREInfo info) {
		fJVMInfo = info;
	}

	@Override
	public ILicenseInfo getLicenseInfo() {
		return fLicenseInfo;
	}

	@Override
	public void setLicenseInfo(ILicenseInfo info) {
		fLicenseInfo = info;
	}

	@Override
	public void swap(IProductFeature feature1, IProductFeature feature2) {
		int index1 = fFeatures.indexOf(feature1);
		int index2 = fFeatures.indexOf(feature2);
		if (index1 == -1 || index2 == -1)
			return;

		fFeatures.set(index2, feature1);
		fFeatures.set(index1, feature2);

		fireStructureChanged(feature1, IModelChangedEvent.CHANGE);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.pde.internal.core.iproduct.IProduct#findPluginConfiguration(java.lang.String)
	 */
	@Override
	public IPluginConfiguration findPluginConfiguration(String id) {
		return (IPluginConfiguration) fPluginConfigurations.get(id);
	}

	@Override
	public boolean includeLaunchers() {
		return fIncludeLaunchers;
	}

	@Override
	public void setIncludeLaunchers(boolean include) {
		boolean old = fIncludeLaunchers;
		fIncludeLaunchers = include;
		if (isEditable())
			firePropertyChanged(P_INCLUDE_LAUNCHERS, Boolean.toString(old), Boolean.toString(fIncludeLaunchers));
	}
}
