File Transfer Protocol Client
=
Program Description
-
This is a simple FTP client implemented by Laura Barton and Sean Blair for Assignment 1 of UBC CPSC 317 2016W1.

Dependencies
-
Developed and tested with Java 1.8 on a Linux ubuntu 14.04 LTS machine

Running instructions
-
- Open a terminal and clone the repo: git clone https://github.com/SeanBlair/FTPClient.git
- Enter the folder: cd FTPClient
- Run Makefile: make run  (Or alternatively to use another FTP server: make; then: java -jar CSftp.jar [ftpServerName] 21)

The FTP client supports the following commands as well as a variety of error messages:
- **user [username]** Provides the FTP server a username.
- **pw [password]**   Provides the FTP server a password.
- **dir**             Opens a new data TCP connection and lists the contents of the current directory.
- **cd [directoryName]** Navigates to the given directoryName.
- **get [fileName]**  Opens a new data TCP connection and retrieves the file named fileName.
- **quit**            Closes the current FTP session.

Example Usage Demo
-
user anonymous

pw lkj

dir

get welcome.msg

cd old-gnu

dir

get malloc.tar.gz

cd ..

dir

quit

Note your two freshly transferred files: welcome.msg and malloc.tar.gz     

:)


