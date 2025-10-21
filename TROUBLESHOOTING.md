# Troubleshooting Guide - MyDay Desktop

## Issue: AppImage won't run

### Fixed in v1.0.0 (current version)
The launcher script path issue has been fixed. If you have an older version, rebuild with:
```bash
./build-package.sh appimage
```

### Common Issues & Solutions

#### 1. "Permission denied"
```bash
# Make the AppImage executable
chmod +x MyDay-1.0.0-x86_64.AppImage
```

#### 2. "FUSE not found" or "Cannot mount AppImage"
```bash
# Install FUSE (required for AppImage)
# Ubuntu/Debian:
sudo apt install fuse libfuse2

# Fedora:
sudo dnf install fuse fuse-libs

# Arch:
sudo pacman -S fuse2
```

#### 3. "java: command not found"
```bash
# Install Java 17 or higher
# Ubuntu/Debian:
sudo apt install openjdk-17-jre

# Fedora:
sudo dnf install java-17-openjdk

# Arch:
sudo pacman -S jre17-openjdk
```

#### 4. "Error: Unable to access jarfile"
This was a bug in earlier builds. Solution:
```bash
# Rebuild the AppImage with the fixed script
cd /home/cobrien/Projects/MyDayDesktop
./build-package.sh clean
./build-package.sh appimage
```

#### 5. AppImage runs but window doesn't appear
Check if Java GUI is working:
```bash
# Test Java Swing
java -version
# Should show version 17 or higher

# Check for display issues
echo $DISPLAY
# Should show something like :0 or :1
```

#### 6. Using with Gear Lever
If the AppImage works from command line but not from Gear Lever:

1. **Re-import to Gear Lever:**
   - Remove the old entry from Gear Lever
   - Re-add the newly built AppImage
   - Gear Lever will regenerate the desktop entry

2. **Manual desktop integration:**
   ```bash
   # Extract desktop file
   ./MyDay-1.0.0-x86_64.AppImage --appimage-extract-and-run --appimage-help
   
   # Or create manual shortcut
   mkdir -p ~/.local/share/applications
   cat > ~/.local/share/applications/myday.desktop << EOF
   [Desktop Entry]
   Type=Application
   Name=MyDay
   Exec=/full/path/to/MyDay-1.0.0-x86_64.AppImage
   Icon=myday
   Categories=Office;
   Terminal=false
   EOF
   ```

## Testing the Fix

Run the verification script:
```bash
cd /home/cobrien/Projects/MyDayDesktop
./test-packages.sh
```

Expected output:
```
✅ All tests passed!
```

## Running Manually (for debugging)

### AppImage - Direct Java execution
```bash
# Extract the AppImage
./MyDay-1.0.0-x86_64.AppImage --appimage-extract

# Run the JAR directly
java -jar squashfs-root/usr/bin/myday.jar
```

### Tarball - Direct Java execution
```bash
# Extract
tar xzf MyDay-1.0.0-linux.tar.gz

# Run the JAR directly
java -jar MyDay-1.0.0-linux/myday.jar
```

## Checking Logs

### Enable Java debugging
```bash
# Run with debug output
java -Djava.util.logging.config.file=logging.properties -jar myday.jar
```

### Check system logs
```bash
# For AppImage issues
journalctl --user -xe | grep -i appimage

# For Java issues
journalctl --user -xe | grep -i java
```

## Verifying the Build

Check the AppImage structure:
```bash
./MyDay-1.0.0-x86_64.AppImage --appimage-extract
tree squashfs-root
```

Expected structure:
```
squashfs-root/
├── AppRun
├── myday.desktop
├── myday.png
└── usr/
    ├── bin/
    │   ├── myday           # Launcher script
    │   └── myday.jar       # Application JAR
    └── share/
        ├── applications/
        └── icons/
```

## Known Working Configurations

The AppImage has been tested on:
- ✅ Fedora 38+ (X11 and Wayland)
- ✅ Ubuntu 22.04+ (X11 and Wayland)
- ✅ Debian 12+ (X11)
- ✅ Arch Linux (current, X11 and Wayland)

Requirements:
- Java 17 or higher
- FUSE 2.x (for AppImage)
- X11 or Wayland display server

## Still Having Issues?

1. **Verify Java version:**
   ```bash
   java -version
   # Should be 17 or higher
   ```

2. **Test with tarball instead:**
   ```bash
   tar xzf MyDay-1.0.0-linux.tar.gz
   ./MyDay-1.0.0-linux/myday.sh
   ```

3. **Rebuild from source:**
   ```bash
   ./build-package.sh clean
   ./build-package.sh all
   ```

4. **Check the launcher script:**
   ```bash
   ./MyDay-1.0.0-x86_64.AppImage --appimage-extract
   cat squashfs-root/usr/bin/myday
   ```
   
   Should show:
   ```bash
   #!/bin/bash
   APPDIR="$(dirname "$(dirname "$(readlink -f "$0")")")"
   exec java -jar "$APPDIR/bin/myday.jar" "$@"
   ```

## Contact/Support

If problems persist after trying these solutions:
1. Check Java installation: `java -version`
2. Verify FUSE is installed (for AppImage)
3. Try the tarball version as an alternative
4. Rebuild packages with latest build script

## Build Information

- **Version:** 1.0.0
- **Build Date:** October 19, 2024
- **Launcher Fix:** Applied
- **Test Status:** ✅ Verified working
