# CI/CD workflow examples

**Tooling:** GitHub Actions  
**Repositories:** separate iOS SDK and Android SDK repositories

## Goal and assumptions

These workflows illustrate the CI/CD and release validation section of the [main document](../docs/Test_Strategy_And_Assignment_Exercises.md). They follow the three stages of the [CI flow diagram](../docs/Checks_within_CI_flow.png): pull request checks, main branch checks, and pre-release checks.

The iOS and Android SDKs are assumed to live in **separate repositories**, not in a monorepo. Each folder here represents the `.github/workflows/` directory of one repository, so both platforms have the same three workflows with native tooling.

The workflows are intentionally **not runnable**. They use GitHub Actions syntax and real tool commands, but schemes, test plans, Gradle modules, `scripts/` helpers, secrets, and the `example-org` organization are placeholders.

## Layout

| Stage in the CI flow | Trigger | iOS SDK repository | Android SDK repository |
| --- | --- | --- | --- |
| PR checks | Pull request to `main` | [`ios/pr-checks.yml`](./ios/pr-checks.yml) | [`android/pr-checks.yml`](./android/pr-checks.yml) |
| Main branch checks | Push to `main`, nightly schedule | [`ios/main-checks.yml`](./ios/main-checks.yml) | [`android/main-checks.yml`](./android/main-checks.yml) |
| Pre-release checks | Tag `vX.Y.Z-rc.N` | [`ios/release-candidate.yml`](./ios/release-candidate.yml) | [`android/release-candidate.yml`](./android/release-candidate.yml) |

## What each stage shows

| Workflow | Checks | Quality gate |
| --- | --- | --- |
| `pr-checks.yml` | Static analysis, unit tests, contract tests, selected integration tests | Required status checks, 15-minute job budget, no retries |
| `main-checks.yml` | Full integration tests, Demo app build and smoke tests against the packaged SDK, critical UI/E2E tests, quarantined tests | Failure notifies the team channel and opens one tracking issue |
| `release-candidate.yml` | Public API diff against the last release, full regression with real-backend and upgrade suites, device/OS matrix, consumer application smoke tests | Release gate job plus manual approval before the final version is published |

## Conventions used in all workflows

- **Time budgets:** every job has `timeout-minutes`, and slow suites live in a later stage instead of the pull-request path.
- **Retries:** none on pull requests. UI/E2E tests get at most one retry, and tests that pass only on retry are reported as flaky.
- **Quarantine:** unstable tests run in a separate non-blocking job (`continue-on-error`) and still report results.
- **Diagnostics:** result bundles, test reports, logs, and screenshots are uploaded as artifacts on failure.
