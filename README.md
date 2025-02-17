Eclipse PDE - Plug-in Development Environment
=====================================================

Project description:
--------------------

* 's goal
  * 👀develop plug-ins & OSGI bundles 👀
* -- provide -- tools / create, develop, test, debug, build and deploy
  * Eclipse plug-ins, fragments, features,
  * update sites
  * RCP products 
  * OSGi components
* 💡built | Eclipse Platform & Eclipse JDT 💡
* part of Eclipse SDK
* 👀== UI + API Tools + Build + Doc + Incubator 👀
  * UI
    * == set of models, tools / facilitate developing 
      * plug-ins
      * OSGi bundles
  * API Tools
    * == IDE + build process integrated tooling / maintain API
  * Build
    * == Ant-based tools & scripts / 
      * automate build processes
      * -- based on -- development-time information (_Example:_ plugin.xml and build.properties)
      * can
        * fetch the relevant projects -- from a -- CVS repository
        * build jars, Javadoc, source zips,
        * put ALL together / ready to ship & send it out to a remote location (_Example:_ local network or a downloads server)
  * Doc
    * -- handles the -- help documentation
  * Incubator
    * -- develops -- NON-SDK features
    
* [Website](https://projects.eclipse.org/projects/eclipse.pde)

Documentation
-----
* [docs](docs)
* [help documentation](org.eclipse.pde.doc.user/toc.xml)

How to build it?
--------------------------------

* requirements
  * Maven v3.3.1+
* `mvn clean verify`

Contact:
--------

* [Github discussions](https://github.com/eclipse-pde/eclipse.pde/discussions)

License
-------

[Eclipse Public License (EPL) v2.0](https://www.eclipse.org/legal/epl-2.0/)
