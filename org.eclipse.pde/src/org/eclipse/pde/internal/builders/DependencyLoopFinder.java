/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.internal.builders;

/**
 * @version 	1.0
 * @author
 */

import java.util.*;

import org.eclipse.pde.core.plugin.*;
import org.eclipse.pde.internal.*;
import org.eclipse.pde.internal.core.*;

public class DependencyLoopFinder {
	private static final String KEY_LOOP_NAME = "Builders.DependencyLoopFinder.loopName";	
	
	public static DependencyLoop [] findLoops(IPlugin root) {
		return findLoops(root, null);
	}

	public static DependencyLoop [] findLoops(IPlugin root, IPlugin [] candidates) {
		return findLoops(root, candidates, false);
	}
	
	public static DependencyLoop [] findLoops(IPlugin root, IPlugin [] candidates, boolean onlyCandidates) {
		Vector loops = new Vector();
		
		Vector path = new Vector();
		findLoops(loops, path, root, candidates, onlyCandidates, new Vector());
		return (DependencyLoop[])loops.toArray(new DependencyLoop[loops.size()]);
	}
	
	private static void findLoops(
		Vector loops,
		Vector path,
		IPlugin subroot,
		IPlugin[] candidates,
		boolean onlyCandidates,
		Vector exploredPlugins) {
		if (path.size() > 0) {
			// test the path so far
			// is the subroot the same as root - if yes, that's it

			IPlugin root = (IPlugin) path.elementAt(0);
			if (isEquivalent(root, subroot)) {
				// our loop!!
				DependencyLoop loop = new DependencyLoop();
				loop.setMembers((IPlugin[]) path.toArray(new IPlugin[path.size()]));
				String pattern = PDE.getResourceString(KEY_LOOP_NAME);
				int no = loops.size() + 1;
				loop.setName(PDE.getFormattedMessage(pattern, ("" + no)));
				loops.add(loop);
				return;
			}
			// is the subroot the same as any other node?
			// if yes, abort - local loop that is not ours
			for (int i = 1; i < path.size(); i++) {
				IPlugin node = (IPlugin) path.elementAt(i);
				if (isEquivalent(subroot, node)) {
					// local loop
					return;
				}
			}
		}
		Vector newPath = path.size() > 0 ? ((Vector) path.clone()) : path;
		newPath.add(subroot);

		if (!onlyCandidates) {
			IPluginImport[] iimports = subroot.getImports();
			for (int i = 0; i < iimports.length; i++) {
				IPluginImport iimport = iimports[i];
				String id = iimport.getId();
				//Be paranoid
				if (id == null)
					continue;
				if (!exploredPlugins.contains(id)) {
					// is plugin in list of non loop yielding plugins
					//Commenting linear lookup - was very slow 
					//when called from here. We will use
					//model manager instead because it
					//has a hash table lookup that is much faster.
					//IPlugin child = PDECore.getDefault().findPlugin(id);
					IPlugin child = findPlugin(id);
					if (child != null) {
						// number of loops before traversing plugin
						int oldLoopSize = loops.size();

						findLoops(loops, newPath, child, null, false, exploredPlugins);

						// number of loops after traversing plugin
						int newLoopsSize = loops.size();

						if (oldLoopSize == newLoopsSize) {// no change in number of loops
							// no loops from going to this node, skip next time
							exploredPlugins.add(id);
						}
					}
				}

			}

		}
		if (candidates != null) {
			for (int i = 0; i < candidates.length; i++) {
				IPlugin candidate = candidates[i];

				// number of loops before traversing plugin
				int oldLoopSize = loops.size();

				findLoops(loops, newPath, candidate, null, false, exploredPlugins);

				// number of loops after traversing plugin
				int newLoopsSize = loops.size();

				if (oldLoopSize == newLoopsSize) { // no change in number of loops
					// no loops from going to this node, skip next time
					exploredPlugins.add(candidate.getId());
				}
			}
		}

	}

	private static IPlugin findPlugin(String id) {
		IPluginModelBase childModel = PDECore.getDefault().getModelManager().findPlugin(id, null, 0);
		if (childModel==null || !(childModel instanceof IPluginModel)) return null;
		return (IPlugin)childModel.getPluginBase();
	}
	
	private static boolean isEquivalent(IPlugin left, IPlugin right) {
		return left.getId().equals(right.getId());
	}
}