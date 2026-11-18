#!/bin/sh
# Development loop for the API container.
# Rebuilds and restarts the app when files under src/ or pom.xml change.
# Only one Maven process runs at a time, so generated sources are never written concurrently.

cd /workspace || exit 1
MARK=/tmp/.last-build
app_pid=""

stop_app() {
  if [ -n "$app_pid" ]; then
    kill -- "-$app_pid" 2>/dev/null || true
    wait "$app_pid" 2>/dev/null || true
    app_pid=""
  fi
}

start_app() {
  stop_app
  touch "$MARK"
  if ./mvnw -B -q -DskipTests compile; then
    setsid ./mvnw -B spring-boot:run &
    app_pid=$!
  else
    echo "[dev] compile failed; fix the error and save again"
  fi
}

start_app
while true; do
  sleep 2
  if [ -n "$(find src pom.xml -newer "$MARK" -print -quit)" ]; then
    echo "[dev] change detected; restarting"
    start_app
  fi
done
