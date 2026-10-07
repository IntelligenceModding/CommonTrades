<h1 align="center">Contributing to Common Trades</h1>

Thank you for your interest in contributing to Common Trades.

This document explains how to report problems, set up the project for development, test changes, and submit pull requests.

## Reporting Issues

Before opening an issue:

- check whether the problem has already been reported;
- make sure you are using a Common Trades version intended for your Minecraft version;
- make sure your mod loader and dependencies are compatible;
- try to determine whether the issue can be reproduced consistently.

Use the [GitHub issue tracker][new-issue] and select the appropriate issue form.

## Feature Requests

Suggestions for new features, compatibility improvements, documentation, or other useful additions are welcome.

For larger features or substantial behavior changes, opening a feature request before beginning development is strongly recommended.

## Choosing the Correct Branch

Common Trades uses separate branches for different Minecraft versions and mod loaders.

When contributing, work against the branch matching the Minecraft version and loader your change targets.

For Minecraft 1.21.1 with NeoForge, target `1.21.1-neoforge`.

## Setting Up a Development Environment

Requirements:

- Java 21;
- [Git][git];
- an IDE or code editor.

[IntelliJ IDEA][idea] is recommended for Java development and Minecraft modding.

Clone the repository:

```bash
git clone https://github.com/IntelligenceModding/CommonTrades.git
cd CommonTrades
git switch 1.21.1-neoforge
```

Create a focused development branch:

```bash
git switch -c feature/your-feature-name
```

## Building and Running

Build on Windows:

```bat
gradlew.bat build
```

Build on Linux or macOS:

```bash
./gradlew build
```

Run the development client:

```bash
./gradlew runClient
```

## Code and Project Structure

Follow the existing project structure and coding style where practical.

Keep changes focused and avoid modifying unrelated files.

Do not include generated build output, personal IDE settings, temporary files, local run directories, compiled jars, compiled classes, or extracted/decompiled Minecraft or mod-loader source files.

## Pull Requests

When opening a pull request:

- target the branch matching the Minecraft version and mod loader you developed against;
- use a clear title;
- explain what changed and why;
- describe how you tested it;
- link related issues where applicable;
- include screenshots for visual changes when useful.

## Licensing

By submitting code, documentation, textures, models, sounds, or other material to Common Trades, you confirm that you have the right to contribute that material.

Unless explicitly agreed otherwise, contributions are made under the license used by this repository.

[repository]: https://github.com/IntelligenceModding/CommonTrades "Common Trades GitHub Repository"
[new-issue]: https://github.com/IntelligenceModding/CommonTrades/issues/new/choose "Create a New Issue"
[git]: https://git-scm.com/ "Download Git"
[idea]: https://www.jetbrains.com/idea/ "IntelliJ IDEA"
