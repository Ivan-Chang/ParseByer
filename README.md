# ParseByer
##Introduction
ParseByer is a Java library to parse everything (just supported json now) to Java Bean.

##How to Use
> Just supported used By adding source code.

###How to add into the project
1. add annotation library to project.
2. add compiler library to project.
3. add classpath android-apt to the dependencies of the buildscript of the project's build.gradle, like this

	~~~
	classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8'
	~~~

4. add the plugin 'com.neenbedankt.android-apt' in app library

	~~~
	apply plugin: 'com.neenbedankt.android-apt'
	~~~

5. add module dependency to app ':annotation' and ':compiler' like this:

	~~~
	compile project(':annotation')
    apt project(':compiler')
	~~~ 
	
###How to use in project
1. The bean you want to parse to needs annotation 'ParseByJson'
2. ParseBy#parseBy(Class, String) uses to parse json string to Class which is bean's class
3. ParseBy#parseBy(Class) uses to create Bean by test data
4. ParseBy#parseTo(T) uses to parse Bean to json string

### Using demo
The demo is the code's app library.
