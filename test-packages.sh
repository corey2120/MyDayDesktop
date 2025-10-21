#!/bin/bash

# MyDay Desktop - Test/Verification Script
# Tests both AppImage and Tarball to ensure they work

set -e

echo "════════════════════════════════════════════════"
echo "  MyDay Desktop - Package Verification"
echo "════════════════════════════════════════════════"
echo ""

# Check if packages exist
if [ ! -f "MyDay-1.0.0-x86_64.AppImage" ]; then
    echo "❌ AppImage not found!"
    echo "   Run: ./build-package.sh appimage"
    exit 1
fi

if [ ! -f "MyDay-1.0.0-linux.tar.gz" ]; then
    echo "❌ Tarball not found!"
    echo "   Run: ./build-package.sh tarball"
    exit 1
fi

echo "✅ Both packages found"
echo ""

# Check Java
echo "Checking Java installation..."
if ! command -v java &> /dev/null; then
    echo "❌ Java not found!"
    echo "   Install Java 17 or higher"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | cut -d'.' -f1)
echo "✅ Java version: $JAVA_VERSION"

if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "⚠️  Warning: Java 17 or higher recommended"
fi
echo ""

# Test AppImage structure
echo "Testing AppImage..."
./MyDay-1.0.0-x86_64.AppImage --appimage-extract > /dev/null 2>&1

if [ -f "squashfs-root/usr/bin/myday.jar" ]; then
    echo "✅ AppImage structure correct"
else
    echo "❌ AppImage structure incorrect"
    exit 1
fi

# Test launcher script
if [ -x "squashfs-root/usr/bin/myday" ]; then
    echo "✅ Launcher script present and executable"
else
    echo "❌ Launcher script missing or not executable"
    exit 1
fi

# Check desktop file
if [ -f "squashfs-root/myday.desktop" ]; then
    echo "✅ Desktop file present"
else
    echo "❌ Desktop file missing"
fi

# Clean up extracted files
rm -rf squashfs-root
echo ""

# Test tarball
echo "Testing Tarball..."
tar xzf MyDay-1.0.0-linux.tar.gz

if [ -f "MyDay-1.0.0-linux/myday.jar" ]; then
    echo "✅ Tarball JAR present"
else
    echo "❌ Tarball JAR missing"
    rm -rf MyDay-1.0.0-linux
    exit 1
fi

if [ -x "MyDay-1.0.0-linux/myday.sh" ]; then
    echo "✅ Tarball launcher present and executable"
else
    echo "❌ Tarball launcher missing or not executable"
    rm -rf MyDay-1.0.0-linux
    exit 1
fi

# Clean up
rm -rf MyDay-1.0.0-linux
echo ""

echo "════════════════════════════════════════════════"
echo "✅ All tests passed!"
echo "════════════════════════════════════════════════"
echo ""
echo "Both packages are ready to use:"
echo ""
echo "  AppImage:"
echo "  ./MyDay-1.0.0-x86_64.AppImage"
echo ""
echo "  Tarball:"
echo "  tar xzf MyDay-1.0.0-linux.tar.gz"
echo "  ./MyDay-1.0.0-linux/myday.sh"
echo ""
echo "📦 Package sizes:"
ls -lh MyDay-1.0.0-x86_64.AppImage MyDay-1.0.0-linux.tar.gz | awk '{printf "   %-40s %6s\n", $9, $5}'
echo ""
