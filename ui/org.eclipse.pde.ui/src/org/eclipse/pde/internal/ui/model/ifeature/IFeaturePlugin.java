package org.eclipse.pde.internal.ui.model.ifeature;
/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */


/**
 * A base class for plug-in and data entires
 */
public interface IFeaturePlugin extends IFeatureObject, IVersonable, IFeatureEntry {
	/**
	 * Returns whether this is a reference to a fragment.
	 * @return <samp>true</samp> if this is a fragment, <samp>false</samp> otherwise.
	 */
	public boolean isFragment();
}