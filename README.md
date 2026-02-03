# Multimedia Classwork (Multi-Module Maven Project)

This repository contains class activities organized as Maven modules within a parent project. Each activity is an independent module with its own `pom.xml`, but they are compiled and managed from the parent `pom.xml`.

## Prerequisites

- Maven Daemon (`mvnd`) installed and available in your `PATH`.

## Building the Entire Project

From the parent root directory (`Multimedios_Graf/`):

```bash
mvnd clean install
```

This will compile all modules listed in the `<modules>` section of the parent `pom.xml`.

## Running a Specific Module

To run the `main` method of a module (example: `homework_1`):

```bash
mvnd -pl homework_1 exec:java -Dexec.mainClass=com.github.donovan_dead.Main
```

- `-pl homework_1` indicates that you only want to work with the `homework_1` module.
- `-Dexec.mainClass` is the main class with the `public static void main` method.

Adjust the package and class name (`com.github.donovan_dead.Main`) according to your actual code.

## Packaging a Module into a JAR

To generate the JAR for a specific module:

```bash
mvnd -pl homework_1 package
```

The JAR file will be located at: `homework_1/target/homework_1-1.0.jar`

## Running the Generated JAR

Once packaged, you can run it with:

```bash
java -jar homework_1/target/homework_1-1.0.jar
```

## Typical Workflow

1.  **Compile everything:** `mvnd clean install`
2.  **Package a module:** `mvnd -pl homework_1 package`
3.  **Run the JAR:** `java -jar homework_1/target/homework_1-1.0.jar`

## Project Structure

```
Multimedios_Graf/
├── pom.xml              <-- Parent POM
├── homework_1/
│   └── pom.xml            <-- Child POM (homework_1)
└── ...
```

With this workflow, you can manage all your activities from the parent's root directory without having to manually go into each module.