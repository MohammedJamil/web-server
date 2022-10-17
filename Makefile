SOURCE_DIR = src
BUILD_DIR = build
COMPILE_OPTS = -d build -cp build
EXEC_OPTS = -ea -cp build


build:
	javac $(COMPILE_OPTS) $(SOURCE_DIR)/*.java
	
run:
	java $(EXEC_OPTS) ServerApp

clean:
	rm -r build