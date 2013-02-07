In order to run the tests with JBoss AS 7, you need to copy a version of AS 7 to the 
test/servers/jboss7/run/target directory and edit test/core/src/test/resources/arquillian.xml
so that the jbossHome property points to your AS 7 directory (path being relative to the 
test/servers/jboss7/run) directory.
