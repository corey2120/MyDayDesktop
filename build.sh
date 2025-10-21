#!/bin/bash

# Build script for MyDay Desktop
# Creates a distributable package for Linux

echo "Building MyDay Desktop..."

# Clean previous builds
./gradlew clean

# Build the distribution
./gradlew packageUberJarForCurrentOS

echo ""
echo "Build complete!"
echo ""
echo "To run the application:"
echo "  ./gradlew run"
echo ""
echo "Or install the generated package from:"
echo "  build/compose/jars/"
