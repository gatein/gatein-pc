- Main project file and modules:
Copy jboss-portal.ipr to the root of the Portal source directory. You might need to change the JDK to use but
it should otherwise work out the box. Alternatively, you can create a new project file at the root of your Portal
installation and import the module files (*.iml) found in the modules directory manually.

- Codestyles and headers: 
The config directory contains standard settings for the JBoss Portal project regarding headers and code style. If
you intend to commit code to JBoss Portal, you need to update your IDEA installation to use these files. Please
refer to the IDEA manual on how to precisely do it, the short version being that you can replace the files found
in your IDEA preference directory by the ones found in the config directory.
