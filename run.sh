#!/usr/bin/env bash
set -euo pipefail

# run.sh - run the fat JAR produced by the Maven build
# Usage:
#   ./run.sh [args...]
# The fat jar should be at target/cgt-coinbase-1-fat.jar (created by the shade plugin).

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# find the fat jar (attached by maven-shade-plugin with classifier 'fat')
FAT_JAR="$(ls target/*-fat.jar 2>/dev/null | head -n 1 || true)"

if [ -z "$FAT_JAR" ]; then
  echo "Fat JAR not found in target/. Please build it first with: mvn -DskipTests package"
  exit 1
fi

echo "Attempting to run fat JAR: $FAT_JAR"
if java -jar "$FAT_JAR" "$@"; then
  exit 0
else
  echo "Running fat JAR failed; falling back to non-fat jar + dependency directory (classpath mode)."
  # prefer existing dependency directory if available
  if [ -d target/dependency ] && [ -n "$(ls -A target/dependency)" ]; then
    CP="target/cgt-coinbase-1.jar:target/dependency/*"
  else
    echo "Dependency directory target/dependency missing or empty — copying runtime dependencies now..."
    mvn dependency:copy-dependencies -DincludeScope=runtime -DoutputDirectory=target/dependency
    CP="target/cgt-coinbase-1.jar:target/dependency/*"
  fi
  echo "Running com.s4apps.CalculateTotals on classpath: $CP"
  java -cp "$CP" com.s4apps.CalculateTotals "$@"
fi
