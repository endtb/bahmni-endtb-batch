# bahmni-endtb-batch

[![Build Status](https://snap-ci.com/Bahmni/bahmni-endtb-batch/branch/master/build_image)](https://snap-ci.com/Bahmni/bahmni-endtb-batch/branch/master)

Standalone Batch Application based on spring-batch. This application will create various CSV export files like Patient Information, Program Enrollment Information, Drug Orders (TB and Non-TB), Various forms filled by the users (Observation Templates), Bacteriology forms information

## Building the project

To build the project, in a terminal from the project directory run:

./gradlew clean build

## Executing from the command line

The above build step will produce a jar file under build/libs/bahmni-batch-x.y.z.jar

This is an executable jar file that can be run as follows:

```java -jar <path_to_this_folder>/build/libs/bahmni-batch-2.3.5.jar > output.log```

This will expect to read in parameters which can be defined in-line or via an application.properties file.

A good approach is to create a new directory in which to execute the project:

mkdir ~/bahmni-batch
cd bahmni-batch

In this folder, create an application.properties file that specifies the appropriate database credentails and 
file system paths.

Then execute the above command from this folder.
