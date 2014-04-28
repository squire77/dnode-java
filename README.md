dnode-java
==========

DNode protocol implementation in Java

DNode provides a remote invocation facility in Javascript whereby callbacks are passed as parameters.
This allows a remote server to invoke your local methods and vice versa.

The dnode-java project enables interworking of Java with Javascript via the DNode protocol. 
Thus, javascript programs can invoke java remote methods or vice versa.

The dnode-java project has not been updated wince 2011. Within the original branch are a number of issues.
In particular, the Java reflective calls are broken due to a reliance on generic types which go away
due to 'type erasure' (an optimization whereby generic types are replaced with actual type-specific code, resulting
in the referenced type itself being erased). The solution is to pass the type explicitly so it can be referenced.
Other updates include fixes to the use of generic types and updates to enabled compatibility with later external libraries versions. 

The JUNIT tests have also been updated to reflect the changes and test.
