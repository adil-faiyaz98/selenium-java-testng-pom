#!/bin/bash
set -e

# Start Xvfb
Xvfb :99 -screen 0 $SCREEN_GEOMETRY -ac &
export DISPLAY=:99

# Wait for Xvfb to start
sleep 1

# Execute the command passed to the script
exec "$@"
