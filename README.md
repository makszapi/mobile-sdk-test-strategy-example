# Mobile SDK Quality Engineering assignment

A quality-engineering approach for an internal mobile SDK used by multiple iOS and Android applications: test strategy, an automation example, CI/CD and release validation, an investigation scenario, initial priorities, and risks.

## Where to find things

| Path | Content |
| --- | --- |
| [`docs/Assignment_scafold.md`](./docs/Test_Strategy_And_Assignment_Exercises.md) | The main document with all six sections, including the compatibility approach and key assumptions |
| [`docs/Checks_within_CI_flow.png`](./docs/Checks_within_CI_flow.png) | CI flow diagram used in the document |
| [`test-examples/`](./test-examples/) | Automation example: contract tests for networking and error handling on iOS (XCTest) and Android (JUnit), with its own [README](./test-examples/README.md) |
| [`workflow-examples/`](./workflow-examples/) | Example GitHub Actions workflows for the separate iOS and Android SDK repositories, with its own [README](./workflow-examples/README.md) |

## Running the examples

There is nothing to install or run. The test code and the workflows are illustrative: they are written against an assumed SDK and assumed repositories to show test design and pipeline structure, not to execute.

## Known limitations

- The examples are not executable. The SDK types, schemes, Gradle modules, and helper scripts they reference do not exist in this repository.
- The automation example covers one capability (networking and error handling) at one test level (contract tests).
- The shared cross-platform contract specification is described but not implemented. The iOS and Android tests keep their scenarios and fixtures inline.
- The workflows were checked for valid YAML syntax only. They were never executed on GitHub Actions.

## What I would improve with more time

- Make the automation example runnable: a small stub SDK as a Swift package and a Gradle module, so that the contract tests execute in CI on both platforms.
- Drive both platforms' contract tests from one shared scenario file and add the scenario coverage check.
- Add an integration-level example for a combination of two modules.
- Add a Demo application smoke test against a packaged SDK artifact.

## Questions for the team

1. How are modules versioned and distributed today? Do they follow semantic versioning, and are they released independently or as one set?
2. Who owns each module and its tests, and how are changes that span several modules coordinated?
3. Is there a stable backend test environment, and is the backend API described by a specification that fixtures can be validated against?
4. What does the existing automation look like per test level, and what are the current pipeline durations, pass rates, and known unstable areas?
5. Where did recent regressions and late integration issues come from, and how were they found?
6. Where is iOS and Android behavior expected to be identical, where are differences intentional, and what is the source of truth today?
7. What is the release cadence, and is there a release-candidate stage today?

## AI usage

- **Where it was used:** Claude Code was used to draft README.md files, review documentation for typos and broken .md formatting, and draft the workflow examples.
- **How the output was validated:** I carefully reviewed and adjusted AI-drafted files (workflow examples and README.md files) as needed without unquestioningly accepting AI suggestions. I made the main technical and testing decisions independently, based on my experience and analysis of the assignment.
