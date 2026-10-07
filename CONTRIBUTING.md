<p align="center">
  <img src="https://raw.githubusercontent.com/IntelligenceModding/CommonTrades/refs/heads/assets/Project%20Title.png" alt="Common Trades" width="900">
</p>

<h1 align="center">Contributing to Common Trades</h1>

<p align="center">
  Thank you for your interest in contributing to Common Trades.
</p>

<p align="center">
  Contributions help improve the mod and are welcome in many forms, including bug fixes, trade discovery improvements, datapack tag improvements, configuration improvements, compatibility improvements, visual marker improvements, code improvements, documentation changes, and other useful additions.
</p>

<p align="center">
  This document explains how to report problems, set up the project for development, test changes, and submit pull requests.
</p>

<p align="center">
  <a href="https://discord.intelligence-modding.de/"><img src="https://img.shields.io/badge/Discord-Ask%20the%20Community-5865F2?style=for-the-badge&logo=discord&logoColor=white" alt="Ask the Intelligence Modding Community on Discord"></a>
  &nbsp;&nbsp;&nbsp;
  <a href="https://github.com/IntelligenceModding/CommonTrades/issues/new/choose"><img src="https://img.shields.io/badge/GitHub-Open%20an%20Issue-181717?style=for-the-badge&logo=github&logoColor=white" alt="Open a GitHub Issue"></a>
</p>

## Table of Contents

- [Reporting Issues](#reporting-issues)
- [Feature Requests](#feature-requests)
- [Choosing the Correct Branch](#choosing-the-correct-branch)
- [Setting Up a Development Environment](#setting-up-a-development-environment)
  - [Requirements](#requirements)
  - [Forking and Cloning the Repository](#forking-and-cloning-the-repository)
  - [Using IntelliJ IDEA](#using-intellij-idea)
- [Building and Running the Mod](#building-and-running-the-mod)
- [Developing Common Trades](#developing-common-trades)
  - [Testing](#testing)
  - [Code and Project Structure](#code-and-project-structure)
  - [Assets](#assets)
  - [Documentation](#documentation)
- [Pull Requests](#pull-requests)
- [Licensing](#licensing)
- [Getting Help](#getting-help)

## Reporting Issues

Reporting bugs is one of the easiest ways to contribute to Common Trades.

Before opening an issue:

- check whether the problem has already been reported;
- make sure you are using a Common Trades version intended for your Minecraft version;
- make sure your mod loader and required dependencies are compatible with the Common Trades version you are using;
- try to determine whether the issue can be reproduced consistently.

When reporting a bug, provide as much relevant information as possible, including:

- Minecraft version;
- Common Trades version;
- mod loader and version;
- other installed mods that may be relevant;
- clear steps to reproduce the problem;
- what you expected to happen;
- what actually happened;
- screenshots or videos where useful;
- crash reports or relevant logs when applicable.

Use the [GitHub issue tracker][new-issue] and select the appropriate issue form.

## Feature Requests

Suggestions for new trade categories, discovery rules, datapack tags, configuration options, visual marker improvements, debug command improvements, compatibility improvements, or other features are welcome.

Before creating a feature request:

- check existing issues for similar suggestions;
- explain what the feature would add to Common Trades;
- explain why it would be useful;
- include screenshots, mockups, references, or examples where useful.

For larger features or substantial changes to existing behavior, opening a feature request before beginning development is strongly recommended.

This allows the idea to be discussed before significant time is spent implementing it.

## Choosing the Correct Branch

Common Trades may use separate branches for different Minecraft versions and mod loaders.

You can view the available branches [here][branches].

When contributing, always work against the branch matching the Minecraft version and loader your change targets.

For example, a change developed for Minecraft 1.21.10 with NeoForge should target the corresponding 1.21.10 NeoForge branch.

Do **not** target the `assets` branch for code changes. The `assets` branch is used for project images and other repository assets.

If a bug or feature exists across several supported Minecraft versions, you normally only need to implement and submit the change for one appropriate branch unless a maintainer specifically asks you to port it to additional versions.

A contribution being accepted for one version does not guarantee that it will be backported or forward-ported to every other version.

## Setting Up a Development Environment

### Requirements

Before working on Common Trades, make sure you have the following installed:

- a Java Development Kit appropriate for the branch you are working on;
- [Git][git];
- an IDE or code editor.

[IntelliJ IDEA][idea] is recommended for Java development and Minecraft modding, although it is not required.

Different Minecraft branches may require different Java versions.

Check the following line in the branch's `build.gradle` file if you are unsure:

```gradle
java.toolchain.languageVersion = JavaLanguageVersion.of(...)
```

For example, the Minecraft 1.21.10 NeoForge branch uses Java 21.

Java builds such as Eclipse Temurin can be downloaded from [Adoptium][adoptium].

### Forking and Cloning the Repository

If you are contributing through a pull request, first fork the [Common Trades repository][repository] to your own GitHub account.

Clone your fork:

```bash
git clone https://github.com/YOUR_USERNAME/CommonTrades.git
cd CommonTrades
```

Add the original repository as an upstream remote:

```bash
git remote add upstream https://github.com/IntelligenceModding/CommonTrades.git
```

You can verify your remotes with:

```bash
git remote -v
```

Before beginning work, switch to the branch matching the Minecraft version you want to modify.

For example:

```bash
git switch 1.21.10-neoforge
```

It is recommended to create your own development branch from there.

For a feature:

```bash
git switch -c feature/your-feature-name
```

For a bug fix:

```bash
git switch -c fix/short-description
```

Keeping your changes on a separate branch makes it easier to update your fork and submit a focused pull request.

### Using IntelliJ IDEA

If you use IntelliJ IDEA:

1. Open IntelliJ IDEA.
2. Select **Get from Version Control**.
3. Enter the URL of your fork.
4. Allow IntelliJ to import the Gradle project.
5. Wait for Gradle to download the required dependencies and finish indexing the project.

The first Gradle import may take some time because Minecraft, the mod loader, mappings, and other dependencies need to be downloaded.

Subsequent imports and builds should generally be much faster.

If the project does not automatically use the correct Java version, make sure the Gradle JVM and project SDK match the Java version required by the branch's `build.gradle`.

## Building and Running the Mod

Common Trades includes the Gradle wrapper, so installing Gradle separately is normally not necessary.

### Building on Windows

```bat
gradlew.bat build
```

### Building on Linux / macOS

```bash
./gradlew build
```

A successful build will normally produce the compiled mod file in:

```text
build/libs/
```

Before submitting a pull request, make sure the project builds successfully.

### Running the Minecraft Client

On Windows:

```bat
gradlew.bat runClient
```

On Linux / macOS:

```bash
./gradlew runClient
```

When using IntelliJ IDEA, you can also use the generated Minecraft client run configuration or the corresponding Gradle `runClient` task.

Running the development client is strongly recommended for changes that affect generated trades, tags, config screens, visual markers, networking, commands, or other in-game behavior.

## Developing Common Trades

### Testing

Changes should be tested before being submitted.

There is currently no expectation that every contribution includes automated tests. For most Common Trades changes, practical in-game testing is more useful.

At minimum:

- make sure the project builds successfully;
- launch the development client for changes that affect the game;
- test the feature or bug fix directly;
- test obvious edge cases where applicable;
- make sure existing related functionality still works;
- check the game log for new errors or warnings caused by your changes.

For trade generation changes, test Wandering Trader spawning or offer generation and confirm vanilla trades are still preserved.

For datapack or tag changes, test `/reload` and verify discovered items with the debug command where applicable.

For config changes, test server config loading, category switches, price values, blacklists, and singleplayer or dedicated-server behavior where relevant.

For visual marker changes, test the Wandering Trader screen in-game and verify the marker and tooltip still match generated offers.

For networking changes, test server-to-client synchronization where possible and make sure missing or stale marker data is handled cleanly.

If your change cannot reasonably be tested in-game, explain how it was verified in the pull request.

### Code and Project Structure

Follow the existing project structure and coding style where practical.

Try to keep changes focused and avoid modifying unrelated files.

Do not include:

- generated build output;
- `.idea` files or other personal IDE settings;
- temporary development files;
- local run directories;
- generated or compiled mod `.jar` files;
- compiled `.class` files;
- extracted or decompiled Minecraft source files;
- extracted or decompiled mod-loader source files;
- unrelated formatting changes.

Tracked project files such as the Gradle wrapper and `gradle-wrapper.jar` should remain unchanged unless a change specifically requires updating them.

If you need to inspect Minecraft or mod-loader source code while developing, keep those reference files outside the tracked project source.

Avoid large unrelated refactors inside pull requests for bug fixes or small features.

### Assets

Contributions involving interface graphics, icons, textures, sounds, or other assets are welcome where they fit the style and purpose of Common Trades.

When contributing assets:

- try to match the existing visual style of the mod;
- keep Minecraft's interface and resource conventions in mind where appropriate;
- keep icons and other interface elements visually clear and consistent;
- use sensible and consistent file names;
- place assets in the correct resource directories;
- make sure you have permission to redistribute everything you submit.

Do not submit copyrighted textures, sounds, icons, models, or other assets taken from another project unless their license or the copyright holder allows their use.

If third-party material is used legally, clearly state:

- where it came from;
- who created it;
- what license or permission allows it to be used.

### Documentation

Documentation improvements are welcome.

This includes changes to:

- `README.md`;
- `CONFIG.md`;
- `DATAPACKS.md`;
- contribution and support files;
- user-facing descriptions;
- language files and translations;
- helpful code comments;
- other Markdown documentation contained in the repository.

Documentation changes should be clear, accurate, and relevant to Common Trades.

Avoid changing documentation solely for personal formatting preferences unless the change meaningfully improves readability, accuracy, or consistency.

## Pull Requests

Pull requests are welcome.

When opening a pull request:

- target the branch matching the Minecraft version and mod loader you developed against;
- use a clear title that summarizes the change;
- explain what you changed;
- explain why the change is useful or necessary;
- describe how you tested it;
- link related issues where applicable;
- include screenshots for visual or interface changes where useful;
- mention anything maintainers should know before reviewing the change.

Keep pull requests focused on one feature, bug fix, or closely related group of changes.

Large pull requests containing many unrelated changes are harder to review and may be asked to be separated.

Before submitting your pull request:

- make sure your branch is based on the correct project branch;
- update your branch if necessary;
- make sure there are no accidental files included;
- review your own changes;
- run a Gradle build;
- test the change in-game where applicable.

Maintainers may:

- ask questions;
- suggest improvements;
- request changes;
- make minor adjustments when integrating the contribution;
- decline a contribution that does not fit the direction or scope of the project.

A submitted pull request is not guaranteed to be merged.

## Licensing

By submitting code, documentation, textures, icons, sounds, or other material to Common Trades, you confirm that you have the right to contribute that material.

Unless explicitly agreed otherwise, contributions are made under the license used by the Common Trades repository.

Do not submit material whose license is incompatible with the project or that cannot legally be redistributed.

If your contribution contains or is derived from third-party material, clearly disclose that information in the pull request.

## Getting Help

If you have trouble setting up the development environment, are unsure which branch to use, or have questions about contributing, use one of the options below.

For large feature ideas, discussing the idea before spending significant time implementing it is recommended.

<p align="center">
  <a href="https://discord.intelligence-modding.de/"><img src="https://img.shields.io/badge/Discord-Ask%20the%20Community-5865F2?style=for-the-badge&logo=discord&logoColor=white" alt="Ask the Intelligence Modding Community on Discord"></a>
  &nbsp;&nbsp;&nbsp;
  <a href="https://github.com/IntelligenceModding/CommonTrades/issues/new/choose"><img src="https://img.shields.io/badge/GitHub-Open%20an%20Issue-181717?style=for-the-badge&logo=github&logoColor=white" alt="Open a GitHub Issue"></a>
</p>

[repository]: https://github.com/IntelligenceModding/CommonTrades "Common Trades GitHub Repository"
[new-issue]: https://github.com/IntelligenceModding/CommonTrades/issues/new/choose "Create a New Issue"
[branches]: https://github.com/IntelligenceModding/CommonTrades/branches "Common Trades Branches"
[git]: https://git-scm.com/ "Download Git"
[idea]: https://www.jetbrains.com/idea/ "IntelliJ IDEA"
[adoptium]: https://adoptium.net/temurin/releases/ "Eclipse Temurin OpenJDK"
