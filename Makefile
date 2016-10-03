all: CSftp.jar
CSftp.jar: CSftp.java
	javac CSftp.java
	javac Command.java
	javac FtpHandler.java
	javac InvalidCommandException.java
	javac ProcessingException.java
	javac WrongNumberOfArgumentsException.java
	jar cvfe CSftp.jar CSftp *.class


run: CSftp.jar  
	java -jar CSftp.jar ftp.gnu.org  21

clean:
	rm -f *.class
	rm -f CSftp.jar
