# CScanner: A Cloud Security Scanner

[![Documentation](https://img.shields.io/badge/documentation-available-green.svg)](https://cscanner.io)
[![GitHub Releases](https://img.shields.io/github/release/cscannerio/core.svg)](https://github.com/cscannerio/core/releases)
[![Code Quality](https://img.shields.io/lgtm/grade/java/g/cscannerio/core.svg)](https://lgtm.com/projects/g/cscannerio/core/)
[![GitHub](https://img.shields.io/github/license/cscannerio/core.svg)](https://github.com/cscannerio/core/blob/master/LICENSE)
[![Discord](https://img.shields.io/discord/413306353545773069.svg)](https://pasztor.at/discord)
[![CircleCI](https://img.shields.io/circleci/project/github/cscannerio/core.svg)](https://circleci.com/gh/cscannerio/core)
[![GitHub last commit](https://img.shields.io/github/last-commit/cscannerio/core.svg)](https://github.com/cscannerio/core)
[![GitHub top language](https://img.shields.io/github/languages/top/cscannerio/core.svg)](https://github.com/cscannerio/core)
[![GitHub repo size](https://img.shields.io/github/repo-size/cscannerio/core.svg)](https://github.com/cscannerio/core)
[![GitHub issues](https://img.shields.io/github/issues/cscannerio/core.svg)](https://github.com/cscannerio/core/issues)
[![GitHub pull requests](https://img.shields.io/github/issues-pr/cscannerio/core.svg)](https://github.com/cscannerio/core/pulls)
[![GitHub stars](https://img.shields.io/github/stars/cscannerio/core.svg?style=social)](https://github.com/cscannerio/core)
[![Twitter Follow](https://img.shields.io/twitter/follow/cscannerio.svg?style=social)](https://twitter.com/cscannerio)

## Things to do

» [Grab the latest release](https://github.com/cscannerio/core/releases)

» [Read the documentation](https://docs.cscanner.io/)

## A brief introduction

This utility is intended to check your cloud configuration for compliance with your companies rules in an automated
fashion, not unlike AWS Config.

For example, if you want to make sure that your port 22 is never open to the world, across all your cloud providers,
you could do something like this:

```yaml
connections:
  # Configure your connections here
rules:
  - type: FIREWALL_PUBLIC_SERVICE_PROHIBITED
    protocol: "tcp"
    ports:
      - 22
```

You would then get a report detailing all your security groups across all your cloud providers and if they
are compliant or are violating the rules.

## Downloading

You can grab [one of the releases](https://github.com/cscannerio/core/releases) from GitHub.

## Running

To run the cscanner, simply point it to your config file:

```
java -jar cscanner.jar your-config-file.yaml
```

Make sure you have at least Java 8 to run this application. Note that you can use the `-h` or `--help` option to get a 
full list of possible filtering and output options.

## Full documentation

For a full documentation please see the cscanner website at [cscanner.io](https://docs.cscanner.io/).

