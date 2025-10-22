#!/bin/bash

# MyDay Desktop - Comprehensive Build Script
# Creates AppImage and tarball distributions

set -e

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_DIR"

VERSION="1.0.2"
APP_NAME="MyDayDesktop"
PACKAGE_NAME="MyDayDesktop-${VERSION}"

echo "======================================"
echo "  MyDay Desktop Build Script"
echo "  Version: $VERSION"
echo "======================================"
echo ""

show_help() {
    cat << EOF
MyDay Desktop - Build Helper

Usage: ./build.sh [command]

Commands:
    run         - Run the application directly (for testing)
    build       - Build the Gradle project only
    jar         - Build fat JAR
    appimage    - Create AppImage (recommended)
    tarball     - Create tarball distribution
    all         - Create both AppImage and tarball
    clean       - Clean build directories
    help        - Show this help message

Examples:
    ./build.sh run          # Quick test
    ./build.sh all          # Build everything
    ./build.sh appimage     # Create AppImage only
EOF
}

run_app() {
    echo "🚀 Running MyDay Desktop..."
    ./gradlew run
}

build_gradle() {
    echo "🔨 Building Gradle project..."
    ./gradlew build --no-daemon
    echo "✅ Build successful!"
}

build_jar() {
    echo "📦 Building fat JAR..."
    ./gradlew packageUberJarForCurrentOS --no-daemon
    
    # Find the generated JAR
    JAR_FILE=$(find build/compose/jars -name "*.jar" -type f | head -1)
    
    if [ -f "$JAR_FILE" ]; then
        echo "✅ JAR created: $JAR_FILE"
        return 0
    else
        echo "❌ JAR file not found!"
        return 1
    fi
}

create_appimage() {
    echo ""
    echo "📦 Creating AppImage..."
    echo "======================================"
    
    # Build the fat JAR first
    build_jar
    
    # Find the JAR
    JAR_FILE=$(find build/compose/jars -name "*.jar" -type f | head -1)
    
    if [ ! -f "$JAR_FILE" ]; then
        echo "❌ JAR file not found!"
        exit 1
    fi
    
    # Create AppDir structure
    APPDIR="appimage/MyDay.AppDir"
    rm -rf "$APPDIR"
    mkdir -p "$APPDIR/usr/bin"
    mkdir -p "$APPDIR/usr/share/applications"
    mkdir -p "$APPDIR/usr/share/icons/hicolor/256x256/apps"
    
    # Copy JAR
    echo "Copying JAR..."
    cp "$JAR_FILE" "$APPDIR/usr/bin/myday.jar"
    
    # Create launcher script
    echo "Creating launcher..."
    cat > "$APPDIR/usr/bin/myday" << 'LAUNCHER_EOF'
#!/bin/bash
APPDIR="$(dirname "$(dirname "$(readlink -f "$0")")")"
exec java -jar "$APPDIR/bin/myday.jar" "$@"
LAUNCHER_EOF
    chmod +x "$APPDIR/usr/bin/myday"
    
    # Create AppRun
    cat > "$APPDIR/AppRun" << 'APPRUN_EOF'
#!/bin/bash
APPDIR="$(dirname "$(readlink -f "$0")")"
exec "$APPDIR/usr/bin/myday" "$@"
APPRUN_EOF
    chmod +x "$APPDIR/AppRun"
    
    # Copy desktop file
    cp com.cobrien.MyDay.desktop "$APPDIR/myday.desktop"
    cp com.cobrien.MyDay.desktop "$APPDIR/usr/share/applications/"

    # Copy the proper icon from resources
    if [ -f "src/main/resources/icon.png" ]; then
        echo "Using icon from src/main/resources/icon.png"
        cp "src/main/resources/icon.png" "$APPDIR/myday.png"
    else
        echo "Warning: icon.png not found, creating placeholder"
        # Create a simple icon (text-based SVG) as fallback
        cat > "$APPDIR/myday.svg" << 'ICON_EOF'
<svg width="256" height="256" xmlns="http://www.w3.org/2000/svg">
  <rect width="256" height="256" fill="#6200EE" rx="32"/>
  <text x="50%" y="50%" font-size="120" fill="white" text-anchor="middle" dy=".3em" font-family="Arial, sans-serif" font-weight="bold">MD</text>
  <circle cx="200" cy="56" r="24" fill="#03DAC6"/>
</svg>
ICON_EOF

        # Convert SVG to PNG if possible
        if command -v convert &> /dev/null; then
            convert "$APPDIR/myday.svg" -resize 512x512 "$APPDIR/myday.png" 2>/dev/null || true
            rm -f "$APPDIR/myday.svg"
        fi
    fi

    cp "$APPDIR/myday.png" "$APPDIR/usr/share/icons/hicolor/256x256/apps/"
    cp "$APPDIR/myday.png" "$APPDIR/.DirIcon"
    
    # Download appimagetool if not present
    if [ ! -f "appimagetool-x86_64.AppImage" ]; then
        echo "Downloading appimagetool..."
        wget -q "https://github.com/AppImage/AppImageKit/releases/download/continuous/appimagetool-x86_64.AppImage"
        chmod +x appimagetool-x86_64.AppImage
    fi
    
    # Create AppImage
    echo "Building AppImage..."
    ARCH=x86_64 ./appimagetool-x86_64.AppImage "$APPDIR" "${APP_NAME}-${VERSION}-x86_64.AppImage" 2>&1 | grep -v "WARNING" || true
    
    if [ -f "${APP_NAME}-${VERSION}-x86_64.AppImage" ]; then
        chmod +x "${APP_NAME}-${VERSION}-x86_64.AppImage"
        echo ""
        echo "✅ AppImage created successfully!"
        echo "📦 File: ${APP_NAME}-${VERSION}-x86_64.AppImage"
        echo "   Size: $(du -h "${APP_NAME}-${VERSION}-x86_64.AppImage" | cut -f1)"
        echo ""
        echo "To run: ./${APP_NAME}-${VERSION}-x86_64.AppImage"
    else
        echo "❌ Failed to create AppImage"
        exit 1
    fi
}

create_tarball() {
    echo ""
    echo "📦 Creating tarball distribution..."
    echo "======================================"
    
    # Build the fat JAR first
    build_jar
    
    # Find the JAR
    JAR_FILE=$(find build/compose/jars -name "*.jar" -type f | head -1)
    
    if [ ! -f "$JAR_FILE" ]; then
        echo "❌ JAR file not found!"
        exit 1
    fi
    
    # Create distribution directory
    DIST_DIR="${PACKAGE_NAME}-linux"
    rm -rf "$DIST_DIR"
    mkdir -p "$DIST_DIR"
    
    # Copy JAR
    cp "$JAR_FILE" "$DIST_DIR/myday.jar"
    
    # Create launcher script
    cat > "$DIST_DIR/myday.sh" << 'LAUNCHER_EOF'
#!/bin/bash
# MyDay Desktop Launcher
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
exec java -jar "$SCRIPT_DIR/myday.jar" "$@"
LAUNCHER_EOF
    chmod +x "$DIST_DIR/myday.sh"
    
    # Copy documentation
    cp README.md "$DIST_DIR/" 2>/dev/null || true
    cp QUICKSTART.md "$DIST_DIR/" 2>/dev/null || true
    cp PROJECT_SUMMARY.md "$DIST_DIR/" 2>/dev/null || true
    
    # Create installation instructions
    cat > "$DIST_DIR/INSTALL.txt" << 'INSTALL_EOF'
MyDay Desktop - Installation Instructions
==========================================

Quick Start:
-----------
1. Extract this archive to any location
2. Run: ./myday.sh

Requirements:
------------
- Java 17 or higher
- Linux with X11 or Wayland

Installation (Optional):
-----------------------
To install system-wide:

1. Copy to /opt:
   sudo cp -r MyDay-1.0.1-linux /opt/myday

2. Create symlink:
   sudo ln -s /opt/myday/myday.sh /usr/local/bin/myday

3. Run from anywhere:
   myday

Data Storage:
------------
All your data is stored in: ~/.myday/data.json

Uninstallation:
--------------
1. Remove the program:
   sudo rm -rf /opt/myday
   sudo rm /usr/local/bin/myday

2. Remove your data (optional):
   rm -rf ~/.myday

Enjoy MyDay Desktop!
INSTALL_EOF
    
    # Create tarball
    tar czf "${PACKAGE_NAME}-linux.tar.gz" "$DIST_DIR"
    
    if [ -f "${PACKAGE_NAME}-linux.tar.gz" ]; then
        echo ""
        echo "✅ Tarball created successfully!"
        echo "📦 File: ${PACKAGE_NAME}-linux.tar.gz"
        echo "   Size: $(du -h "${PACKAGE_NAME}-linux.tar.gz" | cut -f1)"
        echo ""
        echo "Extract with: tar xzf ${PACKAGE_NAME}-linux.tar.gz"
        echo "Then run: ./${DIST_DIR}/myday.sh"
    else
        echo "❌ Failed to create tarball"
        exit 1
    fi
}

build_all() {
    echo "🏗️  Building all distributions..."
    echo ""
    
    # Build Gradle first
    build_gradle
    echo ""
    
    # Create AppImage
    create_appimage
    echo ""
    
    # Create Tarball
    create_tarball
    echo ""
    
    echo "======================================"
    echo "✅ All builds complete!"
    echo "======================================"
    echo ""
    echo "Created files:"
    ls -lh "${APP_NAME}-${VERSION}-x86_64.AppImage" 2>/dev/null || true
    ls -lh "${PACKAGE_NAME}-linux.tar.gz" 2>/dev/null || true
    echo ""
}

clean_build() {
    echo "🧹 Cleaning build directories..."
    rm -rf build/ .gradle/ appimage/ appimagetool-x86_64.AppImage
    rm -f *.AppImage *.tar.gz
    rm -rf MyDay-*-linux/
    echo "✅ Clean complete!"
}

# Main script
case "${1:-help}" in
    run)
        run_app
        ;;
    build)
        build_gradle
        ;;
    jar)
        build_jar
        ;;
    appimage)
        create_appimage
        ;;
    tarball)
        create_tarball
        ;;
    all)
        build_all
        ;;
    clean)
        clean_build
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        echo "Unknown command: $1"
        echo ""
        show_help
        exit 1
        ;;
esac
