package org.eclipse.pde.internal.core;
/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.eclipse.core.boot.BootLoader;
import org.eclipse.core.boot.IPlatformRunnable;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.model.*;
import org.eclipse.update.core.Feature;

public abstract class PluginTool implements IPlatformRunnable, ScriptGeneratorConstants {
	private boolean usage = false;
	private PluginRegistryModel registry = null;
	private URL pluginPath = null;
	private ArrayList plugins = new ArrayList(9);
	private String install = null;
	private List devEntries = null;
	private Hashtable propertyValues = new Hashtable(9);
	private MultiStatus problems = new MultiStatus(PI_PDECORE,IStatus.OK,Policy.bind("label.generationProblems"),null);
	protected String os = BootLoader.getOS();
	protected String ws = BootLoader.getWS();
	protected String nl = BootLoader.getNL();
	// FIXME: it should be moved to BootLoader as well
	protected String arch = System.getProperty("os.arch");
	protected String stamp = "";
	
	public final static String PI_PDECORE = "org.eclipse.pde.core";	
	private static final String USAGE = "-?";
	private static final String PLUGINS = "-plugins";
	private static final String INSTALL = "-install";
	private static final String DEV_ENTRIES = "-dev";
	private static final String OS_ARG = "-os";
	private static final String WS_ARG = "-ws";
	private static final String NL_ARG = "-nl";
	private static final String STAMP = "-stamp";
	private static final String PROPERTYASSIGNMENT_PREFIX = "${";
	private static final String PROPERTYASSIGNMENT_SUFFIX = "}";

/**
 * Deletes all the files and directories from the given root down (inclusive).
 * Returns false if we could not delete some file or an exception occurred
 * at any point in the deletion.
 * Even if an exception occurs, a best effort is made to continue deleting.
 */
public static boolean clear(File root) {
	boolean result = true;
	if (root.isDirectory()) {
		String[] list = root.list();
		// for some unknown reason, list() can return null.  
		// Just skip the children If it does.
		if (list != null)
			for (int i = 0; i < list.length; i++)
				result &= clear(new java.io.File(root, list[i]));
	}
	try {
		if (root.exists())
			result &= root.delete();
	} catch (Exception e) {
		result = false;
	}
	return result;
}
public static ILog getPluginLog() {
	return Platform.getPlugin(PI_PDECORE).getLog();
}
protected void addProblem(IStatus problem) {
	problems.add(problem);
}
protected void addProblems(IStatus problem) {
	problems.addAll(problem);
}
protected String extractPropertyName(String propertyString) {
	String prefixRemoved = propertyString.substring(PROPERTYASSIGNMENT_PREFIX.length());
	return prefixRemoved.substring(0,prefixRemoved.length() - PROPERTYASSIGNMENT_SUFFIX.length());
}
/**
 * convert a list of comma-separated tokens into an array
 */
public static String[] getArrayFromString(String list) {
	return getArrayFromString(list, ",");
}
/**
 * Convert a list of tokens into an array. The list separator has to be specified.
 */
public static String[] getArrayFromString(String list, String separator) {
	if (list == null || list.trim().equals(""))
		return new String[0];
	ArrayList result = new ArrayList();
	for (StringTokenizer tokens = new StringTokenizer(list, separator); tokens.hasMoreTokens();) {
		String token = tokens.nextToken().trim();
		if (!token.equals(""))
			result.add(token);
	}
	return (String[]) result.toArray(new String[result.size()]);
}

protected String getStringFromCollection(Collection list, String prefix, String suffix, String separator) {
	StringBuffer result = new StringBuffer();
	boolean first = true;
	for (Iterator i = list.iterator(); i.hasNext();) {
		if (!first)
			result.append(separator);
		first = false;
		result.append(prefix);
		result.append((String) i.next());
		result.append(suffix);
	}
	return result.toString();
}
protected PluginDescriptorModel[] getDescriptors() {
	PluginDescriptorModel[] descriptors = null;
	if (plugins == null || plugins.isEmpty())
		descriptors = registry.getPlugins();
	else {
		ArrayList list = new ArrayList(plugins.size());
		for (int i = 0; i < plugins.size(); i++) {
			PluginDescriptorModel descriptor = registry.getPlugin((String) plugins.get(i));
			if (descriptor != null)
				list.add(descriptor);
		}
		descriptors = (PluginDescriptorModel[]) list.toArray(new PluginDescriptorModel[list.size()]);
	}
	return descriptors;
}
protected List getDevEntries() {
	return devEntries;
}
protected String getInstall() {
	return install;
}
/**
 * convert a list of comma-separated tokens into an array
 */
protected List getListFromString(String prop) {
	if (prop == null || prop.trim().equals(""))
		return new ArrayList(0);
	ArrayList result = new ArrayList();
	for (StringTokenizer tokens = new StringTokenizer(prop, ","); tokens.hasMoreTokens();) {
		String token = tokens.nextToken().trim();
		if (!token.equals(""))
			result.add(token);
	}
	return result;
}
protected URL[] getPluginPath() {
	// get the plugin path.  If one was spec'd on the command line, use that.
	// Otherwise, if the install location was spec'd, compute the default path.
	// Finally, if nothing was said, allow the system to figure out the plugin
	// path based on the current running state.
	if (pluginPath == null && install != null) {
		try {
			return new URL[] { new URL("file:" + install + "/plugins/")};
		} catch (MalformedURLException e) {
		}
	} else {
		return BootLoader.getPluginPath(pluginPath);
	}
	return null;
}
protected IStatus getProblems() {
	return problems;
}
protected Properties getProperties(Feature descriptor) {
	Properties result = (Properties)propertyValues.get(descriptor);
	if (result != null)
		return result;

	result = readProperties(descriptor.getURL().toString() + "/");
	result = filterProperties(result);
	propertyValues.put(descriptor, result);
	return result;
}
protected Properties getProperties(PluginModel descriptor) {
	Properties result = (Properties)propertyValues.get(descriptor);
	if (result != null)
		return result;

	result = readProperties(descriptor.getLocation());
	result = filterProperties(result);
	propertyValues.put(descriptor,result);
	return result;
}
/**
 * Filters and merges properties that are relative to the current
 * build, based on the values of the build variables (os, ws, nl and arch).
 */
protected Properties filterProperties(Properties target) {
	for(Enumeration keys = target.keys(); keys.hasMoreElements(); ) {
		String key = (String) keys.nextElement();
		if (!key.startsWith(PROPERTYASSIGNMENT_PREFIX))
			continue;
		if (matchesCurrentBuild(key)) {
			String value = target.getProperty(key);
			if (value != null) {
				String realKey = extractRealKey(key);
				String currentValue = target.getProperty(realKey);
				if (currentValue != null) {
					if (!contains(getArrayFromString(currentValue), value))
						value = currentValue + "," + value;
					else
						value = currentValue;
				}
				target.put(realKey, value);
			}
		}
		target.remove(key);
	}
	return target;
}
/**
 * Checks if the given element is already present in the list.
 * This method is case sensitive.
 */
protected boolean contains(String[] list, String element) {
	for (int i = 0; i < list.length; i++) {
		String string = list[i];
		if (string.equals(element))
			return true;
	}
	return false;
}

/**
 * Removes build specific variables from this key.
 * For example ${os/linux,ws/motif}.bin.includes
 * becomes bin.includes
 */
protected String extractRealKey(String target) {
	int index = target.indexOf(PROPERTYASSIGNMENT_SUFFIX);
	String result = target.substring(index + PROPERTYASSIGNMENT_SUFFIX.length() + 1);
	return result;
}

/**
 * Checks if the given key should be included in the current
 * build by looking into the build variables defined with it.
 * For example ${os/linux,ws/motif}.bin.includes is targeted
 * for a linux-motif build and should not be part of a
 * Windows build.
 */
protected boolean matchesCurrentBuild(String key) {
	int prefix = key.indexOf(PROPERTYASSIGNMENT_PREFIX);
	int suffix = key.indexOf(PROPERTYASSIGNMENT_SUFFIX);
	String[] variables = getArrayFromString(key.substring(prefix + PROPERTYASSIGNMENT_PREFIX.length(), suffix));
	for (int i = 0; i < variables.length; i++) {
		String[] var = getArrayFromString(variables[i], "/");
		if (var[1].equals("*"))
			continue;
		if (var[0].equalsIgnoreCase(BUILD_VAR_OS)) {
			if (!var[1].equalsIgnoreCase(os))
				return false;
			continue;
		}
		if (var[0].equalsIgnoreCase(BUILD_VAR_WS)) {
			if (!var[1].equalsIgnoreCase(ws))
				return false;
			continue;
		}
		if (var[0].equalsIgnoreCase(BUILD_VAR_NL)) {
			if (!var[1].equalsIgnoreCase(nl))
				return false;
			continue;
		}
		if (var[0].equalsIgnoreCase(BUILD_VAR_ARCH)) {
			if (!var[1].equalsIgnoreCase(arch))
				return false;
			continue;
		}
	}
	return true;
}
protected Map getPropertyAssignments(Feature descriptor) {
	return getPropertyAssignments(getProperties(descriptor));
}
protected Map getPropertyAssignments(PluginModel descriptor) {
	return getPropertyAssignments(getProperties(descriptor));
}
protected Map getPropertyAssignments(Properties properties) {
	HashMap result = new HashMap(9);	
	Iterator propertiesEnum = properties.keySet().iterator();
	while (propertiesEnum.hasNext()) {
		String current = (String)propertiesEnum.next();
		if (isPropertyAssignment(current))
			result.put(extractPropertyName(current),properties.get(current));
	}
	
	return result;
}
protected PluginRegistryModel getRegistry() {
	return registry;
}
protected String getSubstitution(PluginModel descriptor,String propertyName) {
	return (String)getProperties(descriptor).get(propertyName);
}
protected String getSubstitution(Feature descriptor, String propertyName) {
	return (String)getProperties(descriptor).get(propertyName);
}
protected boolean isPropertyAssignment(String key) {
	return
		key.startsWith(PROPERTYASSIGNMENT_PREFIX) &&
		key.endsWith(PROPERTYASSIGNMENT_SUFFIX);
}
protected String makeRelative(String location, IPath base) {
	IPath path = new Path(location);
	if (path.getDevice() != null && !path.getDevice().equalsIgnoreCase(base.getDevice()))
		return location.toString();
	int baseCount = base.segmentCount();
	int count = base.matchingFirstSegments(path);
	if (count > 0) {
		String temp = "";
		for (int j = 0; j < baseCount - count; j++)
			temp += "../";
		path = new Path(temp).append(path.removeFirstSegments(count));
	}
	return path.toString();
}
/**
 * Print the usage of this launcher on the system console
 */
protected abstract void printUsage(PrintWriter out);
protected String[] processCommandLine(String[] args) {
	for (int i = 0; i < args.length; i++) {
		// check for args without parameters (i.e., a flag arg)

		// look for the usage flag
		if (args[i].equals(USAGE))
			usage = true;

		// check for args with parameters
		if (i == args.length - 1 || args[i + 1].startsWith("-")) 
			continue;
		String arg = args[++i];

		// check for the plugin path arg
		if (args[i - 1].equalsIgnoreCase(PLUGINS))
			try {
				pluginPath = new URL(arg);
			} catch (MalformedURLException e) {
				try {
					pluginPath = new URL("file:" + arg);
				} catch (MalformedURLException e2) {
				}
			}

		// check for the OS arg
		if (args[i - 1].equalsIgnoreCase(OS_ARG))
			os = arg;
			
		// check for the WS arg
		if (args[i - 1].equalsIgnoreCase(WS_ARG))
			ws = arg;
			
		// check for the NL arg
		if (args[i - 1].equalsIgnoreCase(NL_ARG))
			nl = arg;
		
		// check for the STAMP arg
		if (args[i - 1].equalsIgnoreCase(STAMP))
			stamp = arg;
			
		// check for the install location arg
		if (args[i - 1].equalsIgnoreCase(INSTALL))
			install = arg;
			
		// set the additional development model class path entries
		if (args[i - 1].equalsIgnoreCase(DEV_ENTRIES))
			devEntries = getListFromString(arg);
	}
	return new String[0];
}
protected String[] readElementFile(String filename) {
	Properties props = new Properties();
	try {
		InputStream stream = new FileInputStream(filename);
		try {
			props.load(stream);
		} finally {
			stream.close();
		}
	} catch (IOException e) {
	}
	return (String[])props.keySet().toArray(new String[props.size()]);
}
protected Properties readProperties(String modelDirectory) {
	Properties result = new Properties();
	
	try {
		URL propertiesFile = new URL(modelDirectory + ScriptGeneratorConstants.FILENAME_PROPERTIES);
		InputStream is = propertiesFile.openStream();
		try {
			result.load(is);
		} finally {
			is.close();
		}
	} catch (IOException e) {
		// if the file does not exist then we'll use default values, which is fine
	}
	
	return result;
}
public Object run(Object args) throws Exception {
	processCommandLine((String[]) args);
	if (usage) {
		printUsage(new PrintWriter(System.out));
		return null;
	}
	URL[] path = getPluginPath();
	MultiStatus problems = new MultiStatus(PluginTool.PI_PDECORE,ScriptGeneratorConstants.EXCEPTION_MODEL_PARSE,Policy.bind("exception.pluginParse"),null);
	Factory factory = new Factory(problems);
	registry = Platform.parsePlugins(path, factory);
	return null;
}
protected String[] separateNameFromVersion(String name) {
	String result[] = new String[2];
	int lastSeparator = name.lastIndexOf(SEPARATOR_VERSION);
	if (lastSeparator == -1) {
		result[0] = name;
		result[1] = new String();
		return result;
	}
	
	String versionPortion = name.substring(lastSeparator + 1);
	try {
		new PluginVersionIdentifier(versionPortion);
	} catch (NumberFormatException e) {
		result[0] = name;
		result[1] = new String();
		return result;
	}		
	
	result[0] = name.substring(0,lastSeparator);
	result[1] = versionPortion;
	
	return result;
}
public void setDevEntries(List value) {
	devEntries = value;
}
protected void setInstall(String value) {
	install = value;
}
protected void setRegistry(PluginRegistryModel value) {
	registry = value;
}
/**
 * Run this launcher with the arguments specified in the given string.
 * This is a short cut method for people running the launcher from
 * a scrapbook (i.e., swip-and-doit facility).
 */
public static String[] tokenizeArgs(String argString) throws Exception {
	Vector list = new Vector(5);
	for (StringTokenizer tokens = new StringTokenizer(argString, " "); tokens.hasMoreElements();)
		list.addElement((String) tokens.nextElement());
	return (String[]) list.toArray(new String[list.size()]);
}
}
