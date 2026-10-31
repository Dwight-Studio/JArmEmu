#!/usr/bin/env python3
"""Reads the latest <release> of the metainfo file and writes its version and release notes.

Usage: release_notes.py <metainfo.xml> <notes-output-file>
Prints the version of the latest release on stdout.
"""
import sys
import xml.etree.ElementTree as ET

REPO = "https://github.com/Dwight-Studio/JArmEmu"
JAVA_NOTICE = ("**JArmEmu requires Java 21 : "
               "https://adoptium.net/fr/marketplace/?package=jre&os=any&version=21**")

metainfo, output = sys.argv[1:3]
releases = ET.parse(metainfo).getroot().findall("./releases/release")
latest = releases[0]
version = latest.get("version")
previous = releases[1].get("version") if len(releases) > 1 else None

description = latest.find("description")
paragraphs = [" ".join("".join(p.itertext()).split()) for p in description.findall("p")]
title = paragraphs[0] if paragraphs else "Stable release of JArmEmu"
changes = [" ".join("".join(li.itertext()).split()) for li in description.iter("li")]

lines = [f"### {title}", "", JAVA_NOTICE, "", "#### Changelog", ""]
lines += [f"- {change}" for change in changes]
if previous:
    lines += ["", f"**Full Changelog**: {REPO}/compare/v{previous}...v{version}"]

with open(output, "w", encoding="utf-8") as f:
    f.write("\n".join(lines) + "\n")
print(version)
