#FAQ

####Installing java

If java is not installed on your system then go to [java.com](java.com) and download the appropriate version for your system.

####Setting the PATH environment variable
For instructions on locating and editing your PATH environment, visit [https://java.com/en/download/help/path.xml](https://java.com/en/download/help/path.xml) for more information.

For **Windows**, check to see if your a PATH environment variable exists. If it does not exist, create one and add to it the path to your java bin directory.
If a PATH environment variable already exists, then add ";" (without quotation marks) to the end of the PATH variable and then add the path to your java bin directory.

For example:
`Variable name: Path`
`Variable value: C:\Program Files(x86)\Java\jre7\bin`

For **Linux/Mac OS X**, you must locate the directory where you've installed java in. Then you need to add the bin directory to your PATH environment variable.
To add the bin directory to your PATH environment variable, you must add the following line to your bash startup file (~/ .bashrc):
 
`export PATH="$PATH":java-bin-directory` (replacing java-bin-directory with your java bin directory)

For example:
`export PATH="$PATH":/usr/lib/jvm/jdk1.8.0_45/bin`

Adding it to your bash startup file will set the PATH environment variable automatically every time bash is opened.

To check if it was set successfully, enter the command into the terminal: *`java -version`*

####I received the error: 
#####* 'java' is not recognized as an internal or external command, operable program or batch file.
#####* java: command not found
####What's wrong?
Receiving this error either means that java is not installed on your system or that your PATH environment variable is not set to contain the path to java.

Check your program list or program directory to see if java is installed on your system. If java is not installed on your system, see above for instructions on installing java.

If java is installed on your system and you still receive this error, then you need to configure your PATH environment to include the java bin directory. See above for instructions on setting the PATH environment variable.

####Found an issue or an error not listed here?
Open up an issue on the repository and post your `log4j-application.log` file on there.